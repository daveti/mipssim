/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

/**
 *
 * @author daveti
 * MIPS computational units implementation
 * Feb 10, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 */
public class Transitions {

    private final static String instructionAdd = "ADD";
    private final static String instructionSub = "SUB";
    private final static String instructionLd = "LD";
    private final static boolean debug = false;
    private static int clock;

    private static void decode() {

    }

    private static void read() {

    }
    
    private static boolean decodeAndRead(Queue<Ii> inm, Queue<Xi> rgf, Queue<Ii> inb) {
        
        boolean isLd;
        
        // Check INM at first
        if (inm.isEmpty()) {
            return false;
        }
        
        // Check the first instruction
        Ii ins = inm.peek();
        String r1, r2;
        Xi x1, x2;
        r1 = ins.sourceReg;
        switch (ins.opcode) {
            case instructionAdd:
            case instructionSub:
                isLd = false;
                break;
                
            case instructionLd:
                isLd = true;
                break;
                
            default:
                System.out.println("Error - invalid opcode=" + ins.opcode);
                return false;
        }

        // Check the first source reg
        x1 = Utils.getRegister(r1, rgf);
        if (x1 == null) {
            return false;
        }
        
        // Check the second source reg for ADD/SUB
        x2 = null;
        if (isLd == false) {
            r2 = ins.source2nd;
            x2 = Utils.getRegister(r2, rgf);
            if (x2 == null) {
                return false;
            }
        }
        
        // Decode & Read => INB
        Ii newIi;
        if (isLd) {
            newIi = new Ii(ins.opcode, ins.targetReg, x1.value, ins.source2nd, clock+1);
        } else {
            newIi = new Ii(ins.opcode, ins.targetReg, x1.value, x2.value, clock+1);
        }
        inm.dequeue();
        inb.enqueue(newIi);
        
        return true;     
    }

    private static boolean issue1(Queue<Ii> inb, Queue<Ii> aib) {
        
        // Check INB at first
        if (inb.isEmpty()) {
            return false;
        }
        
        // Check the first instruction
        Ii ins = inb.peek();
        if (ins.clock > clock) {
            return false;
        }
        // Only handle the Add/Sub
        if (ins.opcode == null ? instructionLd == null : ins.opcode.equals(instructionLd)) {
            return false;
        }
        
        // INB => AIB
        ins = inb.dequeue();
        ins.clock = clock+1;
        aib.enqueue(ins);
        
        return true;
    }

    private static boolean issue2(Queue<Ii> inb, Queue<Ii> lib) {
        
        // Check INB at first
        if (inb.isEmpty()) {
            return false;
        }

        // Check the first instruction
        Ii ins = inb.peek();
        if (ins.clock > clock) {
            return false;
        }
        // Only handle Ld
        if (ins.opcode == null ? instructionLd != null : !ins.opcode.equals(instructionLd)) {
            return false;
        }
        
        // INB => LIB
        ins = inb.dequeue();
        ins.clock = clock+1;
        lib.enqueue(ins);
        
        return true;
    }

    private static boolean addr(Queue<Ii> lib, Queue<Xi> adb) {
        
        // Check LIB at first
        if (lib.isEmpty()) {
            return false;
        }
        
        // Check the first instruction
        Ii ins = lib.peek();
        if (ins.clock > clock) {
            return false;
        }
        
        // LIB => ADB
        ins = lib.dequeue();
        String targetReg = ins.targetReg;
        String address = (new Integer(Integer.parseInt(ins.sourceReg) + Integer.parseInt(ins.source2nd))).toString();
        Xi newX = new Xi(targetReg, address, clock+1);
        adb.enqueue(newX);
        
        return true;
    }

    private static boolean asu(Queue<Ii> aib, Queue<Xi> reb) {

        // Check AIK at first
        if (aib.isEmpty()) {
            return false;
        }
        
        // Check the first instruction
        Ii ins = aib.peek();
        if (ins.clock > clock) {
            return false;
        }
        
        // AIB => REB
        ins = aib.dequeue();
        String targetReg = ins.targetReg;
        int result;
        switch (ins.opcode) {
            case instructionAdd:
                result = Integer.parseInt(ins.sourceReg) + Integer.parseInt(ins.source2nd);
                break;
                
            case instructionSub:
                result = Integer.parseInt(ins.sourceReg) - Integer.parseInt(ins.source2nd);
                break;
                
            default:
                System.out.println("Error - unsupported opcode=" + ins.opcode);
                return false;
        }
        Xi newX = new Xi(targetReg, (new Integer(result)).toString(), clock+1);
        reb.enqueue(newX);
        
        return true;
    }

    private static boolean load(Queue<Xi> adb, Queue<Di> dam, Queue<Xi> reb) {

        // Check ADB at first
        if (adb.isEmpty()) {
            return false;
        }
        
        // Check the first reg
        Xi x = adb.peek();
        if (x.clock > clock) {
            return false;
        }
        
        // ADB + DAM => REB
        x = adb.dequeue();
        String address = x.value;
        Di d = Utils.getDatamem(address, dam);
        if (d == null) {
            System.out.println("Error - invalid memory access");
            return false;
        }
        String newValue = d.value;
        Xi newX = new Xi(x.reg, newValue, clock+1);
        reb.enqueue(newX);
        
        return true;
    }

    private static boolean write(Queue<Xi> reb, Queue<Xi> rgf) {

        // Check REB at first
        if (reb.isEmpty()) {
            return false;
        }
        
        // Check the first reg
        Xi x = reb.peek();
        if (x.clock > clock) {
            return false;
        }
        
        // REB => RGF
        x = reb.dequeue();
        Xi xEx = Utils.getRegister(x.reg, rgf);
        if (xEx != null) {
            // Update the existing reg
            xEx.value = x.value;
        } else {
            // Add this new reg
            rgf.enqueue(x);
        }
        
        return true;
    }

    /**
     *
     * @param clk
     * @param inm
     * @param inb
     * @param aib
     * @param lib
     * @param adb
     * @param reb
     * @param rgf
     * @param dam
     * @return
     */
    public static boolean simulation(int clk,
                                    Queue<Ii> inm, Queue<Ii> inb, Queue<Ii> aib,
                                    Queue<Ii> lib, Queue<Xi> adb, Queue<Xi> reb,
                                    Queue<Xi> rgf, Queue<Di> dam) {

        clock = clk;
        boolean hasTrans = false;
        boolean ret;
        
        ret = decodeAndRead(inm, rgf, inb);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after decodeAndRead");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after decodeAndRead");
        }
        
        ret = issue1(inb, aib);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after issue1");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after issue1");
        }
        
        ret = issue2(inb, lib);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after issue2");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after issue2");
        }
        
        // NOTE: do not care about write-after-write for REB
        // which means we do not care about the race condition
        // happened between load and asu...
        /*
        ret = asu(aib, reb);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after asu");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after asu");
        }
        */
        
        ret = addr(lib, adb);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after addr");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after addr");
        }
        
        ret = load(adb, dam, reb);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after load");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after load");
        }
        
        // According to the correct-simulation
        // When race condition happens, LOAD happens before ASU
        ret = asu(aib, reb);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after asu");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after asu");
        }
        
        ret = write(reb, rgf);
        if (ret) {
            hasTrans = true;
        }
        
        if (debug) {
            System.out.println("Debug: start after write");
            Utils.stepDump(clk, inm, inb, aib, lib, adb, reb, rgf, dam);
            System.out.println("Debug: end after write");
        }
        
        return hasTrans;
    }

}

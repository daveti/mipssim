/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daveti
 * MIPSsim I/O related implementations
 * Feb 14, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 */
public class Utils {

    private final static String instructionInput = "instructions.txt";
    private final static String registerInput = "registers.txt";
    private final static String datamemInput = "datamemory.txt";
    private final static String simOutput = "simulation.txt";
    private final static int instructionEnum = 1;
    private final static int registerEnum = 2;
    private final static int datamemEnum = 3;
    private final static boolean debug = false;

    public final static String DELIMITER = ",";

    private static void readParseInput(Queue q, int n) throws FileNotFoundException, IOException {

        String inputFile;

        switch (n) {
            case instructionEnum:
                inputFile = instructionInput;
                break;

            case registerEnum:
                inputFile = registerInput;
                break;

            case datamemEnum:
                inputFile = datamemInput;
                break;

            default:
                System.out.println("Error: unknown enum " + n);
                return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            String line = br.readLine();
            while (line != null) {
                if (debug) {
                    System.out.println("Debug: line=" + line);
                }

                // Get the input from the line
                String input = getCsvInput(line);
                if (debug) {
                    System.out.println("Debug: input=" + input);
                }

                // Get the tokens
                String[] tokens = input.split(DELIMITER);
                for (String t : tokens) {
                    if (debug) {
                        System.out.println("Debug: token=" + t);
                    }
                }

                switch (n) {
                    case instructionEnum:
                        parsePushInstruction(tokens, q);
                        break;

                    case registerEnum:
                        parsePushRegister(tokens, q);
                        break;

                    case datamemEnum:
                        parsePushDatamem(tokens, q);
                        break;

                    default:
                        System.out.println("Error - we should never be here");
                        return;
                }

                line = br.readLine();
            }
        }
    }

    /**
     *
     * @param ins: Instruction Queue
     * @param reg: Register Queue
     * @param dat: Data memory Queue
     * @throws IOException
     */
    public static void initQueues(Queue<Ii> ins, Queue<Xi> reg, Queue<Di> dat) throws IOException {

        try {
            readParseInput(ins, instructionEnum);
            readParseInput(reg, registerEnum);
            readParseInput(dat, datamemEnum);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the output
     */
    public static void redirectOutput() {

        try {
            System.setOut(new PrintStream(new File(simOutput)));
        } catch (Exception e) {
        }
    }
    
    public static void stepDump(int step, Queue<Ii> inm, Queue<Ii> inb, Queue<Ii> aib,
                                Queue<Ii> lib, Queue<Xi> adb, Queue<Xi> reb, Queue<Xi> rgf,
                                Queue<Di> dam) {
        
        System.out.println("STEP " + step + ":");
        System.out.println("INM:" + inm);
        System.out.println("INB:" + inb);
        System.out.println("AIB:" + aib);
        System.out.println("LIB:" + lib);
        System.out.println("ADB:" + adb);
        System.out.println("REB:" + reb);
        System.out.println("RGF:" + rgf.toStringSorted());
        System.out.println("DAM:" + dam.toStringSorted());    
    }
    
    public static Xi getRegister(String reg, Queue<Xi> rgf) {
        
        // For RGF, we should not enqueue/dequeue anything
        ArrayList<Xi> arrayList = rgf.toArrayList();
        for (Xi x : arrayList) {
            if (x.reg == null ? reg == null : x.reg.equals(reg)) {
                return x;
            }
        }
        
        return null;       
    }
    
    public static Di getDatamem(String addr, Queue<Di> dam) {
        
        // For DAM, we should not enqueue/dequeue anything
        ArrayList<Di> arrayList = dam.toArrayList();
        for (Di d : arrayList) {
            if (d.address == null ? addr == null : d.address.equals(addr)) {
                return d;
            }
        }
        
        return null;
    }

    private static void parsePushInstruction(String[] tokens, Queue<Ii> q) {

        // Construct the instruction
        Ii instruction = new Ii(tokens[0], tokens[1], tokens[2], tokens[3], 0);

        // Push into Q
        q.enqueue(instruction);
    }

    private static void parsePushRegister(String[] tokens, Queue<Xi> q) {

        // Construct the register
        Xi register = new Xi(tokens[0], tokens[1], 0);

        // Push into Q
        q.enqueue(register);
    }

    private static void parsePushDatamem(String[] tokens, Queue<Di> q) {

        // Construct the datamem
        Di datamem = new Di(tokens[0], tokens[1], 0);

        // Push into Q
        q.enqueue(datamem);
    }

    private static String getCsvInput(String line) {

        // The format should be <X>
        // Retrieve X
        return line.substring(line.indexOf("<") + 1, line.indexOf(">"));
    }
    
}
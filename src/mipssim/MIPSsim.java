/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daveti
 * MIPS simulator
 * with limited instruction support (ADD/SUB/LD)
 * Feb 10, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 * 
 * input: instructions.txt, registers.txt, datamemory.txt
 * output: simulation.txt
 */
public class MIPSsim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Init Qs
        Queue<Ii> INM = new Queue<>();
        Queue<Ii> INB = new Queue<>();
        Queue<Ii> LIB = new Queue<>();
        Queue<Ii> AIB = new Queue<>();
        Queue<Xi> ADB = new Queue<>();
        Queue<Xi> REB = new Queue<>();
        Queue<Xi> RGF = new Queue<>();
        Queue<Di> DAM = new Queue<>();
        
        try {
            // Load the input
            Utils.initQueues(INM, RGF, DAM);
        } catch (IOException ex) {
            Logger.getLogger(MIPSsim.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Redirect the output
        Utils.redirectOutput();
        
        // Start the simulation
        boolean hasTrans;
        int step = 0;
        do {
            // Dump the Qs
            Utils.stepDump(step, INM, INB, AIB, LIB, ADB, REB, RGF, DAM);
            
            // Petri Net Model for a MIPS processor
            hasTrans = Transitions.simulation(step, INM, INB, AIB, LIB, ADB, REB, RGF, DAM);
            
            // Check if simulation is done
            if (hasTrans) {
                System.out.println();
            }
            
            step++;
      
        } while (hasTrans);
    }
    
}

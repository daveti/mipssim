/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

/**
 *
 * @author daveti
 * Instructions
 * Feb 10, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 */
public class Ii {
    
    public String opcode;
    public String targetReg;
    public String sourceReg;
    public String source2nd;   // Reg for ADD/SUB but immediate value for LD
    public int clock;
    
    public Ii(String op, String tarReg, String srcReg, String src2nd, int clk) {
        opcode = op;
        targetReg = tarReg;
        sourceReg = srcReg;
        source2nd = src2nd;
        clock = clk;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("<").append(opcode).append(",").append(targetReg).append(",").append(sourceReg).append(",").append(source2nd).append(">");
        return s.toString();
    }
    
}

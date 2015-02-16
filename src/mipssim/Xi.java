/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

/**
 *
 * @author daveti
 * Register Values
 * Feb 10, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 */
public class Xi implements Comparable<Xi> {
    
    public String reg;
    public String value;
    public int clock;
    
    public Xi(String r, String v, int clk) {
        reg = r;
        value = v;
        clock = clk;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("<").append(reg).append(",").append(value).append(">");
        return s.toString();
    }

    @Override
    public int compareTo(Xi o) {  
        return (Character.getNumericValue(this.reg.charAt(1)) -
                Character.getNumericValue(o.reg.charAt(1)));
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mipssim;

/**
 *
 * @author daveti
 * Data Values in Memory
 * Feb 10, 2015
 * root@davejingtian.org
 * http://davejingtian.org
 */
public class Di implements Comparable<Di> {
    
    public String address;
    public int value;
    
    public Di(String addr, int v) {
        address = addr;
        value = v;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("<").append(address).append(",").append(value).append(">");
        return s.toString();
    }

    @Override
    public int compareTo(Di o) {
        return (Integer.parseInt(this.address) - 
                Integer.parseInt(o.address));
    }
    
}

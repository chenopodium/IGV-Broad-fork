/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iontorrent.data;

import java.util.ArrayList;

/**
 * The inogram of a part of an alignment, including the empties
 * @author Chantal Roth
 */
public class Ionogram {
    
    private String readname;
    
    private String locusinfo;
    
    private int chromosome_center_location;
    
    private int nrbases_left_right;
    
    private ArrayList<FlowValue> flowvalues;
       
    private String floworder;
    
    private FlowValue[] slotrow;
    
    public Ionogram(String readname, int chromosome_center_location) {
        this.readname = readname;
        this.chromosome_center_location = chromosome_center_location;        
        
        flowvalues = new ArrayList<FlowValue>();
    }
    
    public void addFlowValue(FlowValue flowvalue) {
        getFlowvalues().add(flowvalue);
        // add to maps
    }

    
    @Override
    public String toString() {
        String s = readname+"@ "+getLocusinfo()+":\n";
        s += FlowValue.getHeader()+"\n";
        for (FlowValue fv: getFlowvalues()) {
            s += fv.toString()+"\n";
        }
        return s;
    }
     public String toHtml() {
        String nl = "<br>";
        String s = readname+"@ "+getLocusinfo()+":"+nl;
        s += FlowValue.getHeader()+nl;
        for (FlowValue fv: getFlowvalues()) {
            s += fv.toString()+nl;
        }
        return s;
    }
    
    /**
     * @return the chromosome_center_location
     */
    public int getChromosome_center_location() {
        return chromosome_center_location;
    }

    /**
     * @param chromosome_center_location the chromosome_center_location to set
     */
    public void setChromosome_center_location(int chromosome_center_location) {
        this.chromosome_center_location = chromosome_center_location;
    }

    /**
     * @return the nrbases_left_right
     */
    public int getNrbases_left_right() {
        return nrbases_left_right;
    }

    /**
     * @param nrbases_left_right the nrbases_left_right to set
     */
    public void setNrbases_left_right(int nrbases_left_right) {
        this.nrbases_left_right = nrbases_left_right;
    }
    /**
     * @return the floworder
     */
    public String getFloworder() {
        return floworder;
    }

    /**
     * @param floworder the floworder to set
     */
    public void setFloworder(String floworder) {
        this.floworder = floworder;
    }

    /**
     * @return the readname
     */
    public String getReadname() {
        return readname;
    }

    /**
     * @param readname the readname to set
     */
    public void setReadname(String readname) {
        this.readname = readname;
    }

    /**
     * @return the locusinfo
     */
    public String getLocusinfo() {
        return locusinfo;
    }

    /**
     * @param locusinfo the locusinfo to set
     */
    public void setLocusinfo(String locusinfo) {
        this.locusinfo = locusinfo;
    }

    /**
     * @return the flowvalues
     */
    public ArrayList<FlowValue> getFlowvalues() {
        return flowvalues;
    }

    /**
     * @param flowvalues the flowvalues to set
     */
    public void setFlowvalues(ArrayList<FlowValue> flowvalues) {
        this.flowvalues = flowvalues;
    }

    /**
     * @return the slotrow
     */
    public FlowValue[] getSlotrow() {
        return slotrow;
    }

    /**
     * @param slotrow the slotrow to set
     */
    public void setSlotrow(FlowValue[] slotrow) {
        this.slotrow = slotrow;
    }

    public int getMaxValue() {
        int max = 0;
        for (FlowValue f: flowvalues) {
            if (f.getFlowvalue() > max) max = f.getFlowvalue();
        }
        return max;
    }
    
    
}

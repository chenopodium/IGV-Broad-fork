/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iontorrent.data;

import java.util.ArrayList;

/**
 *  compute the slots/flows for a list of subread ionograms
 * @author Chantal Roth
 */
public class IonogramAlignment {
    
    private ArrayList<Ionogram> ionograms;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IonogramAlignment.class);
    
    private int nrslots;    
    private int nrionograms;
    private int maxemptyperlocation[];
    private int slotperlocation[];
    private int relativecenter;
    private int nrrelativelocations;
    private int chromosome_center_location;
    /** each row is a ionogram, each column is a slot, which may or may not map to a flow value */
    FlowValue[][] slotmatrix;
    
    public IonogramAlignment(ArrayList<Ionogram> ionograms, int maxemptyperlocation[] , int nrbases_left_right, int chromosome_center_location) {
        this.ionograms = ionograms;
        this.chromosome_center_location = chromosome_center_location;
        nrionograms = ionograms.size();
        this.maxemptyperlocation = maxemptyperlocation;        
        this.nrrelativelocations = nrbases_left_right*2+1;
        this.relativecenter = nrbases_left_right;
        nrslots = computeSlots();
        
        slotmatrix = new FlowValue[nrionograms][nrslots];
        computeAlignment();               
    }
    private int computeSlots() {
        int slots = 0;
        slotperlocation = new int[maxemptyperlocation.length];
        for (int relativeloc = 0; relativeloc < getNrrelativelocations(); relativeloc++) {
            // plus one as we also have to count the actual incorporation :-)
            slotperlocation[relativeloc] = slots;
            slots += maxemptyperlocation[relativeloc] + 1;
        }
        return slots;
    }
    public int getRelativeLocation(int chromosome_location) {
        return chromosome_location - getChromosome_center_location()+getRelativecenter();
    }
    public int getCenterSlot() {
        return slotperlocation[this.getRelativecenter()];
    }
    public char getAlignmentBase(int slot){
         for (int i = 0;i < this.nrionograms; i++) {
                FlowValue v = slotmatrix[i][slot];
                if (v != null) return v.getAlignmentBase();
         }
         return ' ';
    }
    @Override
    public String toString() {
        if (this.nrionograms <=0) return "got no alignment";
        Ionogram iono = ionograms.get(0);
        String nl = "\n";
        String res = "Ionogram alignent at "+iono.getLocusinfo()+nl+nl;
        // I know using String + is usually not recommended
        // but, this method does not have to be fast or efficient, so for readability
        // + is still much nicer than all those appends :-)
        for (int i = 0;i < this.nrionograms; i++) {
            for (int s = 0; s < this.nrslots; s++) {
                FlowValue v = slotmatrix[i][s];
                if (v == null) res += ",";
                else res = res + v.getFlowvalue();
            }
            res += nl;
        }
        res += nl;
        return res;
    }
    private void computeAlignment() {
        // compute nr of slots:
        // for each actual incorporation, get the maximum number of empties.
        // the sum of each incorporation plus empties is the nr of slots
        // before we can creat an array, we use an array lost for each incorporation event
        p("Computing alignment: got "+this.nrslots+" slots and "+this.nrionograms+" ionograms");
        for (int i = 0; i< getNrionograms(); i++) {
            Ionogram iono = ionograms.get(i);
            int nrempty = 0;
            for (FlowValue fv:  iono.getFlowvalues()) {
                int loc = fv.getChromosome_location();
                int relative = getRelativeLocation(loc);
                int startslot = slotperlocation[relative];
                
                if (fv.isEmpty())nrempty ++;
                else {
                    //incorporation, starting empty from scratch
                    nrempty = 0;
                }
                int slot = startslot + nrempty;
                slotmatrix[i][slot] = fv;                                
            }
            iono.setSlotrow(slotmatrix[i]);
            
        }
    }
 private void p(String msg) {
        log.info(msg);
    }

    private void err(String msg) {
        log.error(msg);
    }
    /**
     * @return the nrslots
     */
    public int getNrslots() {
        return nrslots;
    }

    /**
     * @return the nrionograms
     */
    public int getNrionograms() {
        return nrionograms;
    }

    /**
     * @return the relativecenter
     */
    public int getRelativecenter() {
        return relativecenter;
    }

    /**
     * @return the nrrelativelocations
     */
    public int getNrrelativelocations() {
        return nrrelativelocations;
    }

    /**
     * @return the chromosome_center_location
     */
    public int getChromosome_center_location() {
        return chromosome_center_location;
    }

    public ArrayList<Ionogram> getIonograms() {
        return this.ionograms;
    }

    public int getMaxValue() {
        int max = 0;
        for (Ionogram iono: ionograms) {
            int v = iono.getMaxValue();
            if (v > max) max = v;
        }
        return max;
    }
}

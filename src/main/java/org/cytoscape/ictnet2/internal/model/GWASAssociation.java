package org.cytoscape.ictnet2.internal.model;

public class GWASAssociation {
	private Integer gID;
	private Integer disID;
    private Integer confidence;   
    private Integer primary;
    private String pubmeds;
    
    public GWASAssociation(int disID, int gID, int confidence, int primary, String pubmeds){
    	this.gID = gID;
    	this.disID = disID;
        this.confidence = confidence;        
        this.primary = primary;
        this.pubmeds = pubmeds;
    }//
    public int getGeneID(){
    	return gID;
    }
    
    public int getDiseaseID(){
    	return disID;
    }
    public int getConfidence(){
    	return confidence;
    }
     
    public int getPrimary(){
    	return primary;
    }
    public String getPubmeds(){
    	return pubmeds;
    }

}

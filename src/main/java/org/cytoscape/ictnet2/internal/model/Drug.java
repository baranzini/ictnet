package org.cytoscape.ictnet2.internal.model;

public class Drug extends Molecule{ 
    private String mesh_id;
                
    public Drug(String mesh_id, String name){
    	this.ID = -1; //drugbankID
    	this.mesh_id = mesh_id;
    	this.name = name;
    }//
    
    public Drug(Integer drugbankID, String mesh_id, String name){
    	this.ID = drugbankID;
    	this.mesh_id = mesh_id;
    	this.name = name;
    }//  
     
    public String getMeshID(){
    	return mesh_id;
    }//
}

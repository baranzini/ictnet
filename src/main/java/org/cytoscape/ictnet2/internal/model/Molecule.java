package org.cytoscape.ictnet2.internal.model;

public abstract class Molecule {
	Integer ID;
	String name;
	
	public String getName(){
    	return name;
    }//
    
    public int getID(){
    	return ID;
    }//
    
    public String toString(){
    	return this.name;
    }//

}

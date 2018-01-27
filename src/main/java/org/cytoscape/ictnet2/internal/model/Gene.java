package org.cytoscape.ictnet2.internal.model;

public class Gene extends Molecule{	
	private String description;
	private String location;
	
	public Gene(int ID){
		this.ID = ID;
		this.description = "";
		this.name = "";
		this.location = "";
	}//
	
	public Gene(int ID, String symbol, String des, String location){
		this.ID = ID;
		this.name = symbol;
		this.description = des;
		this.location = location;
	}//
	
	public void setName(String symbol){
		this.name = symbol;
	}//
	
	public void setGeneDescription(String des){
		this.description = des;
	}//
	
	public String getGeneDescription(){
		return this.description;
	}//
	
	public void setGeneLocation(String location){
		this.location = location;
	}//
	
	public String getGeneLocation(){
		return this.location;
	}//

}

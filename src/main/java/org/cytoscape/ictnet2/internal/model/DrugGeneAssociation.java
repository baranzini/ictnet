package org.cytoscape.ictnet2.internal.model;

public class DrugGeneAssociation {
	private Integer gene_id;
	private String mesh_id;		
	private String pubmeds;
	private int pharmacological;
	private String actions;
	
	public DrugGeneAssociation(String mesh_id, Integer gene_id, String pubmeds){
		this.gene_id = gene_id;
		this.mesh_id = mesh_id;		
		this.pubmeds = pubmeds;	
		actions = "";
		pharmacological = 0;
	}
	
	public DrugGeneAssociation(String mesh_id, Integer gene_id, int pharmacological, String actions){
		this.gene_id = gene_id;
		this.mesh_id = mesh_id;
		this.pharmacological = pharmacological;
		this.actions = actions;
		pubmeds = "";		
	}
	
	public Integer getGeneID(){
		return gene_id;
	}
	
	public String getMeshID(){
		return mesh_id;
	}	
	
	public String getPubmeds(){
		return pubmeds;
	}
}

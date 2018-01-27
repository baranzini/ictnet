package org.cytoscape.ictnet2.internal.model;

public class TissueGeneAssociation {
	private String tissue_id; //bto_id CHAR(11)
	private Integer gene_id;
	private double log_expr;
	
	public TissueGeneAssociation(String tissue_id, Integer gene_id, double log_expr){
		this.tissue_id = tissue_id;
		this.gene_id = gene_id;
		this.log_expr = log_expr;		
	}//
	
	public String getTissueID(){
		return tissue_id;
	}//
	
	public Integer getGeneID(){
		return gene_id;
	}//
	
	public Double getValue(){
		return log_expr;
	}

}

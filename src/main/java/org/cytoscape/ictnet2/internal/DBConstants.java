package org.cytoscape.ictnet2.internal;
/*
 * This file contains all the constants used for database connection.
 * @author Lili Wang
 * 
 * */
public class DBConstants {
	public static int MAX_QUERY_LEN = 60000;
	//INPUT: query string; 
	//OUTPUT: doid_id and name; 
	public static String SP_DISEASE_ONTOLOGY = "sp_disease_ontology_query";
	
	//INPUT: query strings
	//OUTPUT: doid_ids and names; 
	public static String SP_DISEASE_ONTOLOGY_BATCH = "sp_disease_query_batch";
	
	//NO INPUT
	//download the whole disease ontology tree
	public static String SP_DISEASE_ONTOLOGY_ALL = "sp_disease_ontology_all_query";
	
	//INPUT: doid_ids; 
	//OUTPUT: gene ids; 
	public static String SP_DISEASE_GENE_OMIM = "sp_disease_gene_omim_query";
	
	//INPUT: doid_ids; 
	//OUTPUT: gene ids, pubmeds, pvalues and OR_or_betas.
	public static String SP_DISEASE_GENE_EFO = "sp_disease_gene_gwas_query";
	
	//INPUT: doid_ids; 
	//OUTPUT: gene ids; 
	public static String SP_DISEASE_GENE_MEDIC = "sp_disease_gene_medic_query";
		
	//INPUT: doid_ids;
	//OUTPUT: bto names;
	public static String SP_DISEASE_TISSUE = "sp_disease_tissue_query";
	
	//INPUT: doid_ids;
	//OUTPUT: ctd names;
	public static String SP_DISEASE_DRUG_CTD = "sp_disease_drug_ctd_query";
	
	//INPUT: mesh_ids; 
	//OUTPUT: side effect names;
	public static String SP_SIDE_EFFECT = "sp_side_effect_query";
	
	//INPUT: query string
	//OUTPUT: ids and names
	public static String SP_GENE_QUERY_BATCH = "sp_gene_query_batch";
	
	//INPUT: query strings
	//OUTPUT: id and name
	public static String SP_GENE_QUERY = "sp_gene_query";
		
	//INPUT: gene id list
	//OUTPUT: gene attributes
	public static String SP_GENE_ATTIBUTES = "sp_gene_attributes_query";
	
	//INPUT: gene id
	//OUTPUT: neighbor gene ids
	public static String SP_PPI = "sp_ppi_query";	
	
	//INPUT: gene id list
	//OUTPUT: neighbor gene ids
	// to find the new neighbors which are not in the input list
	public static String SP_PPI_NEW_NEIGHBOR_BATCH = "sp_ppi_new_neighbor_batch_query";
	
	//INPUT: gene id list
	//OUTPUT: neighbor gene ids
	// to find ppis connecting proteins in the input list
	public static String SP_PPI_EDGE_BATCH = "sp_ppi_edge_batch_query";
	
	//INPUT: tissue list and gene list
	//OUTPUT: tissue-gene edges
	public static String SP_TISSUE_GENE_EDGE = "sp_tissue_gene_edge_query";
	
	//INPUT: ctd drug list and gene list
	//OUTPUT: ctd drug-gene edges
	public static String SP_DRUG_GENE_CTD_EDGE = "sp_drug_gene_ctd_edge_query";
	
	//INPUT: drugbank drug list and gene list
	//OUTPUT: drugbank drug-gene edges
	public static String SP_DRUG_GENE_DRUGBANK_EDGE = "sp_drug_gene_drugbank_edge_query";
	
	//INPUT: gene ids
	//OUTPUT: neighbor gene ids
	public static String SP_MIRNA = "sp_mirna_query";
	
	//INPUT: gene ids
	//OUTPUT: gene id, tissue id, name and log_expression
	public static String SP_GENE_TISSUE = "sp_gene_tissue_query";
			
	//INPUT: gene ids
	//OUTPUT: disease ids and names
	public static String SP_GENE_DISEASE_EFO = "sp_gene_disease_gwas_query";
	public static String SP_GENE_DISEASE_OMIM = "sp_gene_disease_omim_query";
	public static String SP_GENE_DISEASE_MEDIC = "sp_gene_disease_medic_query";
	
	//INPUT: gene ids
	//OUTPUT: drug ids and names
	public static String SP_GENE_DRUG_CTD = "sp_gene_drug_ctd_query";
	public static String SP_GENE_DRUG_DRUGBANK = "sp_gene_drug_drugbank_query";
		
	//INPUT: tissue ids and side effect ids
	//OUTPUT: tissue-side effect edges
	public static String SP_TISSUE_SIDE_EFFECT_EDGE = "sp_tissue_side_effect_edge_query";
	
	//INPUT: disease ids and tissue ids
	//OUTPUT: disease-tissue edges
	public static String SP_DISEASE_TISSUE_EDGE = "sp_disease_tissue_edge_query";
	
	//INPUT: disease ids and drug ids (only CTD)
	//OUTPUT: disease-drug edges
	public static String SP_DISEASE_DRUG_CTD_EDGE = "sp_disease_drug_ctd_edge_query"; 
	
	//INPUT: query string; 
	//OUTPUT: mesh_id and name; 
	public static String SP_DRUG_QUERY = "sp_drug_query";
	
	//INPUT: query strings; 
	//OUTPUT: mesh_ids and names; 
	public static String SP_DRUG_QUERY_BATCH = "sp_drug_query_batch";
	
	//INPUT: drug ids
	//OUTPUT: gene ids
	public static String SP_DRUG_GENE_CTD = "sp_drug_gene_ctd_query";
	public static String SP_DRUG_GENE_DRUGBANK = "sp_drug_gene_drugbank_query";
	
	//INPUT: drug ids
	//OUTPUT: disease ids
	public static String SP_DRUG_DISEASE_CTD = "sp_drug_disease_ctd_query";
	
	//INPUT: gene ids and disease ids
	//OUTPUT: disease-gene edges
	public static String SP_GENE_DISEASE_EFO_EDGE = "sp_gene_disease_gwas_edge_query";
	public static String SP_GENE_DISEASE_OMIM_EDGE = "sp_gene_disease_omim_edge_query";
	public static String SP_GENE_DISEASE_MEDIC_EDGE = "sp_gene_disease_medic_edge_query";
	
	public static String sqlQueryStringCheck(String sql){
		String _tmp = "";
		for(int i = 0; i<sql.length(); i++){
			if(sql.charAt(i) == '\''){
				_tmp += "\\";
			}else{
				_tmp += Character.toString(sql.charAt(i));
			}//if-else				
		}//for
		return _tmp;
	}//sqlQueryStringCheck	

}

package org.cytoscape.ictnet2.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

public class CyAttributeConstants {
	public static int DISEASE_ROOT_CODE = 4;
	public static int DISEASE_CODE = 0;
	public static int GENE_CODE = 1;
	public static int DRUG_CODE = 2;
	public static String DISEASE_ROOT_NAME = "Disease";
	public static String DEF_VS_NAME = "iCTNet Style";
	public static String DEF_LAYOUT_NAME = "attributes-layout";
	
	//node types
	public static String NODE_GENE = "gene";
	public static String NODE_DISEASE = "disease";
	public static String NODE_TISSUE = "tissue";
	public static String NODE_DRUG = "drug";
	public static String NODE_SIDE_EFFECT = "side_effect";
	public static String NODE_MIRNA = "miRNA";
	//edge types	
	public static String EDGE_DIS_GEN = "disease-gene";
	public static String EDGE_DIS_TIS = "disease-tissue";
	public static String EDGE_DIS_DRG = "disease-drug";
	public static String EDGE_DRG_GEN = "drug-gene";
	public static String EDGE_DRG_SIDE = "drug-side_effect";
	public static String EDGE_PPI = "ppi";
	public static String EDGE_GEN_TIS = "gene-tissue";
	//data source
	public static String DATA_CTD = "CTD";
	public static String DATA_MEDIC = "Medic";
	public static String DATA_GWAS = "GWAS";
	public static String DATA_GWAS_OMIM = "GWAS, OMIM";
	public static String DATA_GWAS_MEDIC = "GWAS, Medic";
	public static String DATA_OMIM_MEDIC = "OMIM, Medic";
	public static String DATA_GWAS_OMIM_MEDIC = "GWAS, OMIM, Medic";
	public static String DATA_OMIM = "OMIM";
	public static String DATA_DRUGBANK = "Drugbank";
	public static String DATA_CTD_DRUGBANK = "CTD, Drugbank";
	public static String DATA_SIDE = "Side_effect";
	public static String DATA_TISSUE ="Tissue_ontology";
	public static String DATA_ATLAS = "Gene_Expression_Atlas";
	//node attributes
	public static String ATTR_ID = "ID";
	public static String ATTR_EXTRA_ID = "extraID";
	public static String ATTR_TYPE = "Type";
	public static String ATTR_DESCRIP = "Description";
	public static String ATTR_LOC = "Location";
	public static String ATTR_CONFI = "Confidence";
	public static String ATTR_PRIMARY = "Primary";
	public static String ATTR_PUBMED = "PubMed";
	public static String ATTR_DATA_SOURCE = "Data_source";
	public static String ATTR_DIS_LEVEL = "Disease_Ontology_Level";
	public static String ATTR_DIS_LEVEL1 = "Disease_Ontology_Level1";
	public static String ATTR_DIS_LEVEL2 = "Disease_Ontology_Level2";
	public static String ATTR_DIS_LEVEL3 = "Disease_Ontology_Level3";
	public static String ATTR_DIS_LEVEL4 = "Disease_Ontology_Level4";
	public static String ATTR_DIS_LEVEL5 = "Disease_Ontology_Level5";
	public static String ATTR_DIS_LEVEL6 = "Disease_Ontology_Level6";
	public static String ATTR_DIS_LEVEL7 = "Disease_Ontology_Level7";
	public static String ATTR_DIS_LEVEL8 = "Disease_Ontology_Level8";
	
	public static CyNode getNodeWithID(CyNetwork cyNet, CyTable cyTable, String column, String ID){
		final Collection<CyRow> matchingRows = cyTable.getMatchingRows(column, ID);
		final Set<CyNode> nodes = new HashSet<CyNode>();
		final String primaryKeyColname = cyTable.getPrimaryKey().getName();
		for (final CyRow row : matchingRows){
		    final Long nodeId = row.get(primaryKeyColname, Long.class);		    
		    if (nodeId == null)
		        continue;
		    final CyNode node = cyNet.getNode(nodeId);
		    if (node == null)
		        continue;  
		    nodes.add(node);		    
		}//	
		
		if (nodes.size()>1){
			System.out.println(column + ID + " has duplicates.");
		}else if (nodes.size() == 0){
			return null;
		}//if-else		
		return nodes.iterator().next();		
	}//
}

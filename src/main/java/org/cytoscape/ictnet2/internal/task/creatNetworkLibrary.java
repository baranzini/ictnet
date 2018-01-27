package org.cytoscape.ictnet2.internal.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.DBConnector;
import org.cytoscape.ictnet2.internal.DBQueryLibrary;
import org.cytoscape.ictnet2.internal.model.DrugGeneAssociation;
import org.cytoscape.ictnet2.internal.model.Gene;
import org.cytoscape.ictnet2.internal.model.PPI;
import org.cytoscape.ictnet2.internal.model.TissueGeneAssociation;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

public class creatNetworkLibrary {
	
	public static boolean flag = false;
	
	public static void setStopFlag(boolean f){
		flag = f;
	}
	
	public static void nameEdgesInsideNetwork(CyNetwork ictNet){
		List<CyEdge> allEdges = ictNet.getEdgeList();
    	for(CyEdge edge: allEdges){
    		CyNode source = edge.getSource();
    		CyNode target = edge.getTarget();
    		String sName = ictNet.getRow(source).get(CyNetwork.NAME, String.class);
    		String tName = ictNet.getRow(target).get(CyNetwork.NAME, String.class);
    		ictNet.getRow(edge).set(CyNetwork.NAME, sName+"-"+tName);
    	}//for
	}//
	
	public static void addGeneAttributes(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, HashMap<Integer, Gene> geneMap){
		DBQueryLibrary.dbQueryGeneAttrs(dbconnector.getConnection(), geneMap);
    	for(Integer gID: geneMap.keySet()){
    		CyNode geneNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID));
    		if (geneNode == null){   			
    			continue;    			
    		}//if
    		Gene gene = geneMap.get(gID);
    		ictNet.getRow(geneNode).set(CyNetwork.NAME, gene.getName());
    		ictNet.getRow(geneNode).set(CyAttributeConstants.ATTR_DESCRIP, gene.getGeneDescription());
    		ictNet.getRow(geneNode).set(CyAttributeConstants.ATTR_LOC, gene.getGeneLocation());    		
    	}//for		
	}//
	
	public static void addSideEffect(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, Set<String> meshIDs, 
			String sideEffectStr, double sideEffectFreq, boolean uncat, Set<String> sideEffectIDs){		
		HashMap<String, HashMap<String, String>> sideEffectMap = 
				DBQueryLibrary.dbQuerySideEffect(dbconnector.getConnection(),meshIDs, sideEffectStr, sideEffectFreq, uncat);		
		for(String meshID: sideEffectMap.keySet()){
			CyNode drugNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, meshID); 
			if (drugNode == null){
				continue;
			}//if
			HashMap<String, String> sideEffects = sideEffectMap.get(meshID);
			CyNode sfNode = null;
			for(String sfID: sideEffects.keySet()){
				if (!sideEffectIDs.contains(sfID)){
					sideEffectIDs.add(sfID);
					sfNode = ictNet.addNode();
					ictNet.getRow(sfNode).set(CyAttributeConstants.ATTR_ID, sfID);
					ictNet.getRow(sfNode).set(CyNetwork.NAME, sideEffects.get(sfID));
					ictNet.getRow(sfNode).set(CyAttributeConstants.ATTR_TYPE, "side_effect");
				}else{
					sfNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, sfID); 
	    			if (sfNode == null){
	    				continue;
	    			}//if
				}//if-else				
				CyEdge dsEdge = ictNet.addEdge(drugNode, sfNode, false);
				ictNet.getRow(dsEdge).set(CyAttributeConstants.ATTR_TYPE, "drug-side_effect");
				ictNet.getRow(dsEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "CTD");
			}//for
		}//for		
	}//
	
	public static void addSideEffectTissueEdges(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, Set<String> sideEffectIDs, Set<String> tissueIDs){
		HashMap<String, Set<String>> sideEffectTissueEdges = 
    			DBQueryLibrary.dbQuerySideEffectTissueEdge(dbconnector.getConnection(), sideEffectIDs,  tissueIDs);
    	for(String sID: sideEffectTissueEdges.keySet()){
    		CyNode sNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, sID); 
			if (sNode == null){
				continue;
			}//if
    		for(String tID: sideEffectTissueEdges.get(sID)){
    			CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, tID); 
    			if (tNode == null){
    				continue;
    			}//if
    			CyEdge newEdge = ictNet.addEdge(sNode, tNode, false);
				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, "side effect-tissue");
				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "Side effect");
    		}//for
    	}//for
	}
	
	public static void addMiRNA(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, HashMap<Integer, Gene> geneMap){
		Set<PPI> miRNA_proteins = DBQueryLibrary.dbQueryMiRNA(dbconnector.getConnection(), geneMap.keySet());
		for(PPI mp: miRNA_proteins){
			Integer sID = mp.getSource();
			Integer tID = mp.getTarget();
			CyNode sNode = null;
			if (!geneMap.containsKey(sID)){
				geneMap.put(sID, new Gene(sID));//add mirna id in order to load attributes    				
    			sNode = ictNet.addNode();
    			ictNet.getRow(sNode).set(CyAttributeConstants.ATTR_ID, Integer.toString(sID));
    			ictNet.getRow(sNode).set(CyAttributeConstants.ATTR_TYPE, "miRNA");
			}else{
				sNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(sID));    				
			}//if-else    			
			
			if (sNode == null){
				continue;
			}//if
			CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(tID));
			if (tNode == null){
				continue;
			}//if
			CyEdge mgEdge = ictNet.addEdge(sNode, tNode, false);
			ictNet.getRow(mgEdge).set(CyAttributeConstants.ATTR_TYPE, "miRNA-gene");
			ictNet.getRow(mgEdge).set(CyAttributeConstants.ATTR_PUBMED, mp.getPubmeds());
		}//for
	}//
	
	
	public static void addPPI(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, 
			HashMap<Integer, Gene> geneMap, int ppiDepthIndex){		
		//add PPI edges connecting current proteins    	
		Set<PPI> curPPIs = DBQueryLibrary.dbQueryPPIEdgeBatch(dbconnector.getConnection(), geneMap.keySet());
		for(PPI newEdge: curPPIs){
			if (flag)
		    	return;
			Integer sID = newEdge.getSource();
			Integer tID = newEdge.getTarget();
			CyNode sNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(sID));
			if (sNode == null){
				continue;
			}//if
			CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(tID));
			if (tNode == null){
				continue;
			}//if        				
			CyEdge ppiEdge = ictNet.addEdge(sNode, tNode, false); 
			ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_TYPE, "ppi");
		}//for
		
		//add new neighbors and edges
		if (ppiDepthIndex > 0){
		    if (flag)
		    	return;
			boolean ppiLoop = true;
			int ppiDepth = ppiDepthIndex;
			if (ppiDepth == 3)
				ppiDepth = 6;
			while(ppiLoop){
				if (flag)
			    	return;
				System.out.println("ppi loop: " + ppiDepth);
				    				
				Set<PPI> newPPIs = DBQueryLibrary.dbQueryPPINewNeighborBatch(dbconnector.getConnection(),geneMap.keySet());
				HashSet<Integer> newGeneIDs = new HashSet<Integer>();
				// add new neighbors
    			for(PPI newPPI: newPPIs){
    				if (flag)
    			    	return;
    				Integer sID = newPPI.getSource();
    				Integer tID = newPPI.getTarget();
    				if (geneMap.containsKey(sID)){
    					if (geneMap.containsKey(tID))
        					continue;
    					CyNode currentGeneNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(sID));
        				if (currentGeneNode == null){
        					continue;
        				}//if             				
        				CyNode tNode = null;            				
    					if (!newGeneIDs.contains(tID)){
    						newGeneIDs.add(tID);
    						tNode = ictNet.addNode();
    	        			ictNet.getRow(tNode).set(CyAttributeConstants.ATTR_ID, Integer.toString(tID));
    	        			ictNet.getRow(tNode).set(CyAttributeConstants.ATTR_TYPE, "gene");        	        			
    					}else{
    						tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(tID));                				
    					}//if-else
    					if (tNode == null){
        					continue;
        				}//if 
        				CyEdge ppiEdge = ictNet.addEdge(currentGeneNode, tNode, false);
        				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_TYPE, "ppi");
        				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_PUBMED, newPPI.getPubmeds());
    				}else{
    					CyNode currentGeneNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(tID));
        				if (currentGeneNode == null){
        					continue;
        				}//if 
        				CyNode sNode = null;
    					if (!newGeneIDs.contains(sID)){
    						newGeneIDs.add(sID);
    						sNode = ictNet.addNode();
    	        			ictNet.getRow(sNode).set(CyAttributeConstants.ATTR_ID, Integer.toString(sID));
    	        			ictNet.getRow(sNode).set(CyAttributeConstants.ATTR_TYPE, "gene");        	        			
    					}else{
    						sNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(sID));                				
    					}//if-else
    					
    					if (sNode == null){
        					continue;
        				}//if
        				CyEdge ppiEdge = ictNet.addEdge(sNode, currentGeneNode, false);
        				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_TYPE, "ppi");
        				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_PUBMED,newPPI.getPubmeds());
    				}//if-else       				
    			}//for
    			
    			//add edges connecting newly added proteins        			
				Set<PPI> newEdges = DBQueryLibrary.dbQueryPPIEdgeBatch(dbconnector.getConnection(),newGeneIDs);
				for(PPI newEdge: newEdges){
					if (flag)
				    	return;
					Integer sID = newEdge.getSource();
    				Integer tID = newEdge.getTarget();
    				CyNode sNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(sID));
    				if (sNode == null){
    					continue;
    				}//if
    				CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(tID));
    				if (tNode == null){
    					continue;
    				}//if        				
    				CyEdge ppiEdge = ictNet.addEdge(sNode, tNode, false);  
    				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_TYPE, "ppi");
    				ictNet.getRow(ppiEdge).set(CyAttributeConstants.ATTR_PUBMED,newEdge.getPubmeds());
				}//for
				
    			if (newGeneIDs.size() > 0){
    				for(int newID: newGeneIDs){
    					geneMap.put(newID, new Gene(newID));
    				}//for        				
    			}else{
    				ppiLoop = false;
    			}//if-else
    			
    			ppiDepth --;
    			if (ppiDepth == 0)
    				ppiLoop = false;        			
			}//while    			
		}//if
	}//
	
	public static void addGeneTissueEdges(CyNetwork ictNet, CyTable nodeTable, DBConnector dbconnector, 
			Set<String> tissueIDs, Set<Integer> geneIDs){		 
		Set<TissueGeneAssociation> tisGeneAssociations = DBQueryLibrary.dbQueryTissueGeneAssociation(dbconnector.getConnection(),tissueIDs, geneIDs);
		System.out.println("Tissue-Gene edges: " + tisGeneAssociations.size());
		for(TissueGeneAssociation tgEdge: tisGeneAssociations){
			Integer gID = tgEdge.getGeneID();
			String tID = tgEdge.getTissueID();
			CyNode gNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID));
			if (gNode == null){
				continue;
			}//if
			CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, tID);
			if (tNode == null){
				continue;
			}//if        				
			CyEdge newEdge = ictNet.addEdge(gNode, tNode, false);
			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, "gene-tissue");
			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "Gene_Expression_Atlas");
		}//for
	}//
	public static void addGeneDrugEdges(CyNetwork ictNet, CyTable nodeTable, 
			HashMap<Integer, HashMap<String, DrugGeneAssociation>> DrugGeneCTDMap, 
			HashMap<Integer, HashMap<String, DrugGeneAssociation>> DrugGeneDrugBankMap){		
    	HashSet<Integer> drugGeneIDs = new HashSet<Integer>();
    	drugGeneIDs.addAll(DrugGeneCTDMap.keySet());
    	drugGeneIDs.addAll(DrugGeneDrugBankMap.keySet());
    	for(Integer gID: drugGeneIDs){
    		CyNode gNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID));
			if (gNode == null){
				continue;
			}//if
    		HashMap<String, DrugGeneAssociation> CTDEdges = new HashMap<String, DrugGeneAssociation>();
    		HashMap<String, DrugGeneAssociation> drugbankEdges = new HashMap<String, DrugGeneAssociation>();
    		Set<String> drugIDs = new HashSet<String>();
    		if (DrugGeneCTDMap.containsKey(gID)){
    			CTDEdges = DrugGeneCTDMap.get(gID);
    			drugIDs.addAll(CTDEdges.keySet());
    		}//
    		if(DrugGeneDrugBankMap.containsKey(gID)){
    			drugbankEdges = DrugGeneDrugBankMap.get(gID);
    			drugIDs.addAll(drugbankEdges.keySet());
    		}//if
    		
    		
    		for(String drugID: drugIDs){    			
				CyNode drugNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, drugID);
				if (drugNode == null){
					continue;
				}//if
				CyEdge newEdge = ictNet.addEdge(gNode, drugNode, false);
				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DRG_GEN);
				if (CTDEdges.containsKey(drugID)){
				    ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_PUBMED, CTDEdges.get(drugID).getPubmeds());
				    if (drugbankEdges.containsKey(drugID)){
				    	ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "DrugBank, CTD");
				    	continue;
				    }else{
				        ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "CTD");
				        continue;
				    }//if-else
				}//if
				if (drugbankEdges.containsKey(drugID)){
					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "DrugBank");
				}//if				
    		}//for
    	}//for
	}//

}

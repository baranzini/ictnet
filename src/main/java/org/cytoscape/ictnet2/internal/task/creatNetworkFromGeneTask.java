package org.cytoscape.ictnet2.internal.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.DBConnector;
import org.cytoscape.ictnet2.internal.DBQueryLibrary;
import org.cytoscape.ictnet2.internal.ServicesUtil;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Drug;
import org.cytoscape.ictnet2.internal.model.DrugGeneAssociation;
import org.cytoscape.ictnet2.internal.model.GWASAssociation;
import org.cytoscape.ictnet2.internal.model.Gene;
import org.cytoscape.ictnet2.internal.model.TissueGeneAssociation;
import org.cytoscape.ictnet2.internal.model.ictnetVisualStyle;
import org.cytoscape.ictnet2.internal.ui.TreeViewPanel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class creatNetworkFromGeneTask extends AbstractTask{
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkManager networkManager;
	private final CyNetworkViewManager networkViewManager;
	private final VisualMappingManager vmm;
	private final DBConnector dbconnector;
	private final Set<Integer> geneIDs;
	private final boolean omimFlag;
	private final boolean efoFlag;
	private final boolean medicFlag;
	private final boolean ppiFlag;
	private final boolean ctdFlag; //disease-drug
	private final boolean drugbankFlag; //disease-drug
	private final boolean ctdGeneFlag;
	private final boolean drugbankGeneFlag;
	private final boolean tissueFlag; //disease-tissue
	private final boolean tissueGeneFlag;
	private final boolean tissueSideEffectFlag;
	private final boolean miRNAFlag;
	private final boolean sideEffectFlag;
	private final boolean uncategoriedSideEffect;
	private final String sideEffectStr;
	private final double sideEffectFreq;
	private final int ppiDepthIndex; // "=0", "=1", "=2", ">2"	
	private final int gwasIndex;
	private final TreeViewPanel treePanel;
	private Thread currentThread;
	
	public creatNetworkFromGeneTask(final CyNetworkFactory networkFactory, final CyNetworkViewFactory networkViewFactory, 
			final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
			final VisualMappingManager vmm, DBConnector dbconnector, 
			Set<Integer> geneIDs, TreeViewPanel treePanel,  
			boolean omimFlag, boolean efoFlag, boolean medicFlag, boolean ppiFlag, 
			boolean ctdFlag, boolean drugbankFlag, boolean ctdGeneFlag, boolean drugbankGeneFlag,
			boolean tissueFlag, boolean tissueGeneFlag, boolean miRNAFlag, String sideEffectStr, double sideEffectFreq,boolean uncategoriedSideEffect,
			int ppiDepthIndex, int gwasIndex){
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.vmm = vmm;
		this.dbconnector = dbconnector;
		this.geneIDs = geneIDs;
		this.treePanel = treePanel;
		this.omimFlag = omimFlag;
		this.efoFlag = efoFlag;
		this.medicFlag = medicFlag;
		this.ppiFlag = ppiFlag;
		this.ctdFlag = ctdFlag;
		this.drugbankFlag = drugbankFlag;
		this.ctdGeneFlag = ctdGeneFlag;
		this.drugbankGeneFlag = drugbankGeneFlag;
		this.tissueFlag = tissueFlag;
		this.tissueGeneFlag = tissueGeneFlag;
		this.sideEffectFlag = (sideEffectStr.length()>0 || sideEffectFreq > -1.0 || uncategoriedSideEffect);
		this.uncategoriedSideEffect = uncategoriedSideEffect;
		this.tissueSideEffectFlag = tissueFlag && sideEffectFlag;
		this.sideEffectStr = sideEffectStr;
		this.sideEffectFreq = sideEffectFreq;
		this.miRNAFlag = miRNAFlag;		
		this.ppiDepthIndex = ppiDepthIndex;
		this.gwasIndex = gwasIndex;
	}
	
	public void cancel(){
    	cancelled = true;   
    	creatNetworkLibrary.setStopFlag(cancelled);
    	System.out.println("Cancelling task...");
    }//
		    
    public String getTitle(){
		return "Creating a network";
    }//
    
    public void halt(){
    	currentThread.interrupt(); 
    }//

	public void run(TaskMonitor taskMonitor) throws Exception{
		currentThread = Thread.currentThread();
		taskMonitor.setTitle("Creating a Network");
    	taskMonitor.setProgress(0.0d);
    	taskMonitor.setStatusMessage("Searching data on the server..."); 
    	// Create a new network
    	CyNetwork ictNet = networkFactory.createNetwork();
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	ictNet.getRow(ictNet).set(CyNetwork.NAME, "iCTNet_gene_"+dateFormat.format(new Date()));    	
    	
    	CyTable nodeTable = ictNet.getDefaultNodeTable(); 
    	//node attributes
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_ID) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_ID, String.class, false);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_EXTRA_ID) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_EXTRA_ID, String.class, false); 
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_TYPE) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_TYPE, String.class, false);    	
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_DESCRIP) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DESCRIP, String.class, false);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_LOC) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_LOC, String.class, false);
    	
    	//disease ontology level
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL1) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL1, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL2) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL2, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL3) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL3, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL4) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL4, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL5) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL5, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL6) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL6, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL7) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL7, String.class, false);
    	if (nodeTable.getColumn(CyAttributeConstants.ATTR_DIS_LEVEL8) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DIS_LEVEL8, String.class, false);
    	
    	//edge attributes
    	CyTable edgeTable = ictNet.getDefaultEdgeTable();
    	if(edgeTable.getColumn(CyAttributeConstants.ATTR_CONFI) == null)
    		edgeTable.createColumn(CyAttributeConstants.ATTR_CONFI, String.class, false);
    	if(edgeTable.getColumn(CyAttributeConstants.ATTR_TYPE) == null)
    		edgeTable.createColumn(CyAttributeConstants.ATTR_TYPE, String.class, false); 
    	if(edgeTable.getColumn(CyAttributeConstants.ATTR_PRIMARY) == null)
    		edgeTable.createColumn(CyAttributeConstants.ATTR_PRIMARY, String.class, false);  
    	if(edgeTable.getColumn(CyAttributeConstants.ATTR_PUBMED) == null)
    		edgeTable.createColumn(CyAttributeConstants.ATTR_PUBMED, String.class, false); 
    	if(edgeTable.getColumn(CyAttributeConstants.ATTR_DATA_SOURCE) == null)
    		edgeTable.createColumn(CyAttributeConstants.ATTR_DATA_SOURCE, String.class, false);
    	System.out.println("----------iCTNet App: creating a new network---------");  
    	
    	HashMap<Integer, Gene> geneMap = new HashMap<Integer, Gene>();
    	HashSet<Integer> diseaseIDs = new HashSet<Integer>();
    	Set<String> tissueIDs = new HashSet<String>();
    	Set<String> meshIDs = new HashSet<String>();
    	Set<Integer> drugbankIDs = new HashSet<Integer>();
    	Set<String> sideEffectIDs = new HashSet<String>();
    	   	
    	taskMonitor.setProgress(0.1d);
    	//
    	for(Integer gID: geneIDs) {
			CyNode gNode = null;
			if (!geneMap.containsKey(gID)){
				geneMap.put(gID, new Gene(gID));
				gNode = ictNet.addNode();
    			ictNet.getRow(gNode).set(CyAttributeConstants.ATTR_ID, Integer.toString(gID));
    			ictNet.getRow(gNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_GENE); 
			}//if
		}//for
    	if (cancelled)
			return;
    	//PPI
    	if(ppiFlag){
    		 taskMonitor.setStatusMessage("Searching protein-protein interactions...");   		 
    		 creatNetworkLibrary.addPPI(ictNet, nodeTable, dbconnector, geneMap, ppiDepthIndex);
    	}//if PPI    	
    	System.out.println("Gene nodes: " + geneMap.size());
    	taskMonitor.setProgress(0.25d);
    	if (cancelled)
			return;
    	//miRNA - gene
    	if(miRNAFlag){
    		taskMonitor.setStatusMessage("Searching miRNA-gene associations..."); 
    		creatNetworkLibrary.addMiRNA(ictNet, nodeTable, dbconnector, geneMap);
    	}//if miRNA-gene
    	taskMonitor.setProgress(0.3d);
    	if (cancelled)
			return;
    	//gene-tissue
    	if(tissueGeneFlag){
    		taskMonitor.setStatusMessage("Searching tissue-gene associations...");
    		HashMap<String, String> tissues = new HashMap<String, String>(); 
    		Set<TissueGeneAssociation> tisGeneAssociations = DBQueryLibrary.dbQueryGeneTissue(dbconnector.getConnection(), geneMap.keySet(), tissues);
    		
    		System.out.println("Tissue-Gene edges: " + tisGeneAssociations.size());
    		for(TissueGeneAssociation tgEdge: tisGeneAssociations){
				Integer gID = tgEdge.getGeneID();
				String tID = tgEdge.getTissueID();
				CyNode tNode = null;
				if (!tissueIDs.contains(tID)){
					tissueIDs.add(tID);
					tNode = ictNet.addNode();
	    			ictNet.getRow(tNode).set(CyAttributeConstants.ATTR_ID, tID);
	    			ictNet.getRow(tNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_TISSUE);	    			
	    			ictNet.getRow(tNode).set(CyNetwork.NAME, tissues.get(tID));
				}else{
					tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, tID);
					if (tNode == null){
						continue;
					}//if
				}//if-else
				
				CyNode gNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID));
				if (gNode == null){
					continue;
				}//if
				        				
				CyEdge newEdge = ictNet.addEdge(gNode, tNode, false);
				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_GEN_TIS);
				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_ATLAS);
			}//for
    	}//if tissue-gene  
    	taskMonitor.setProgress(0.4d); 
    	//gene-drug
    	if (cancelled)
			return;
    	if (ctdGeneFlag){
    		HashMap<String, Drug> drugMap = DBQueryLibrary.dbQueryGeneDrugCTD(dbconnector.getConnection(), geneMap.keySet());    		
    		for(String meshID: drugMap.keySet()){    			
    			if (!meshIDs.contains(meshID)){
					meshIDs.add(meshID);
					Drug drug = drugMap.get(meshID);
					CyNode drugNode = ictNet.addNode();
        			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_ID, meshID);
        			ictNet.getRow(drugNode).set(CyNetwork.NAME, drug.getName());
        			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_DRUG);
    			}//if
    		}//for
    	}//if
    	if (cancelled)
			return;
    	if(drugbankGeneFlag){
    		HashMap<String, Drug> drugMap = DBQueryLibrary.dbQueryGeneDrugDrugbank(dbconnector.getConnection(), geneMap.keySet());    		
    		for(String meshID: drugMap.keySet()){    			
    			if (!meshIDs.contains(meshID)){
					meshIDs.add(meshID);
					Drug drug = drugMap.get(meshID);
					CyNode drugNode = ictNet.addNode();
        			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_ID, meshID);
        			ictNet.getRow(drugNode).set(CyNetwork.NAME, drug.getName());
        			int drugbankID = drug.getID();
        			drugbankIDs.add(drugbankID);
        			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_EXTRA_ID, Integer.toString(drugbankID));        			
        			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_DRUG);        			
    			}//if
    		}//for	
    	}//if   	
    	if (cancelled)
			return;
    	//drug - gene - CTD
    	HashMap<Integer, HashMap<String, DrugGeneAssociation>> DrugGeneCTDMap = new HashMap<Integer, HashMap<String, DrugGeneAssociation>> ();
    	//drug - gene - drugbank
    	HashMap<Integer, HashMap<String, DrugGeneAssociation>> DrugGeneDrugBankMap = new HashMap<Integer, HashMap<String, DrugGeneAssociation>> ();
    	if(ctdGeneFlag){
    		taskMonitor.setStatusMessage("Searching drug-gene associations..."); 
    		DrugGeneCTDMap = DBQueryLibrary.dbQueryDrugGeneEdgeCTD(dbconnector.getConnection(),meshIDs, geneMap.keySet());    		
    	}//if
    	if (cancelled)
			return;
    	if(drugbankGeneFlag){
    		DrugGeneDrugBankMap = DBQueryLibrary.dbQueryDrugGeneEdgeDrugBank(dbconnector.getConnection(), drugbankIDs, geneMap.keySet());
    	}//if 
    	if (cancelled)
			return;
    	//add drug-gene edges
    	creatNetworkLibrary.addGeneDrugEdges(ictNet, nodeTable, DrugGeneCTDMap, DrugGeneDrugBankMap);	
    	
    	//gene-disease
    	HashMap<Integer, Set<GWASAssociation>> gwasMap = new HashMap<Integer, Set<GWASAssociation>>();    	
    	if (this.efoFlag){   //GWAS
    		taskMonitor.setStatusMessage("Searching gwas disease-gene associations...");
    		System.out.println("Searching gwas disease-gene associations...");
			gwasMap = DBQueryLibrary.dbQueryGeneDiseaseGWAS(dbconnector.getConnection(), geneMap.keySet(), gwasIndex);
			System.out.println("gwasMap : " + gwasMap);
			diseaseIDs.addAll(gwasMap.keySet());
    	}//if
    	if (cancelled)
			return;
    	HashMap<Integer, Set<Integer>> omimMap = new HashMap<Integer, Set<Integer>>();    	
    	if(this.omimFlag){
    		taskMonitor.setStatusMessage("Searching OMIM disease-gene associations..."); 
    		System.out.println("Searching OMIM disease-gene associations...");
    		omimMap = DBQueryLibrary.dbQueryGeneDiseaseOMIM(dbconnector.getConnection(), geneMap.keySet());
    		diseaseIDs.addAll(omimMap.keySet());
    	}//if
    	if (cancelled)
			return;
    	HashMap<Integer, Set<Integer>> medicMap = new HashMap<Integer, Set<Integer>>();     	
    	if(this.medicFlag){
    		taskMonitor.setStatusMessage("Searching MEDIC disease-gene associations..."); 
    		System.out.println("Searching MEDIC disease-gene associations...");
    		medicMap = DBQueryLibrary.dbQueryGeneDiseaseMedic(dbconnector.getConnection(), geneMap.keySet());
    		diseaseIDs.addAll(medicMap.keySet());
    	}//if
    	if (cancelled)
			return;
    	//disease-tissue edges
    	HashMap<Integer, Set<String>> diseaseTissueEdges = null;
    	if (tissueFlag){
    		diseaseTissueEdges = DBQueryLibrary.dbQueryDiseaseTissueEdge(dbconnector.getConnection(), diseaseIDs, tissueIDs);        	
    	}//if   	
    	
    	HashMap<Integer, Set<String>> diseaseDrugEdges = null;
    	//disease-drug edges
    	if (ctdFlag){
    		taskMonitor.setStatusMessage("Searching disease-drug associations...");
    		System.out.println("Searching disease-drug associations...");
    		diseaseDrugEdges = DBQueryLibrary.dbQueryDiseaseDrugEdgeCTD(dbconnector.getConnection(), diseaseIDs, meshIDs);
    	}//if
    	if (cancelled)
			return;
    	//add disease names
    	treePanel.checkTreeManager.findMatchedTreeNodes(diseaseIDs);
    	HashMap<Integer, Disease> treeNodeMap = treePanel.checkTreeManager.getSelectedTreeNodes();
    	for(int disID: diseaseIDs){
    		CyNode disNode = ictNet.addNode();  
    		Disease disease = treeNodeMap.get(disID);
    		ictNet.getRow(disNode).set(CyAttributeConstants.ATTR_ID, "DIS:"+disID);
    		ictNet.getRow(disNode).set(CyNetwork.NAME, disease.getName());
    		ictNet.getRow(disNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_DISEASE);
    		String[] ontologyLevels = disease.getDiseaseOntology();
    		for(int i= 8; i>0; i--){
    			String attribute = CyAttributeConstants.ATTR_DIS_LEVEL + i;
    			ictNet.getRow(disNode).set(attribute, ontologyLevels[i-1]);    			
    		}//for
    		
    		Set<GWASAssociation> gwasEdges = new HashSet<GWASAssociation>();
    		if (gwasMap.containsKey(disID))
    			gwasEdges = gwasMap.get(disID);
    		HashMap<Integer, GWASAssociation> gwasDisMap = new HashMap<Integer, GWASAssociation>();
    		for (GWASAssociation gwasEdge: gwasEdges){
    			gwasDisMap.put(gwasEdge.getGeneID(), gwasEdge);
    		}//for
    		Set<Integer> omimGeneIDs = new HashSet<Integer>();
    		if (omimMap.containsKey(disID)) 
    			omimGeneIDs = omimMap.get(disID);
    		
    		Set<Integer> medicGeneIDs = new HashSet<Integer>();
    		if (medicMap.containsKey(disID))
    			medicGeneIDs = medicMap.get(disID);
    		Set<Integer> associatedGeneIDs = new HashSet<Integer>();
    		associatedGeneIDs.addAll(gwasDisMap.keySet());
    		associatedGeneIDs.addAll(omimGeneIDs);
    		associatedGeneIDs.addAll(medicGeneIDs);
    		//add gene-disease edges
    		for(Integer gID: associatedGeneIDs) {
    			CyNode gNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID)); 
    			if (gNode == null){
    				continue;
    			}//if    			
    			
    			//add disease-gene edges
    			if (gwasDisMap.containsKey(gID)){
    				GWASAssociation gwasEdge =gwasDisMap.get(gID);
    				CyEdge newEdge = ictNet.addEdge(disNode, gNode, false);        				
    				String confidenceStr;
    				if (gwasEdge.getConfidence() == 0){
        			     confidenceStr = "low";
    				}else{
    					 confidenceStr = "high";
    				}
    				String primaryStr;
    				if (gwasEdge.getPrimary() == 1){
    					primaryStr = "primary";    					
    				}else{
    					primaryStr = "secondary";  
    				}
    				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_CONFI, confidenceStr);
        			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_GEN);        			
        			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_PRIMARY, primaryStr);
        			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_PUBMED, gwasEdge.getPubmeds());
        			
        			if (omimGeneIDs.contains(gID)){
        				if (medicGeneIDs.contains(gID)){
        					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_GWAS_OMIM_MEDIC);
        				}else{
        					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_GWAS_OMIM);
        				}//if-else        					
        			}else{
        				if (medicGeneIDs.contains(gID)){
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_GWAS_MEDIC);
        				}else{
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_GWAS);
            			}//if
        			}//if-else       			
    			}else{
    				if (omimGeneIDs.contains(gID)){
        				CyEdge newEdge = ictNet.addEdge(disNode, gNode, false); 
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_GEN);  
        				if (medicGeneIDs.contains(gID)){
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_OMIM_MEDIC);
            			}else{
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE,CyAttributeConstants.DATA_OMIM);
            			}//if-else
        			}else{
        				if (medicGeneIDs.contains(gID)){
        					CyEdge newEdge = ictNet.addEdge(disNode, gNode, false); 
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_GEN); 
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_MEDIC);
        				}//if 
        			}//if-else
    			}//if-else    			
    		}//for   		
    		
    		//add disease-tissue edges
    		if (diseaseTissueEdges != null){
    			if (diseaseTissueEdges.containsKey(disID)){
    				for(String tid: diseaseTissueEdges.get(disID)){    			
            			CyNode tNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, tid); 
            			if (tNode == null){
            				continue;
            			}//if    			
            			CyEdge newEdge = ictNet.addEdge(disNode, tNode, false);
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_TIS);
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_TISSUE);
            		}//for
    			}//if    			
    		}//if
    		taskMonitor.setProgress(0.6d);
    		//add disease-drug edges
    		if (diseaseDrugEdges != null){
    			if (diseaseDrugEdges.containsKey(disID)){
    				for(String mid: diseaseDrugEdges.get(disID)){
    					CyNode mNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, mid); 
            			if (mNode == null){
            				continue;
            			}//if 
            			CyEdge newEdge = ictNet.addEdge(disNode, mNode, false);
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_DRG);
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE,CyAttributeConstants.DATA_CTD);
    				}//for
    			}//if
    		}//if
    	}//for
    	taskMonitor.setProgress(0.7d);
    	if (cancelled)
			return;
    	System.gc();
    	// gene-drug
    	if (cancelled)
			return;
    	//add side effect
    	if(this.sideEffectFlag){
    		taskMonitor.setStatusMessage("Searching side effect..."); 
    		System.out.println("Searching side effect...");
    		creatNetworkLibrary.addSideEffect(ictNet, nodeTable, dbconnector, meshIDs, sideEffectStr, sideEffectFreq, uncategoriedSideEffect, sideEffectIDs);
    	}//if    	
    	taskMonitor.setProgress(0.8d);
    	if (cancelled)
			return;
    	//side effect - tissue edges
    	if (tissueSideEffectFlag){
    		taskMonitor.setStatusMessage("Searching side effect-tissue associations..."); 
    		System.out.println("Searching side effect-tissue associations...");
    	    creatNetworkLibrary.addSideEffectTissueEdges(ictNet, nodeTable, dbconnector, sideEffectIDs, tissueIDs);
    	}//if
    	    	
    	taskMonitor.setProgress(0.9d); 
    	if (cancelled)
			return;
    	creatNetworkLibrary.addGeneAttributes(ictNet, nodeTable, dbconnector, geneMap);
    	
    	// name all edges
    	creatNetworkLibrary.nameEdgesInsideNetwork(ictNet);
    	taskMonitor.setStatusMessage("Creating a new network...");     	
    	taskMonitor.setProgress(1d);
    	networkManager.addNetwork(ictNet);
    	
    	//create network view
    	CyNetworkView ictNetView = networkViewFactory.createNetworkView(ictNet);        
    	networkViewManager.addNetworkView(ictNetView);
    	
    	//network visual style
    	//if the style already existed, then just use it.     	
    	Iterator it = vmm.getAllVisualStyles().iterator();
    	VisualStyle vs = null;
    	while (it.hasNext()){
    			VisualStyle curVS = (VisualStyle)it.next();
    			if (curVS.getTitle().equalsIgnoreCase(CyAttributeConstants.DEF_VS_NAME)){
    				vs = curVS;
    				break;
    			}//if
    	}//while
    	
    	if (vs == null){
    	     vs = new ictnetVisualStyle().getVisualStyle();
    	     vmm.addVisualStyle(vs);   	
    	}//if
    	
    	vs.apply(ictNetView);    	
    	//network layout
    	CyLayoutAlgorithmManager layoutManager = ServicesUtil.layoutManagerServiceRef;
    	
    	CyLayoutAlgorithm layout= layoutManager.getLayout(CyAttributeConstants.DEF_LAYOUT_NAME);    	
    	TaskIterator itr = layout.createTaskIterator(ictNetView, layout.createLayoutContext(), 
    			CyLayoutAlgorithm.ALL_NODE_VIEWS, CyAttributeConstants.ATTR_TYPE);
    	insertTasksAfterCurrentTask(itr);
    	System.gc();
    	//update network view
        ictNetView.updateView();  
	}//

}

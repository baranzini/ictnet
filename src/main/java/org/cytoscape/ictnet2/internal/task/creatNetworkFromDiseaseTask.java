package org.cytoscape.ictnet2.internal.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.DBConnector;
import org.cytoscape.ictnet2.internal.ServicesUtil;
import org.cytoscape.ictnet2.internal.DBQueryLibrary;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Drug;
import org.cytoscape.ictnet2.internal.model.DrugGeneAssociation;
import org.cytoscape.ictnet2.internal.model.GWASAssociation;
import org.cytoscape.ictnet2.internal.model.Gene;
import org.cytoscape.ictnet2.internal.model.ictnetVisualStyle;
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

public class creatNetworkFromDiseaseTask extends AbstractTask{
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkManager networkManager;
	private final CyNetworkViewManager networkViewManager;
	private final VisualMappingManager vmm;
	private final DBConnector dbconnector;
	private final List<Disease> diseaseList;
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
	private Thread currentThread;
	
	public creatNetworkFromDiseaseTask(final CyNetworkFactory networkFactory, final CyNetworkViewFactory networkViewFactory, 
			final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
			final VisualMappingManager vmm, DBConnector dbconnector, 
			List<Disease> diseaseList, 
			boolean omimFlag, boolean efoFlag, boolean medicFlag, boolean ppiFlag, 
			boolean ctdFlag, boolean drugbankFlag, boolean ctdGeneFlag, boolean drugbankGeneFlag,
			boolean tissueFlag, boolean tissueGeneFlag, boolean miRNAFlag, String sideEffectStr, double sideEffectFreq, boolean uncategoriedSideEffect,
			int ppiDepthIndex, int gwasIndex){
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.networkManager = networkManager;
		this.networkViewManager = networkViewManager;
		this.vmm = vmm;
		this.dbconnector = dbconnector;
		this.diseaseList = diseaseList;
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
    	ictNet.getRow(ictNet).set(CyNetwork.NAME, "iCTNet_disease_"+dateFormat.format(new Date()));    	
    	
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
    	Set<Integer> diseaseIDs = new HashSet<Integer>();
    	Set<String> tissueIDs = new HashSet<String>();
    	Set<String> meshIDs = new HashSet<String>(); 
    	Set<Integer> drugbankIDs = new HashSet<Integer>();
    	Set<String> sideEffectIDs = new HashSet<String>();
    	//start from the disease    	
    	taskMonitor.setProgress(0.1d);
    	for(Disease disease: diseaseList){
    		diseaseIDs.add(disease.getID()); 
    	}//for   	
    	if (cancelled)
			return;
    	HashMap<Integer, Set<GWASAssociation>> gwasMap = new HashMap<Integer, Set<GWASAssociation>>();
    	if (efoFlag){   //GWAS 
			taskMonitor.setStatusMessage("Searching gwas disease-gene associations...");   			   
			gwasMap = DBQueryLibrary.dbQueryDiseaseGeneGWAS(dbconnector.getConnection(), diseaseIDs, gwasIndex);
    	}//if
    	if (cancelled)
			return;
    	HashMap<Integer, Set<Integer>> omimMap = new HashMap<Integer, Set<Integer>>(); 
    	if (omimFlag){ //OMIM
			taskMonitor.setStatusMessage("Searching OMIM disease-gene associations...");
			omimMap = DBQueryLibrary.dbQueryDiseaseGeneOMIM(dbconnector.getConnection(), diseaseIDs);
    	}//if
    	if (cancelled)
			return;
    	HashMap<Integer, Set<Integer>> medicMap = new HashMap<Integer, Set<Integer>>();
    	if (medicFlag){ //OMIM
			taskMonitor.setStatusMessage("Searching MEDIC disease-gene associations...");
			medicMap = DBQueryLibrary.dbQueryDiseaseGeneMedic(dbconnector.getConnection(), diseaseIDs);    		
    	}//if
    	if (cancelled)
			return;
    	//disease - tissue
    	HashMap<Integer, HashMap<String, String>> tissueMap = new HashMap<Integer, HashMap<String, String>>();
		if (tissueFlag){
			taskMonitor.setStatusMessage("Searching disease-tissue associations..."); 
			 tissueMap = DBQueryLibrary.dbQueryDiseaseTissue(dbconnector.getConnection(),diseaseIDs);
		}//if
		if (cancelled)
			return;
		HashMap<Integer, Set<Drug>> ctdDiseaseDrugs = new HashMap<Integer, Set<Drug>>();
		if(ctdFlag){
			taskMonitor.setStatusMessage("Searching disease-drug associations..."); 
			ctdDiseaseDrugs = DBQueryLibrary.dbQueryDiseaseDrugCTD(dbconnector.getConnection(),diseaseIDs);
		}//if
		if (cancelled)
			return;
    	for(Disease disease: diseaseList){
    		if (cancelled)
				return;
    		CyNode disNode = ictNet.addNode();
    		int disID = disease.getID();
    		ictNet.getRow(disNode).set(CyAttributeConstants.ATTR_ID, "DIS:"+disID);
    		ictNet.getRow(disNode).set(CyNetwork.NAME, disease.getName());
    		ictNet.getRow(disNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_DISEASE);
    		String[] ontologyLevels = disease.getDiseaseOntology();
    		for(int i= 8; i>0; i--){
    			String attribute = CyAttributeConstants.ATTR_DIS_LEVEL + i;
    			ictNet.getRow(disNode).set(attribute, ontologyLevels[i-1]);    			
    		}//for
    		
    		HashSet<Integer> associatedGeneIDs = new HashSet<Integer>();
    		Set<GWASAssociation> gwasEdges = new HashSet<GWASAssociation>();
    		HashMap<Integer, GWASAssociation> disMap = new HashMap<Integer, GWASAssociation>();
    		Set<Integer> omimGeneIDs = new HashSet<Integer>();
    		Set<Integer> medicGeneIDs = new HashSet<Integer>();
            if (gwasMap.containsKey(disID)){
            	gwasEdges = gwasMap.get(disID);
            }//
           
            for(GWASAssociation edge: gwasEdges){
            	disMap.put(edge.getGeneID(), edge);
            }//for
            associatedGeneIDs.addAll(disMap.keySet());
            if(omimMap.containsKey(disID)){
            	omimGeneIDs = omimMap.get(disID);
            }//if
            associatedGeneIDs.addAll(omimGeneIDs);
            if(medicMap.containsKey(disID)){
            	medicGeneIDs = medicMap.get(disID);
            }//if
            associatedGeneIDs.addAll(medicGeneIDs);
            
            for(Integer gID: associatedGeneIDs) {
    			CyNode gNode = null;
    			if (!geneMap.containsKey(gID)){
    				geneMap.put(gID, new Gene(gID));
    				gNode = ictNet.addNode();
        			ictNet.getRow(gNode).set(CyAttributeConstants.ATTR_ID, Integer.toString(gID));
        			ictNet.getRow(gNode).set(CyAttributeConstants.ATTR_TYPE, "gene");               			                			
    			}else{
    				gNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, Integer.toString(gID)); 
    				if (gNode == null){
    					continue;
    				}//if
    			}//if-else
    			if (cancelled)
    				return;
    			//add disease-gene edges
    			if (disMap.containsKey(gID)){
    				GWASAssociation gwasEdge = disMap.get(gID);
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
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, "GWAS");
            			}
        			}//if-else       			
    			}else{
    				if (omimGeneIDs.contains(gID)){
        				CyEdge newEdge = ictNet.addEdge(disNode, gNode, false); 
        				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_GEN);  
        				if (medicGeneIDs.contains(gID)){
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_OMIM_MEDIC);
            			}else{
            				ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_OMIM);
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
            if (cancelled)
				return;
            if (tissueFlag){
            	if(tissueMap.containsKey(disID)){
            		HashMap<String, String> tissues = tissueMap.get(disID);
            		for(String tisID: tissues.keySet()){
        				CyNode tisNode = null;
        				if (!tissueIDs.contains(tisID)){
        					tissueIDs.add(tisID);
            				tisNode = ictNet.addNode();
            				ictNet.getRow(tisNode).set(CyAttributeConstants.ATTR_ID, tisID);
            				ictNet.getRow(tisNode).set(CyNetwork.NAME, tissues.get(tisID));
            				ictNet.getRow(tisNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_TISSUE);        				
        				}else{
        					tisNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, tisID);    					
        				}//if-else
        				if (tisNode == null){
    						continue;
    					}//if        				
    					CyEdge newEdge = ictNet.addEdge(disNode, tisNode, false);
    					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_TIS);
    					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_TISSUE);
        			}//for
            	}//if           	
            }//if
            if (cancelled)
				return;
            if(ctdFlag){
            	if(ctdDiseaseDrugs.containsKey(disID)){
            		for(Drug drug: ctdDiseaseDrugs.get(disID)){
            			String meshID = drug.getMeshID();
            			if (!meshIDs.contains(meshID)){
        					meshIDs.add(meshID);
        					CyNode drugNode = ictNet.addNode();
                			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_ID, meshID);
                			ictNet.getRow(drugNode).set(CyNetwork.NAME, drug.getName());
                			int drugbankID = drug.getID();
                			if (drugbankID != -1){
                				drugbankIDs.add(drugbankID);
                				ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_EXTRA_ID, Integer.toString(drugbankID));
                			}//if
                			ictNet.getRow(drugNode).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.NODE_DRUG);
                			CyEdge newEdge = ictNet.addEdge(disNode, drugNode, false);
                			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_DRG); 
                			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_CTD);                			
        				}else{
        					CyNode drugNode = CyAttributeConstants.getNodeWithID(ictNet, nodeTable, CyAttributeConstants.ATTR_ID, meshID);
        					if (drugNode == null){
        						continue;
        					}//if
        					CyEdge newEdge = ictNet.addEdge(disNode, drugNode, false);
        					ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_TYPE, CyAttributeConstants.EDGE_DIS_DRG);        					
                			ictNet.getRow(newEdge).set(CyAttributeConstants.ATTR_DATA_SOURCE, CyAttributeConstants.DATA_CTD);                			
        				}//if-else  
            		}//for            		
            	}//if
            }//if
    	}//for    		
    			
    	taskMonitor.setProgress(0.4d); 
    	if (cancelled)
			return;
    	if(ppiFlag){
    		taskMonitor.setStatusMessage("Searching protein-protein interactions..."); 
    		creatNetworkLibrary.addPPI(ictNet, nodeTable, dbconnector, geneMap, ppiDepthIndex);
    	}//if PPI    	
    	System.out.println("Gene nodes: " + geneMap.size());
    	
    	System.gc();
    	taskMonitor.setProgress(0.6d);
    	if (cancelled)
			return;
    	// tissue - gene
    	if(tissueGeneFlag){
    		taskMonitor.setStatusMessage("Searching tissue-gene associations...");
    		creatNetworkLibrary.addGeneTissueEdges(ictNet, nodeTable, dbconnector, tissueIDs, geneMap.keySet());   		
    	}//if tissue-gene    	
    	    	
    	System.gc();
    	taskMonitor.setProgress(0.7d);
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
    	//add drug-gene edges
    	creatNetworkLibrary.addGeneDrugEdges(ictNet, nodeTable, DrugGeneCTDMap, DrugGeneDrugBankMap);	    	
    	
    	System.gc();
    	taskMonitor.setProgress(0.8d);
    	if (cancelled)
			return;
    	//add side effect
    	if(this.sideEffectFlag){
    		taskMonitor.setStatusMessage("Searching side effect...");
    		System.out.println("Searching side effect...");
    	    creatNetworkLibrary.addSideEffect(ictNet, nodeTable, dbconnector, meshIDs, sideEffectStr, sideEffectFreq, uncategoriedSideEffect, sideEffectIDs);
    	}//if
    	if (cancelled)
			return;
    	//side effect - tissue edges
    	if (tissueSideEffectFlag){
    		taskMonitor.setStatusMessage("Searching side effect-tissue associations..."); 
    		System.out.println("Searching side effect-tissue associations...");
    	    creatNetworkLibrary.addSideEffectTissueEdges(ictNet, nodeTable, dbconnector, sideEffectIDs, tissueIDs);
    	}//if
    	taskMonitor.setProgress(0.85d);
    	if (cancelled)
			return;
    	// miRNA - gene
    	if(miRNAFlag){
    		taskMonitor.setStatusMessage("Searching miRNA-gene associations..."); 
    		System.out.println("Searching miRNA-gene associations...");
    		creatNetworkLibrary.addMiRNA(ictNet, nodeTable, dbconnector, geneMap);
    	}//if miRNA-gene
    	
    	taskMonitor.setProgress(0.9d); 
    	if (cancelled)
			return;
    	creatNetworkLibrary.addGeneAttributes(ictNet, nodeTable, dbconnector, geneMap);
    	
    	// name all edges
    	creatNetworkLibrary.nameEdgesInsideNetwork(ictNet);
    	taskMonitor.setStatusMessage("Creating a new network...");     	
    	taskMonitor.setProgress(1d);
    	networkManager.addNetwork(ictNet);
    	System.gc();
    	//create network view
    	CyNetworkView ictNetView = networkViewFactory.createNetworkView(ictNet);        
    	networkViewManager.addNetworkView(ictNetView);
    	
    	//network visual style
    	// If the style already existed, then use it.    	
    	Iterator it = vmm.getAllVisualStyles().iterator();
    	VisualStyle vs = null;
    	while (it.hasNext()){
    			VisualStyle curVS = (VisualStyle)it.next();
    			if (curVS.getTitle().equalsIgnoreCase(CyAttributeConstants.DEF_VS_NAME)){
    				vs = curVS;
    				break;
    			}//if
    	}//while
    	
    	//create the visual style
    	if (vs == null){
    	     vs = new ictnetVisualStyle().getVisualStyle();
    	     vmm.addVisualStyle(vs);   	
    	}//if
    	
    	vs.apply(ictNetView);    	
    	//network layout
    	CyLayoutAlgorithmManager layoutManager = ServicesUtil.layoutManagerServiceRef;
    	if (cancelled)
			return;
    	CyLayoutAlgorithm layout= layoutManager.getLayout(CyAttributeConstants.DEF_LAYOUT_NAME);    	
    	TaskIterator itr = layout.createTaskIterator(ictNetView, layout.createLayoutContext(), 
    			CyLayoutAlgorithm.ALL_NODE_VIEWS, CyAttributeConstants.ATTR_TYPE);
    	insertTasksAfterCurrentTask(itr);
    	System.gc();
    	//update network view
        ictNetView.updateView();    	
    }//

}

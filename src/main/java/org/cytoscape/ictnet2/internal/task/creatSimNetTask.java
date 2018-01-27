package org.cytoscape.ictnet2.internal.task;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.ServicesUtil;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.VirtualColumnInfo;
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


/**
 * This file creates the disease similarity network for the current Cytoscape network.
 * 
 * @author Lili Wang 
 */
public class creatSimNetTask extends AbstractTask{
	final CyNetworkFactory networkFactory;
	final CyNetworkViewFactory networkViewFactory;
	final CyNetworkManager networkManager;
	final CyNetworkViewManager networkViewManager;
	final CyApplicationManager appManager;  
	final VisualMappingManager vmm;
	private Map<CyNode, CyNode> old2NewNodeMap;
    private String field;
    private int filter = 0; 
    private boolean flag = false;
    private String edgeAttr = "Value";
    
	public creatSimNetTask(final CyNetworkFactory networkFactory, 
			final CyNetworkViewFactory networkViewFactory, 
			final CyNetworkManager networkManager, 
			final CyNetworkViewManager networkViewManager, 
			final CyApplicationManager appManager,
			final VisualMappingManager vmm, String f, int i){
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.networkViewFactory = networkViewFactory;
		this.networkViewManager = networkViewManager;
		this.appManager = appManager;
		this.vmm = vmm;
		field = f;	
		filter = i;
	}
	
	
	public boolean isFlag(){
		return flag;
	}
	
		
	public void halt() {
		// not implemented
	}
	public String getTitle() {
		return "Creating similarity network";
	}
	
	/**
	 * Executes Task.
	 * @return 
	 */
	public void run(TaskMonitor taskMonitor) throws Exception{
		taskMonitor.setTitle("Creating Similarity Network for: " + field);
    	taskMonitor.setProgress(0.0d);
		taskMonitor.setStatusMessage("Creating Similarity Network for: " + field);
			
		final CyNetwork currentNet = appManager.getCurrentNetwork();
    	List<CyNode> nodeList = currentNet.getNodeList();
    	List<CyNode> fieldList = new ArrayList<CyNode>();	
    	
    	for(int i = 0; i < nodeList.size(); i++){     		
    		CyNode node = nodeList.get(i);
    		CyRow row = currentNet.getRow(node);
    		String typeStr = row.get("Type", String.class);
    		if (typeStr.equalsIgnoreCase(field)){				
				fieldList.add(node);				
			}//if
    	}//for 	
    	taskMonitor.setProgress(0.3d);
    	if (fieldList.size() == 0){
    		throw new Exception("No nodes in the selected type.");
    	}//if
    	// Create a new network
    	CyNetwork ictNet = networkFactory.createNetwork();    
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");    	
    	ictNet.getRow(ictNet).set(CyNetwork.NAME, "iCTNet_"+ field + "_similarity_network_"+dateFormat.format(new Date())); 
    	
    	CyTable nodeTable = ictNet.getDefaultNodeTable(); 
    	//node attributes
    	Set<String> attributeSet = new HashSet<String>();
    	attributeSet.add(CyNetwork.NAME);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_ID) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_ID, String.class, false);
    	attributeSet.add(CyAttributeConstants.ATTR_ID); 
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_EXTRA_ID) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_EXTRA_ID, String.class, false); 
    	attributeSet.add(CyAttributeConstants.ATTR_EXTRA_ID);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_TYPE) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_TYPE, String.class, false); 
    	attributeSet.add(CyAttributeConstants.ATTR_TYPE);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_DESCRIP) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_DESCRIP, String.class, false);
    	attributeSet.add(CyAttributeConstants.ATTR_DESCRIP);
    	if(nodeTable.getColumn(CyAttributeConstants.ATTR_LOC) == null)
    		nodeTable.createColumn(CyAttributeConstants.ATTR_LOC, String.class, false);
    	attributeSet.add(CyAttributeConstants.ATTR_LOC);
    	
    	//edge attributes
    	CyTable edgeTable = ictNet.getDefaultEdgeTable();
    	if(edgeTable.getColumn(edgeAttr) == null)
    		edgeTable.createColumn(edgeAttr, Double.class, false);
    	
    	old2NewNodeMap = new WeakHashMap<CyNode, CyNode>();
		for(CyNode node: fieldList){
			final CyNode newNode = ictNet.addNode();
			old2NewNodeMap.put(node, newNode);
			cloneRow(ictNet, currentNet, newNode, node, attributeSet);
		}//for
		taskMonitor.setProgress(0.5d);
	    CyNode[] fieldArray =  fieldList.toArray(new CyNode[fieldList.size()]);		
		for(int disCnt1 = 0; disCnt1 < fieldArray.length-1; disCnt1 ++){	
			CyNode node1 = fieldArray[disCnt1];
			List<CyNode> neighbors1 = currentNet.getNeighborList(node1, CyEdge.Type.ANY);
			if (neighbors1 == null){
				continue;
			}//if
				
			for(int disCnt2 = disCnt1+1; disCnt2 < fieldArray.length; disCnt2 ++){
				CyNode node2 = fieldArray[disCnt2];
				List<CyNode> neighbors2 = currentNet.getNeighborList(node2, CyEdge.Type.ANY);
				if (neighbors2 == null){
					continue;
				}//if
					
				int cnt = 0;
				for(CyNode nb1: neighbors1){					
					for(CyNode nb2: neighbors2){						
						if (nb1 == nb2)
							cnt += 1;						 
					}//for					
				}//for								
					
				if (cnt > filter){	
					final CyNode newSource = old2NewNodeMap.get(node1);
					final CyNode newTarget = old2NewNodeMap.get(node2);
						
					CyEdge newEdge = ictNet.addEdge(newSource, newTarget, true);
					ictNet.getRow(newEdge).set(edgeAttr, new Double(cnt));				
				}//if			
			}//for
		}//for				
		    
		flag = true;
		networkManager.addNetwork(ictNet);		
		
		//create network view
    	CyNetworkView ictNetView = networkViewFactory.createNetworkView(ictNet);        
    	networkViewManager.addNetworkView(ictNetView);
		// visual style
		VisualStyle vs = vmm.getCurrentVisualStyle();
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
		taskMonitor.setProgress(1.0d);		
	}//run
    

    private void cloneRow(final CyNetwork newNet, final CyNetwork oldNet, 
		              final CyNode newNode, final CyNode oldNode, Set<String> attributeSet) {       
        for(String attribute: attributeSet){
        	String attrStr = oldNet.getRow(oldNode).get(attribute, String.class);
        	newNet.getRow(newNode).set(attribute, attrStr);
        }//for
    }//

 


}//end
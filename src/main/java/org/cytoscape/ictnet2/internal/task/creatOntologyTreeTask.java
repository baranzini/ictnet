package org.cytoscape.ictnet2.internal.task;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.cytoscape.ictnet2.internal.DBConnector;
import org.cytoscape.ictnet2.internal.DBConstants;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.OntologyTreeNode;
import org.cytoscape.ictnet2.internal.ui.CheckTreeSelectionModel;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class creatOntologyTreeTask extends AbstractTask{
	private final DBConnector dbConter;	
	private final JTree tree;	
	private HashMap<Integer, OntologyTreeNode> disTreeNodeMap;
	
	public creatOntologyTreeTask(DBConnector dbConter, JTree tree){
		this.dbConter = dbConter;		
		this.tree = tree;		
		this.disTreeNodeMap = new HashMap<Integer, OntologyTreeNode>();
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		taskMonitor.setTitle("Creating disease ontology tree...");	
		System.out.println("To creat disease ontology tree.");
		taskMonitor.setProgress(0.1d);    	
		downloadDiseaseOntolog();	
		System.out.println("Created the disease ontology tree.");
    	taskMonitor.setProgress(0.6d);    	
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
    	
    	Disease rootDis =  (Disease) (rootNode.getUserObject());
		findChildNode(rootNode, rootDis.getID(), 0);		
    	expandFirstLevelOfTree(this.tree); 
    	
    	taskMonitor.setStatusMessage("Searching data on the server..."); 
    	taskMonitor.setProgress(1d);    	
	}//
	
	private void downloadDiseaseOntolog(){
		HashSet<DefaultMutableTreeNode> childList = new HashSet<DefaultMutableTreeNode>();		
    	String sql = "{call " + DBConstants.SP_DISEASE_ONTOLOGY_ALL + "()}"; 
    	
    	try{    		
    		CallableStatement cs = this.dbConter.getConnection().prepareCall(sql);    		   		
			ResultSet rs = cs.executeQuery();			
			while(rs.next()){				
				int pID = rs.getInt("d.doid_id");
				String pName = rs.getString("d.name");				
				String childIDStr = rs.getString(3);			
				OntologyTreeNode disTreeNode = new OntologyTreeNode(pID, pName);
				if (childIDStr != null)
				    disTreeNode.setChildSet(childIDStr);
				disTreeNodeMap.put(pID, disTreeNode);						
			}//while			
			rs.close();
			cs.close();
			this.dbConter.shutDown();	
    	}catch(SQLException sqlex){
			System.out.println("sqlex:"+sqlex.toString());	
		}catch(Exception ex){
			System.out.println("exception:"+ex.toString());
		}//
	}//
	
	private void findChildNode(DefaultMutableTreeNode rootNode, int code, int level){		
		if (disTreeNodeMap.containsKey(code)){
			Set<DefaultMutableTreeNode> childList = new HashSet<DefaultMutableTreeNode>();			
			Set<Integer> childIDs = disTreeNodeMap.get(code).getChildSet();
			for(Integer childID: childIDs){
				OntologyTreeNode childNode = disTreeNodeMap.get(childID); 
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new Disease(childID, childNode.getName(),
						level+1, (Disease)(rootNode.getUserObject())));
				rootNode.add(newChild);
				childList.add(newChild);
			}//for
			
			for(DefaultMutableTreeNode childnode: childList){
		    	Disease childDis = (Disease) (childnode.getUserObject());
			    findChildNode(childnode, childDis.getID(), level+1);
		    }//for
		}//if	
    }//findChildNode	
	 
	public int getNumberOfNodes(TreeModel model, Object node){  
		int count = 1;
		int nChildren = model.getChildCount(node);  
		for (int i = 0; i < nChildren; i++){  
		    count += getNumberOfNodes(model, model.getChild(node, i));  
		}//for  
		return count;  
    }//
	
	private void expandFirstLevelOfTree(JTree tree){		
		TreeModel treeModel = tree.getModel();		
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();		
		TreePath tpath = new TreePath(rootNode.getPath());			
		tree.expandPath(tpath);		
	}//
}

package org.cytoscape.ictnet2.internal.ui;

import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.cytoscape.ictnet2.internal.model.Disease;

public class CheckTreeSelectionModel extends DefaultTreeSelectionModel{ 
    private TreeModel model;    
    
    public CheckTreeSelectionModel(TreeModel model){ 
        this.model = model; 
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
    }     
    
    public void addSelectionPath(TreePath tpath, boolean selectDescendants){
    	 if (selectDescendants){    		
 			super.addSelectionPath(tpath); 			
     		Object node = tpath.getLastPathComponent();     		
     		int childCount = this.model.getChildCount(node);
     		for(int i = 0; i< childCount; i++){
     			Object childNode = this.model.getChild(node, i);
     			this.addSelectionPath(tpath.pathByAddingChild(childNode), selectDescendants);
     		}//for   		
 	    }else{
 		    super.addSelectionPath(tpath);
 	    }//if-else   	
    }  
    
    public void removeSelectionPath(TreePath tpath, boolean selectDescendants){
    	if (selectDescendants){
    		super.removeSelectionPath(tpath);
    		Object node = tpath.getLastPathComponent();
    		int childCount = this.model.getChildCount(node);
    		for(int i = 0; i< childCount; i++){
    			Object childNode = this.model.getChild(node, i);
    			this.removeSelectionPath(tpath.pathByAddingChild(childNode), selectDescendants);
    		}//for
    	}else{
    		super.removeSelectionPath(tpath);
    	}//if-else    		   
    }     
   
    public HashMap<Integer, Disease> getSelectedNodes(TreePath[] paths){    	 
    	HashMap<Integer, Disease> selectedDis = new HashMap<Integer, Disease>();    	
    	if(paths == null)
    		return selectedDis;
    	int selectedNum = paths.length;    	
		for(int i=0; i<selectedNum; i++){
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
			Disease parentDis = (Disease) parentNode.getUserObject();
			selectedDis.put(parentDis.getID(), parentDis);					
		}//for		
		return selectedDis;
    }
}
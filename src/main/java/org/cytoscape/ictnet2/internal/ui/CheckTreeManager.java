package org.cytoscape.ictnet2.internal.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Molecule;

public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener{ 
    private CheckTreeSelectionModel selectionModel; 
    private JTree tree = new JTree(); 
    int hotspot = new JCheckBox().getPreferredSize().width; 
    private final QueryPanel queryPanel; 
    private boolean selectDescendants = true;  
 
    public CheckTreeManager(JTree tree, QueryPanel queryPanel){ 
        this.tree = tree;
        this.queryPanel = queryPanel;       
        selectionModel = new CheckTreeSelectionModel(tree.getModel()); 
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel)); 
        tree.addMouseListener(this); 
        selectionModel.addTreeSelectionListener(this); 
    } 
    public void setSelectDescendants(boolean flag){
    	selectDescendants = flag;
    }
    
    public boolean isSelectDescendants(){
    	return selectDescendants;
    }
    
    public void mouseClicked(MouseEvent me){    	
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());        
        if(path==null) 
            return; 
        if(me.getX()>tree.getPathBounds(path).x+hotspot)
            return; 
 
        boolean selected = selectionModel.isPathSelected(path); 
        selectionModel.removeTreeSelectionListener(this);         
        this.queryPanel.typeComboBox.setSelectedIndex(CyAttributeConstants.DISEASE_CODE);
        try{ 
            if(selected){ 
            	 HashMap<Integer, Disease> previousSelected = this.getSelectedTreeNodes();
                 selectionModel.removeSelectionPath(path, selectDescendants); 
            	 HashMap<Integer, Disease> currentSelected = this.getSelectedTreeNodes();
                 HashSet<Integer> removedDisIDs = new HashSet<Integer>();
                 for (int preDisID: previousSelected.keySet()){
                 	if (!currentSelected.keySet().contains(preDisID)){
                 		removedDisIDs.add(preDisID);                		
                 	}//if
                 }//for
                 this.queryPanel.removeTableRow(removedDisIDs);               
            }else{ 
                selectionModel.addSelectionPath(path, selectDescendants); 
                HashMap<Integer, Disease> currentSelected = this.getSelectedTreeNodes();
                Set<Molecule> selectedNodes = new HashSet<Molecule>();
                for(Disease dis: currentSelected.values()){
                	selectedNodes.add((Molecule) dis);
                }//for
                this.queryPanel.updateTableRow(selectedNodes, true, CyAttributeConstants.DISEASE_CODE);
            }//if-else
        }finally{ 
            selectionModel.addTreeSelectionListener(this); 
            tree.treeDidChange(); 
        }// 
    } 
 
    public CheckTreeSelectionModel getSelectionModel(){
        return selectionModel; 
    } 
 
    public void valueChanged(TreeSelectionEvent e){ 
        tree.treeDidChange(); 
    } 
    
    public HashMap<Integer, Disease> getSelectedTreeNodes(){
    	TreePath checkedPaths[] = selectionModel.getSelectionPaths();		 
		HashMap<Integer, Disease> selectedDis = selectionModel.getSelectedNodes(checkedPaths);
		return selectedDis;
    }
    public void removeTreeNode(int disID){
    	selectionModel.removeTreeSelectionListener(this);
    	HashMap<Integer, Disease> previousSelected = this.getSelectedTreeNodes();
    	findTreeNodeRemove((DefaultMutableTreeNode)tree.getModel().getRoot(), disID);
    	//add children nodes into the table
    	HashMap<Integer, Disease> currentSelected = this.getSelectedTreeNodes();
    	HashSet<Integer> removedDisIDs = new HashSet<Integer>();
        for (int preDisID: previousSelected.keySet()){
        	if (preDisID == disID)
        		continue;
        	if (!currentSelected.keySet().contains(preDisID)){        		
        		removedDisIDs.add(preDisID);                		
        	}//if
        }//for
        this.queryPanel.removeTableRow(removedDisIDs);  
    	selectionModel.addTreeSelectionListener(this);
    	tree.treeDidChange(); 
    }
    public void findMatchedTreeNodes(HashSet<Integer> disIDs){
    	selectionModel.removeTreeSelectionListener(this); 
    	collapseAndClearTree((DefaultMutableTreeNode)tree.getModel().getRoot());
    	findTreeNodeAdd((DefaultMutableTreeNode)tree.getModel().getRoot(), disIDs);
    	//add children nodes into the table
    	HashMap<Integer, Disease> currentSelected = this.getSelectedTreeNodes();
        Set<Molecule> selectedNodes = new HashSet<Molecule>();
        for(Disease dis: currentSelected.values()){
        	selectedNodes.add((Molecule) dis);
        }//for
        this.queryPanel.addTableRow(selectedNodes, true, CyAttributeConstants.DISEASE_CODE);       
    	tree.scrollPathToVisible(selectionModel.getLeadSelectionPath());
    	selectionModel.addTreeSelectionListener(this);    	
    	tree.treeDidChange();    	    	
    }//
    private void collapseAndClearTree(DefaultMutableTreeNode parentNode){
    	int row = tree.getRowCount();
    	while(row > 0){
    		tree.collapseRow(row);
    		row --;
    	}//while
    	selectionModel.clearSelection();    	
    }//
    
    private void findTreeNodeAdd(DefaultMutableTreeNode parentNode, HashSet<Integer> disIDs){    	
		Disease parentDis = (Disease) parentNode.getUserObject();
		int childCnt = parentNode.getChildCount();
		TreePath tpath = new TreePath (parentNode.getPath());
		if (disIDs.contains(parentDis.getID())){			
		    if (!selectionModel.isPathSelected(tpath)){
			    selectionModel.addSelectionPath(tpath, selectDescendants);			   
		    }//if
			
			// if this node is the leaf, then we need get the parent node to expand the tree
			// as JTree expandPath does not work leaf node.
			if (childCnt == 0){
				tpath = tpath.getParentPath();				
			}//if						
			tree.expandPath(tpath);	
			for(int i=0; i< childCnt; i++){
				findTreeNodeAdd((DefaultMutableTreeNode)parentNode.getChildAt(i), disIDs);
			}//for
		}else{			
			for(int i=0; i< childCnt; i++){
				findTreeNodeAdd((DefaultMutableTreeNode)parentNode.getChildAt(i), disIDs);
			}//for			
		}//			
	}// 
    
    private void findTreeNodeRemove(DefaultMutableTreeNode parentNode, int disID){
    	Disease parentDis = (Disease) parentNode.getUserObject();
		int childCnt = parentNode.getChildCount();
		TreePath tpath = new TreePath (parentNode.getPath());
		if (disID == parentDis.getID()){			
		    if (selectionModel.isPathSelected(tpath)){
			    selectionModel.removeSelectionPath(tpath, selectDescendants);
		    }//if
			
			// if this node is the leaf, then we need get the parent node to expand the tree
			// as JTree expandPath does not work leaf node.
			if (childCnt == 0){
				tpath = tpath.getParentPath();				
			}//if						
			tree.collapsePath(tpath);			
		}else{			
			for(int i=0; i< childCnt; i++){
				findTreeNodeRemove((DefaultMutableTreeNode)parentNode.getChildAt(i), disID);
			}//for			
		}//	
    }
    
}

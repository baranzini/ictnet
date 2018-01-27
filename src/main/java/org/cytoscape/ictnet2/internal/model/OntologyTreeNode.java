package org.cytoscape.ictnet2.internal.model;

import java.util.HashSet;
import java.util.Set;

public class OntologyTreeNode {
	private int ID;
	private String name;
	private Set<Integer> childIDSet;
	
	public OntologyTreeNode(int ID, String name){
		this.ID = ID;
		this.name = name;
	    childIDSet = new HashSet<Integer>();
	}
	
	public Set<Integer> getChildSet(){
		return childIDSet;
	}
	
	public void setChildSet(String childIDStr){
		String [] childIDArray = childIDStr.split(",");
		for (String id: childIDArray){
			if (id.length() > 0)
			    childIDSet.add(Integer.parseInt(id));
		}//for		
	}
	
	public int getID(){
		return ID;
	}
	
	public String getName(){
		return name;
	}
	
	public int getChildCount(){
		return childIDSet.size();
	}

}

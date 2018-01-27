package org.cytoscape.ictnet2.internal.model;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class PPI {
	private Integer source;
	private Integer target;
	private Set<String> pubmeds;
	public PPI(Integer source, Integer target, String pubmedStr){
		this.source = source;
		this.target = target;
		pubmeds = new HashSet<String>();
		String[] pubmedArray = pubmedStr.split(",");
		for (String id: pubmedArray){
			if (pubmeds.contains(id))
				continue;
			else
				pubmeds.add(id);
		}//for
	}//
	
	public void addPubmed(String pubmedStr){
		String[] pubmedArray = pubmedStr.split(",");
		for (String id: pubmedArray){
			if (pubmeds.contains(id))
				continue;
			else
				pubmeds.add(id);
		}//for
	}
	public String getPubmeds(){
		StringBuilder pubmedStr = new StringBuilder();	    
		for(String id: pubmeds){			
			pubmedStr.append( id+ ",");
		}//for		
		String str = pubmedStr.toString();
		if (str.length() > 0)
		    str = str.substring(0, str.length()-1);
		return str;		
	}
	public Integer getSource(){
		return source;
	}//
	
	public Integer getTarget(){
		return target;
	}//
	
}

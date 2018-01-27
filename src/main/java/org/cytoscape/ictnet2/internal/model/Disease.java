package org.cytoscape.ictnet2.internal.model;

public class Disease extends Molecule implements Comparable<Disease>{    
    private Integer level;
    private Disease parent = null;    
    
    public Disease(int ID, String name){
    	this.ID = ID;
    	this.name = name;
    	this.level = -1;
    }//
    
    public Disease(int ID, String name, int level){
    	this.ID = ID;
    	this.name = name;    	
    	this.level = level;    
    }//
    
    public Disease(int ID, String name, int level, Disease parent){
    	this.ID = ID;
    	this.name = name;    	
    	this.level = level;
    	this.parent = parent;
    }//   

	@Override
	public int compareTo(Disease o) {		
		return o.getID() - this.ID;
	}//
	
	public int getLevel(){
		return level;
	}//
    
    public void setLevel(int level){
    	this.level = level;
    }//   
    
    public Disease getParent(){
    	return parent;
    }//
    
    public String[] getDiseaseOntology(){
    	return getDiseaseOntology(this);
    }
    
    private String[] getDiseaseOntology(Disease disease){//8 levels
    	String[] ontologyLevels = new String[8];
    	int level = disease.getLevel();	    	
    	if (level > 0){
    		if (level > 8){	    			
    			return getDiseaseOntology(disease.getParent());	    			
    		}else{
    			for (int i = level; i>0; i--){
    				ontologyLevels[i-1] = disease.getName();
    				disease = disease.getParent();	    				
    			}//for
    		}//if-else
    	}//if    		
    	return ontologyLevels;
    }
   
}

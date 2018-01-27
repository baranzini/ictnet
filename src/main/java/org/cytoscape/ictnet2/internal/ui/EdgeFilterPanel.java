package org.cytoscape.ictnet2.internal.ui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class EdgeFilterPanel extends JPanel implements MouseListener{
	DiseaseGeneFilterPanel dgFilterPane;
	PPIFilterPanel ppiFilterPane;
	GeneDrugFilterPanel gdFilterPane;
	OtherFilterPanel otherFilterPane;
	private int searchTypeIdx = 0;
	
	public EdgeFilterPanel(){
		super();	
	    initComponents();
	    addMouseListener();
	}//
	
	public void initComponents(){
		dgFilterPane = new DiseaseGeneFilterPanel();
		ppiFilterPane = new PPIFilterPanel();
		gdFilterPane = new GeneDrugFilterPanel();
		otherFilterPane = new OtherFilterPanel();	
		
		javax.swing.GroupLayout scrollPaneLayout = new javax.swing.GroupLayout(this);
	    this.setLayout(scrollPaneLayout);
	    scrollPaneLayout.setHorizontalGroup(
	    	scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    	.addGroup(scrollPaneLayout.createSequentialGroup()               
	            .addGroup(scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,false)
	                .addComponent(dgFilterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
	                .addComponent(ppiFilterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
	                .addComponent(gdFilterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
	                .addComponent(otherFilterPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)) 
	            .addContainerGap(2, Short.MAX_VALUE)
	        )
	    );
	    
	    scrollPaneLayout.setVerticalGroup(
	    	scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(scrollPaneLayout.createSequentialGroup()                
	            .addComponent(dgFilterPane)
	            .addComponent(ppiFilterPane)
	            .addComponent(gdFilterPane) 
	            .addComponent(otherFilterPane)
	        )
	    );
	}//
	public void setSearchTypeIdx(int searchTypeIdx){
		this.searchTypeIdx = searchTypeIdx;
	}
	private void addMouseListener(){
		dgFilterPane.gwasCheckBox.addMouseListener(this);
		dgFilterPane.omimCheckBox.addMouseListener(this);
		dgFilterPane.medicCheckBox.addMouseListener(this);
	}
	public boolean isOMIMSelected(){
		return dgFilterPane.omimCheckBox.isSelected();
	}
	
	public boolean isEFOSelected(){
		return dgFilterPane.gwasCheckBox.isSelected();
	}
	public int getConfidenceIndex(){
		return dgFilterPane.gwasComboBox.getSelectedIndex();
	}
	
	public boolean isMEDICSelected(){
		return dgFilterPane.medicCheckBox.isSelected();
	}
	
	public int getPPISelectedIndex(){
		return ppiFilterPane.ppiComboBox.getSelectedIndex();		
	}//
	
	public boolean isPPISelected(){
		return ppiFilterPane.ppiCheckBox.isSelected();
	}
	
	public boolean isCTDSelected(){
		return gdFilterPane.CTDCheckBox.isSelected();
	}
	
	public boolean isDrugBankSelected(){
		return gdFilterPane.drugbankCheckBox.isSelected();
	}
	
	public boolean isCTDGeneSelected(){
		return gdFilterPane.CTDGeneCheckBox.isSelected();
	}
	
	public boolean isDrugBankGeneSelected(){
		return gdFilterPane.drugbankGeneCheckBox.isSelected();
	}
	
	public boolean isDiseaseTissueSelected(){
		return otherFilterPane.diseaseTissueCheckBox.isSelected();
	}
	
	public boolean isGeneTissueSelected(){
		return otherFilterPane.geneTissueCheckBox.isSelected();
	}
	
	public boolean ismiRNASelected(){
		return otherFilterPane.mirnaGeneCheckBox.isSelected();
	}
	
	public boolean isAtLeastOneDiseaseSourceSelected(){
		return dgFilterPane.isAtLeastOneDiseaseSourceSelected();
	}
	
	public String getSideEffectSelection(){		
		return gdFilterPane.getCategoryIDList();
	}
	
	public double getSideEffectPercentage(){		
		return gdFilterPane.getPercentage();
	}
	
	public boolean getUncategorizedSelection(){
		return gdFilterPane.getUncategoriedSelection();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(this.searchTypeIdx){
		   case 0:			   
			   if (this.isAtLeastOneDiseaseSourceSelected()){				    
					gdFilterPane.setEnable(true);
					ppiFilterPane.setEnable(true);
					otherFilterPane.setEnable(true);
				}else{					
					gdFilterPane.setEnable(false);
					ppiFilterPane.setEnable(false);
					otherFilterPane.setEnable(false);
				}//if-else
			    revalidate();
				repaint();
		}//switch
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void setDrugPanelCheckBox(boolean flag){
		gdFilterPane.setCheckBoxStatus(flag);
	}

}

package org.cytoscape.ictnet2.internal.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class EdgeSelectionPanel extends JPanel{	
	JLabel plusLabel;
	JLabel plusIconLabel;
	JPanel topPanel;
	JScrollPane edgeSelectionScrollPane;
	EdgeFilterPanel edgeFilterPane;
	
	public EdgeSelectionPanel(String title){
	    super();	
	    initComponents(title);    
	}//EdgeSelectPanel
	
	private void initComponents(String title){ 
		TitledBorder titled = BorderFactory.createTitledBorder(title);
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);
		this.setMinimumSize(new Dimension(340, 38));
		
		topPanel = new JPanel(); 
		plusLabel = new JLabel();	
    	plusLabel.setText("Show Advanced Options");       	
        plusIconLabel = new JLabel(); 	
    	edgeFilterPane = new EdgeFilterPanel();       
        edgeSelectionScrollPane = new JScrollPane(edgeFilterPane);
        
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)           
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(plusIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)                
                    .addComponent(plusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)                   
                    .addGap(100, 100, 100))
            );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plusIconLabel)
                        .addComponent(plusLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))                
            ); 
        
        this.setLayout(new GridBagLayout());
    	GridBagConstraints gridBagConstraints;
    	gridBagConstraints = new GridBagConstraints();
     	gridBagConstraints.gridx = 0;  
     	gridBagConstraints.weightx = 1.0;  
     	gridBagConstraints.weighty = 0.01;
     	gridBagConstraints.fill = gridBagConstraints.HORIZONTAL; 
     	gridBagConstraints.anchor = gridBagConstraints.NORTHWEST;
        add(topPanel, gridBagConstraints); 
  
    	gridBagConstraints = new GridBagConstraints();
     	gridBagConstraints.gridy = 1;     	
     	gridBagConstraints.weighty = 0.99;
     	gridBagConstraints.fill = gridBagConstraints.BOTH;     	
        add(edgeSelectionScrollPane, gridBagConstraints);  	
    	
	}//	
	
	public void setDrugPanelCheckBox(boolean flag){
		edgeFilterPane.setDrugPanelCheckBox(flag);
	}//
	
}

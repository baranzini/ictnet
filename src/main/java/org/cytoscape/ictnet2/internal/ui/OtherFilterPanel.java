package org.cytoscape.ictnet2.internal.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class OtherFilterPanel extends JPanel{	
	JCheckBox mirnaGeneCheckBox;	
	JCheckBox geneTissueCheckBox;
	JCheckBox diseaseTissueCheckBox;	
	
	public OtherFilterPanel(){
		initComponents();
	}
	
	private void initComponents() {
		TitledBorder titled = BorderFactory.createTitledBorder("Other associations");
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);		
		
		mirnaGeneCheckBox = new JCheckBox();		
		geneTissueCheckBox = new JCheckBox();
		diseaseTissueCheckBox = new JCheckBox();		

	    mirnaGeneCheckBox.setText("miRNA-Gene");
	    mirnaGeneCheckBox.setSelected(false);
	    geneTissueCheckBox.setText("Gene-Tissue");	
	    geneTissueCheckBox.setSelected(false);
	    diseaseTissueCheckBox.setText("Disease-Tissue");
	    diseaseTissueCheckBox.setSelected(false);	   

	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	    this.setLayout(layout);
	    layout.setHorizontalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(mirnaGeneCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(geneTissueCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(0, 0, Short.MAX_VALUE))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(diseaseTissueCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(0, 0, Short.MAX_VALUE))	                
	            ))
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(mirnaGeneCheckBox)	               
	                .addComponent(geneTissueCheckBox)
	                )
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(diseaseTissueCheckBox)	     	       
	        ))
	    );
	}//
	
	public void setEnable(boolean flag){
		this.diseaseTissueCheckBox.setEnabled(flag);		
		this.geneTissueCheckBox.setEnabled(flag);
		this.mirnaGeneCheckBox.setEnabled(flag);
	}//

}

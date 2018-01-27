package org.cytoscape.ictnet2.internal.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class DiseaseGeneFilterPanel extends JPanel{
	JCheckBox gwasCheckBox;
	JCheckBox omimCheckBox;
	JCheckBox medicCheckBox;
	private JLabel jLabel1;
	JComboBox gwasComboBox;	
	
	public DiseaseGeneFilterPanel(){
		initComponents();
	}
	
	private void initComponents() {
		TitledBorder titled = BorderFactory.createTitledBorder("Disease-gene association");
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);
		
		jLabel1 = new JLabel();
		String[] confidence = {"both", "low", "high"};
		gwasComboBox = new JComboBox(confidence);
	    gwasCheckBox = new JCheckBox();
	    omimCheckBox = new JCheckBox();
	    medicCheckBox = new JCheckBox();

	    jLabel1.setText("Confidence:");   
	    gwasCheckBox.setText("GWAS data");
	    gwasCheckBox.setSelected(true);
	    omimCheckBox.setText("OMIM data");
	    omimCheckBox.setSelected(false);
	    medicCheckBox.setText("Medic data");
	    medicCheckBox.setSelected(false);

	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	    this.setLayout(layout);
	    layout.setHorizontalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(gwasCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(2,2,2)
	                    .addComponent(gwasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(omimCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(medicCheckBox)
	                    .addGap(0, 0, Short.MAX_VALUE))	               
	            ))
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(gwasCheckBox)
	                .addComponent(jLabel1)
	                .addComponent(gwasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                )
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(omimCheckBox)
	    	        .addComponent(medicCheckBox)
	        ))
	    );
	}
	
	public boolean isAtLeastOneDiseaseSourceSelected(){
		return gwasCheckBox.isSelected()||omimCheckBox.isSelected()||medicCheckBox.isSelected();
	}//
	
	public void setEnable(boolean flag){
		this.gwasCheckBox.setEnabled(flag);
		this.omimCheckBox.setEnabled(flag);
        this.medicCheckBox.setEnabled(flag);
	}//
}

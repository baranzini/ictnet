package org.cytoscape.ictnet2.internal.ui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PPIFilterPanel extends JPanel{
	JCheckBox ppiCheckBox;	
	JLabel jLabel1;
	JComboBox ppiComboBox;	
	
	public PPIFilterPanel(){
		initComponents();
	}
		
	private void initComponents() {
		TitledBorder titled = BorderFactory.createTitledBorder("Protein-protein association");
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);
		
		jLabel1 = new JLabel();		
	    ppiCheckBox = new JCheckBox();
	    ppiComboBox = new JComboBox(new javax.swing.DefaultComboBoxModel(new String[] { "=0", "=1", "=2", ">2"}));

	    jLabel1.setText("Depth:");	   
	    ppiCheckBox.setText("PPI ");
	    ppiCheckBox.setSelected(true);
	    ppiComboBox.setEnabled(true);        
        ppiComboBox.setSelectedIndex(0);
	    
	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	    this.setLayout(layout);
	    layout.setHorizontalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(ppiCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(2,2,2)
	                    .addComponent(ppiComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(0, 0, Short.MAX_VALUE))
	               )
	            )
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(ppiCheckBox)
	                .addComponent(jLabel1)
	                .addComponent(ppiComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	            ))
	    );
	}//
	
	public void setEnable(boolean flag){
		this.ppiCheckBox.setEnabled(flag);
		this.ppiComboBox.setEnabled(flag);
	}//
}

package org.cytoscape.ictnet2.internal.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.model.Disease;

public class TreeViewPanel extends JPanel{
	JLabel plusLabel;
	JLabel plusIconLabel;
	JPanel topPanel;
	JScrollPane treeViewPanel;
	JTree ontologyTree;
	public CheckTreeManager checkTreeManager;	
	JCheckBox includeDescendant;
	
	public TreeViewPanel(String title){
		super();		
	   	initComponents(title);	   	
	}//
	
	private void initComponents(String title){		
		TitledBorder titled = BorderFactory.createTitledBorder(title);
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);	
		topPanel = new JPanel();
		this.setMinimumSize(new Dimension(340, 40));
		this.setPreferredSize(new Dimension(340, 220));
		
		includeDescendant = new JCheckBox("include descendant(s)");
		includeDescendant.setSelected(true);    	
    	
    	plusLabel = new JLabel();	
    	plusLabel.setText("Tree View");       	
        plusIconLabel = new JLabel();    	
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)           
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(plusIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)                
                    .addComponent(plusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(includeDescendant, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20))
            );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(plusIconLabel)
                        .addComponent(plusLabel)
                        .addComponent(includeDescendant))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))                
            ); 
    	
    	
	    Disease rootDis = new Disease(CyAttributeConstants.DISEASE_ROOT_CODE, CyAttributeConstants.DISEASE_ROOT_NAME);
	    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootDis);
	    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);		
	    ontologyTree = new JTree(treeModel);     	
	    treeViewPanel = new JScrollPane(ontologyTree);
	
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
     	gridBagConstraints.gridy = 2;     
     	gridBagConstraints.weighty = 0.98;
     	gridBagConstraints.fill = gridBagConstraints.BOTH;     	
        add(treeViewPanel, gridBagConstraints);    	
	}//

}

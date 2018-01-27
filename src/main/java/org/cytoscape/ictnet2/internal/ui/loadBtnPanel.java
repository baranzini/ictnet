package org.cytoscape.ictnet2.internal.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

public class loadBtnPanel extends JPanel{	
	JButton loadBtn;
	
	public loadBtnPanel(){
		initComponents();
	}
	
	private void initComponents() {
		loadBtn = new JButton();
		loadBtn.setText("Load");
		
		this.setLayout(new GridBagLayout());
    	GridBagConstraints gridBagConstraints;
    	gridBagConstraints = new GridBagConstraints();
     	gridBagConstraints.gridx = 0;     
     	gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;    	
        add(loadBtn, gridBagConstraints); 
	}//

}

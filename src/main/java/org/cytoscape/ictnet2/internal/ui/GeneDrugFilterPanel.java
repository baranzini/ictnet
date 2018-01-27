package org.cytoscape.ictnet2.internal.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;

public class GeneDrugFilterPanel extends JPanel implements ActionListener{
	JCheckBox drugbankCheckBox;	
	JCheckBox CTDCheckBox;
	JCheckBox drugbankGeneCheckBox;
	JCheckBox CTDGeneCheckBox;
	JCheckBox frequencyCheckBox;
	JCheckBox uncategoriedCheckBox;
	private JLabel drugBankLabel;
	private JLabel CTDLabel;
	DefaultTableModel tableModel;
	JTable sideEffectTable;
	private JLabel sideEffectLabel;
	static final int FREQ_MIN= 0;
	static final int FREQ_MAX = 100;
	static final int FREQ_INIT = 50;
	JSlider frequencySlider;
	boolean flag = false;
	
	public GeneDrugFilterPanel(){
		initComponents();
	}//
		
	private void initComponents() {
		TitledBorder titled = BorderFactory.createTitledBorder("Drug association");
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);		
				
		drugBankLabel = new JLabel("DrugBank:");
		CTDLabel = new JLabel("CTD:");
		drugbankCheckBox = new JCheckBox();
	    CTDCheckBox = new JCheckBox();
	    drugbankCheckBox.setText("Disease_Drug");
	    drugbankCheckBox.setSelected(flag);
	    CTDCheckBox.setText("Disease_Drug");
	    CTDCheckBox.setSelected(flag);	    
	    
		drugbankGeneCheckBox = new JCheckBox();
	    CTDGeneCheckBox = new JCheckBox();
	    drugbankGeneCheckBox.setText("Gene_Drug");
	    drugbankGeneCheckBox.setSelected(flag);
	    CTDGeneCheckBox.setText("Gene_Drug");
	    CTDGeneCheckBox.setSelected(flag);
	    
	    uncategoriedCheckBox = new JCheckBox();
	    frequencyCheckBox = new JCheckBox();
	    	   
	    sideEffectLabel = new JLabel("Drug_Side effect:");
	    Object[][] rowData = {{"infrequent", new Boolean(false)}, 
	    		              {"rare", new Boolean(false)},
	    		              {"postmarketing", new Boolean(false)},
	    		              {"potential", new Boolean(false)},
	    		              {"frequent", new Boolean(false)}
	    };
	    tableModel= new DefaultTableModel(rowData, new String[]{" ", "Category"});    		
	    sideEffectTable = new JTable(tableModel){
	    	public Class getColumnClass(int column){	   
			    return getValueAt(0, column).getClass();
		    }
	    };
	    
	    frequencyCheckBox.setText("Frequency");	    
	    frequencyCheckBox.addActionListener(this);
	    uncategoriedCheckBox.setText("Uncategoried");
	    
	    sideEffectTable.getColumnModel().getColumn(0).setPreferredWidth(225);
	    sideEffectTable.getColumnModel().getColumn(1).setPreferredWidth(45);
	    frequencySlider = new JSlider(JSlider.HORIZONTAL, FREQ_MIN, FREQ_MAX, FREQ_INIT);
	    frequencySlider.setMajorTickSpacing(10);
	    frequencySlider.setMinorTickSpacing(1);
	    frequencySlider.setPaintTicks(true);
	    frequencySlider.setPaintLabels(true);	   
	    frequencySlider.setEnabled(false);
	    
	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	    this.setLayout(layout);
	    layout.setHorizontalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            	.addGroup(layout.createSequentialGroup()
	    	             .addComponent(drugBankLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	    	             .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	    	             .addComponent(CTDLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	    	             .addGap(0, 0, Short.MAX_VALUE))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(drugbankCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(CTDCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(0, 0, Short.MAX_VALUE))
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(drugbankGeneCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(CTDGeneCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGap(0, 0, Short.MAX_VALUE))
	                .addComponent(sideEffectLabel)
	                .addComponent(sideEffectTable)
	                .addComponent(frequencyCheckBox)
	                .addComponent(frequencySlider)
	                .addComponent(uncategoriedCheckBox)
	            ))
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(drugBankLabel)	               
	                .addComponent(CTDLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	            	.addComponent(drugbankCheckBox)	               
	                .addComponent(CTDCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	    	        .addComponent(drugbankGeneCheckBox)	               
	    	        .addComponent(CTDGeneCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(sideEffectLabel) 
                .addComponent(sideEffectTable)
                .addComponent(frequencyCheckBox)
                .addComponent(frequencySlider)
                .addComponent(uncategoriedCheckBox)
	        )
	    );
	}//
	
	public void setEnable(boolean flag){
		this.CTDCheckBox.setEnabled(flag);
		this.CTDGeneCheckBox.setEnabled(flag);
		this.drugbankCheckBox.setEnabled(flag);
		this.drugbankGeneCheckBox.setEnabled(flag);
	}//
	
	public String getCategoryIDList(){
		StringBuilder tmp = new StringBuilder();
		int cnt = tableModel.getRowCount();		
		for(int i=0; i<cnt; i++){
			if ((Boolean)tableModel.getValueAt(i, 1)){
				tmp.append(i + ",");				
			}//if
		}//for
		if (tmp.length() > 0)
		    tmp.deleteCharAt(tmp.length()-1);		
		return tmp.toString();		
	}//
	
	public double getPercentage(){
		if (frequencyCheckBox.isSelected())
		    return frequencySlider.getValue();
		else
			return -1.0;
	}//
	
	public boolean getUncategoriedSelection(){
		return uncategoriedCheckBox.isSelected();
	}//

	@Override
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		Object _actionObject = evt.getSource();    	
		// handle Button events		
		if(_actionObject instanceof JCheckBox) {
			JCheckBox _checkBox = (JCheckBox) _actionObject;
			if (_checkBox ==  frequencyCheckBox) {				
				frequencySlider.setEnabled(frequencyCheckBox.isSelected());
			}//
		}//
	}
	
	public void setCheckBoxStatus(boolean flag){
		drugbankGeneCheckBox.setSelected(flag);
		CTDGeneCheckBox.setSelected(flag);
		drugbankCheckBox.setSelected(flag);
		CTDCheckBox.setSelected(flag);	
	}
	

}

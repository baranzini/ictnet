package org.cytoscape.ictnet2.internal.ui;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JComboBox;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.ictnet2.internal.task.creatOntologyTreeTask;
import org.cytoscape.ictnet2.internal.task.creatSimNetTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;



public class SimNetDialog extends JDialog implements ActionListener{	
	private final CyNetworkManager networkManager;	
	private final TaskManager<?, ?> taskManager;
	private final CyApplicationManager appManager;
	final CyNetworkFactory networkFactory;
	final CyNetworkViewFactory networkViewFactory;	
	final CyNetworkViewManager networkViewManager;	
	final VisualMappingManager vmm;
    private JButton createBtn;
    private JButton cancleBtn;
    private JComboBox fieldComboBox;
    private JComboBox filterComboBox;   
    private JLabel desLabel1, desLabel2, fieldLabel, filterLabel;
    private JSeparator jSeparator1, jSeparator2;
    private String[] fieldOpt;    
    
    
	public SimNetDialog(CySwingApplication swingApp, 
			            final CyNetworkManager networkManager,
			            final TaskManager<?,?> taskManager, 
			            final CyApplicationManager appManager,
			            final CyNetworkFactory networkFactory,
			            final CyNetworkViewFactory networkViewFactory, 
			            final CyNetworkViewManager networkViewManager, 
			            final VisualMappingManager vmm,
			            boolean model){
		super(swingApp.getJFrame(), model);
		this.networkManager = networkManager;
		this.taskManager = taskManager;
		this.appManager = appManager;
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.networkViewManager = networkViewManager;
		this.vmm = vmm;
		JFrame parent = swingApp.getJFrame();				
		int x = (int)(parent.getBounds().getCenterX() - 70);
		if (x<0) x=0;
		int y = (int)((parent.getBounds().getCenterY() - 70));
		if (x<0) x=0;
		setBounds(x,y,150,150);
		setTitle("Create Similarity Network");
		initComponents();
	}
	private void initComponents(){
		    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
		    createBtn = new javax.swing.JButton();
		    createBtn.setText("Create");
		    createBtn.addActionListener(this);
		    
		    cancleBtn = new javax.swing.JButton();
		    cancleBtn.setText("Close");
		    cancleBtn.addActionListener(this);
		    
	        jSeparator1 = new javax.swing.JSeparator();
	        jSeparator2 = new javax.swing.JSeparator();
	        
	        fieldOpt = new String[] { "disease", "gene", "tissue", "drug", "miRNA", "side_effect"};
	        
	        fieldComboBox = new JComboBox();
	        fieldComboBox.setModel(new javax.swing.DefaultComboBoxModel(fieldOpt));
	        fieldComboBox.setSelectedIndex(0);
	        
	        filterComboBox = new JComboBox();
	        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	        		new String[] { ">0", ">1", ">2", ">3" }));
	        filterComboBox.setSelectedIndex(0);
	        
	        desLabel1 = new javax.swing.JLabel();
	        desLabel1.setText("Please select the field to create the network");
	        desLabel2 = new javax.swing.JLabel();
	        desLabel2.setText("and the number of shared nodes as filter.");
	        
	        fieldLabel = new javax.swing.JLabel();
	        fieldLabel.setText("Field");
	        
	        filterLabel = new javax.swing.JLabel();
	        filterLabel.setText("Filter");	        
	        
	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(43, 43, 43)
	                        .addComponent(createBtn)
	                        .addGap(18, 18, 18)
	                        .addComponent(cancleBtn))
	                    .addGroup(layout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)))
	                .addContainerGap())
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap(61, Short.MAX_VALUE)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                    .addComponent(fieldLabel)
	                    .addComponent(filterLabel))
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(fieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(43, 43, 43))
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
	                .addContainerGap())
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(desLabel1)
	                .addContainerGap(10, Short.MAX_VALUE))
	             .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(desLabel2)
	                .addContainerGap(10, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(desLabel1)
	                .addComponent(desLabel2)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
	                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                    .addComponent(fieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(fieldLabel))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(filterLabel))
	                .addGap(8, 8, 8)
	                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(createBtn)
	                    .addComponent(cancleBtn)))
	        );

	        pack();
	}
	 public void actionPerformed(java.awt.event.ActionEvent evt){
		 Object _actionObject = evt.getSource();
			// handle Button events
		 if(_actionObject instanceof JButton) {
				JButton _btn = (JButton) _actionObject;
				if (_btn == createBtn) {				
					createBtnActionPerformed(evt);			
				}else{
					cancelBtnActionPerformed(evt);
				}//if-else			 
		 }//
	 }
	 private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {	    	
	    	this.dispose();
	 } 
	 private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {
		 this.dispose();
		 String field =  fieldOpt[fieldComboBox.getSelectedIndex()];
		 
		 int num  = filterComboBox.getSelectedIndex();
		 
		 creatSimNetTask creatSimTask = new creatSimNetTask(networkFactory, networkViewFactory, 
					networkManager, networkViewManager, appManager, vmm,
					field, num);
		 TaskIterator ti = new TaskIterator(creatSimTask);
		 taskManager.execute(ti);
	}  
}//end
package org.cytoscape.ictnet2.internal.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.DBConnector;
import org.cytoscape.ictnet2.internal.DBQueryLibrary;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Drug;
import org.cytoscape.ictnet2.internal.model.Molecule;
import org.cytoscape.ictnet2.internal.task.creatNetworkFromDiseaseTask;
import org.cytoscape.ictnet2.internal.task.creatNetworkFromDrugTask;
import org.cytoscape.ictnet2.internal.task.creatNetworkFromGeneTask;
import org.cytoscape.ictnet2.internal.task.creatOntologyTreeTask;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class ictnetMainPanel extends JPanel implements ActionListener, MouseListener, KeyListener{
	private static final ImageIcon minusIcon = new ImageIcon(
			TreeViewPanel.class.getResource("/images/minus.gif"));
	private static final ImageIcon plusIcon = new ImageIcon(
			TreeViewPanel.class.getResource("/images/plus.gif"));
	
	//private final CyApplicationManager appManager;
	//private final CyRootNetworkManager rootNetworkManager;
	private final CyNetworkManager networkManager;	
	private final TaskManager<?, ?> taskManager;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkViewManager networkViewManager;
	private final VisualMappingManager vmm;
	private final CySwingApplication desktop;
	
	private QueryPanel queryPanel;
	private TreeViewPanel treePanel;
	private EdgeSelectionPanel edgePanel;
	private loadBtnPanel loadPanel;
	private DBConnector dbconnector;
	private boolean treeFlag = false;
	private JSplitPane jSplitPane;
	private JSplitPane topSplitPane;
	private FileUtil fileUtil;
	private File batchFile;
	private int searchTypeIdx = 0; // 0 = disease; 1 = gene; 2 = drug;
			    
	public ictnetMainPanel(
			final TaskManager<?,?> taskManager,
			final CyNetworkFactory networkFactory,
			final CyNetworkViewFactory networkViewFactory,
			final CyNetworkManager networkManager,
			final CyNetworkViewManager networkViewManager,
			final CySwingApplication desktop,
			final VisualMappingManager vmm,
			FileUtil fileServiceRef
			){
		//this.appManager = appManager;
    	//this.rootNetworkManager = rootNetworkManager;
    	this.networkManager = networkManager;
    	this.taskManager = taskManager;	
    	this.networkFactory = networkFactory;
    	this.networkViewFactory = networkViewFactory;
    	this.networkViewManager = networkViewManager;
    	this.vmm = vmm;
    	this.desktop = desktop;
    	this.fileUtil = fileServiceRef;
    	initComponents(); 
    	
    	treePanel.checkTreeManager = new CheckTreeManager(treePanel.ontologyTree, queryPanel);    	
    	if (!treeFlag){
			createDiseaseOntologyTree();			
		}//if    	
		addEventListeners();	
		this.setPreferredSize(new Dimension(340, 1000)); 		
	}//
	
	private void initComponents(){    
	    GridBagConstraints gridBagConstraints;
	    setLayout(new GridBagLayout());	    
	        
	    queryPanel = new QueryPanel("Search by Typing");       
        treePanel = new TreeViewPanel("Tree View of Disease Ontology");
        treePanel.plusIconLabel.setIcon(minusIcon);  	
          	    	   	
    	edgePanel = new EdgeSelectionPanel("Interaction Selection"); 
    	edgePanel.plusIconLabel.setIcon(minusIcon);
    		
		jSplitPane = new JSplitPane();	
	    jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);	    
	    jSplitPane.setTopComponent(treePanel);
	    jSplitPane.setBottomComponent(edgePanel);
	    jSplitPane.setDividerLocation(220);
	    
	    topSplitPane = new JSplitPane();
	    topSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    topSplitPane.setTopComponent(queryPanel);
	    topSplitPane.setBottomComponent(jSplitPane);
	    topSplitPane.setDividerLocation(120);
	    
	    gridBagConstraints = new GridBagConstraints();
    	gridBagConstraints.gridx = 0;    	 
    	gridBagConstraints.fill = gridBagConstraints.BOTH; 
    	gridBagConstraints.weightx = 1.0;
    	gridBagConstraints.weighty = 1.0;
        add(topSplitPane, gridBagConstraints);   
		
		loadPanel = new loadBtnPanel();
		gridBagConstraints = new GridBagConstraints();
    	gridBagConstraints.gridy = 1;    	
    	gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;
    	gridBagConstraints.weightx = 1.0;
		add(loadPanel, gridBagConstraints);		
	}//
	
	private void addEventListeners(){
		loadPanel.loadBtn.addActionListener(this);
		treePanel.plusIconLabel.addMouseListener(this);	
		treePanel.includeDescendant.addMouseListener(this);
		edgePanel.plusIconLabel.addMouseListener(this);
		queryPanel.qTextField.addKeyListener(this);	
		queryPanel.Browse_btn.addActionListener(this);
		queryPanel.typeComboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){				
				int currentSelectedIdx = queryPanel.typeComboBox.getSelectedIndex();
				if (currentSelectedIdx == 2)
					edgePanel.setDrugPanelCheckBox(true);
				else
					edgePanel.setDrugPanelCheckBox(false);
				
				if (searchTypeIdx != currentSelectedIdx){					
					searchTypeIdx = currentSelectedIdx;
					queryPanel.removeAllRowFromTable();
					queryPanel.setDataType(currentSelectedIdx);
					queryPanel.qTextField.setText("");
					edgePanel.edgeFilterPane.setSearchTypeIdx(currentSelectedIdx);
				}//if					
			}//
		});
		
	}//    
	
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		Object _actionObject = evt.getSource();    	
		// handle Button events		
		if(_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;
			if (_btn == loadPanel.loadBtn) {	
				switch(searchTypeIdx){			       
				    case 0: //disease search
				    	DBDiseaseLoadActionPerformed();
				    	break;
				    case 1:
				    	DBGeneLoadActionPerformed();
				    	break;
				    case 2:
				    	DBDrugLoadActionPerformed();
				    	break;
				}//switch
			}else if (_btn == queryPanel.Browse_btn){
				BrowseBtnActionPerformed();
			}//if
		}//if
	}//
	
	private void BrowseBtnActionPerformed() {
		final List<FileChooserFilter> filterCollection = new ArrayList<FileChooserFilter>();
		batchFile = fileUtil.getFile(desktop.getJFrame(), "Import batch search file", FileUtil.LOAD, filterCollection);
		if(batchFile != null){
			Set<String> objectSet = new HashSet<String>();
			try{
				BufferedReader inFile = new BufferedReader(new FileReader(batchFile));
		    	String line = inFile.readLine();
		    	while(line != null){    		
		    		if (line.length() == 0){
		    			continue;
		    		}//if
		    		objectSet.add(line);
		    		line = inFile.readLine();
		    	}//while
		    	inFile.close();
			}catch(Exception ex){
				System.out.println("Failed to read file: "+ex.toString());
				JOptionPane.showMessageDialog(this.getParent(), "Failed to read file.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}//	
			if (objectSet.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No search symbols have been imported.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}//if
			switch(searchTypeIdx){			       
			case 0: //disease search				
				this.DBDiseaseBatchQueryActionPerformed(objectSet);
				break;
			case 1: //gene search				
				this.DBGeneBatchQueryActionPerformed(objectSet);
				break;
			case 2: //drug search			
			    this.DBDrugBatchQueryActionPerformed(objectSet);
			    break;
			}//switch			
		}//
	}
	
	private void DBDiseaseQueryActionPerformed(){		
		String queryName = queryPanel.qTextField.getText();
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Molecule> queryResults = DBQueryLibrary.dbQueryDiseaseName(dbconnector.getConnection(), queryName);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No disease matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{				
				queryPanel.updateTableRow(queryResults, false,  CyAttributeConstants.DISEASE_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	
	private void DBDiseaseBatchQueryActionPerformed(Set<String> qSet){		
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Molecule> queryResults = DBQueryLibrary.dbBatchQueryDiseaseName(dbconnector.getConnection(), qSet);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No disease matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{
				queryPanel.updateTableRow(queryResults, false,  CyAttributeConstants.DISEASE_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	
	private void DBGeneQueryActionPerformed(){		
		String queryName = queryPanel.qTextField.getText();
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Molecule> queryResults = DBQueryLibrary.dbQueryGeneName(dbconnector.getConnection(), queryName);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No gene matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{				
				queryPanel.updateTableRow(queryResults, false,  CyAttributeConstants.GENE_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	
	private void DBGeneBatchQueryActionPerformed(Set<String> qSet){		
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Molecule> queryResults = DBQueryLibrary.dbBatchQueryGeneName(dbconnector.getConnection(), qSet);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No gene matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{
				queryPanel.updateTableRow(queryResults, false,  CyAttributeConstants.GENE_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	
	private void DBDrugQueryActionPerformed(){
		String queryName = queryPanel.qTextField.getText();
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Drug> queryResults = DBQueryLibrary.dbQueryDrugName(dbconnector.getConnection(), queryName);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No drug matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{
				queryPanel.addTableRow(queryResults, false,  CyAttributeConstants.DRUG_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	
	private void DBDrugBatchQueryActionPerformed(Set<String> qSet){		
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);			
		}else{		
			List<Drug> queryResults = DBQueryLibrary.dbBatchQueryDrugName(dbconnector.getConnection(), qSet);
			if (queryResults.size() == 0){
				JOptionPane.showMessageDialog(this.getParent(), "No drug matched", "Error", JOptionPane.ERROR_MESSAGE);
			}else{
				queryPanel.addTableRow(queryResults, false,  CyAttributeConstants.DRUG_CODE);				
			}//if-else			
			dbconnector.shutDown();
		}//if-else	
	}//
	private void DBDiseaseLoadActionPerformed(){
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}//if		
		
		HashMap<Integer, Disease> treeNodeMap = treePanel.checkTreeManager.getSelectedTreeNodes();
		List<Disease> diseaseList = queryPanel.getTableCheckedRowsAsDiseases(treeNodeMap);
		if ((diseaseList.size()>50 && edgePanel.edgeFilterPane.getPPISelectedIndex() > 0) || diseaseList.size()>100){
			JOptionPane.showMessageDialog(this.getParent(), "The size of network may be big, so it may take minutes to download. Please be patient!", "Warnning", JOptionPane.WARNING_MESSAGE);
		}//
		if (diseaseList.size()>0){
			creatNetworkFromDiseaseTask creatNetTask = new creatNetworkFromDiseaseTask(networkFactory, networkViewFactory, 
					networkManager, networkViewManager, vmm,
					dbconnector, diseaseList,
					edgePanel.edgeFilterPane.isOMIMSelected(), edgePanel.edgeFilterPane.isEFOSelected(),
					edgePanel.edgeFilterPane.isMEDICSelected(), edgePanel.edgeFilterPane.isPPISelected(),
					edgePanel.edgeFilterPane.isCTDSelected(), edgePanel.edgeFilterPane.isDrugBankSelected(),
					edgePanel.edgeFilterPane.isCTDGeneSelected(), edgePanel.edgeFilterPane.isDrugBankGeneSelected(),
					edgePanel.edgeFilterPane.isDiseaseTissueSelected(), edgePanel.edgeFilterPane.isGeneTissueSelected(),
					edgePanel.edgeFilterPane.ismiRNASelected(), edgePanel.edgeFilterPane.getSideEffectSelection(),
					edgePanel.edgeFilterPane.getSideEffectPercentage(), edgePanel.edgeFilterPane.getUncategorizedSelection(),
					edgePanel.edgeFilterPane.getPPISelectedIndex(), edgePanel.edgeFilterPane.getConfidenceIndex());
			TaskIterator ti = new TaskIterator(creatNetTask);		
	    	taskManager.execute(ti);
		}else{
			JOptionPane.showMessageDialog(this.getParent(), "No disease entry checked!", "Error", JOptionPane.ERROR_MESSAGE);
		}//if-else
		
		if (dbconnector != null)
		    dbconnector.shutDown();
	}
	
	private void DBGeneLoadActionPerformed(){
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}//if		
		
		Set<Integer> geneIDs = queryPanel.getCheckedIntIDs();
		if ((geneIDs.size()>500 && edgePanel.edgeFilterPane.getPPISelectedIndex() > 0) || geneIDs.size()>1000){
			JOptionPane.showMessageDialog(this.getParent(), "The size of network may be big, so it may take minutes to download. Please be patient!", "Warnning", JOptionPane.WARNING_MESSAGE);
		}//
		if (geneIDs.size()>0){
			creatNetworkFromGeneTask creatNetTask = new creatNetworkFromGeneTask(networkFactory, networkViewFactory, 
					networkManager, networkViewManager, vmm,
					dbconnector, geneIDs, treePanel,
					edgePanel.edgeFilterPane.isOMIMSelected(), edgePanel.edgeFilterPane.isEFOSelected(),
					edgePanel.edgeFilterPane.isMEDICSelected(), edgePanel.edgeFilterPane.isPPISelected(),
					edgePanel.edgeFilterPane.isCTDSelected(), edgePanel.edgeFilterPane.isDrugBankSelected(),
					edgePanel.edgeFilterPane.isCTDGeneSelected(), edgePanel.edgeFilterPane.isDrugBankGeneSelected(),
					edgePanel.edgeFilterPane.isDiseaseTissueSelected(), edgePanel.edgeFilterPane.isGeneTissueSelected(),
					edgePanel.edgeFilterPane.ismiRNASelected(),  edgePanel.edgeFilterPane.getSideEffectSelection(),
					edgePanel.edgeFilterPane.getSideEffectPercentage(),edgePanel.edgeFilterPane.getUncategorizedSelection(),
					edgePanel.edgeFilterPane.getPPISelectedIndex(), edgePanel.edgeFilterPane.getConfidenceIndex());
			TaskIterator ti = new TaskIterator(creatNetTask);		
	    	taskManager.execute(ti);
		}else{
			JOptionPane.showMessageDialog(this.getParent(), "No gene entry checked!", "Error", JOptionPane.ERROR_MESSAGE);
		}//if-else
		
		if (dbconnector != null)
		    dbconnector.shutDown();
	}//
	
	private void DBDrugLoadActionPerformed(){
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(this.getParent(), "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}//if		
		
		List<Drug> drugList = queryPanel.getTableCheckedRowsAsDrugs();
		if ((drugList.size()>100 && edgePanel.edgeFilterPane.getPPISelectedIndex() > 0) || drugList.size()>200){
			JOptionPane.showMessageDialog(this.getParent(), "The size of network may be big, so it may take minutes to download. Please be patient!", "Warnning", JOptionPane.WARNING_MESSAGE);
		}//
		if (drugList.size()>0){
			creatNetworkFromDrugTask creatNetTask = new creatNetworkFromDrugTask(networkFactory, networkViewFactory, 
					networkManager, networkViewManager, vmm,
					dbconnector, drugList, treePanel,
					edgePanel.edgeFilterPane.isOMIMSelected(), edgePanel.edgeFilterPane.isEFOSelected(),
					edgePanel.edgeFilterPane.isMEDICSelected(), edgePanel.edgeFilterPane.isPPISelected(),
					edgePanel.edgeFilterPane.isCTDSelected(), edgePanel.edgeFilterPane.isDrugBankSelected(),
					edgePanel.edgeFilterPane.isCTDGeneSelected(), edgePanel.edgeFilterPane.isDrugBankGeneSelected(),
					edgePanel.edgeFilterPane.isDiseaseTissueSelected(), edgePanel.edgeFilterPane.isGeneTissueSelected(),
					edgePanel.edgeFilterPane.ismiRNASelected(),  edgePanel.edgeFilterPane.getSideEffectSelection(),
					edgePanel.edgeFilterPane.getSideEffectPercentage(),edgePanel.edgeFilterPane.getUncategorizedSelection(),
					edgePanel.edgeFilterPane.getPPISelectedIndex(), edgePanel.edgeFilterPane.getConfidenceIndex());
			TaskIterator ti = new TaskIterator(creatNetTask);		
	    	taskManager.execute(ti);
		}else{
			JOptionPane.showMessageDialog(this.getParent(), "No drug entry checked!", "Error", JOptionPane.ERROR_MESSAGE);
		}//if-else
		
		if (dbconnector != null)
		    dbconnector.shutDown();
	}//

	@Override
	public void mouseClicked(MouseEvent e) {
		Object _actionObject = e.getSource();
		// click on the plus/minus sign to hide/show advancedPanel 
		if (_actionObject instanceof JLabel) {
			JLabel _lbl = (JLabel) _actionObject;
				
			if (_lbl ==  treePanel.plusIconLabel) {
				if ( treePanel.treeViewPanel.isVisible()) {
					treePanel.treeViewPanel.setVisible(false);
					treePanel.plusIconLabel.setIcon(plusIcon);					
					jSplitPane.setDividerLocation(40);					
				}else {
					treePanel.treeViewPanel.setVisible(true);
					treePanel.plusIconLabel.setIcon(minusIcon);
					jSplitPane.setDividerLocation(220);
				}//if-else
				revalidate();
				repaint();
			}else if (_lbl ==  edgePanel.plusIconLabel) {
				if ( edgePanel.edgeSelectionScrollPane.isVisible()) {
					edgePanel.edgeSelectionScrollPane.setVisible(false);	        					
					edgePanel.plusIconLabel.setIcon(plusIcon);
				}else {
					edgePanel.edgeSelectionScrollPane.setVisible(true);
					edgePanel.plusIconLabel.setIcon(minusIcon);
				}//if-else
				revalidate();
				repaint();
			}//if
		}else if (_actionObject instanceof JCheckBox){
			JCheckBox _cBox = (JCheckBox) _actionObject;
			if (_cBox == treePanel.includeDescendant){				
				treePanel.checkTreeManager.setSelectDescendants(treePanel.includeDescendant.isSelected());
			}//
		}//if-else		
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

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int code = e.getKeyCode();
		// if enter key
		if(code == 10){			
			switch(searchTypeIdx){			       
			case 0: //disease search				
				this.DBDiseaseQueryActionPerformed();
				break;
			case 1: //gene search				
				this.DBGeneQueryActionPerformed();
				break;
			case 2: //drug search			
			    this.DBDrugQueryActionPerformed();
			    break;
			}//switch			
		}//if
	}
	
	private void createDiseaseOntologyTree(){
		if (dbconnector == null){
			dbconnector = new DBConnector();
		}//if
		
		if (dbconnector.getConnection() == null){
			JOptionPane.showMessageDialog(null, "Failed to connect the sever!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}else{	
			creatOntologyTreeTask creatTreeTask = new creatOntologyTreeTask(dbconnector, treePanel.ontologyTree);
			TaskIterator ti = new TaskIterator(creatTreeTask);		
	    	taskManager.execute(ti);
	    	queryPanel.setTreeManager(treePanel.checkTreeManager);    	    
    	    treeFlag = true;
		}//
	}	
	
   	/*	
    public static void main(String[] args){
	    JFrame frame = new JFrame("test");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new ictnetMainPanel());
	    frame.setVisible(true);		 
    }//
    */	

}

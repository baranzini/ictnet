package org.cytoscape.ictnet2.internal.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.model.Disease;
import org.cytoscape.ictnet2.internal.model.Drug;
import org.cytoscape.ictnet2.internal.model.Molecule;

public class QueryPanel extends JPanel implements MouseListener{	
	JPanel inputPanel;
	JPopupMenu popMenu;
	JMenuItem checkSelectedItem;
	JMenuItem delSelectedItem;
	JMenuItem delUnSelectedItem;
	JMenuItem delCheckedItem;
	JMenuItem delUnCheckedItem;
	JMenuItem delAllItem;
	JLabel textLabel;
	JTextField qTextField;
	JComboBox typeComboBox;
	JButton Browse_btn;
	JTable qTable;	
	JScrollPane scrollPane;		
	DefaultTableModel tmodel;
	CheckTreeManager treeManager = null;
	private int dataType = 0; // 0 = disease; 1 = gene; 2 = drug;
	
	public QueryPanel(String title){
		super();		
		initComponents(title);
	}//	
	
	private void initComponents(String title){
		TitledBorder titled = BorderFactory.createTitledBorder(title);
		titled.setTitleJustification(TitledBorder.CENTER);
		this.setBorder(titled);		
		inputPanel = new JPanel();
		textLabel = new JLabel("Search: ");
		String[] nodeTypeStrs = {"disease","gene","drug"};
		typeComboBox = new JComboBox(nodeTypeStrs);
		typeComboBox.setSelectedIndex(0);	
    	
    	//popup menu to remove row in the JTable
    	popMenu = new JPopupMenu();
    	checkSelectedItem = new JMenuItem("Check selected row(s)");
    	checkSelectedItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		checkSelectedRowFromTable();    		 	    		 
	    	}//
	    });    	
        popMenu.add(checkSelectedItem);
    	
    	delSelectedItem = new JMenuItem("Remove selected row(s)");
    	delSelectedItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		removeSelectedRowFromTable();    		 	    		 
	    	}//
	    });    	
    	popMenu.add(delSelectedItem);
    	
    	delUnSelectedItem = new JMenuItem("Remove unselected row(s)");
    	delUnSelectedItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		removeNonSelectedRowFromTable();    		 	    		 
	    	}//
	    });    	
    	popMenu.add(delUnSelectedItem);
    	
    	delCheckedItem = new JMenuItem("Remove checked row(s)");
    	delCheckedItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		removeCheckedRowFromTable();    		 	    		 
	    	}//
	    });    	
    	popMenu.add(delCheckedItem);
    	
    	delUnCheckedItem = new JMenuItem("Remove unchecked row(s)");
    	delUnCheckedItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		removeUnCheckedRowFromTable();    		 	    		 
	    	}//
	    });    	
    	popMenu.add(delUnCheckedItem);
    	
    	
    	delAllItem = new JMenuItem("Remove all");
    	delAllItem.addMouseListener(new MouseAdapter(){
	    	public void mouseReleased(MouseEvent e){
	    		removeAllRowFromTable();   		 	    		 
	    	}//
	    });    	
    	popMenu.add(delAllItem);
    	
    	qTextField = new JTextField();  	
    	Browse_btn = new JButton();
    	Browse_btn.setText("...");
    	tmodel = new DefaultTableModel(new Object[][]{}, new String[]{" ", "ID", "Title"});
    	TableRowSorter<DefaultTableModel> tableSorter = new TableRowSorter<DefaultTableModel>(tmodel);    	
    	qTable = new JTable(tmodel){
    		public Class getColumnClass(int column){
    			return getValueAt(0, column).getClass();
    		}//
    	};   	
    	qTable.setRowSorter(tableSorter);
    	qTable.addMouseListener(this);    	
    	qTable.getColumnModel().getColumn(0).setPreferredWidth(25);
    	qTable.getColumnModel().getColumn(1).setPreferredWidth(45);
    	qTable.getColumnModel().getColumn(2).setPreferredWidth(250);
    	scrollPane = new javax.swing.JScrollPane();
    	scrollPane.setOpaque(true);
    	scrollPane.setViewportView(qTable);
    	scrollPane.setBackground(Color.WHITE);
    	
    	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(inputPanel);
    	inputPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)                   	
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(textLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)                            
                            .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(qTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2,2,2)
                            .addComponent(Browse_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        		))                    
                         .addGap(2, 2, 2))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()                  
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(qTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Browse_btn)
                        .addComponent(typeComboBox)
                        .addComponent(textLabel))                   
                    .addGap(2, 2, 2))
            );       
        this.setMinimumSize(new Dimension(340, 48));
    	this.setPreferredSize(new Dimension(340, 120));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;   
        gridBagConstraints.weightx = 1.0; 
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;     	
        gridBagConstraints.anchor = gridBagConstraints.NORTHWEST;
        add(inputPanel, gridBagConstraints); 
      
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;     
        gridBagConstraints.weighty = 0.99;
        gridBagConstraints.fill = gridBagConstraints.BOTH;     	
        add(scrollPane, gridBagConstraints);             
	}//	
	
	private void updateTableTitle(){		
		int count = tmodel.getRowCount();		
		if (count == 0){
			qTable.getColumnModel().getColumn(2).setHeaderValue("Title");
			qTable.revalidate();
			qTable.updateUI();
			return;
		}//if
		int selectedCnt = qTable.getSelectedRowCount();		
		int checkedCnt = getCheckedCount();
		if (selectedCnt > 0){			
			if (count == 1){
				qTable.getColumnModel().getColumn(2).setHeaderValue("Title ("+count+" row,"+ checkedCnt + " checked,"+ selectedCnt +" selected)");
			}else{
				qTable.getColumnModel().getColumn(2).setHeaderValue("Title ("+count+" rows, "+ checkedCnt + " checked,"+ selectedCnt +" selected)");
			}//if-else	
			qTable.revalidate();
			qTable.updateUI();
		}else{				
			if (count == 1){
				qTable.getColumnModel().getColumn(2).setHeaderValue("Title ("+count+" row, "+ checkedCnt + "checked)");
			}else{
				qTable.getColumnModel().getColumn(2).setHeaderValue("Title ("+count+" rows, "+ checkedCnt + "checked)");
			}//if-else	
			qTable.revalidate();
			qTable.updateUI();
		}//if-else
	}
	
	public void setDataType(int code){
		this.dataType = code;
	}
	
	public void mouseClicked(MouseEvent e){	
		Object _actionObject = e.getSource();
		// click on the plus/minus sign to hide/show advancedPanel 
		if (_actionObject instanceof JTable) {
			if (qTable == _actionObject){
				int column = qTable.getSelectedColumn();
				if (column == 0 && dataType == CyAttributeConstants.DISEASE_CODE){
					int currentRow = qTable.getSelectedRow();
					if ((Boolean)qTable.getValueAt(currentRow, 0)){
						mapCheckedRowFromTableToTree();
					}else{
						uncheckRowFromTableToTree((Integer) qTable.getValueAt(currentRow, 1));
					}//if-else				 
					updateTableTitle();
				}//
			}//			
		}//	
		
	}//
	public void mousePressed(MouseEvent e){
		
	}//
	public void mouseReleased(MouseEvent e){		
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
                && !e.isControlDown() && !e.isShiftDown()) {			
			popMenu.show(qTable, e.getX(), e.getY());			
		}//if		
	}//
	public void mouseEntered(MouseEvent e){
		
    }//

    public void mouseExited(MouseEvent e){
    	
    }//

    public void mouseDragged(MouseEvent e){
    	
    }//
    public void mouseMoved(MouseEvent e){
    	
    }//    
    
    private void checkSelectedRowFromTable(){    	
		int[] selectedIdxs = qTable.getSelectedRows();	
		Arrays.sort(selectedIdxs);
		for(int i = selectedIdxs.length-1; i >=0; i--){			
			tmodel.setValueAt(true, selectedIdxs[i], 0);
		}//for
		if (dataType == CyAttributeConstants.DISEASE_CODE){
		    mapCheckedRowFromTableToTree();	
		}//if
		updateTableTitle();
    }//
	private void removeSelectedRowFromTable(){		
		int[] selectedIdxs = qTable.getSelectedRows();		
		Arrays.sort(selectedIdxs);
		for(int i = selectedIdxs.length-1; i >=0; i--){		
			tmodel.removeRow(selectedIdxs[i]);
		}//for		
		updateTableTitle();
	}	
	
	private void removeNonSelectedRowFromTable(){
		int rowCount = qTable.getRowCount();
		int[] selectedIdxs = qTable.getSelectedRows();
		Set<Integer> selectedSet = new HashSet<Integer>();
		for(int id: selectedIdxs){
			selectedSet.add(id);			
		}//for	
		for(int rowNum = rowCount -1; rowNum >= 0; rowNum--){
			if (!selectedSet.contains(rowNum))
				tmodel.removeRow(rowNum);
		}//for	
		qTable.clearSelection();
		updateTableTitle();
	}//
	
	private void removeCheckedRowFromTable(){
		int rowCount = qTable.getRowCount();		
		for(int rowNum = rowCount -1; rowNum >= 0; rowNum--){
			if ((Boolean)qTable.getValueAt(rowNum, 0))
				tmodel.removeRow(rowNum);
		}//for		
		updateTableTitle();
	}	
	
	private void removeUnCheckedRowFromTable(){
		int rowCount = qTable.getRowCount();		
		for(int rowNum = rowCount -1; rowNum >= 0; rowNum--){
			if (!(Boolean)qTable.getValueAt(rowNum, 0))
				tmodel.removeRow(rowNum);
		}//for		
		updateTableTitle();
	}	
	
	public void removeAllRowFromTable(){		
		int cnt = tmodel.getRowCount();		
		for(int i=cnt-1; i>=0; i--){			
			tmodel.removeRow(i);
		}//for
		if (dataType == CyAttributeConstants.DISEASE_CODE){
			mapCheckedRowFromTableToTree();				
		}//remove selected nodes in the disease ontology tree		
        updateTableTitle();       
	}//
	
	public void mapCheckedRowFromTableToTree(){		
		HashSet<Integer> checkedDisIDs = this.getCheckedIntIDs();		
		if (treeManager != null){
			treeManager.findMatchedTreeNodes(checkedDisIDs);			
		}//		
	}//
	
	public void uncheckRowFromTableToTree(int disID){
		if (treeManager != null){
			treeManager.removeTreeNode(disID);			
		}//
	}
	
	public List<Disease> getTableCheckedRowsAsDiseases(HashMap<Integer, Disease> disMap){
		int cnt = tmodel.getRowCount();
		List<Disease> results = new ArrayList<Disease>();
		for(int i=0; i<cnt; i++){
			if ((Boolean) tmodel.getValueAt(i, 0)){
				int disID = (Integer)tmodel.getValueAt(i, 1);
				if (disMap.containsKey(disID)){				
					results.add(disMap.get(disID));
				}else{
				    Disease tmp = new Disease(disID, (String)tmodel.getValueAt(i, 2));	
				    results.add(tmp);
				}//if-else
			}//if			
		}//for
		return results;		
	}//
	
	public List<Drug> getTableCheckedRowsAsDrugs(){
		int cnt = tmodel.getRowCount();
		List<Drug> results = new ArrayList<Drug>();
		for(int i=0; i<cnt; i++){
			if ((Boolean) tmodel.getValueAt(i, 0)){
				String meshID = (String)tmodel.getValueAt(i, 1);
				String name = (String)tmodel.getValueAt(i, 2);	
				Drug newDrug = new Drug(meshID, name);
				results.add(newDrug);				
			}//if			
		}//for
		return results;	
	}//
	
	public HashSet<Integer> getCheckedIntIDs(){
		int cnt = tmodel.getRowCount();
		HashSet<Integer> tmp = new HashSet<Integer>();
		for(int i=0; i<cnt; i++){
			if ((Boolean)tmodel.getValueAt(i, 0)){
			    tmp.add((Integer)tmodel.getValueAt(i, 1));			    
			}//
		}//for
		return tmp;		
	}
	
	public HashSet<Integer> getIDs(){
		int cnt = tmodel.getRowCount();
		HashSet<Integer> tmp = new HashSet<Integer>();
		for(int i=0; i<cnt; i++){
			tmp.add((Integer)tmodel.getValueAt(i, 1));			
		}//for
		return tmp;		
	}//
	
	public HashSet<String> getStringIDs(){
		int cnt = tmodel.getRowCount();
		HashSet<String> tmp = new HashSet<String>();
		for(int i=0; i<cnt; i++){
			tmp.add((String)tmodel.getValueAt(i, 1));			
		}//for
		return tmp;
	}
	
	public int getCheckedCount(){
		int cnt = tmodel.getRowCount();
		int count = 0;
		for(int i=0; i<cnt; i++){
			if ((Boolean)tmodel.getValueAt(i, 0))
				count ++;
		}//for
		return count;
	}
	public void addTableRow(List<Drug> drugList, boolean flag, int type){
		if (dataType != type){			
			qTable.removeAll();	
			if (dataType == CyAttributeConstants.DISEASE_CODE){
				mapCheckedRowFromTableToTree();				
			}//remove selected nodes in the disease ontology tree
			dataType = type;
		}//if
		qTable.clearSelection();
		this.removeUnCheckedRowFromTable();
		HashSet<String> existingIDs = this.getStringIDs();
		for(Drug drug: drugList){
			String newID = drug.getMeshID();
			if (existingIDs.contains(newID))
				continue;
			else
			    addOneRow(drug, flag);					
		}//for
		updateTableTitle();	
	}
	
	public void updateTableRow(Collection<Molecule> oList, boolean flag, int type){		
		if (dataType != type){			
			qTable.removeAll();	
			if (dataType == CyAttributeConstants.DISEASE_CODE){
				mapCheckedRowFromTableToTree();				
			}//remove selected nodes in the disease ontology tree
			dataType = type;
		}//if
		qTable.clearSelection();
		this.removeUnCheckedRowFromTable();
		HashSet<Integer> existingIDs = this.getIDs();
		for(Molecule mol: oList){
			int newID = mol.getID();
			if (existingIDs.contains(newID))
				continue;
			else
			    addOneRow(mol, flag);					
		}//for
		updateTableTitle();		
	}//
	
	
	public void addTableRow(Collection<Molecule> oList, boolean flag, int type){		
		if (dataType != type){			
			qTable.removeAll();	
			if (dataType == CyAttributeConstants.DISEASE_CODE){
				mapCheckedRowFromTableToTree();				
			}//remove selected nodes in the disease ontology tree
			//dataType = type;
			return;
		}//if
		qTable.clearSelection();		
		HashSet<Integer> existingIDs = this.getIDs();
		for(Molecule mol: oList){
			int newID = mol.getID();
			if (existingIDs.contains(newID))
				continue;
			else
			    addOneRow(mol, flag);					
		}//for
		updateTableTitle();		
	}//	
	
	
	public void setTreeManager(CheckTreeManager tManager){		
		treeManager = tManager;
	}//
	
	public void removeTableRow(Collection<Integer> disIDs){
		int cnt = tmodel.getRowCount();		
		for(int i=cnt-1; i>=0; i--){
			int disID = (Integer)tmodel.getValueAt(i, 1);
			if (disIDs.contains(disID))
			    tmodel.removeRow(i);
		}//for
		updateTableTitle();		
	}//
	
	public void addOneRow(Molecule oDis, boolean flag){
		Vector row = new Vector();
		row.add(flag);
		row.add(oDis.getID());
		row.add(oDis.getName());
		tmodel.insertRow(0,row);		
	}//
	
	public void addOneRow(Drug drug, boolean flag){
		Vector row = new Vector();
		row.add(flag);
		row.add(drug.getMeshID());
		row.add(drug.getName());
		tmodel.insertRow(0,row);	
	}

}

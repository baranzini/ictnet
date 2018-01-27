package org.cytoscape.ictnet2.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.ictnet2.internal.ui.SimNetDialog;

public class SimNetSubMenuItemAction extends AbstractCyAction {
	private final CySwingApplication swingApp; 
    private final CyNetworkManager networkManager;
    private final TaskManager<?,?> taskManager; 
    private final CyApplicationManager appManager;
    private final CyNetworkFactory networkFactory;
    private final CyNetworkViewFactory networkViewFactory; 
    private final CyNetworkViewManager networkViewManager; 
    private final VisualMappingManager vmm;
    private SimNetDialog simNetDialog = null;
    
    public SimNetSubMenuItemAction(final CySwingApplication swingApp, 
    		final CyNetworkManager networkManager, 
    		final TaskManager<?,?> taskManager, 
            final CyApplicationManager appManager, 
            final CyNetworkFactory networkFactory, 
            final CyNetworkViewFactory networkViewFactory, 
            final CyNetworkViewManager networkViewManager, 
            final VisualMappingManager vmm){
    	super("Create Similarity Network");
    	this.swingApp = swingApp;
    	this.networkManager = networkManager;
    	this.taskManager = taskManager;
    	this.appManager = appManager;
    	this.networkFactory = networkFactory;
    	this.networkViewFactory = networkViewFactory;
    	this.networkViewManager = networkViewManager;
    	this.vmm = vmm;
    	setPreferredMenu("Apps.iCTNet");
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (simNetDialog == null){
			simNetDialog = new SimNetDialog(swingApp, networkManager, taskManager, appManager, 
					networkFactory, networkViewFactory, networkViewManager, vmm, 
					true);
		}//if
		simNetDialog.pack();
		simNetDialog.setVisible(true);
	}
}

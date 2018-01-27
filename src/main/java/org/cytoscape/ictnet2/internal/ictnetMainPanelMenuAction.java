package org.cytoscape.ictnet2.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.ictnet2.internal.ui.ictnetMainPanel;

public class ictnetMainPanelMenuAction extends AbstractCyAction {
	private static final long serialVersionUID = 3453434324343552L;
	private static final String APP_MENU_TITLE = "Main Panel";
	private static final String PARENT_MENU = "Apps.iCTNet";
	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelWest;	
	private final ictnetMainPanel mainPanel;
	
	public ictnetMainPanelMenuAction(CySwingApplication swingApp, ictnetMainPanel mainPanel){
		super(APP_MENU_TITLE);
		System.out.println("iCTNet App started");
		setPreferredMenu(PARENT_MENU);
		this.desktopApp = swingApp;
		this.cytoPanelWest = this.desktopApp.getCytoPanel(CytoPanelName.WEST);
		this.mainPanel = mainPanel;
	}
	public void actionPerformed(ActionEvent e) {
		// If the state of the cytoPanelEast is HIDE, show it
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		// Select my panel
		int index = cytoPanelWest.indexOfComponent(mainPanel);
		if (index == -1) {
			return;
		}
		cytoPanelWest.setSelectedIndex(index);
		mainPanel.setVisible(true);		
	}
	
	public boolean isInMenuBar() {
		return true;
	}
	
}
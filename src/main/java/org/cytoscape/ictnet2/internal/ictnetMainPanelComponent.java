package org.cytoscape.ictnet2.internal;

import java.awt.Component;
import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import org.cytoscape.ictnet2.internal.ui.ictnetMainPanel;

public class ictnetMainPanelComponent implements CytoPanelComponent{
	ictnetMainPanel ictnetPanel;
	
	public ictnetMainPanelComponent(ictnetMainPanel ictnetPanel){
		this.ictnetPanel = ictnetPanel;
	}
	
	public Component getComponent() {
		return ictnetPanel;
	}//
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}//

	public String getTitle() {
		return "iCTNet Panel";
	}//
	
	public Icon getIcon() {
		return null;
	}//

}

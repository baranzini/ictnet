package org.cytoscape.ictnet2.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyVersion;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;
import org.cytoscape.ictnet2.internal.ui.ictnetMainPanel;

public class CyActivator extends AbstractCyActivator {
	public CyActivator(){		
		super();		
	}//

	@Override
	public void start(BundleContext bc) throws Exception {		
		CySwingApplication swingAppServiceRef = getService(bc, CySwingApplication.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		CyRootNetworkManager cyRootNetworkManagerServiceRef = getService(bc, CyRootNetworkManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);				
		CyServiceRegistrar cyServiceRegistrarServiceRef = getService(bc,CyServiceRegistrar.class);		
		TaskManager taskManagerServiceRef = getService(bc,TaskManager.class);	
		DialogTaskManager dialogManagerServiceRef = getService(bc, DialogTaskManager.class);
		CyVersion cytoscapeVersionService = getService(bc,CyVersion.class);
		CyNetworkFactory networkFactory = getService(bc, CyNetworkFactory.class);
		CyNetworkViewFactory networkViewFactory = getService(bc, CyNetworkViewFactory.class);
		CyNetworkViewManager networkViewManager = getService(bc, CyNetworkViewManager.class);
		VisualMappingManager visualMapManagerServiceRef = getService(bc, VisualMappingManager.class);
		VisualStyleFactory visualStyleFactoryServiceRef = getService(bc, VisualStyleFactory.class);
		VisualMappingFunctionFactory discreteMappingFactory = getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
		VisualMappingFunctionFactory passthroughMappingFactory = getService(bc, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
		CyLayoutAlgorithmManager layoutManagerServiceRef = getService(bc, CyLayoutAlgorithmManager.class);
		FileUtil fileServiceRef = getService(bc,FileUtil.class);	
		
		ServicesUtil.cySwingApplicationServiceRef = swingAppServiceRef;
		ServicesUtil.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
		ServicesUtil.cyRootNetworkManagerServiceRef = cyRootNetworkManagerServiceRef;
		ServicesUtil.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;		
		ServicesUtil.cyServiceRegistrarServiceRef = cyServiceRegistrarServiceRef;			
		ServicesUtil.taskManagerServiceRef = taskManagerServiceRef;		
		ServicesUtil.cytoscapeVersionService = cytoscapeVersionService;
		ServicesUtil.cyNetworkFactory = networkFactory;
		ServicesUtil.networkViewFactory = networkViewFactory;
		ServicesUtil.networkViewManager = networkViewManager;
		ServicesUtil.visualMapManagerServiceRef = visualMapManagerServiceRef;
		ServicesUtil.visualStyleFactoryServiceRef = visualStyleFactoryServiceRef;
		ServicesUtil.discreteMappingFactory = discreteMappingFactory;		
		ServicesUtil.layoutManagerServiceRef = layoutManagerServiceRef;
		ServicesUtil.passthroughMappingFactory = passthroughMappingFactory;
		
		ictnetMainPanel ictnetPanel = new ictnetMainPanel(taskManagerServiceRef, 
				networkFactory, 
				networkViewFactory, 
				cyNetworkManagerServiceRef, 
				networkViewManager, 
				swingAppServiceRef, 
				visualMapManagerServiceRef, 
				fileServiceRef);		
		ictnetMainPanelComponent mainPanelComponent = new ictnetMainPanelComponent(ictnetPanel);
		ictnetMainPanelMenuAction mainPanelAction = new ictnetMainPanelMenuAction(swingAppServiceRef, ictnetPanel);
		SimNetSubMenuItemAction simNetSubMenuItemAction = new SimNetSubMenuItemAction(swingAppServiceRef, 
				cyNetworkManagerServiceRef, 
				taskManagerServiceRef,
				cyApplicationManagerServiceRef, 
				networkFactory,
				networkViewFactory, 
				networkViewManager,
				visualMapManagerServiceRef
				);
		
		registerService(bc, mainPanelComponent, CytoPanelComponent.class, new Properties());		
		registerService(bc, mainPanelAction, CyAction.class, new Properties());	
		registerService(bc, simNetSubMenuItemAction, CyAction.class, new Properties());
	}

}

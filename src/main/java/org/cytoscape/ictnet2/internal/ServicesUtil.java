package org.cytoscape.ictnet2.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyVersion;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;

public class ServicesUtil {	
	public static CySwingApplication cySwingApplicationServiceRef;	
	public static CyApplicationManager cyApplicationManagerServiceRef;	
	public static CyRootNetworkManager cyRootNetworkManagerServiceRef;
	public static CyNetworkManager cyNetworkManagerServiceRef;
	public static CyServiceRegistrar cyServiceRegistrarServiceRef;	
	public static TaskManager taskManagerServiceRef;	
	public static CyVersion cytoscapeVersionService;
	public static CyNetworkFactory cyNetworkFactory;
	public static CyNetworkViewFactory networkViewFactory;
	public static CyNetworkViewManager networkViewManager;
	public static VisualMappingManager visualMapManagerServiceRef;
	public static VisualStyleFactory visualStyleFactoryServiceRef;
	public static VisualMappingFunctionFactory discreteMappingFactory;
	public static VisualMappingFunctionFactory passthroughMappingFactory;
	public static CyLayoutAlgorithmManager layoutManagerServiceRef;	
}
package org.cytoscape.ictnet2.internal.model;

import java.awt.Color;
import java.awt.Paint;
import java.util.Set;

import org.cytoscape.ictnet2.internal.CyAttributeConstants;
import org.cytoscape.ictnet2.internal.ServicesUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

public class ictnetVisualStyle {	
	private static final Color COLOR_GENE = new Color(0, 204, 0);
	private static final Color COLOR_DISEASE = new Color(255, 51, 230);
	private static final Color COLOR_TISSUE = new Color(204, 204, 0);
	private static final Color COLOR_DRUG = new Color(0, 51, 255);
	private static final Color COLOR_SIDE_EFFECT = new Color(180, 80, 200);
	private static final Color COLOR_MIRNA = new Color(255, 100, 100);
	
	private final VisualStyleFactory visualStyleFactory;
    private final VisualMappingFunctionFactory discreteMappingFactory;
    private final VisualMappingFunctionFactory passthroughMappingFactory;
      
    public ictnetVisualStyle(){
    	this.visualStyleFactory = ServicesUtil.visualStyleFactoryServiceRef;
    	this.discreteMappingFactory = ServicesUtil.discreteMappingFactory; 
    	this.passthroughMappingFactory = ServicesUtil.passthroughMappingFactory;    	
    }

	
	public  VisualStyle getVisualStyle(){	
		final VisualStyle defStyle = visualStyleFactory.createVisualStyle(CyAttributeConstants.DEF_VS_NAME);
		final Set<VisualPropertyDependency<?>> dependencys = defStyle.getAllVisualPropertyDependencies();
        // Disable add dependencys
        for(VisualPropertyDependency<?> dep: dependencys) {
            dep.setDependency(false);
        }//for
		
        //set up the background color
        defStyle.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, Color.gray);
        //node label
        PassthroughMapping pMapping = (PassthroughMapping) passthroughMappingFactory
        		.createVisualMappingFunction(CyNetwork.NAME, String.class, BasicVisualLexicon.NODE_LABEL);
        defStyle.addVisualMappingFunction(pMapping);                        
        
        // node color
        final DiscreteMapping<String, Paint> nodeColorMapping = (DiscreteMapping<String, Paint>) discreteMappingFactory
                .createVisualMappingFunction(CyAttributeConstants.ATTR_TYPE, String.class, BasicVisualLexicon.NODE_FILL_COLOR);
       
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_GENE, COLOR_GENE);
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_DISEASE, COLOR_DISEASE);
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_TISSUE, COLOR_TISSUE);
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_DRUG, COLOR_DRUG);
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_SIDE_EFFECT, COLOR_SIDE_EFFECT);
		nodeColorMapping.putMapValue(CyAttributeConstants.NODE_MIRNA, COLOR_MIRNA);
		defStyle.addVisualMappingFunction(nodeColorMapping);
		
		//node shape
		final DiscreteMapping<String, NodeShape> nodeShapeMapping = (DiscreteMapping<String, NodeShape>) discreteMappingFactory
                .createVisualMappingFunction(CyAttributeConstants.ATTR_TYPE, String.class, BasicVisualLexicon.NODE_SHAPE);		
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_GENE, NodeShapeVisualProperty.TRIANGLE);
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_DISEASE, NodeShapeVisualProperty.ELLIPSE);
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_TISSUE, NodeShapeVisualProperty.DIAMOND);
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_DRUG, NodeShapeVisualProperty.ROUND_RECTANGLE);
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_SIDE_EFFECT, NodeShapeVisualProperty.OCTAGON);
		nodeShapeMapping.putMapValue(CyAttributeConstants.NODE_MIRNA, NodeShapeVisualProperty.PARALLELOGRAM);
		defStyle.addVisualMappingFunction(nodeShapeMapping);

		//node size
		final DiscreteMapping<String, Double> nodeSizeMapping = (DiscreteMapping<String, Double>) discreteMappingFactory
                .createVisualMappingFunction(CyAttributeConstants.ATTR_TYPE, String.class, BasicVisualLexicon.NODE_SIZE);
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_GENE, 30.0);
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_DISEASE, 100.0);
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_TISSUE, 50.0);
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_DRUG, 50.0);
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_SIDE_EFFECT, 40.0);	
		nodeSizeMapping.putMapValue(CyAttributeConstants.NODE_MIRNA, 30.0);	
		defStyle.addVisualMappingFunction(nodeSizeMapping);
		
		//edge line type
		final DiscreteMapping<String, LineType> edgeLineTypeMapping = (DiscreteMapping<String, LineType>) discreteMappingFactory
                .createVisualMappingFunction(CyAttributeConstants.ATTR_TYPE, String.class, BasicVisualLexicon.EDGE_LINE_TYPE);		
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_DRG_GEN, LineTypeVisualProperty.LONG_DASH);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_DIS_GEN, LineTypeVisualProperty.SOLID);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_DIS_DRG, LineTypeVisualProperty.DASH_DOT);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_DIS_TIS, LineTypeVisualProperty.DASH_DOT);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_DRG_SIDE, LineTypeVisualProperty.DOT);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_GEN_TIS, LineTypeVisualProperty.EQUAL_DASH);
		edgeLineTypeMapping.putMapValue(CyAttributeConstants.EDGE_PPI, LineTypeVisualProperty.SOLID);
		
		defStyle.addVisualMappingFunction(edgeLineTypeMapping);
		
		final DiscreteMapping<String, Integer> edgeLineOpacityMapping = (DiscreteMapping<String, Integer>) discreteMappingFactory
                .createVisualMappingFunction(CyAttributeConstants.ATTR_TYPE, String.class, BasicVisualLexicon.EDGE_TRANSPARENCY);			
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_DRG_GEN, 150);
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_DIS_GEN, 150);
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_DIS_DRG, 150);
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_DIS_TIS, 150);		
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_DRG_SIDE, 80);
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_GEN_TIS, 50);
		edgeLineOpacityMapping.putMapValue(CyAttributeConstants.EDGE_PPI, 100);		
		defStyle.addVisualMappingFunction(edgeLineOpacityMapping);
		
		//edge color
		final DiscreteMapping<String, Paint> edgeColorMapping = (DiscreteMapping<String, Paint>) discreteMappingFactory
				.createVisualMappingFunction(CyAttributeConstants.ATTR_DATA_SOURCE, String.class, BasicVisualLexicon.EDGE_PAINT);	
		
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_ATLAS, new Color(0, 200, 0));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_CTD, new Color(0, 200, 100));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_DRUGBANK, new Color(0, 200, 100));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_GWAS, new Color(100, 200, 0));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_GWAS_MEDIC, new Color(100, 200, 0));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_GWAS_OMIM_MEDIC, new Color(100, 200, 0));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_GWAS_OMIM, new Color(100, 200, 0));
		edgeColorMapping.putMapValue(CyAttributeConstants.DATA_TISSUE, new Color(0, 100, 200));		
		defStyle.addVisualMappingFunction(edgeColorMapping);
		
        return defStyle;
	}

}

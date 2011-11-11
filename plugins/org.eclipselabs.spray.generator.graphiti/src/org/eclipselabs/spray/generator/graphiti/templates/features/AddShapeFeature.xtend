package org.eclipselabs.spray.generator.graphiti.templates.features

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.graphiti.util.IColorConstant
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.xtend2.lib.StringConcatenation
import org.eclipselabs.spray.generator.graphiti.templates.FileGenerator
import org.eclipselabs.spray.generator.graphiti.util.LayoutExtensions
import org.eclipselabs.spray.generator.graphiti.util.NamingExtensions
import org.eclipselabs.spray.generator.graphiti.util.SprayElementNameProvider
import org.eclipselabs.spray.mm.spray.Container
import org.eclipselabs.spray.mm.spray.Line
import org.eclipselabs.spray.mm.spray.MetaReference
import org.eclipselabs.spray.mm.spray.Text

import static org.eclipselabs.spray.generator.graphiti.util.GeneratorUtil.*


class AddShapeFeature extends FileGenerator  {
    @Inject extension LayoutExtensions
    @Inject extension IQualifiedNameProvider
    @Inject extension NamingExtensions
    @Inject extension SprayElementNameProvider  
    override StringConcatenation generateBaseFile(EObject modelElement) {
        mainFile( modelElement as Container, javaGenFile.baseClassName)
    }

    override StringConcatenation generateExtensionFile(EObject modelElement) {
        mainExtensionPointFile( modelElement as Container, javaGenFile.className)
    }
    
    def mainExtensionPointFile(Container container, String className) '''    
        «extensionHeader(this)»
        package «feature_package()»;
        
        import org.eclipse.graphiti.features.IFeatureProvider;
        
        public class «className» extends «className»Base {
            public «className»(IFeatureProvider fp) {
                super(fp);
            }
        }
    '''

    def mainFile(Container container, String className) '''
        «var containerType = constainerClass(container)»
        «header(this)»
        package «feature_package()»;
        
        import «container.represents.javaInterfaceName»;
        import org.eclipse.graphiti.features.IFeatureProvider;
        import org.eclipse.graphiti.features.context.IAddContext;
        import org.eclipse.graphiti.features.context.IContext;
        import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
        import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
        import org.eclipse.graphiti.mm.algorithms.Text;
        import org.eclipse.graphiti.mm.algorithms.Polyline;
        import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
        import org.eclipse.graphiti.mm.pictograms.ContainerShape;
        import org.eclipse.graphiti.mm.pictograms.Diagram;
        import org.eclipse.graphiti.mm.pictograms.PictogramElement;
        import org.eclipse.graphiti.mm.pictograms.Shape;
        import org.eclipse.graphiti.services.Graphiti;
        import org.eclipse.graphiti.services.IGaService;
        import org.eclipse.graphiti.services.IPeCreateService;
        import org.eclipse.graphiti.services.IPeService;
        import «util_package()».ISprayContainer;
        import «util_package()».«containerType»;
        import «util_package()».SprayContainerService;
        // MARKER_IMPORT

        public class «className» extends AbstractAddShapeFeature {
        
            protected final static String typeOrAliasName = "«container.represents.visibleName»";
        
            protected Diagram targetDiagram = null;
        
            protected «containerType» container = null;
        
            protected IGaService              gaService;
            protected IPeCreateService        peCreateService;
            protected IPeService              peService;
        
            public «className»(IFeatureProvider fp) {
                super(fp);
                container = new «containerType»();
                gaService = Graphiti.getGaService();
                peCreateService = Graphiti.getPeCreateService();
                peService = Graphiti.getPeService();
            }
        
            public boolean canAdd(IAddContext context) {
                final Object newObject = context.getNewObject();
                if (newObject instanceof «container.represents.name») {
                    // check if user wants to add to a diagram
                    if (context.getTargetContainer() instanceof Diagram) {
                        return true;
                    }
                }
                return false;
            }

            public PictogramElement add(IAddContext context) {
                «container.represents.name» addedModelElement = («container.represents.name») context.getNewObject();
                targetDiagram = peService.getDiagramForShape(context.getTargetContainer());
        
                ContainerShape containerShape = container.createContainer(context, addedModelElement);
                «IF container.hasFillColor»
                    GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
                    containerGa.setBackground(manageColor(«container.fillColor»));
                «ENDIF»    
                ContainerShape textContainer = SprayContainerService.findTextShape(containerShape);
                link(containerShape, addedModelElement);
        
                «FOR part : container.parts»
                    «part.createShape»
                «ENDFOR»
                
                // add a chopbox anchor to the shape
                peCreateService.createChopboxAnchor(containerShape);
        
                // call the update and layout features
                updatePictogramElement(containerShape);
                layoutPictogramElement(containerShape);
                
                return containerShape;
            }
            
            «FOR part : container.parts»
                
            «ENDFOR»
            
            @Override
            public boolean hasDoneChanges() {
                return false;
            }
        
            @Override
            public boolean canUndo(IContext context) {
                return false;
            }
            
        }
        '''
        
        def dispatch createShape (EObject part) '''
                System.out.println("Spray: unhandled Container child [«part.getClass().name»]");
        '''
        
        def dispatch createShape (Line line) '''
            «val varname = line.shapeName.toFirstLower»
            // Part is Line
            {
                // create shape for line
                Shape «varname» = peCreateService.createShape(textContainer, false);
                // create and set graphics algorithm
                Polyline polyline = gaService.createPolyline(«varname», new int[] { 0, 0, 0, 0 });
                polyline.setForeground(manageColor(«line.lineColor» ));
                polyline.setLineWidth(«line.layout.lineWidth»);
            «IF line.layout.lineWidth == 0»
                polyline.setLineVisible(false);
            «ENDIF»
                gaService.setLocation(polyline, 0, 0);
                Graphiti.getPeService().setPropertyValue(«varname», ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.LINE);
            }
        '''
        
        def dispatch createShape (Text text) '''
            «val varname = text.shapeName.toFirstLower»
            // Part is Text
            {
                String type = "«text.fullyQualifiedName»";
                // create shape for text and set text graphics algorithm
                Shape «varname» = peCreateService.createShape(textContainer, false);
                Text text = gaService.createDefaultText(getDiagram(), «varname»);
                text.setFont(gaService.manageFont(getDiagram(), text.getFont().getName(), 12));
                text.setForeground(manageColor(«text.lineColor»));
                text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
                text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
            «IF text.layout.bold»
                text.getFont().setBold(true);
            «ENDIF»
            «IF text.layout.italic»
                text.getFont().setItalic(true);
            «ENDIF»
                gaService.setLocationAndSize(text, 0, 0, 0, 0);
                peService.setPropertyValue(«varname», "MODEL_TYPE", type);
                peService.setPropertyValue(«varname», ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.TEXT);
                // create link and wire it
                link(«varname», addedModelElement);
            }
        '''
        
        def dispatch createShape (MetaReference metaRef) '''
            «val varname = metaRef.name.toFirstLower»
            «val target = metaRef.target» 
            // Part is reference list
            {
                // Create a dummy invisible line to have an anchor point for adding new elements to the list
                Shape dummy = peCreateService.createShape(textContainer, false);
                peService.setPropertyValue(dummy, "MODEL_TYPE", "«target.EReferenceType.name»");
                Polyline p = gaService.createPolyline(dummy, new int[] { 0, 0, 0, 0 });
                p.setForeground(manageColor(«typeof(IColorConstant).shortName».BLACK));
                p.setLineWidth(0);
                p.setLineVisible(false);
                gaService.setLocation(p, 0, 0);
                peService.setPropertyValue(dummy, ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.LINE);
            }
            for («target.EReferenceType.javaInterfaceName.shortName» p : addedModelElement.get«target.name.toFirstUpper»()) {
                Shape «varname» = peCreateService.createContainerShape(textContainer, true);
                peService.setPropertyValue(«varname», "STATIC", "true");
                peService.setPropertyValue(«varname», "MODEL_TYPE", "«target.EReferenceType.name»");
                peService.setPropertyValue(«varname», ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.TEXT);
                // create and set text graphics algorithm
                Text text = gaService.createDefaultText(getDiagram(), «varname», p.get«metaRef.labelPropertyName.toFirstUpper»());
                // TODO should have a text color here, refer to representation of reference type
                text.setForeground(manageColor(«metaRef.container.textColor»)); 
                text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
                text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
                gaService.setLocationAndSize(text, 0, 0, 0, 0);
                // create link and wire it
                link(«varname», p);
            }
        '''
}
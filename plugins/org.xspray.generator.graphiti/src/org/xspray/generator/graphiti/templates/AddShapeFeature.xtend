package org.xspray.generator.graphiti.templates

import java.util.List
import org.xspray.mm.xspray.*
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.emf.ecore.*
import org.eclipse.xtext.xtend2.lib.*
import static extension org.xspray.generator.graphiti.util.GeneratorUtil.*
import static extension org.xspray.generator.graphiti.util.MetaModel.*
import static extension org.xspray.generator.graphiti.util.XtendProperties.*
import org.eclipse.xtext.generator.IFileSystemAccess


class AddShapeFeature extends FileGenerator  {
	
	override StringConcatenation generateBaseFile(EObject modelElement) {
		mainFile( modelElement as Container, javaGenFile.baseClassName)
    }

    override StringConcatenation generateExtentionFile(EObject modelElement) {
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
		«var diagramName = container.represents.diagram.name »
		«var pack = findEClass(container.represents).EPackage.name »
		«var fullPackage = fullPackageName(findEClass(container.represents)) »
		«var containerType = constainerClass(container)»
		«header(this)»
		package «feature_package()»;
		
		import «fullPackage».«container.represents.name»;
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
		import «util_package()».ISprayColorConstants;
		import «util_package()».ISprayContainer;
		import «util_package()».«containerType»;
		import «util_package()».SprayContainerService;
		«FOR metaRef : container.parts.filter(p | p instanceof MetaReference) »
		    «setValue("metaRefName", metaRef.name)»
			«var eReference = findEClass(container.represents).EAllReferences.findFirst(e|e.name == getValue("metaRefName")) » 
		import «fullPackageName(eReference.EReferenceType)».«eReference.EReferenceType.name»;
		«ENDFOR»
		
		public class «className» extends AbstractAddShapeFeature {
		
		    protected final static String typeOrAliasName = "«container.represents.visibleName()»";
		
		    protected Diagram targetDiagram = null;
		
		    protected «containerType» container = null;
		
		    protected IGaService gaService = null;
		
			public «className»(IFeatureProvider fp) {
				super(fp);
				container = new «containerType»();
			    gaService = Graphiti.getGaService();
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
				targetDiagram = Graphiti.getPeService().getDiagramForShape(context.getTargetContainer());
				IPeCreateService peCreateService = Graphiti.getPeCreateService();
		
				ContainerShape containerShape = container.createContainer(context, addedModelElement);
			«IF container.layout.fillColor != Color::NULL_COLOR»
		        GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		        containerGa.setBackground(manageColor(ISprayColorConstants.«container.layout.fillColor.toString()»));
			«ENDIF»	
		        ContainerShape textContainer = SprayContainerService.findTextShape(containerShape);
		        link(containerShape, addedModelElement);
		
		«FOR part : container.parts »
			«IF part instanceof Line»
			    «var line = part as Line»
				// Part is Line
				{
					// create shape for line
					Shape shape = peCreateService.createShape(textContainer, false);
					// create and set graphics algorithm
					Polyline polyline = gaService.createPolyline(shape, new int[] { 0, 0, 0, 0 });
					polyline.setForeground(manageColor(ISprayColorConstants.«line.layout.lineColor» /* «container.name» */));
					polyline.setLineWidth(«line.layout.lineWidth»);
				«IF line.layout.lineWidth == 0»
				    polyline.setLineVisible(false);
				«ENDIF»
		            gaService.setLocation(polyline, 0, 0);
		            Graphiti.getPeService().setPropertyValue(shape, ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.LINE);
				}
			«ELSEIF part instanceof Text»
			    «var text = part as Text»
				// Part is Text
				{
					String type = 
						«FOR y : text.value SEPARATOR  " + "»
						«IF y instanceof StringLiteral»"«(y as StringLiteral).name»"
						«ELSEIF y instanceof MetaAttribute»"«(y as MetaAttribute).name.toFirstUpper()»"
						«ENDIF»
						«ENDFOR»;			
					// create shape for text and set text graphics algorithm
					Shape shape = peCreateService.createShape(textContainer, false);
					Text text = gaService.createDefaultText(getDiagram(), shape);
					text.setForeground(manageColor(ISprayColorConstants.«text.layout.lineColor»));
					text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
					text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
		        «IF text.layout.bold»
		            text.getFont().setBold(true);
		        «ENDIF»
		        «IF text.layout.italic»
		            text.getFont().setItalic(true);
		        «ENDIF»
					gaService.setLocationAndSize(text, 0, 0, 0, 0);
			        Graphiti.getPeService().setPropertyValue(shape, "MODEL_TYPE", type);
		            Graphiti.getPeService().setPropertyValue(shape, ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.TEXT);
					// create link and wire it
					link(shape, addedModelElement);
				}
			«ELSEIF part instanceof MetaReference»
			    «var metaRef = part as MetaReference»
			    «setValue("metaRefName", metaRef.name)»
				«var eReference = findEClass(container.represents).EAllReferences.findFirst(e|e.name == getValue("metaRefName")) » 
		    	// Part is reference list
				{
				    // Create a dummy invisible line to have an ancjhor point for adding new elements to the list
					Shape dummy = peCreateService.createShape(textContainer, false);
			        Graphiti.getPeService().setPropertyValue(dummy, "MODEL_TYPE", "«eReference.EReferenceType.name»");
					Polyline p = gaService.createPolyline(dummy, new int[] { 0, 0, 0, 0 });
					p.setForeground(manageColor(ISprayColorConstants.BLACK));
					p.setLineWidth(0);
					p.setLineVisible(false);
		            gaService.setLocation(p, 0, 0);
		            Graphiti.getPeService().setPropertyValue(dummy, ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.LINE);
				}
				for («eReference.EReferenceType.name» p : addedModelElement.get«metaRef.name.toFirstUpper()»()) {
					Shape shape = peCreateService.createContainerShape(textContainer, true);
			        Graphiti.getPeService().setPropertyValue(shape, "STATIC", "true");
			        Graphiti.getPeService().setPropertyValue(shape, "MODEL_TYPE", "«eReference.EReferenceType.name»");
		            Graphiti.getPeService().setPropertyValue(shape, ISprayContainer.CONCEPT_SHAPE_KEY, ISprayContainer.TEXT);
					// create and set text graphics algorithm
					Text text = gaService.createDefaultText(getDiagram(), shape, p.get«metaRef.labelProperty.toFirstUpper()»());
					// TODO should have a text color here, refer to representation of reference type
					text.setForeground(manageColor(ISprayColorConstants.«container.layout.textColor»)); 
					text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
					text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
					gaService.setLocationAndSize(text, 0, 0, 0, 0);
					// create link and wire it
					link(shape, p);
				}
			«ELSE»
			    // TODO
			    System.out.println("Spray: unhandled Container child [«part.getClass().name»]");
			«ENDIF»
		«ENDFOR»
				
				// add a chopbox anchor to the shape
		        peCreateService.createChopboxAnchor(containerShape);
		
		        // call the update and layout features
		        updatePictogramElement(containerShape);
		        layoutPictogramElement(containerShape);
				
				return containerShape;
			}
			
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
}
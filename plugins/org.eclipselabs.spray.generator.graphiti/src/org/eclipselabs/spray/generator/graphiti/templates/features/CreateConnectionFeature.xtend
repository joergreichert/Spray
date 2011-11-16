package org.eclipselabs.spray.generator.graphiti.templates.features

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.xtend2.lib.StringConcatenation
import org.eclipselabs.spray.generator.graphiti.templates.FileGenerator
import org.eclipselabs.spray.generator.graphiti.util.NamingExtensions
import org.eclipselabs.spray.mm.spray.Connection
import org.eclipselabs.spray.mm.spray.Diagram
import org.eclipselabs.spray.mm.spray.MetaClass
import org.eclipselabs.spray.xtext.util.GenModelHelper

import static org.eclipselabs.spray.generator.graphiti.util.GeneratorUtil.*
import org.eclipselabs.spray.mm.spray.CreateBehavior


class CreateConnectionFeature extends FileGenerator  {
    @Inject extension NamingExtensions
    @Inject extension GenModelHelper
    
    override StringConcatenation generateBaseFile(EObject modelElement) {
        mainFile( modelElement as MetaClass, javaGenFile.baseClassName)
    }

    override StringConcatenation generateExtensionFile(EObject modelElement) {
        mainExtensionPointFile( modelElement as MetaClass, javaGenFile.className)
    }
    
    def mainExtensionPointFile(MetaClass metaClass, String className) '''
        «extensionHeader(this)»
        package «feature_package()»;
        
        import org.eclipse.graphiti.features.IFeatureProvider;
        
        public class «className» extends «className»Base {
            public «className»(IFeatureProvider fp) {
                super(fp);
            }
            @Override
            public boolean hasDoneChanges() {
                return false;
            }
        
        }
    '''

    def mainFile (MetaClass metaClass, String className) '''
        «val connection = metaClass.representedBy as Connection»
        «val from = connection.from.EType as EClass»
        «val to = connection.to.EType as EClass»
        «val diagram = metaClass.diagram as Diagram»
        «header(this)»
        package «feature_package()»;
        import org.eclipse.graphiti.features.IFeatureProvider;
        import org.eclipse.graphiti.features.context.ICreateConnectionContext;
        import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
        import org.eclipse.graphiti.mm.pictograms.Anchor;
        import org.eclipse.graphiti.mm.pictograms.Connection;
        import org.eclipselabs.spray.runtime.graphiti.features.AbstractCreateConnectionFeature;
        // MARKER_IMPORT
        
        public class «className» extends AbstractCreateConnectionFeature {
        
            public «className»(IFeatureProvider fp) {
                // provide name and description for the UI, e.g. the palette
                super(fp, "«metaClass.visibleName»", "Create «metaClass.visibleName»");
            }
        
            public boolean canCreate(ICreateConnectionContext context) {
                // return true if both anchors belong to an EClass
                // and those EClasses are not identical
                «from.javaInterfaceName.shortName» source = get«from.name»(context.getSourceAnchor());
                «to.javaInterfaceName.shortName» target = get«to.name»(context.getTargetAnchor());
                if ( (source != null) && (target != null) && (source != target) ) {
                    return true;
                }
                return false;
            }
        
            public boolean canStartConnection(ICreateConnectionContext context) {
                // return true if start anchor belongs to a EClass
                if (get«from.name»(context.getSourceAnchor()) != null) {
                    return true;
                }
                return false;
            }
        
            public Connection create(ICreateConnectionContext context) {
                «val containmentRef = metaClass.behaviorsList.filter(typeof(CreateBehavior)).head.containmentReference»
                «val modelClassName = containmentRef.EContainingClass.javaInterfaceName.shortName»
                Connection newConnection = null;
        
                // get EClasses which should be connected
                «from.name» source = get«from.name»(context.getSourceAnchor());
                «to.name» target = get«to.name»(context.getTargetAnchor());
        
                if (source != null && target != null) {
                    // create new business object
                    «metaClass.javaInterfaceName.shortName» eReference = create«metaClass.name»(source, target);
                    «diagram.modelServiceClassName.shortName» modelService = new «diagram.modelServiceClassName.shortName»(getFeatureProvider().getDiagramTypeProvider());
                    // add the element to containment reference
                    «modelClassName» model = modelService.getModel();
                    «IF containmentRef.many»
                        model.get«containmentRef.name.toFirstUpper»().add(eReference);
                    «ELSE»
                        model.set«containmentRef.name.toFirstUpper»(eReference);
                    «ENDIF»
                    // add connection for business object
                    AddConnectionContext addContext = new AddConnectionContext(
                            context.getSourceAnchor(), context.getTargetAnchor());
                    addContext.setNewObject(eReference);
                    newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
                }
        
                return newConnection;
            }
        
            /**
             * Returns the «from.name» belonging to the anchor, or null if not available.
             */
            private «from.name» get«from.name»(Anchor anchor) {
                if (anchor != null) {
                    Object object = getBusinessObjectForPictogramElement(anchor.getParent());
                    if (object instanceof «from.name») {
                        return («from.name») object;
                    }
                }
                return null;
            }
            «IF from.name != to.name»
            /**
             * Returns the «to.name» belonging to the anchor, or null if not available.
             */
            private «to.name» get«to.name»(Anchor anchor) {
                if (anchor != null) {
                    Object object = getBusinessObjectForPictogramElement(anchor.getParent());
                    if (object instanceof «to.name») {
                        return («to.name») object;
                    }
                }
                return null;
            }
            «ENDIF»
        
            /**
             * Creates a EReference between two EClasses.
             */
            protected «metaClass.name» create«metaClass.name»(«from.name» source, «to.name» target) {
                // TODO: Domain Object
                «metaClass.name» domainObject = «metaClass.EFactoryInterfaceName.shortName».eINSTANCE.create«metaClass.name»();
                «IF metaClass.type.EAttributes.exists(att|att.name == "name") »
                    domainObject.setName("new «metaClass.visibleName»");
                «ENDIF»
                domainObject.set«connection.from.name.toFirstUpper»(source);
                domainObject.set«connection.to.name.toFirstUpper»(target);

                changesDone = true;
                return domainObject;
            }
            
            «IF (metaClass.icon != null) && ! metaClass.icon.equalsIgnoreCase("")»
                @Override
                public String getCreateImageId() {
                    return «metaClass.diagram.imageProviderClassName.shortName».«diagram.getImageIdentifier(metaClass.icon)»; 
                }
            «ENDIF»
            
        }
    '''
}
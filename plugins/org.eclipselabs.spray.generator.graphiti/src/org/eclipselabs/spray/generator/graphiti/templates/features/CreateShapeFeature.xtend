package org.eclipselabs.spray.generator.graphiti.templates.features

import com.google.inject.Inject
import org.eclipselabs.spray.generator.graphiti.templates.FileGenerator
import org.eclipselabs.spray.generator.graphiti.util.NamingExtensions
import org.eclipselabs.spray.generator.graphiti.util.mm.MetaClassExtensions
import org.eclipselabs.spray.mm.spray.CreateBehavior
import org.eclipselabs.spray.mm.spray.MetaClass

import static org.eclipselabs.spray.generator.graphiti.util.GeneratorUtil.*
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EDataType

class CreateShapeFeature extends FileGenerator<MetaClass> {
    @Inject extension NamingExtensions
    @Inject extension MetaClassExtensions
    
    override CharSequence generateBaseFile(MetaClass modelElement) {
        mainFile( modelElement, javaGenFile.baseClassName)
    }

    override CharSequence generateExtensionFile(MetaClass modelElement) {
        mainExtensionPointFile( modelElement, javaGenFile.className)
    }
    
    def mainExtensionPointFile(MetaClass metaClass, String className) '''    
        «extensionHeader(this)»
        package «feature_package()»;
        
        import org.eclipse.graphiti.features.IFeatureProvider;
        
        public class «className» extends «className»Base {
            public «className»(IFeatureProvider fp) {
                super(fp);
            }
        }
    '''

    def mainFile (MetaClass metaClass, String className) '''
        «val diagram = metaClass.diagram»
        «header(this)»
        package «feature_package()»;

        import org.eclipse.graphiti.features.IFeatureProvider;
        import org.eclipse.graphiti.features.context.ICreateContext;
        import org.eclipse.graphiti.mm.pictograms.Diagram;
        import org.eclipselabs.spray.runtime.graphiti.containers.SampleUtil;
        import org.eclipselabs.spray.runtime.graphiti.features.AbstractCreateFeature;
        import «metaClass.javaInterfaceName»;
        // MARKER_IMPORT
        
        public class «className» extends AbstractCreateFeature {
            protected static String TITLE = "Create «metaClass.visibleName»";
            protected static String USER_QUESTION = "Enter new «metaClass.visibleName» name";
            protected «diagram.modelServiceClassName.shortName» modelService;
            protected «metaClass.name» newClass = null;
            «generate_additionalFields(metaClass)»
        
        
            public «className» (IFeatureProvider fp) {
                // set name and description of the creation feature
                super(fp, "«metaClass.createFeatureLabel»", "«metaClass.createFeatureDescription»");
                modelService = new «diagram.modelServiceClassName.shortName»(fp.getDiagramTypeProvider());
            }
        
            «generate_canCreate(metaClass)»
            «generate_create(metaClass)»
            «generate_createModelElement(metaClass)»
            «generate_getCreateImageId(metaClass)»
            «generate_additionalFields(metaClass)»
        }
    '''
    
    def generate_canCreate (MetaClass metaClass) '''
        «overrideHeader()»
        public boolean canCreate(ICreateContext context) {
            // TODO: Respect the cardinality of the containment reference
            return context.getTargetContainer() instanceof Diagram;
        }
    '''
    
    def generate_create (MetaClass metaClass) '''
        «overrideHeader()»
        public Object[] create(final ICreateContext context) {
            newClass = create«metaClass.visibleName»(context);
        
            if (newClass == null ) {
                return EMPTY;
            }

            // do the add
            addGraphicalRepresentation(context, newClass);
            // return newly created business object(s)
            return new Object[] { newClass };
        }
    '''
    
    def generate_createModelElement (MetaClass metaClass) '''
        «val diagram = metaClass.diagram»
        «val modelClassName = diagram.modelType.javaInterfaceName.shortName»
        «val createBehavior = metaClass.behaviorsList.filter(typeof(CreateBehavior)).head»
        «val containmentRef = createBehavior.containmentReference»
        /**
         * Creates a new {@link «metaClass.name»} instance and adds it to the containing type.
         */
        protected «metaClass.name» create«metaClass.visibleName»(ICreateContext context) {
            «handleAskFor(metaClass, createBehavior.askFor)»
            
            «IF containmentRef != null»
               // add the element to containment reference
               «modelClassName» model = modelService.getModel();
               «IF containmentRef.many»
                   model.get«containmentRef.name.toFirstUpper»().add(newClass);
               «ELSE»
                   model.set«containmentRef.name.toFirstUpper»(newClass);
               «ENDIF»
               
			«ENDIF»
            setDoneChanges(true);
            return newClass;
        }
    '''
    
    def handleAskFor(MetaClass metaClass, EAttribute attribute) '''
        // create «metaClass.name» instance
        «metaClass.name» newClass = «metaClass.EFactoryInterfaceName.shortName».eINSTANCE.create«metaClass.name»();
        «IF attribute != null»
           // ask user for «metaClass.visibleName» «attribute.name»
           «IF (attribute.EType as EDataType).EFactoryInterfaceName.matches('java.lang.String')»
              String new«attribute.name.toFirstUpper» = SampleUtil.askString(TITLE, USER_QUESTION, "", null);
              if (new«attribute.name.toFirstUpper» == null || new«attribute.name.toFirstUpper».trim().length() == 0) {
                 return null;
              } else {
                 newClass.set«attribute.name.toFirstUpper»(new«attribute.name.toFirstUpper»);
              }
           «ELSE»
              «val type = (attribute.EType as EDataType).EFactoryInterfaceName» 
              «val typeName = if("double".matches(type)) "Double" else if("int".matches(type)) "Integer" else "Object"»
              org.eclipse.jface.dialogs.IInputValidator validator = new org.eclipse.jface.dialogs.IInputValidator() {
                 public String isValid(String _newText) {
                    String message = null;
                    try {
                       «typeName».valueOf(_newText);
                    } catch(Exception nfe) {
                       message = _newText + " cannot be cast to «typeName»";
                    }
                    return message;
                 }
              };
              String new«attribute.name.toFirstUpper»String = SampleUtil.askString(TITLE, USER_QUESTION, "", validator);
              «typeName» new«attribute.name.toFirstUpper» = «typeName».valueOf(new«attribute.name.toFirstUpper»String);	
              newClass.set«attribute.name.toFirstUpper»(new«attribute.name.toFirstUpper»);
           «ENDIF»
        «ENDIF»
    '''
    
    def generate_getCreateImageId (MetaClass metaClass) '''
        «val diagram = metaClass.diagram»
        «IF (metaClass.icon != null)»
            «overrideHeader()»
            public String getCreateImageId() {
                return «diagram.imageProviderClassName.shortName».«diagram.getImageIdentifier(metaClass.icon)»;
            }
        «ENDIF»
    '''
    
}
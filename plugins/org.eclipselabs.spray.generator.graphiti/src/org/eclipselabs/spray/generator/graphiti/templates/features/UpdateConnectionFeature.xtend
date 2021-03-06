/** ****************************************************************************
 * Copyright (c)  The Spray Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spray Dev Team - initial API and implementation
 **************************************************************************** */
package org.eclipselabs.spray.generator.graphiti.templates.features

import javax.inject.Inject
import org.eclipselabs.spray.generator.graphiti.util.NamingExtensions
import org.eclipselabs.spray.generator.graphiti.util.mm.DiagramExtensions
import org.eclipselabs.spray.mm.spray.ConnectionInSpray
import org.eclipselabs.spray.xtext.generator.FileGenerator

import static org.eclipselabs.spray.generator.common.GeneratorUtil.*

class UpdateConnectionFeature extends FileGenerator<ConnectionInSpray>  {
    @Inject extension NamingExtensions
    @Inject extension DiagramExtensions
    
    override CharSequence generateBaseFile(ConnectionInSpray modelElement) {
        mainFile( modelElement, javaGenFile.baseClassName)
    }

    override CharSequence generateExtensionFile(ConnectionInSpray modelElement) {
        mainExtensionPointFile( modelElement, javaGenFile.className)
    }
    
    def mainExtensionPointFile(ConnectionInSpray connection, String className) '''    
        «extensionHeader(this)»
        package «feature_package()»;
        
        import org.eclipse.graphiti.features.IFeatureProvider;
        
        public class «className» extends «className»Base {
            public «className»(final IFeatureProvider fp) {
                super(fp);
            }
        }
    '''
    
    def mainFile(ConnectionInSpray connection, String className) '''
        «header(this)»
        package «feature_package()»;

        import org.eclipse.emf.ecore.EObject;
        import org.eclipse.graphiti.features.IFeatureProvider;
        import org.eclipse.graphiti.features.IReason;
        import org.eclipse.graphiti.features.context.IUpdateContext;
        import org.eclipse.graphiti.features.impl.Reason;
        import org.eclipse.graphiti.mm.algorithms.AbstractText;
        import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
        import org.eclipse.graphiti.mm.pictograms.Diagram;
        import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
        import org.eclipse.graphiti.mm.pictograms.PictogramElement;
        import org.eclipse.graphiti.services.IGaService;
        import org.eclipse.xtext.xbase.lib.ObjectExtensions;
        import org.eclipselabs.spray.runtime.graphiti.features.AbstractUpdateFeature;
        // MARKER_IMPORT
                
        public abstract class «className» extends AbstractUpdateFeature {
            «generate_additionalFields(connection)»
        
            public «className»(final IFeatureProvider fp) {
                super(fp);
                gaService = «connection.diagram.activatorClassName.shortName».get(IGaService.class);
            }
        
            «generate_canUpdate(connection)»
            «generate_updateNeeded(connection)»
            «generate_update(connection)»
            «generate_getValue(connection)»
            «generate_additionalMethods(connection)»
        }
    '''

    def generate_canUpdate (ConnectionInSpray connection) '''
        «val metaClassName = connection.represents.itfName»
        «overrideHeader()»
        public boolean canUpdate(final IUpdateContext context) {
            // return true, if linked business object is a EClass
            final PictogramElement pictogramElement = context.getPictogramElement();
            final EObject bo = getBusinessObjectForPictogramElement(pictogramElement);
            return (bo instanceof «metaClassName») && (!(pictogramElement instanceof Diagram));
        }
    '''

    def generate_updateNeeded (ConnectionInSpray connection) '''
        «val metaClassName = connection.represents.itfName»
        «overrideHeader()»
        public IReason updateNeeded(final IUpdateContext context) {
            final PictogramElement pictogramElement = context.getPictogramElement();
            final EObject bo = getBusinessObjectForPictogramElement(pictogramElement);
            if ( ! (bo instanceof «metaClassName»)) {
                return Reason.createFalseReason();
            }
            «metaClassName» eClass = («metaClassName») bo;

            if (pictogramElement instanceof FreeFormConnection) {
                final FreeFormConnection free = (FreeFormConnection) pictogramElement;
                for (final ConnectionDecorator decorator : free.getConnectionDecorators()) {
                    final String type = peService.getPropertyValue(decorator, PROPERTY_MODEL_TYPE);
                    final String value = getValue(type, eClass);
                    final AbstractText text = (AbstractText) decorator.getGraphicsAlgorithm();
                    final String current = text.getValue();
                    if (ObjectExtensions.operator_notEquals(current, value)) {
                        return Reason.createTrueReason(type + " is changed");
                    }
                }
            }
            return Reason.createFalseReason();
        }
    '''

    def generate_update (ConnectionInSpray connection) '''
        «val metaClassName = connection.represents.itfName»
        «overrideHeader()»
        public boolean update(final IUpdateContext context) {
            final PictogramElement pictogramElement = context.getPictogramElement();
            final «metaClassName» eClass = («metaClassName») getBusinessObjectForPictogramElement(pictogramElement);

            final FreeFormConnection free = (FreeFormConnection) pictogramElement;
            for (final ConnectionDecorator decorator : free.getConnectionDecorators()) {
                final String type = peService.getPropertyValue(decorator, PROPERTY_MODEL_TYPE);
                final String value = getValue(type, eClass);
                final AbstractText text = (AbstractText) decorator.getGraphicsAlgorithm();
                text.setValue(value);
                setDoneChanges(true);
            }
            return true;
        }
    '''
    
    def generate_getValue (ConnectionInSpray connection) '''
        protected String getValue(final String type, final «connection.represents.itfName» eClass) {
            String result = "";
«««            «IF connection.fromLabel != null»
«««            if(PROPERTY_MODEL_TYPE_CONNECTION_FROM_LABEL.equals(type) ){
«««                «valueGenerator(connection.fromLabel, "eClass")»
«««            }
«««            «ENDIF»
«««            «IF connection.toLabel!=null»
«««            if(PROPERTY_MODEL_TYPE_CONNECTION_TO_LABEL.equals(type) ){
«««                «valueGenerator(connection.toLabel, "eClass")»
«««            }
«««            «ENDIF»
«««            «IF connection.connectionLabel!=null»
«««            if(PROPERTY_MODEL_TYPE_CONNECTION_LABEL.equals(type) ){
«««                «valueGenerator(connection.connectionLabel, "eClass")»
«««            }
«««            «ENDIF»
            return result;
        }
    '''
    
}
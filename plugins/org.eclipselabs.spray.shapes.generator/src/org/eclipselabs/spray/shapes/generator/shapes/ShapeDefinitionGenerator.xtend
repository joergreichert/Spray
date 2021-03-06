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
package org.eclipselabs.spray.shapes.generator.shapes

import org.eclipselabs.spray.xtext.generator.FileGenerator
import org.eclipselabs.spray.shapes.ShapeDefinition
import javax.inject.Inject
import org.eclipselabs.spray.shapes.generator.GeneratorShapeDefinition
import org.eclipselabs.spray.xtext.generator.filesystem.GenFile
import org.eclipselabs.spray.xtext.generator.filesystem.JavaGenFile

class ShapeDefinitionGenerator extends FileGenerator<ShapeDefinition> {
	@Inject extension GeneratorShapeDefinition

	override generate(ShapeDefinition shapeDefinition, GenFile genFile) {
		(genFile as JavaGenFile).setPackageAndClass(packageName, shapeDefinition.gapClassName)
		super.generate(shapeDefinition, genFile)
	}

    override CharSequence generateBaseFile(ShapeDefinition shapeDefinition) {
    	compile(shapeDefinition)
    }

    override CharSequence generateExtensionFile(ShapeDefinition shapeDefinition) {
        mainExtensionPointFile(shapeDefinition)
    }

    def mainExtensionPointFile(ShapeDefinition shapeDefinition) '''
        «extensionHeader(this)»
        package «packageName»;
         
        import org.eclipse.graphiti.features.IFeatureProvider;
         
        public class «shapeDefinition.gapClassName» extends «shapeDefinition.className» {

            public «shapeDefinition.gapClassName»(IFeatureProvider fp) {
        		super(fp);
        	}
        }
    '''
}
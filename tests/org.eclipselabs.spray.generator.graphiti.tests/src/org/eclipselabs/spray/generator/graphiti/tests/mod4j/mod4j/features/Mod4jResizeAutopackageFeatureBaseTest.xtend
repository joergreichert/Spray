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
package org.eclipselabs.spray.generator.graphiti.tests.mod4j.mod4j.features

import org.eclipse.xtext.junit4.InjectWith
import org.eclipselabs.spray.generator.graphiti.tests.AbstractSprayGeneratorTest
import org.eclipselabs.spray.generator.graphiti.tests.SprayTestsInjectorProvider
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner2))
@InjectWith(typeof(SprayTestsInjectorProvider))
class Mod4jResizeAutopackageFeatureBaseTest extends AbstractSprayGeneratorTest {

    @Test
    def void test() {
       val fsa = triggerGenerator("mod4j/mod4j-busmod.spray")
       val keyClass = "DEFAULT_OUTPUTorg/eclipselabs/spray/examples/mod4j/features/Mod4jResizeAutopackageFeatureBase.java"
       assertTrue(keyClass + " should have been generated", fsa.files.containsKey(keyClass))
       assertEquals(keyClass + " should have the expected content", fsa.files.get(keyClass).toString, expectedContent.toString)
    }

    def private expectedContent() '''
        
             /*************************************************************************************
              *
              * Generated by Spray ResizeFeature.xtend
              *
              * This file contains generated and should not be changed.
              * Use the extension point class (the direct subclass of this class) to add manual code
              *
              *************************************************************************************/
         	package org.eclipselabs.spray.examples.mod4j.features;
        
         	import org.eclipse.graphiti.features.IFeatureProvider;
         	import org.eclipse.graphiti.features.IResizeConfiguration;
         	import org.eclipse.graphiti.features.context.IResizeShapeContext;
         	import org.eclipse.graphiti.features.DefaultResizeConfiguration;
             import org.eclipselabs.spray.runtime.graphiti.features.DefaultResizeShapeFeature;
         	import org.eclipselabs.spray.runtime.graphiti.shape.SprayLayoutManager;
         	import org.eclipselabs.spray.examples.mod4j.shapes.;
         	
         	
         	
         	public abstract class Mod4jResizeAutopackageFeatureBase extends DefaultResizeShapeFeature {
         	
         		SprayLayoutManager layoutManager; 
         	
         		public Mod4jResizeAutopackageFeatureBase(final IFeatureProvider fp) {
         			super(fp);
         			layoutManager =  new (fp).getShapeLayout( );
         		}
         	
                 public class ResizeConfiguration_null extends DefaultResizeConfiguration  {
                 
                 public boolean isHorizontalResizeAllowed() {
                 		return layoutManager.isStretchHorizontal( );
                 }
                 
                 public boolean isVerticalResizeAllowed() {
                 		return layoutManager.isStretchVertical( );
                 }
                 
                 }
                             /**
                              * {@inheritDoc}
                              */
                             @Override
                 public IResizeConfiguration getResizeConfiguration(IResizeShapeContext context) {
                 	return new ResizeConfiguration_null( );
                 }
         	}
    '''
}

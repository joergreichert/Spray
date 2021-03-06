/** ****************************************************************************
 * Copyright (c) 1, 2013The Spray Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spray Dev Team - initial API and implementation
 **************************************************************************** */
package org.eclipselabs.spray.generator.graphiti.tests;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.eclipse.xtext.xbase.XbaseFactory;
import org.eclipselabs.spray.xtext.generator.IPostProcessor;
import org.eclipselabs.spray.xtext.generator.formatting.CodeFormatterProvider;
import org.eclipselabs.spray.xtext.generator.formatting.JavaPostProcessor;
import org.eclipselabs.spray.xtext.generator.outputconfig.SprayOutputConfigurationProvider;
import org.eclipselabs.spray.xtext.util.GenModelHelper;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Add any test related bindings / overwriting of bindings here.
 * 
 * @author Karsten Thoms (karsten.thoms@gmail.com)
 */
public class SprayTestsModule extends AbstractGenericModule {
    static class TestGenModelHelper extends GenModelHelper {
        @Override
        public String getJavaInterfaceName(EClass eClass) {
            if (eClass.getEPackage()==EcorePackage.eINSTANCE) {
                return super.getJavaInterfaceName(eClass);
            }
            return "samplepackage." + eClass.getName();
        }
    }
    
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(XMLNamespacePackage.class).toInstance(XMLNamespacePackage.eINSTANCE);
		binder.bind(GenModelFactory.class).toInstance(GenModelFactory.eINSTANCE);
		binder.bind(XbaseFactory.class).toInstance(XbaseFactory.eINSTANCE);
	}

    public Class<? extends IFileSystemAccess> bindIFileSystemAccess() {
        return InMemoryFilesystemAccessExt.class;
    }

    public Class<? extends GenModelHelper> bindGenModelHelper() {
        return TestGenModelHelper.class;
    }
    
    public void configureJavaPostProcessor(Binder binder) {
        binder.bind(IPostProcessor.class).annotatedWith(Names.named("java")).to(JavaPostProcessor.class);
    }

    public void configureJavaFormatterConfig(Binder binder) {
        binder.bind(String.class).annotatedWith(Names.named(CodeFormatterProvider.JDT_FORMATTER_CONFIG)).toInstance("org/eclipselabs/spray/generator/xtext/generator/formatting/formatter.xml");
    }

    public Class<? extends IOutputConfigurationProvider> bindIOutputConfigurationProvider() {
        return SprayOutputConfigurationProvider.class;
    }
    
}

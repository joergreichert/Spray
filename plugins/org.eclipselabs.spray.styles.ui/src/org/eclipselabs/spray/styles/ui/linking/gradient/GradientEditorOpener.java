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
package org.eclipselabs.spray.styles.ui.linking.gradient;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.spray.runtime.xtext.ui.linking.DSLEditorOpener;
import org.eclipselabs.spray.runtime.xtext.ui.linking.DSLResourceVisitor;
import org.eclipselabs.spray.styles.Gradient;

import com.google.inject.Inject;

public class GradientEditorOpener extends DSLEditorOpener<Gradient> {

	private static final String STYLE_DSL_EDITORID = "org.eclipselabs.spray.styles.Style";

	@Inject(optional = true)
	private IWorkbench workbench;
	
	@Inject
	private GradientResourceVisitor gradientResourceVisitor;
	
	@Override
	protected String getDSLEditorId() {
		return STYLE_DSL_EDITORID;
	}

	@Override
	protected DSLResourceVisitor<Gradient> getDSLResourceVisitor(ResourceSet rs) {
		gradientResourceVisitor.setResourceSet(rs);
		return gradientResourceVisitor;
	}

	@Override
	protected boolean areEObjectsEqual(Gradient givenGradient,
			Gradient gradientToInspect) {
		return gradientToInspect.getName().equals(
						givenGradient.getName());
	}

	@Override
	protected IWorkbench getWorkbench() {
		if(workbench == null) {
			return PlatformUI.getWorkbench();
		}
		return workbench;
	}	
}

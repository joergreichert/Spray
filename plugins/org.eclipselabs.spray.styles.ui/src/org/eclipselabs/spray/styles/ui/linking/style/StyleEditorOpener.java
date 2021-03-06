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
package org.eclipselabs.spray.styles.ui.linking.style;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.spray.runtime.xtext.ui.linking.DSLEditorOpener;
import org.eclipselabs.spray.runtime.xtext.ui.linking.DSLResourceVisitor;
import org.eclipselabs.spray.styles.Style;

import com.google.inject.Inject;

public class StyleEditorOpener extends DSLEditorOpener<Style> {

	private static final String STYLE_DSL_EDITORID = "org.eclipselabs.spray.styles.Style";

	@Inject(optional = true)
	private IWorkbench workbench;
	
	@Inject 
	private StyleResourceVisitor styleResourceVisitor;
	
	@Override
	protected String getDSLEditorId() {
		return STYLE_DSL_EDITORID;
	}

	@Override
	protected DSLResourceVisitor<Style> getDSLResourceVisitor(ResourceSet rs) {
		styleResourceVisitor.setResourceSet(rs);
		return 	styleResourceVisitor;
	}

	@Override
	protected boolean areEObjectsEqual(Style givenStyle,
			Style styleToInspect) {
		return styleToInspect.getName().equals(
						givenStyle.getName());
	}

	@Override
	protected IWorkbench getWorkbench() {
		if(workbench == null) {
			return PlatformUI.getWorkbench();
		}
		return workbench;
	}	
}

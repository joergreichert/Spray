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
package org.eclipselabs.spray.shapes.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

public class ShapesEObjectHoverProvider extends DefaultEObjectHoverProvider {
	private ShapesHoverControlCreator hoverControlCreator;

	public IInformationControlCreator getHoverControlCreator() {
		if (hoverControlCreator == null)
			hoverControlCreator = new ShapesHoverControlCreator(this,
					getInformationPresenterControlCreator());
		return hoverControlCreator;
	}

	public IInformationControlCreatorProvider getHoverInfo(
			final EObject object, final ITextViewer viewer, final IRegion region) {
		return new IInformationControlCreatorProvider() {

			public IInformationControlCreator getHoverControlCreator() {
				return ShapesEObjectHoverProvider.this.getHoverControlCreator();
			}

			public Object getInfo() {
				return getHoverInfo(object, region, null);
			}
		};
	}
}

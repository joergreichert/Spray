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
package org.eclipselabs.spray.shapes.scoping;

import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipselabs.spray.runtime.graphiti.shape.ISprayShape;

import com.google.common.base.Predicate;

public class ShapeScopeRestrictor implements Predicate<IEObjectDescription> {

	private final String SHAPE_INTERFACE = ISprayShape.class.getName();

	@Override
	public boolean apply(IEObjectDescription input) {
		if (input.getEObjectOrProxy() instanceof JvmGenericType) {
			return isStyle((JvmGenericType) input.getEObjectOrProxy());
		} else {
			return false;
		}
	}

	private boolean isStyle(JvmGenericType type) {
		for (JvmTypeReference itfRef : type.getExtendedInterfaces()) {
			if (isStyle(itfRef)) {
				return true;
			}
		}
		for (JvmTypeReference superTypeRef : type.getSuperTypes()) {
			if (isStyle(superTypeRef)) {
				return true;
			}
		}
		return false;
	}

	private boolean isStyle(JvmTypeReference typeRef) {
		if (SHAPE_INTERFACE.equals(typeRef.getIdentifier())) {
			return true;
		}
		JvmGenericType type = (JvmGenericType) typeRef.getType();
		for (JvmTypeReference itfRef : type.getExtendedInterfaces()) {
			if (SHAPE_INTERFACE.equals(itfRef.getIdentifier())) {
				return true;
			}
		}
		for (JvmTypeReference superTypeRef : type.getSuperTypes()) {
			if (isStyle(superTypeRef)) {
				return true;
			}
		}
		return false;
	}

}

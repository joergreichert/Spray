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
package org.eclipselabs.spray.xtext.customizing;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipselabs.spray.mm.spray.MetaClass;
import org.eclipselabs.spray.mm.spray.MetaReference;
import org.eclipselabs.spray.mm.spray.ShapeInSpray;
import org.eclipselabs.spray.mm.spray.SprayPackage;

import javax.inject.Inject;

/**
 * Spray specific {@link IQualifiedNameProvider}.
 * 
 * @author Karsten Thoms (karsten.thoms@itemis.de)
 */
public class SprayQualifiedNameProvider extends DefaultDeclarativeQualifiedNameProvider {
    @Inject
    private IQualifiedNameConverter converter;

    QualifiedName qualifiedName(JvmGenericType type) {
        return converter.toQualifiedName(type.getQualifiedName());
    }

    /**
     * @param element
     * @return [diagram name].[referenced EClass name]
     */
    public QualifiedName qualifiedName(MetaClass element) {
        if (element.getAlias() == null) {
            List<INode> nodes = NodeModelUtils.findNodesForFeature(element, SprayPackage.Literals.META_CLASS__TYPE);
            String eClassName = NodeModelUtils.getTokenText(nodes.get(0));
            return QualifiedName.create(element.getDiagram().getName(), eClassName);
        } else {
            return QualifiedName.create(element.getDiagram().getName(), element.getAlias());
        }
    }

    public QualifiedName qualifiedName(ShapeInSpray element) {
        EObject parentObject = element.eContainer();
        QualifiedName parent = null;
        while (parent == null && parentObject != null) {
            parent = getFullyQualifiedName(parentObject);
            parentObject = parentObject.eContainer();
        }
        String name = element.getAlias() != null ? element.getAlias() : calculateNameByPositionInParentChildren(element);
        return parent.append(name);
    }

    private String calculateNameByPositionInParentChildren(EObject element) {
        String parentName = "";
        if (element.eContainer() instanceof MetaReference) {
            parentName = calculateNameByPositionInParentChildren(element.eContainer());
            if (parentName == null) {
                parentName = "";
            }
        }
        return parentName + element.eClass().getName() + element.eContainer().eContents().indexOf(element);
    }
}

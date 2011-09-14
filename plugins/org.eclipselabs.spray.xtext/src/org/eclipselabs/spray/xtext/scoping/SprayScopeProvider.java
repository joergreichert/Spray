/*
 * generated by Xtext
 */
package org.eclipselabs.spray.xtext.scoping;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.FilteringScope;
import org.eclipse.xtext.scoping.impl.MapBasedScope;
import org.eclipselabs.spray.mm.spray.Connection;
import org.eclipselabs.spray.mm.spray.MetaAttribute;
import org.eclipselabs.spray.mm.spray.MetaClass;
import org.eclipselabs.spray.mm.spray.MetaReference;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.CONNECTION;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.CONNECTION__FROM;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.CONNECTION__TO;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_ATTRIBUTE;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_ATTRIBUTE__ATTRIBUTE;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_ATTRIBUTE__PATHSEGMENTS;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_CLASS__TYPE;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_REFERENCE;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_REFERENCE__LABEL_PROPERTY;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.META_REFERENCE__REFERENCE;
import static org.eclipselabs.spray.mm.spray.SprayPackage.Literals.TEXT;

/**
 * This class contains custom scoping description.
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 */
public class SprayScopeProvider extends AbstractDeclarativeScopeProvider {
    @Override
    public IScope getScope(EObject context, EReference reference) {
        if (reference == META_CLASS__TYPE) {
            // filter out types with URL schema qualified names
            final IScope scope = delegateGetScope(context, reference);
            final Predicate<IEObjectDescription> filter = new Predicate<IEObjectDescription>() {
                @Override
                public boolean apply(IEObjectDescription input) {
                    return !input.getQualifiedName().toString().startsWith("http://");
                }
            };
            return new FilteringScope(scope, filter);
        } else if (context.eClass() == CONNECTION && reference == CONNECTION__FROM) {
            final MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            final IScope result = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(metaClass.getType().getEAllReferences()));
            return result;
        } else if (context.eClass() == CONNECTION && reference == CONNECTION__TO) {
            final Connection connection = (Connection) context;
            final MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            // filter 'from' from the possible references
            Iterable<EReference> targetReferences = Iterables.filter(metaClass.getType().getEAllReferences(), new Predicate<EReference>() {
                @Override
                public boolean apply(EReference input) {
                    return input != connection.getFrom();
                }
            });
            final IScope result = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(targetReferences));
            return result;
        } else if (context.eClass() == META_REFERENCE && reference == META_REFERENCE__REFERENCE) {
            final MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            final IScope result = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(metaClass.getType().getEAllReferences()));
            return result;
        } else if (context.eClass() == META_REFERENCE && reference == META_REFERENCE__LABEL_PROPERTY) {
            MetaReference metaRef = (MetaReference) context;
            EReference ref = metaRef.getReference();
            if (ref.eIsProxy()) {
                ref = (EReference) EcoreUtil.resolve(ref, context);
                if (ref.eIsProxy()) {
                    // still a proxy?
                    return IScope.NULLSCOPE;
                }
            }
            final IScope result = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(metaRef.getReference().getEReferenceType().getEAllAttributes()));
            return result;
        } else if (context.eClass() == META_ATTRIBUTE && reference == META_ATTRIBUTE__PATHSEGMENTS) {
            MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            MetaAttribute attr = (MetaAttribute) context;
            EClass currentClass = metaClass.getType();
            for (EReference ref : attr.getPathsegments()) {
                if (ref.eIsProxy()) {
                    IScope scope = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(currentClass.getEAllReferences()));
                    return scope;
                }
                currentClass = ref.getEReferenceType();
            }

            IScope scope = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(currentClass.getEAllReferences()));
            return scope;
        } else if (context.eClass() == META_ATTRIBUTE && reference == META_ATTRIBUTE__ATTRIBUTE) {
            MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            EClass currentClass = metaClass.getType();
            MetaAttribute metaAttr = (MetaAttribute) context;
            for (EReference ref : metaAttr.getPathsegments()) {
                if (ref.eIsProxy()) {
                    ref = (EReference) EcoreUtil.resolve(ref, currentClass);
                    if (ref.eIsProxy()) {
                        // still a proxy?
                        return IScope.NULLSCOPE;
                    }
                }
                currentClass = ref.getEReferenceType();
            }
            final IScope scope = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(currentClass.getEAllAttributes()));
            return scope;
        } else if (context.eClass() == TEXT && reference == META_ATTRIBUTE__PATHSEGMENTS) {
            MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            IScope scope = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(metaClass.getType().getEAllReferences()));
            return scope;
        } else if (context.eClass() == TEXT && reference == META_ATTRIBUTE__ATTRIBUTE) {
            MetaClass metaClass = EcoreUtil2.getContainerOfType(context, MetaClass.class);
            IScope scope = MapBasedScope.createScope(IScope.NULLSCOPE, Scopes.scopedElementsFor(metaClass.getType().getEAllAttributes()));
            return scope;
        }
        return super.getScope(context, reference);
    }
}

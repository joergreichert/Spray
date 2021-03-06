/*
 * generated by Xtext
 */
package org.eclipselabs.spray.xtext.ui.contentassist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.xtext.ui.ITypesProposalProvider;
import org.eclipse.xtext.common.types.xtext.ui.ITypesProposalProvider.Filter;
import org.eclipse.xtext.common.types.xtext.ui.TypeMatchFilters;
import org.eclipse.xtext.common.ui.contentassist.TerminalsProposalProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.ReplacementTextApplier;
import org.eclipselabs.spray.mm.spray.ConnectionInSpray;
import org.eclipselabs.spray.mm.spray.CustomBehavior;
import org.eclipselabs.spray.mm.spray.ShapeCompartmentAssignment;
import org.eclipselabs.spray.mm.spray.ShapeFromDsl;
import org.eclipselabs.spray.mm.spray.ShapePropertyAssignment;
import org.eclipselabs.spray.mm.spray.SprayPackage;
import org.eclipselabs.spray.mm.spray.SprayStyleRef;
import org.eclipselabs.spray.runtime.graphiti.shape.ISprayConnection;
import org.eclipselabs.spray.runtime.graphiti.shape.ISprayShape;
import org.eclipselabs.spray.runtime.graphiti.styles.ISprayStyle;
import org.eclipselabs.spray.xtext.api.IConstants;
import org.eclipselabs.spray.xtext.naming.EscapeKeywordFunction;
import org.eclipselabs.spray.xtext.scoping.AppInjectedAccess;
import org.eclipselabs.spray.xtext.scoping.PackageSelector;
import org.eclipselabs.spray.xtext.services.SprayGrammarAccess;
import org.eclipselabs.spray.xtext.ui.labeling.SprayDescriptionLabelProvider;
import org.eclipselabs.spray.xtext.util.TextBodyFetcher;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import javax.inject.Inject;
import com.google.inject.name.Named;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class SprayProposalProvider extends AbstractSprayProposalProvider {
    @Inject
    private IWorkspaceRoot                root;
    @Inject
    @Named(IConstants.NAME_VALID_ICON_FILE_EXTENSIONS)
    private Set                           validIconFileExtensions;
    @Inject
    private IGlobalScopeProvider          globalScopeProvider;
    @Inject
    private SprayDescriptionLabelProvider descriptionLabelProvider;
    @Inject
    private SprayGrammarAccess            grammar;
    private static final Set<String>      FILTERED_KEYWORDS = Sets.newHashSet("text", "line", "class", "behavior", "style", "custom");

    private IResourceDescriptions         dscriptions       = null;
    @Inject
    private IJvmTypeProvider.Factory      jvmTypeProviderFactory;
    @Inject
    private ITypesProposalProvider        typeProposalProvider;
    @Inject
    private IQualifiedNameConverter       qnConverter;
    @Inject
    private EscapeKeywordFunction         escapeKeywordFunction;
    @Inject
    ITypesProposalProvider                proposalProvider;
    @Inject
    IJvmTypeProvider.Factory              typeProviderFactory;
    @Inject
    private PackageSelector               packageSelector;
    @Inject
    private TextBodyFetcher               textBodyFetcher;
    @Inject
    private TerminalsProposalProvider     terminalsProposalProvider;

    public List<String> listVisibleResources() {
        List<String> result = new ArrayList<String>();
        ResourceDescriptionsProvider serviceProvider = AppInjectedAccess.getit();
        dscriptions = serviceProvider.createResourceDescriptions();
        for (IResourceDescription rd : dscriptions.getAllResourceDescriptions()) {
            for (IEObjectDescription od : rd.getExportedObjects()) {
                if (od.getEClass().getName().startsWith("Shape")) {
                    //                    System.out.println("Shape qualified : [" + od.getQualifiedName() + "] " + od.getEObjectOrProxy().getClass().getName());
                    result.add(od.getName().toString());
                }
            }
        }
        return result;
    }

    @Override
    public void completeMetaClass_Icon(EObject model, Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
        URI uri = model.eResource().getURI();
        if (!uri.isPlatformResource()) {
            return;
        }
        final String projectName = uri.segment(1);
        IPath iconFolderPath = new Path(projectName).append("icons");
        IFolder folder = root.getFolder(iconFolderPath);
        if (!folder.exists()) {
            return;
        }
        try {
            folder.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) {
                    if (resource instanceof IFile) {
                        IFile file = (IFile) resource;
                        if (validIconFileExtensions.contains(file.getFileExtension())) {
                            final String proposalText = "\"" + file.getFullPath().removeFirstSegments(2).toString() + "\"";
                            final StyledString displayString = new StyledString(file.getFullPath().removeFirstSegments(2).toString());
                            final Image image = getImage(file.getLocation());
                            ICompletionProposal proposal = doCreateProposal(proposalText, displayString, image, 0, context);
                            acceptor.accept(proposal);
                        }
                    }
                    return true;
                }
            });
        } catch (CoreException e) {
            ; // ignore
        }
    }

    protected Image getImage(IPath path) {
        URL url = null;
        try {
            url = path.toFile().toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
        final Image image = ImageDescriptor.createFromURL(url).createImage();
        return image;
    }

    public void complete_Import(final EObject model, RuleCall ruleCall, final ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        ConfigurableCompletionProposal proposal = (ConfigurableCompletionProposal) createCompletionProposal("Select element...", context);
        if (proposal == null)
            return;

        proposal.setTextApplier(new ReplacementTextApplier() {
            @Override
            public String getActualReplacementString(ConfigurableCompletionProposal proposal) {
                Display display = context.getViewer().getTextWidget().getDisplay();
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(display.getActiveShell(), descriptionLabelProvider);
                dialog.setTitle("Select element to import");
                dialog.setBlockOnOpen(true);
                dialog.setFilter("*" + context.getPrefix());
                IScope globalElementScope = globalScopeProvider.getScope(model.eResource(), SprayPackage.Literals.META_CLASS__TYPE, null);
                dialog.setElements(Iterables.toArray(globalElementScope.getAllElements(), Object.class));
                dialog.open();

                String result = dialog.getFirstResult() != null ? ((IEObjectDescription) dialog.getFirstResult()).getQualifiedName().toString() : null;
                if (result == null) {
                    result = context.getPrefix();
                } else {
                    result += ";";
                }
                return result;
            }

        });
        proposal.setPriority(600);
        acceptor.accept(proposal);
    };

    @Override
    public void completeKeyword(Keyword keyword, ContentAssistContext contentAssistContext, ICompletionProposalAcceptor acceptor) {
        if (FILTERED_KEYWORDS.contains(keyword.getValue())) {
            // don't propose keyword
            return;
        }
        super.completeKeyword(keyword, contentAssistContext, acceptor);
    }

    @Override
    public void completeJvmParameterizedTypeReference_Type(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        if (EcoreUtil2.getContainerOfType(model, CustomBehavior.class) != null) {
            final IJvmTypeProvider jvmTypeProvider = jvmTypeProviderFactory.createTypeProvider(model.eResource().getResourceSet());
            // Graphiti specific
            final JvmType interfaceToImplement = jvmTypeProvider.findTypeByName(ICustomFeature.class.getName());
            typeProposalProvider.createSubTypeProposals(interfaceToImplement, this, context, SprayPackage.Literals.BEHAVIOR__REALIZED_BY, TypeMatchFilters.canInstantiate(), acceptor);
        } else {
            super.completeJvmParameterizedTypeReference_Type(model, assignment, context, acceptor);
        }
    }

    @Override
    public void complete_JvmTypeReference(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        IJvmTypeProvider typeProvider = typeProviderFactory.findOrCreateTypeProvider(model.eResource().getResourceSet());
        Filter filter = TypeMatchFilters.and(TypeMatchFilters.isPublic(), TypeMatchFilters.canInstantiate());
        if (model instanceof SprayStyleRef) {
            JvmType superType = typeProvider.findTypeByName(ISprayStyle.class.getName());
            proposalProvider.createSubTypeProposals(superType, this, context, SprayPackage.Literals.SPRAY_STYLE_REF__JAVA_STYLE, filter, acceptor);
        }
        if (model instanceof ShapeFromDsl) {
            JvmType superType = typeProvider.findTypeByName(ISprayShape.class.getName());
            proposalProvider.createSubTypeProposals(superType, this, context, SprayPackage.Literals.SHAPE_FROM_DSL__SHAPE, filter, acceptor);
        }
        if (model instanceof ConnectionInSpray) {
            JvmType superType = typeProvider.findTypeByName(ISprayConnection.class.getName());
            proposalProvider.createSubTypeProposals(superType, this, context, SprayPackage.Literals.CONNECTION_IN_SPRAY__CONNECTION, filter, acceptor);
        }
        super.complete_JvmTypeReference(model, ruleCall, context, acceptor);
    }

    @Override
    public void completeImport_ImportedNamespace(EObject model, Assignment assignment, final ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        EObject container = model.eContainer();
        IJavaProject project = packageSelector.getJavaProject(model);
        Set<EPackage> ePackages = new HashSet<EPackage>();
        if (project != null) {
            List<String> alreadyImported = packageSelector.getAlreadyImported(container);
            Iterables.addAll(ePackages, packageSelector.getFilteredEPackages(model));
            createImportProposals(context, acceptor, project, alreadyImported, new ArrayList<EPackage>(ePackages));
        }
    }

    private void createImportProposals(final ContentAssistContext context, ICompletionProposalAcceptor acceptor, IJavaProject javaProject, List<String> alreadyImported, List<EPackage> ePackages) {
        Iterable<EPackage> filteredEPackages = packageSelector.filterAccessibleEPackages(javaProject, ePackages);
        for (EPackage ePackage : filteredEPackages) {
            createImportProposals(context, acceptor, alreadyImported, ePackage);
        }
    }

    private void createImportProposals(final ContentAssistContext context, ICompletionProposalAcceptor acceptor, List<String> alreadyImported, EPackage ePackage) {
        String name = ePackage.getName() + ".*";
        if (!alreadyImported.contains(name)) {
            ConfigurableCompletionProposal proposal = (ConfigurableCompletionProposal) createCompletionProposal(name, context);
            if (proposal != null) {
                String displayString = ePackage.getName() + " (" + ePackage.getNsURI() + ")";
                proposal.setDisplayString(displayString);
                acceptor.accept(proposal);
            }
        }
    }

    @Override
    public void completeShapeDslKey_DslKey(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        if (model instanceof ShapePropertyAssignment) {
            final Predicate<EObject> filterPredicate = textBodyFetcher.getPropertyAssignmentIdsFilter();
            createCompletionProposalsForTextBodyIds(context, acceptor, model, filterPredicate);
        } else if (model instanceof ShapeCompartmentAssignment) {
            final Predicate<EObject> filterPredicate = textBodyFetcher.getCompartmentAssignmentIdsFilter();
            createCompletionProposalsForTextBodyIds(context, acceptor, model, filterPredicate);
        } else {
            super.completeShapeDslKey_DslKey(model, assignment, context, acceptor);
        }
    }

    private void createCompletionProposalsForTextBodyIds(ContentAssistContext context, ICompletionProposalAcceptor acceptor, EObject model, Predicate<EObject> filterPredicate) {
        Set<String> textBodyIds = textBodyFetcher.getTextBodyIds(model, textBodyFetcher.getShapeContainerElementResolver(), filterPredicate);
        createCompletionProposalsForTextBodyIds(context, acceptor, textBodyIds);
    }

    private void createCompletionProposalsForTextBodyIds(ContentAssistContext context, ICompletionProposalAcceptor acceptor, Set<String> textBodyIds) {
        for (String textBodyId : textBodyIds) {
            ConfigurableCompletionProposal proposal = (ConfigurableCompletionProposal) createCompletionProposal(textBodyId, context);
            if (proposal != null) {
                acceptor.accept(proposal);
            }
        }
    }

    @Override
    public void complete_ID(EObject model, RuleCall ruleCall, final ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        if (!(model instanceof ShapePropertyAssignment || model instanceof ShapeCompartmentAssignment)) {
            terminalsProposalProvider.complete_ID(model, ruleCall, context, acceptor);
        }
    }

    @Override
    public void complete_STRING(EObject model, RuleCall ruleCall, final ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
        terminalsProposalProvider.complete_STRING(model, ruleCall, context, acceptor);
    }

    @Override
    protected boolean isKeywordWorthyToPropose(Keyword keyword) {
        return true;
    }
}

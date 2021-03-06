package org.eclipselabs.spray.generator.graphiti.tests.mod4j.mod4j.property

import org.eclipse.xtext.junit4.InjectWith
import org.eclipselabs.spray.generator.graphiti.tests.AbstractSprayGeneratorTest
import org.eclipselabs.spray.generator.graphiti.tests.SprayTestsInjectorProvider
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner2))
@InjectWith(typeof(SprayTestsInjectorProvider))
class AssociationTargetRoleNameSectionBaseTest extends AbstractSprayGeneratorTest {

    @Test
    def void test() {
       val fsa = triggerGenerator("mod4j/mod4j-busmod.spray")
       val keyClass = "DEFAULT_OUTPUTorg/eclipselabs/spray/examples/mod4j/property/AssociationTargetRoleNameSectionBase.java"
       assertTrue(keyClass + " should have been generated", fsa.files.containsKey(keyClass))
       assertEquals(keyClass + " should have the expected content", fsa.files.get(keyClass).toString, expectedContent.toString)
    }

    def private expectedContent() '''
        /*************************************************************************************
         *
         * Generated by Spray PropertySection.xtend
         *
         * This file contains generated and should not be changed.
         * Use the extension point class (the direct subclass of this class) to add manual code
         *
         *************************************************************************************/
        package org.eclipselabs.spray.examples.mod4j.property;
        
        import org.eclipse.emf.ecore.EObject;
        import org.eclipse.emf.transaction.RecordingCommand;
        import org.eclipse.emf.transaction.TransactionalEditingDomain;
        import org.eclipse.graphiti.mm.pictograms.PictogramElement;
        import org.eclipse.graphiti.services.Graphiti;
        import org.eclipse.graphiti.ui.platform.GFPropertySection;
        import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
        import org.eclipse.jface.action.IStatusLineManager;
        import org.eclipse.swt.SWT;
        import org.eclipse.swt.custom.CLabel;
        import org.eclipse.swt.events.FocusEvent;
        import org.eclipse.swt.events.FocusListener;
        import org.eclipse.swt.events.ModifyEvent;
        import org.eclipse.swt.events.ModifyListener;
        import org.eclipse.swt.events.SelectionAdapter;
        import org.eclipse.swt.events.SelectionEvent;
        import org.eclipse.swt.events.SelectionListener;
        import org.eclipse.swt.layout.FormAttachment;
        import org.eclipse.swt.layout.FormData;
        import org.eclipse.swt.widgets.Composite;
        import org.eclipse.swt.widgets.Text;
        import org.eclipse.swt.custom.CCombo;
        import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
        import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
        import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
        import java.util.List;
        import samplepackage.Association;
        import samplepackage.Association;
        
        public abstract class AssociationTargetRoleNameSectionBase extends GFPropertySection implements ITabbedPropertyConstants {
        
            protected Association bc = null;
            protected Text        targetRoleNameWidget;
        
            /**
             * {@inheritDoc}
             */
            @Override
            public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
                super.createControls(parent, tabbedPropertySheetPage);
        
                final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
                final Composite composite = factory.createFlatFormComposite(parent);
                FormData data;
        
                targetRoleNameWidget = factory.createText(composite, "");
                data = new FormData();
                data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
                data.right = new FormAttachment(100, 0);
                data.top = new FormAttachment(0, VSPACE);
                targetRoleNameWidget.setLayoutData(data);
        
                CLabel valueLabel = factory.createCLabel(composite, "TargetRoleName");
                data = new FormData();
                data.left = new FormAttachment(0, 0);
                data.right = new FormAttachment(targetRoleNameWidget, -HSPACE);
                data.top = new FormAttachment(targetRoleNameWidget, 0, SWT.CENTER);
                valueLabel.setLayoutData(data);
            }
        
            /**
             * {@inheritDoc}
             */
            @Override
            public void refresh() {
                targetRoleNameWidget.removeModifyListener(nameListener);
        
                final PictogramElement pe = getSelectedPictogramElement();
                if (pe != null) {
                    final EObject bo = (EObject) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                    // the filter assured, that it is a Association
                    if (bo == null)
                        return;
                    bc = (Association) bo;
                    String value = "";
                    value = bc.getTargetRoleName();
                    targetRoleNameWidget.setText(value == null ? "" : value);
                    targetRoleNameWidget.addModifyListener(nameListener);
                }
            }
        
            private ModifyListener nameListener = new ModifyListener() {
                                                    public void modifyText(ModifyEvent arg0) {
                                                        TransactionalEditingDomain editingDomain = getDiagramEditor().getEditingDomain();
                                                        editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
                                                            @Override
                                                            protected void doExecute() {
                                                                changePropertyValue();
                                                            }
                                                        });
                                                    }
                                                };
        
            protected void changePropertyValue() {
                String newValue = targetRoleNameWidget.getText();
                if (!newValue.equals(bc.getTargetRoleName())) {
                    bc.setTargetRoleName(newValue);
                }
            }
        
        }
    '''
}

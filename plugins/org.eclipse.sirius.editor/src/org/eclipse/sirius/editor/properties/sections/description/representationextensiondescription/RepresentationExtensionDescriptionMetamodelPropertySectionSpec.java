/*******************************************************************************
 * Copyright (c) 2011, 2016 THALES GLOBAL SERVICES.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.editor.properties.sections.description.representationextensiondescription;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.AddFromFilesystemButtonListener;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.AddFromRegistryButtonListener;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.AddFromWorkspaceButtonListener;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.DescriptionMetamodelsUpdater;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.RemoveMetamodelsSelectionButtonListener;
import org.eclipse.sirius.editor.properties.sections.description.representationdescription.RepresentationDescriptionMetamodelPropertySectionSpec;
import org.eclipse.sirius.viewpoint.description.DescriptionPackage;
import org.eclipse.sirius.viewpoint.description.RepresentationExtensionDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

/**
 * A {@link AbstractSiriusPropertySection} for the metamodels tab.
 * 
 * @author <a href="mailto:esteban.dugueperoux@obeo.fr">Esteban Dugueperoux</a>
 */
public class RepresentationExtensionDescriptionMetamodelPropertySectionSpec extends RepresentationDescriptionMetamodelPropertySectionSpec {

    @Override
    protected void addButtons() {
        RepresentationExtensionDescription representationExtensionDescription = (RepresentationExtensionDescription) eObject;
        this.descriptionMetamodelsUpdater = new DescriptionMetamodelsUpdater(representationExtensionDescription, DescriptionPackage.eINSTANCE.getRepresentationExtensionDescription_Metamodel());
        addFromRegistryButton = getWidgetFactory().createButton(composite, "Add from registry", SWT.PUSH);
        addFromRegistryButton.addSelectionListener(new AddFromRegistryButtonListener(this, descriptionMetamodelsUpdater));
        FormData data = new FormData();
        data.top = new FormAttachment(listHeader, 0, SWT.RIGHT);
        data.left = new FormAttachment(metamodelsTable, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.right = new FormAttachment(100);
        addFromRegistryButton.setLayoutData(data);

        addFromWorkspaceButton = getWidgetFactory().createButton(composite, "Add from workspace", SWT.PUSH);
        addFromWorkspaceButton.addSelectionListener(new AddFromWorkspaceButtonListener(this, descriptionMetamodelsUpdater));
        data = new FormData();
        data.top = new FormAttachment(addFromRegistryButton, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.left = new FormAttachment(metamodelsTable, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.right = new FormAttachment(100);
        addFromWorkspaceButton.setLayoutData(data);

        addFromFilesystemButton = getWidgetFactory().createButton(composite, "Add from filesystem", SWT.PUSH);
        addFromFilesystemButton.addSelectionListener(new AddFromFilesystemButtonListener(this, descriptionMetamodelsUpdater));
        data = new FormData();
        data.top = new FormAttachment(addFromWorkspaceButton, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.left = new FormAttachment(metamodelsTable, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.right = new FormAttachment(100);
        addFromFilesystemButton.setLayoutData(data);

        removeMetamodelsSelectionButton = getWidgetFactory().createButton(composite, "Remove", SWT.PUSH);
        removeMetamodelsSelectionButton.addSelectionListener(new RemoveMetamodelsSelectionButtonListener(this, removeMetamodelsSelectionButton, metamodelsTable, descriptionMetamodelsUpdater));
        removeMetamodelsSelectionButton.setEnabled(false);
        data = new FormData();
        data.top = new FormAttachment(addFromFilesystemButton, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.left = new FormAttachment(metamodelsTable, ITabbedPropertyConstants.HMARGIN, SWT.RIGHT);
        data.right = new FormAttachment(100);
        removeMetamodelsSelectionButton.setLayoutData(data);
    }

    @Override
    protected EStructuralFeature getFeature() {
        return DescriptionPackage.eINSTANCE.getRepresentationExtensionDescription_Metamodel();
    }

}

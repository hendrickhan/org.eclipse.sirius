/*******************************************************************************
 * Copyright (c) 2016 Obeo.
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
package org.eclipse.sirius.properties.editor.properties.sections.properties.widgetstyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.sirius.editor.properties.sections.common.AbstractCheckBoxGroupPropertySection;
import org.eclipse.sirius.properties.LabelWidgetStyle;
import org.eclipse.sirius.properties.PropertiesPackage;
import org.eclipse.sirius.properties.WidgetStyle;
import org.eclipse.sirius.viewpoint.FontFormat;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Custom property section for label format to use more appropriate widgets.
 * 
 * @author pcdavid
 */
//CHECKSTYLE:OFF
public class WidgetStyleLabelFontFormatPropertySectionSpec extends AbstractCheckBoxGroupPropertySection {

    private static final String BOLD = "Bold";

    private static final String ITALIC = "Italic";

    private static final String UNDERLINE = "Underline";

    private static final String STRIKE_THROUGH = "Strike through";

    private static final String TOOL_TIP = "The font formatting style to use for the label";

    /**
     * Constructor.
     */
    public WidgetStyleLabelFontFormatPropertySectionSpec() {
        buttonGroup = false;
    }

    @Override
    protected boolean isEqual(List<?> newList) {
        List<String> value = convertPropertiesToUI(((WidgetStyle) eObject).getLabelFontFormat());
        return value.equals(newList);
    }

    @Override
    protected List<?> getChoiceOfValues() {
        List<String> values = new ArrayList<String>();
        values.add(BOLD);
        values.add(ITALIC);
        values.add(UNDERLINE);
        values.add(STRIKE_THROUGH);
        return values;
    }

    protected List<FontFormat> getSelectedValue() {
        List<?> possibleValues = getChoiceOfValues();
        List<Object> selectedValues = new ArrayList<Object>();
        List<FontFormat> fontFormat = new ArrayList<FontFormat>();

        for (int i = 0; i < button.length; i++) {
            if (button[i].getSelection())
                selectedValues.add(possibleValues.get(i));
        }

        if (selectedValues.size() > 0) {
            for (Object selectedValue : selectedValues) {
                if (selectedValue.equals(ITALIC)) {
                    fontFormat.add(FontFormat.ITALIC_LITERAL);
                }
                if (selectedValue.equals(BOLD)) {
                    fontFormat.add(FontFormat.BOLD_LITERAL);
                }
                if (selectedValue.equals(UNDERLINE)) {
                    fontFormat.add(FontFormat.UNDERLINE_LITERAL);
                }
                if (selectedValue.equals(STRIKE_THROUGH)) {
                    fontFormat.add(FontFormat.STRIKE_THROUGH_LITERAL);
                }
            }
        }

        return fontFormat;
    }

    @Override
    protected EStructuralFeature getFeature() {
        return PropertiesPackage.eINSTANCE.getWidgetStyle_LabelFontFormat();
    }

    @Override
    protected String getDefaultLabelText() {
        return "Label Font Format";
    }

    @Override
    protected String getFeatureAsText() {
        String featureText;
        final EStructuralFeature eFeature = getFeature();
        final IItemPropertyDescriptor propertyDescriptor = getPropertyDescriptor(eFeature);
        if (propertyDescriptor != null) {
            featureText = propertyDescriptor.getLabelProvider(eObject).getText(eObject.eGet(eFeature));
            return featureText;
        }
        return getDefaultFeatureAsText();
    }

    @Override
    protected String getDefaultFeatureAsText() {
        String returnStr = "";
        if (eObject instanceof LabelWidgetStyle) {
            for (String str : convertPropertiesToUI(((WidgetStyle) eObject).getLabelFontFormat())) {
                if (returnStr.length() > 0) {
                    returnStr = returnStr + ", " + str;
                } else {
                    returnStr = str;
                }
            }
        }
        return returnStr;
    }

    @Override
    protected String getLabelText() {
        return super.getLabelText() + ":";
    }

    protected EEnum getFeatures() {
        return ViewpointPackage.eINSTANCE.getFontFormat();

    }

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
        super.createControls(parent, tabbedPropertySheetPage);
        FormData data;

        // Create a new composite to add button in fake group
        Composite fakeGroup = getWidgetFactory().createComposite(composite);
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = 0;
        rowLayout.spacing = 5;
        fakeGroup.setLayout(rowLayout);
        data = new FormData();
        data.left = new FormAttachment(0, LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        fakeGroup.setLayoutData(data);

        for (int i = 0; i < button.length; i++) {
            button[i].setParent(fakeGroup);
            button[i].setLayoutData(null);
        }

        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(fakeGroup, -ITabbedPropertyConstants.HSPACE - 20);
        data.top = new FormAttachment(fakeGroup, 0, SWT.CENTER);
        nameLabel.setLayoutData(data);
        nameLabel.setToolTipText(TOOL_TIP);

        data = new FormData();
        CLabel help = getWidgetFactory().createCLabel(composite, "");
        data.top = new FormAttachment(nameLabel, 0, SWT.TOP);
        data.left = new FormAttachment(nameLabel);
        help.setLayoutData(data);
        help.setImage(getHelpIcon());
        help.setToolTipText(TOOL_TIP);

    }

    @Override
    public void handleSelectionChanged() {
        boolean equals = isEqual(getSelectedValues());

        if (!equals) {
            EditingDomain editingDomain = ((IEditingDomainProvider) getPart()).getEditingDomain();
            Object value = getSelectedValue();

            if (eObjectList.size() == 1) {
                // apply the property change to single selected object
                editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, eObject, getFeature(), value));
            } else {
                CompoundCommand compoundCommand = new CompoundCommand();
                /* apply the property change to all selected elements */
                for (Iterator<EObject> i = eObjectList.iterator(); i.hasNext(); ) {
                    EObject nextObject = i.next();
                    compoundCommand.append(SetCommand.create(editingDomain, nextObject, getFeature(), value));
                }
                editingDomain.getCommandStack().execute(compoundCommand);
            }
        }
    }

    protected EAttribute getAttribute() {
        EAttribute attribute = null;
        for (EAttribute eAttribute : PropertiesPackage.eINSTANCE.getWidgetStyle().getEAllAttributes()) {
            if (eAttribute.getEType().equals(getFeatures())) {
                attribute = eAttribute;
                break;
            }
        }
        return attribute;
    }

    public static List<String> convertPropertiesToUI(List<FontFormat> font) {
        List<String> formats = new ArrayList<String>();
        for (FontFormat format : font) {
            if (format.getValue() == 1) {
                formats.add(ITALIC);
            } else if (format.getValue() == 2) {
                formats.add(BOLD);
            } else if (format.getValue() == 3) {
                formats.add(UNDERLINE);
            } else if (format.getValue() == 4) {
                formats.add(STRIKE_THROUGH);
            }
        }

        return formats;
    }


}

/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 *
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
package org.eclipse.sirius.tree.ui.tools.internal.quickoutline;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.sirius.common.ui.tools.api.dialog.quickoutline.QuickOutlineAdapterFactoryLabelProvider;
import org.eclipse.sirius.viewpoint.DRepresentationElement;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.swt.graphics.Image;

/**
 * A Tree Quick Outline specific {@link QuickOutlineAdapterFactoryLabelProvider}
 * .
 * 
 * @author Yann
 */
public class DTreeQuickOutlineLabelProvider extends QuickOutlineAdapterFactoryLabelProvider {

    private ILabelProvider semanticLabelProvider;

    /**
     * Default constructor.
     * 
     * @param adapterFactory
     *            the adapter factory.
     */
    public DTreeQuickOutlineLabelProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
        semanticLabelProvider = new AdapterFactoryLabelProvider(adapterFactory);
    }

    @Override
    public String getText(Object element) {
        String res = null;
        if (element instanceof DRepresentationElement) {
            res = ((DRepresentationElement) element).getName();
        } else if (element instanceof DSemanticDecorator) {
            EObject target = ((DSemanticDecorator) element).getTarget();
            res = semanticLabelProvider.getText(target);
        } else {
            res = super.getText(element);
        }
        return res;
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof DSemanticDecorator) {
            EObject target = ((DSemanticDecorator) element).getTarget();
            return semanticLabelProvider.getImage(target);
        }
        return super.getImage(element);
    }

    @Override
    public void dispose() {
        if (semanticLabelProvider != null) {
            semanticLabelProvider.dispose();
            semanticLabelProvider = null;
        }
        super.dispose();
    }

}

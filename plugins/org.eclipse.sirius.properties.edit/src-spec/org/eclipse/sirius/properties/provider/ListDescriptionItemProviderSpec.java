/**
 * Copyright (c) 2016, 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *
 */
package org.eclipse.sirius.properties.provider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.sirius.properties.ListDescription;

/**
 * Subclass used to not have to modify the generated code.
 *
 * @author sbegaudeau
 */
public class ListDescriptionItemProviderSpec extends ListDescriptionItemProvider {

    /**
     * The constructor.
     *
     * @param adapterFactory
     *            The adapter factory
     */
    public ListDescriptionItemProviderSpec(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    @Override
    public String getText(Object object) {
        String label = ((ListDescription) object).getLabelExpression();
        return label == null || label.length() == 0 ? getString("_UI_ListDescription_type") : //$NON-NLS-1$
                label;
    }

    @Override
    protected CommandParameter createChildParameter(Object feature, Object child) {
        Utils.addNoopNavigationOperations(child);
        return super.createChildParameter(feature, child);
    }
}
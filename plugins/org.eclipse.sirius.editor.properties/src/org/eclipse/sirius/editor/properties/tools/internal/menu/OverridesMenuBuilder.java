/*******************************************************************************
 * Copyright (c) 2017 Obeo.
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
package org.eclipse.sirius.editor.properties.tools.internal.menu;

import org.eclipse.sirius.editor.properties.internal.Messages;
import org.eclipse.sirius.editor.tools.api.menu.AbstractTypeRestrictingMenuBuilder;
import org.eclipse.sirius.properties.PropertiesPackage;

/**
 * The menu builder for new overrides.
 * 
 * @author sbegaudeau
 */
public class OverridesMenuBuilder extends AbstractTypeRestrictingMenuBuilder {

    /**
     * Create the menu.
     */
    public OverridesMenuBuilder() {
        this.addValidType(PropertiesPackage.eINSTANCE.getAbstractOverrideDescription());
    }

    @Override
    public String getLabel() {
        return Messages.OverridesMenuBuilder_label;
    }

    @Override
    public int getPriority() {
        return PropertiesMenuBuilderConstants.OVERRIDES;
    }

}

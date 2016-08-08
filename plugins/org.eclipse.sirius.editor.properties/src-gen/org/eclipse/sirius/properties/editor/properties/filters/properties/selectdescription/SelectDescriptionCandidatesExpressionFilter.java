/*******************************************************************************
 * Copyright (c) 2016 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.properties.editor.properties.filters.properties.selectdescription;

// Start of user code specific imports

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.sirius.editor.properties.filters.common.ViewpointPropertyFilter;
import org.eclipse.sirius.properties.PropertiesPackage;

// End of user code specific imports

/**
 * A filter for the candidatesExpression property section.
 */
public class SelectDescriptionCandidatesExpressionFilter extends ViewpointPropertyFilter {

    /**
     * {@inheritDoc}
     */
    protected EStructuralFeature getFeature() {
        return PropertiesPackage.eINSTANCE.getSelectDescription_CandidatesExpression();
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isRightInputType(Object arg0) {
        return arg0 instanceof org.eclipse.sirius.properties.SelectDescription;
    }

    // Start of user code user methods

    // End of user code user methods

}

/*******************************************************************************
 * Copyright (c) 2010, 2016 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.tests.unit.diagram.format.data.manager.extension;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.ui.tools.api.format.IFormatDataManagerProvider;
import org.eclipse.sirius.diagram.ui.tools.api.format.SiriusFormatDataManager;

/**
 * Sample extension provider.
 * 
 * @author mporhel
 * 
 */
public class SampleDataProvider implements IFormatDataManagerProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean provides(DDiagram diagram) {
        if (diagram instanceof DSemanticDiagram) {
            DSemanticDiagram dSem = (DSemanticDiagram) diagram;
            EObject semanticTarget = dSem.getTarget();
            return semanticTarget instanceof EModelElement && ((EModelElement) semanticTarget).getEAnnotation(SampleManager.SAMPLE_SOURCE) != null;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiriusFormatDataManager getFormatDataManager() {
        return new SampleManager();
    }

}

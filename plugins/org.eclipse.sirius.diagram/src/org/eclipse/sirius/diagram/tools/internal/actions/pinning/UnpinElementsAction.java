/*******************************************************************************
 * Copyright (c) 2009, 2015 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.diagram.tools.internal.actions.pinning;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.tools.api.layout.PinHelper;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

/**
 * An external action which marks diagram elements as "un-pinned" so they can be
 * moved during automatic layout.
 * 
 * @author pcdavid
 */
public class UnpinElementsAction implements IExternalJavaAction {

    @Override
    public boolean canExecute(final Collection<? extends EObject> selections) {
        return selections.size() == 1;
    }

    @Override
    public void execute(final Collection<? extends EObject> selections, final Map<String, Object> parameters) {
        Object viewObj = parameters.get("view"); //$NON-NLS-1$
        PinHelper pinHelper = new PinHelper();
        if (viewObj instanceof DDiagramElement) {
            DDiagramElement dDiagramElement = (DDiagramElement) viewObj;
            pinHelper.markAsUnpinned(dDiagramElement);
        }
    }
}

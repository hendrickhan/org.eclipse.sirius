/*******************************************************************************
 * Copyright (c) 2007, 2018 THALES GLOBAL SERVICES and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.ui.internal.edit.commands;

import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.sirius.diagram.DiagramPackage;

public class DNode3CreateCommand extends DNodeCreateCommand {
    public DNode3CreateCommand(CreateElementRequest req) {
        super(req, DiagramPackage.eINSTANCE.getDNodeContainer());
    }
}

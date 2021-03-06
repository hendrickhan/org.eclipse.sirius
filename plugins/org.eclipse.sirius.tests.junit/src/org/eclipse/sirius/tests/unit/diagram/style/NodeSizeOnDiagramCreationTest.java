/*******************************************************************************
 * Copyright (c) 2010, 2014 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.tests.unit.diagram.style;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.tests.support.api.TestsUtil;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;

/**
 * 
 * @author mporhel
 */
public class NodeSizeOnDiagramCreationTest extends AbstractNodeSizeTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        diagram = (DDiagram) createRepresentation(REPRESENTATION_DESC_NAME, semanticModel);
        assertNotNull(diagram);
        editor = (DiagramEditor) DialectUIManager.INSTANCE.openEditor(session, diagram, new NullProgressMonitor());
        TestsUtil.synchronizationWithUIThread();
    };

}

/*******************************************************************************
 * Copyright (c) 2013, 2018 THALES GLOBAL SERVICES and others.
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
package org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures;

import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.diagram.ui.figures.ShapeCompartmentFigure;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;

public class CombinedFragmentInvisibleResizableCompartmentFigure extends ShapeCompartmentFigure {
    /**
     * Create a new compartment figure without borders. Overridden to remove
     * border margin.
     * 
     * @param title
     *            compartment title.
     * @param mode
     *            mapping mode.
     */
    public CombinedFragmentInvisibleResizableCompartmentFigure(String title, IMapMode mode) {
        super(title, mode);
    }

    /**
     * {@inheritDoc} Overridden to remove border margin.
     */
    @Override
    protected void configureFigure(IMapMode mm) {
        super.configureFigure(mm);
        ScrollPane scrollpane = getScrollPane();

        int mb = mm.DPtoLP(0);
        scrollpane.setBorder(new MarginBorder(mb, mb, mb, mb));
        int sz = mm.DPtoLP(0);
        scrollpane.setMinimumSize(new Dimension(sz, sz));
    }
}

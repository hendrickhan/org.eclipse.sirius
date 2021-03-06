/*******************************************************************************
 * Copyright (c) 2013 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.diagram.sequence.business.internal.refresh;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.sirius.diagram.ui.internal.refresh.edge.SlidableAnchor;

/**
 * Specialized anchor with some customizations for sequence diagrams.
 * 
 * @author mporhel
 */
public class SequenceSlidableAnchor extends SlidableAnchor {

    /**
     * Constructor.
     * 
     * @param owner
     *            the figure that this anchor is associated with.
     * @param pp
     *            the PrecisionPoint that the anchor will initially attach to.
     */
    public SequenceSlidableAnchor(Node owner, PrecisionPoint pp) {
        super(owner, pp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point getLocation(Point ownReference, Point foreignReference) {
        return ownReference.getCopy();
    }
}

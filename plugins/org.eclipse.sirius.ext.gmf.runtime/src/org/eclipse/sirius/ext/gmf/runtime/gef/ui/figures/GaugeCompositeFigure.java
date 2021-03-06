/*******************************************************************************
 * Copyright (c) 2007, 2018 THALES GLOBAL SERVICES and others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.RectangularDropShadowLineBorder;
import org.eclipse.sirius.ext.draw2d.figure.StyledFigure;

/**
 * Figures that paints many gauges.
 * 
 * @author ymortier
 */
public class GaugeCompositeFigure extends AbstractTransparentNode implements StyledFigure { 

    private static final String HORIZONTAL_LITERAL = "HORIZONTAL"; //$NON-NLS-1$

    private static final String VERTICAL_LITERAL = "VERTICAL"; //$NON-NLS-1$
    
    private static final String SQUARE_LITERAL = "SQUARE"; //$NON-NLS-1$

    private String alignment = HORIZONTAL_LITERAL;
    
    private List<GaugeSectionFigure> gauges;

    /**
     * Create a new {@link GaugeCompositeFigure}.
     */
    public GaugeCompositeFigure() {
        this.setLayoutManager(new XYLayout());
        this.gauges = new ArrayList<GaugeSectionFigure>();
        this.setBorder(new RectangularDropShadowLineBorder());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.Figure#setBounds(org.eclipse.draw2d.geometry.Rectangle)
     */
    @Override
    public void setBounds(final Rectangle rect) {
        super.setBounds(rect);
        this.computeChildrenBounds(rect);
    }

    /**
     * Set a new alignment.
     * 
     * @param alignment
     *            new alignment.
     * @since 0.9.0
     */
    public void setAlignment(final String alignment) {
        this.alignment = alignment;
    }

    /**
     * Returns the gauge.
     * 
     * @return the gauge.
     */
    public List<GaugeSectionFigure> getGauges() {
        return Collections.unmodifiableList(gauges);
    }

    /**
     * Returns the gauge at the specified index.
     * 
     * @param index
     *            index of the gauge to return.
     * @return the gauge at the specified index.
     */
    public GaugeSectionFigure getGaugeAt(final int index) {
        return this.gauges.get(index);
    }

    /**
     * Adds a gauge.
     */
    public void addGauge() {
        final GaugeSectionFigure gauge = GaugeSectionFigure.createDefaultSection();
        this.add(gauge, 0);
        this.gauges.add(gauge);
        this.computeChildrenBounds(this.getBounds());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    private void computeChildrenBounds(final Rectangle rect) {
        final int borderInsetsH = (getBorder() == null || getBorder().getInsets(this) == null) ? 0 : getBorder().getInsets(this).left + getBorder().getInsets(this).right;
        final int borderInsetsV = (getBorder() == null || getBorder().getInsets(this) == null) ? 0 : getBorder().getInsets(this).top + getBorder().getInsets(this).bottom;

        Dimension childDimension = null;

        int sideSize = -1;
        int vSideSize = -1;

        int nbGauges = this.gauges.size();
        if (nbGauges == 0) {
            nbGauges = 1;
        }

        switch (this.alignment) {
        case VERTICAL_LITERAL:
            childDimension = new Dimension(rect.width - borderInsetsH, (rect.height - borderInsetsV) / nbGauges);
            break;
        case HORIZONTAL_LITERAL:
            childDimension = new Dimension((rect.width - borderInsetsH) / nbGauges, rect.height - borderInsetsV);
            break;
        case SQUARE_LITERAL:
        default:
            // ... square ...
            sideSize = (int) Math.ceil(Math.sqrt(nbGauges));
            // adjust v size
            vSideSize = sideSize;
            if (nbGauges != (sideSize * sideSize) && nbGauges % sideSize == 0) {
                vSideSize--;
            }
            sideSize = sideSize <= 0 ? 1 : sideSize;
            vSideSize = vSideSize <= 0 ? 1 : vSideSize;
            childDimension = new Dimension((rect.width - borderInsetsH) / sideSize, (rect.height - borderInsetsV) / vSideSize);
            break;
        }

        List<GaugeSectionFigure> sections = this.getGauges();
        if (!(sections instanceof RandomAccess)) {
            // Convert sections to an ArrayList to improve performance.
            sections = new ArrayList<GaugeSectionFigure>(this.getGauges());
        }
        for (int i = 0; i < sections.size(); i++) {
            final GaugeSectionFigure figure = sections.get(i);
            Point p = null;
            switch (this.alignment) {
            case VERTICAL_LITERAL:
                p = new Point(0, childDimension.height * i);
                break;
            case HORIZONTAL_LITERAL:
                p = new Point(childDimension.width * i, 0);
                break;
            case SQUARE_LITERAL:
            default:
                p = new PrecisionPoint(childDimension.width * (i % sideSize), childDimension.height * (Math.floor((double) i / sideSize) % vSideSize));
                break;
            }
            figure.setBounds(new Rectangle(p, childDimension));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransparent(boolean transparent) {
        super.setTransparent(transparent);
        for (GaugeSectionFigure section : gauges) {
            section.setTransparent(transparent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSiriusAlpha(int alpha) {
        super.setSiriusAlpha(alpha);
        for (GaugeSectionFigure section : gauges) {
            section.setSiriusAlpha(alpha);
        }
    }
}

/*******************************************************************************
 * Copyright (c) 2007, 2008 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.diagram.ui.internal.edit.parts;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.ui.edit.api.part.AbstractNotSelectableShapeNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramBorderNodeEditPart;
import org.eclipse.sirius.diagram.ui.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramBorderNodeEditPartOperation;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramElementEditPartOperation;
import org.eclipse.sirius.diagram.ui.edit.internal.part.DiagramNodeEditPartOperation;
import org.eclipse.sirius.diagram.ui.internal.edit.policies.FixedLayoutEditPolicy;
import org.eclipse.sirius.ext.draw2d.figure.ODesignEllipseFigure;
import org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures.AirStyleDefaultSizeNodeFigure;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;

/**
 * @was-generated
 */
public class EllipseEditPart extends AbstractNotSelectableShapeNodeEditPart implements IStyleEditPart {

    /**
     * @see org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart#refreshVisuals()
     * @not-generated
     */
    @Override
    protected void refreshVisuals() {
        super.refreshVisuals();
        if (this.resolveSemanticElement() instanceof org.eclipse.sirius.diagram.Ellipse) {
            org.eclipse.sirius.diagram.Ellipse ellipse = (org.eclipse.sirius.diagram.Ellipse) this.resolveSemanticElement();
            int borderSize = 0;
            if (ellipse.getBorderSize() != null) {
                borderSize = ellipse.getBorderSize().intValue();
            }
            this.getPrimaryShape().setLineWidth(borderSize);
            DiagramNodeEditPartOperation.refreshFigure(this);
            DiagramElementEditPartOperation.refreshLabelAlignment(((GraphicalEditPart) getParent()).getContentPane(), ellipse);
        }
    }

    /**
     * @not-generated
     * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart#refreshBackgroundColor()
     */
    @Override
    protected void refreshBackgroundColor() {
        if (getMetamodelType().isInstance(resolveSemanticElement())) {
            org.eclipse.sirius.diagram.Ellipse ellipse = (org.eclipse.sirius.diagram.Ellipse) this.resolveSemanticElement();
            if (ellipse.getColor() != null) {
                this.getPrimaryShape().setBackgroundColor(VisualBindingManager.getDefault().getColorFromRGBValues(ellipse.getColor()));
            }
        }
    }

    /**
     * @not-generated
     * @see org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart#refreshForegroundColor()
     */
    @Override
    protected void refreshForegroundColor() {
        if (getMetamodelType().isInstance(resolveSemanticElement())) {
            org.eclipse.sirius.diagram.Ellipse ellipse = (org.eclipse.sirius.diagram.Ellipse) this.resolveSemanticElement();
            if (ellipse.getBorderColor() != null) {
                this.getPrimaryShape().setForegroundColor(VisualBindingManager.getDefault().getColorFromRGBValues(ellipse.getBorderColor()));
            }
        }
    }

    /**
     * @was-generated
     */
    public static final int VISUAL_ID = 3016;

    /**
     * @was-generated
     */
    protected IFigure contentPane;

    /**
     * @was-generated
     */
    protected IFigure primaryShape;

    /**
     * @was-generated
     */
    public EllipseEditPart(View view) {
        super(view);
    }

    /**
     * @not-generated : prevent drag of elements
     */
    @Override
    public DragTracker getDragTracker(Request request) {
        return getParent().getDragTracker(request);
    }

    /**
     * @not-generated no edit policies brings better editing :)
     */
    @Override
    protected void createDefaultEditPolicies() {
        // Do nothing.
    }

    /**
     * @not-generated
     */
    protected LayoutEditPolicy createLayoutEditPolicy() {
        return new FixedLayoutEditPolicy();
    }

    /**
     * @not-generated
     */
    protected IFigure createNodeShape() {
        ODesignEllipseFigure ellipse = new ODesignEllipseFigure();
        EditPart parent = this.getParent();
        if (parent instanceof IDiagramBorderNodeEditPart) {
            DiagramBorderNodeEditPartOperation.updateTransparencyMode((IDiagramBorderNodeEditPart) parent, ellipse);
        }
        return primaryShape = ellipse;
    }

    /**
     * @was-generated
     */
    public Ellipse getPrimaryShape() {
        return (Ellipse) primaryShape;
    }

    /**
     * @not-generated
     */
    protected NodeFigure createNodePlate() {
        DefaultSizeNodeFigure result = new AirStyleDefaultSizeNodeFigure(getMapMode().DPtoLP(40), getMapMode().DPtoLP(40));
        return result;
    }

    /**
     * @was-generated
     */
    @Override
    public EditPolicy getPrimaryDragEditPolicy() {
        EditPolicy result = super.getPrimaryDragEditPolicy();
        if (result instanceof ResizableEditPolicy) {
            ResizableEditPolicy ep = (ResizableEditPolicy) result;
            ep.setResizeDirections(PositionConstants.NONE);
        }
        return result;
    }

    /**
     * Creates figure for this edit part.
     * 
     * Body of this method does not depend on settings in generation model so
     * you may safely remove <i>generated</i> tag and modify it.
     * 
     * @was-generated
     */
    @Override
    protected NodeFigure createNodeFigure() {
        NodeFigure figure = createNodePlate();
        figure.setLayoutManager(new StackLayout());
        IFigure shape = createNodeShape();
        figure.add(shape);
        contentPane = setupContentPane(shape);
        return figure;
    }

    /**
     * Default implementation treats passed figure as content pane. Respects
     * layout one may have set for generated figure.
     * 
     * @param nodeShape
     *            instance of generated figure class
     * @was-generated
     */
    protected IFigure setupContentPane(IFigure nodeShape) {
        return nodeShape; // use nodeShape itself as contentPane
    }

    /**
     * @was-generated
     */
    @Override
    public IFigure getContentPane() {
        if (contentPane != null) {
            return contentPane;
        }
        return super.getContentPane();
    }

    /**
     * @not-generated
     */
    protected Class<?> getMetamodelType() {
        return org.eclipse.sirius.diagram.Ellipse.class;
    }
}

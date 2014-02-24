/*******************************************************************************
 * Copyright (c) 2007, 2010 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.diagram.edit.internal.part;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.core.listener.DiagramEventBroker;
import org.eclipse.gmf.runtime.diagram.core.listener.NotificationListener;
import org.eclipse.gmf.runtime.diagram.core.listener.NotificationPreCommitListener;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.ContainerStyle;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DDiagramElementContainer;
import org.eclipse.sirius.diagram.DEdge;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.DNodeListElement;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.DiagramPackage;
import org.eclipse.sirius.diagram.EdgeStyle;
import org.eclipse.sirius.diagram.NodeStyle;
import org.eclipse.sirius.diagram.business.api.helper.display.DisplayServiceManager;
import org.eclipse.sirius.diagram.business.api.query.EObjectQuery;
import org.eclipse.sirius.diagram.business.internal.experimental.sync.DDiagramElementSynchronizer;
import org.eclipse.sirius.diagram.business.internal.query.StyleConfigurationQuery;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.edit.api.part.IDiagramBorderNodeEditPart;
import org.eclipse.sirius.diagram.edit.api.part.IDiagramElementEditPart;
import org.eclipse.sirius.diagram.edit.api.part.IDiagramNameEditPart;
import org.eclipse.sirius.diagram.edit.api.part.IDiagramNodeEditPart;
import org.eclipse.sirius.diagram.edit.api.part.IStyleEditPart;
import org.eclipse.sirius.diagram.part.SiriusDiagramEditorPlugin;
import org.eclipse.sirius.diagram.tools.api.graphical.edit.styles.IStyleConfigurationRegistry;
import org.eclipse.sirius.diagram.tools.api.graphical.edit.styles.StyleConfiguration;
import org.eclipse.sirius.diagram.tools.api.part.IDiagramDialectGraphicalViewer;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SiriusWrapLabel;
import org.eclipse.sirius.diagram.ui.tools.api.figure.StyledFigure;
import org.eclipse.sirius.diagram.ui.tools.internal.commands.SemanticChangedCommand;
import org.eclipse.sirius.ecore.extender.business.api.permission.IPermissionAuthority;
import org.eclipse.sirius.ecore.extender.business.api.permission.PermissionAuthorityRegistry;
import org.eclipse.sirius.ui.tools.api.color.VisualBindingManager;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.DStylizable;
import org.eclipse.sirius.viewpoint.DView;
import org.eclipse.sirius.viewpoint.LabelStyle;
import org.eclipse.sirius.viewpoint.RGBValues;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.Style;
import org.eclipse.sirius.viewpoint.ViewpointPackage;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.Iterables;

/**
 * Implementation of {@link IDiagramElementEditPart}.
 * 
 * @author ymortier
 */
public final class DiagramElementEditPartOperation {

    /**
     * Avoid instantiation.
     */
    private DiagramElementEditPartOperation() {
        // empty.
    }

    /**
     * Returns the diagram element that is represented by <code>self</code>.
     * 
     * @param self
     *            the edit part.
     * @return the diagram element that is represented by <code>self</code>.
     */
    public static DDiagramElement resolveDiagramElement(final IDiagramElementEditPart self) {
        final EObject semanticHost = self.resolveSemanticElement();
        if (semanticHost instanceof DDiagramElement) {
            return (DDiagramElement) semanticHost;
        }
        return null;
    }

    /**
     * Register the edit part with the semantic element.
     * 
     * @param self
     *            the edit part
     */
    public static void registerModel(final IDiagramElementEditPart self) {
        final EditPartViewer viewer = self.getViewer();
        if (viewer instanceof IDiagramDialectGraphicalViewer) {
            final EObject semantic = self.resolveTargetSemanticElement();
            if (semantic != null) {
                ((IDiagramDialectGraphicalViewer) viewer).registerEditPartForSemanticElement(semantic, self);
            }
        }
    }

    /**
     * Unregister the edit part with the semantic element.
     * 
     * @param self
     *            the edit part
     */
    public static void unregisterModel(final IDiagramElementEditPart self) {
        final EditPartViewer viewer = self.getViewer();
        if (viewer instanceof IDiagramDialectGraphicalViewer) {
            final EObject semantic = self.resolveTargetSemanticElement();
            IDiagramDialectGraphicalViewer gViewer = (IDiagramDialectGraphicalViewer) viewer;
            if (semantic != null) {
                gViewer.unregisterEditPartForSemanticElement(semantic, self);
            } else {
                gViewer.unregisterEditPart(self);
            }
        }
    }

    /**
     * Returns the target value of the {@link DSemanticDecorator} that is
     * represented by the <code>self</code> edit part.
     * 
     * @param self
     *            the edit part.
     * @return the target value of the {@link DSemanticDecorator} that is
     *         represented by this edit part.
     */
    public static EObject resolveTargetSemanticElement(final IDiagramElementEditPart self) {
        final EObject semanticHost = self.resolveSemanticElement();
        if (semanticHost instanceof DSemanticDecorator) {
            return ((DSemanticDecorator) semanticHost).getTarget();
        }
        return null;
    }

    /**
     * Creates a listener for semantic elements.
     * 
     * @param self
     *            the edit part.
     * @return the listener to install on semantic elements.
     */
    public static NotificationPreCommitListener createEAdpaterSemanticElements(final IDiagramElementEditPart self) {

        return new NotificationPreCommitListener() {

            public Command transactionAboutToCommit(final Notification msg) {
                return new SemanticChangedCommand(self.getEditingDomain(), self, msg);
            }

        };
    }

    /**
     * This method is invoked when one of the semantic elements of a Diagram
     * elements throws a notification.
     * 
     * @param self
     *            the edit part.
     * @param message
     *            the notification.
     */
    public static void semanticChanged(final IDiagramElementEditPart self, final Notification message) {
        final IPermissionAuthority auth = PermissionAuthorityRegistry.getDefault().getPermissionAuthority(self.getEditingDomain().getResourceSet());
        if (auth.canEditInstance(self.resolveSemanticElement())) {
            switch (message.getEventType()) {
            /*
             * PLEASE do not tell to refresh the diagrams on a remove event or
             * the element deletion will fail when another diagram is opened.
             */
            case Notification.SET:
            case Notification.UNSET:
            case Notification.ADD:
            case Notification.ADD_MANY:
            case Notification.MOVE:
                DiagramElementEditPartOperation.refreshSemantic(self);
                DiagramElementEditPartOperation.refreshLabelIcon(self);
                break;
            case Notification.REMOVE:
            case Notification.REMOVE_MANY:
            default:
                break;
            }
        }
    }

    private static void refreshLabelIcon(IDiagramElementEditPart self) {
        if (self instanceof IDiagramNodeEditPart && ((IDiagramNodeEditPart) self).getNodeLabel() != null && ((IDiagramNodeEditPart) self).getNodeLabel().getParent() != null) {
            DiagramElementEditPartOperation.refreshIcon(((IDiagramNodeEditPart) self).getNodeLabel(), self.getLabelIcon());
        } else if (self instanceof IDiagramBorderNodeEditPart && ((IDiagramBorderNodeEditPart) self).getNodeLabel() != null && ((IDiagramBorderNodeEditPart) self).getNodeLabel().getParent() != null) {
            DiagramElementEditPartOperation.refreshIcon(((IDiagramBorderNodeEditPart) self).getNodeLabel(), self.getLabelIcon());
        } else if (self instanceof IDiagramNameEditPart) {
            DiagramElementEditPartOperation.refreshIcon((IDiagramNameEditPart) self);
        } else {
            for (IDiagramNameEditPart nameEditPart : Iterables.filter(self.getChildren(), IDiagramNameEditPart.class)) {
                DiagramElementEditPartOperation.refreshIcon(nameEditPart);
            }
        }
    }

    private static void refreshIcon(SiriusWrapLabel figure, Image icon) {
        if (figure != null) {
            figure.setIcon(icon);
        }
    }

    private static void refreshIcon(IDiagramNameEditPart self) {
        IFigure figure = self.getFigure();

        if (figure != null) {
            if (figure instanceof SiriusWrapLabel) {
                ((SiriusWrapLabel) figure).setIcon(self.getLabelIcon());
            } else {
                ((Label) figure).setIcon(self.getLabelIcon());
            }
        }
    }

    private static void refreshSemantic(final IDiagramElementEditPart self) {
        // Step 1: get the DDiagramElement corresponding to this editpart
        final DDiagramElement dde = self.resolveDiagramElement();

        // Step 2: refresh the corresponding DDiagramElement using the
        // DDiagramElementSynchronizer
        DDiagram diagram = dde == null ? null : dde.getParentDiagram();
        if (dde != null && diagram instanceof DSemanticDiagram) {
            DSemanticDiagram semDiag = (DSemanticDiagram) diagram;
            // If the diagram has no target, this means that it will be deleted.
            // There is no need to refresh
            if (semDiag.getTarget() != null) {
                Session session = SessionManager.INSTANCE.getSession(semDiag.getTarget());
                final DDiagramElementSynchronizer sync = new DDiagramElementSynchronizer(semDiag, session.getInterpreter(), SiriusPlugin.getDefault().getModelAccessorRegistry()
                        .getModelAccessor(semDiag.getTarget()));

                // refresh diagram element.
                sync.refresh(dde);
            }
        }
    }

    /**
     * Returns the listener to install on the diagram element.
     * 
     * @param self
     *            the edit part.
     * @return the created listener.
     */
    public static NotificationListener createEApdaterDiagramElement(final IDiagramElementEditPart self) {
        return new NotificationListener() {

            public void notifyChanged(Notification msg) {
                DiagramElementEditPartOperation.diagramElementChanged(self, msg);
            }
        };

    }

    /**
     * This method is invoked when the diagram element of the specified edit
     * part is modified.
     * 
     * @param self
     *            the edit part.
     * @param notification
     *            the message.
     */
    private static void diagramElementChanged(final IDiagramElementEditPart self, final Notification notification) {
        if (!self.isActive() || !(self instanceof NotificationListener) || hasDanglingNotationView(self)) {
            // An inactive part should not try to react to changes in the
            // underlying model.
            return;
        }
        final DiagramEventBroker broker = DiagramElementEditPartOperation.getDiagramEventBroker(self);
        if (DiagramPackage.eINSTANCE.getDNode_OwnedStyle().equals(notification.getFeature())) {
            if (notification.getOldValue() instanceof NodeStyle) {
                final NodeStyle oldNodeStyle = (NodeStyle) notification.getOldValue();
                broker.removeNotificationListener(oldNodeStyle, (NotificationListener) self);
            }
            if (notification.getNewValue() instanceof NodeStyle) {
                final NodeStyle newNodeStyle = (NodeStyle) notification.getNewValue();
                broker.addNotificationListener(newNodeStyle, (NotificationListener) self);
            }
            self.refresh();
        } else if (DiagramPackage.eINSTANCE.getDDiagramElementContainer_OwnedStyle().equals(notification.getFeature())) {
            if (notification.getOldValue() instanceof ContainerStyle) {
                final ContainerStyle oldContainerStyle = (ContainerStyle) notification.getOldValue();
                broker.removeNotificationListener(oldContainerStyle, (NotificationListener) self);
            }
            if (notification.getNewValue() instanceof ContainerStyle) {
                final ContainerStyle newContainerStyle = (ContainerStyle) notification.getNewValue();
                broker.addNotificationListener(newContainerStyle, (NotificationListener) self);
            }
            self.refresh();
        } else if (DiagramPackage.eINSTANCE.getDEdge_OwnedStyle().equals(notification.getFeature())) {
            if (notification.getOldValue() instanceof EdgeStyle) {
                final EdgeStyle oldEdgeStyle = (EdgeStyle) notification.getOldValue();
                broker.removeNotificationListener(oldEdgeStyle, (NotificationListener) self);
            }
            if (notification.getNewValue() instanceof EdgeStyle) {
                final EdgeStyle newEdgeStyle = (EdgeStyle) notification.getNewValue();
                broker.addNotificationListener(newEdgeStyle, (NotificationListener) self);
            }
            self.refresh();
        }
    }

    private static boolean hasDanglingNotationView(IDiagramElementEditPart self) {
        boolean danglingView = false;
        View notationView = self.getNotationView();

        danglingView = notationView == null || notationView.eContainer() == null;
        if (!danglingView && notationView instanceof Edge) {
            Edge edge = (Edge) notationView;
            danglingView = edge.getSource() == null || edge.getTarget() == null;
        }
        return danglingView;
    }

    /**
     * Resolves all semantic elements that are represented by this diagram
     * element.
     * 
     * @param self
     *            the edit part.
     * @return all semantic elements that are represented by this diagram
     *         element.
     */
    public static List<EObject> resolveAllSemanticElements(final IDiagramElementEditPart self) {
        final DDiagramElement diagramElement = self.resolveDiagramElement();
        if (diagramElement != null) {
            return diagramElement.getSemanticElements();
        }
        return Collections.emptyList();
    }

    /**
     * Returns the edit part that represents the style of the diagram element
     * that is represented by <code>self</code> or <code>null</code> if no edit
     * part can be found.
     * 
     * @param self
     *            the edit part of the diagram element.
     * @return the edit part that represents the style of the diagram element
     *         that is represented by <code>self</code> or <code>null</code> if
     *         no edit part can be found.
     */
    public static IStyleEditPart getStyleEditPart(final IDiagramElementEditPart self) {
        IStyleEditPart result = null;
        final Iterator<?> iterChildren = self.getChildren().iterator();
        while (iterChildren.hasNext() && result == null) {
            final EditPart nextChild = (EditPart) iterChildren.next();
            if (nextChild instanceof IStyleEditPart) {
                result = (IStyleEditPart) nextChild;
            }
        }
        return result;
    }

    /**
     * Activates the edit part.
     * 
     * @param self
     *            the edit part to activate.
     */
    public static void activate(final IDiagramElementEditPart self) {
        final Iterator<EObject> iterSemanticElements = self.resolveAllSemanticElements().iterator();
        final DiagramEventBroker broker = DiagramElementEditPartOperation.getDiagramEventBroker(self);
        // To refresh navigate decorator, listens add/remove of DDiagram on
        // DView.ownedRepresentations references of all active viewpoints
        addNavigateDecoratorRefresher(self, broker);
        while (iterSemanticElements.hasNext()) {
            final EObject semantic = iterSemanticElements.next();
            if (self.getEAdapterSemanticElements() != null) {
                broker.addNotificationListener(semantic, self.getEAdapterSemanticElements());
            }
            if (self.getEditModeListener() != null) {
                broker.addNotificationListener(semantic, self.getEditModeListener());
            }
            if (semantic.eContainer() != null && self.getEditModeListener() != null) {
                // Add listener for container to update the edit mode when the
                // element gets deleted.
                broker.addNotificationListener(semantic.eContainer(), self.getEditModeListener());
            }
        }
        // Listen the viewpoint element.
        final EObject element = self.resolveDiagramElement();
        if (element != null && self.getEAdapterDiagramElement() != null) {
            broker.addNotificationListener(element, self.getEAdapterDiagramElement());
        }
        // We must listen to the style
        if (element instanceof DNode) {
            final DNode viewNode = (DNode) element;
            if (viewNode.getOwnedStyle() != null && self instanceof NotificationListener) {
                broker.addNotificationListener(viewNode.getOwnedStyle(), (NotificationListener) self);
            }
        } else if (element instanceof DDiagramElementContainer && self instanceof NotificationListener) {
            final DDiagramElementContainer viewPointElementContainer = (DDiagramElementContainer) element;
            if (viewPointElementContainer.getOwnedStyle() != null) {
                broker.addNotificationListener(viewPointElementContainer.getOwnedStyle(), (NotificationListener) self);
            }
        } else if (element instanceof DEdge) {
            final DEdge viewEdge = (DEdge) element;
            if (viewEdge.getOwnedStyle() != null && self instanceof NotificationListener) {
                broker.addNotificationListener(viewEdge.getOwnedStyle(), (NotificationListener) self);
            }
        } else if (element instanceof DNodeListElement) {
            final DNodeListElement dNodeListElement = (DNodeListElement) element;
            if (self instanceof NotificationListener) {
                broker.addNotificationListener(dNodeListElement.getOwnedStyle(), (NotificationListener) self);
            }
        }
    }

    private static void addNavigateDecoratorRefresher(IDiagramElementEditPart self, DiagramEventBroker broker) {
        EObject semanticElement = self.resolveSemanticElement();
        Session session = new EObjectQuery(semanticElement).getSession();
        if (session != null && self instanceof NotificationListener) {
            for (DView dView : session.getSelectedViews()) {
                broker.addNotificationListener(dView, ViewpointPackage.Literals.DVIEW__OWNED_REPRESENTATIONS, (NotificationListener) self);
            }
        }
    }

    private static void removeNavigateDecoratorRefresher(IDiagramElementEditPart self, DiagramEventBroker broker) {
        EObject semanticElement = self.resolveSemanticElement();
        Session session = new EObjectQuery(semanticElement).getSession();
        if (session != null && self instanceof NotificationListener) {
            for (DView dView : session.getSelectedViews()) {
                broker.removeNotificationListener(dView, ViewpointPackage.Literals.DVIEW__OWNED_REPRESENTATIONS, (NotificationListener) self);
            }
        }
    }

    private static DiagramEventBroker getDiagramEventBroker(final IDiagramElementEditPart self) {
        final TransactionalEditingDomain theEditingDomain = self.getEditingDomain();
        if (theEditingDomain != null) {
            return DiagramEventBroker.getInstance(theEditingDomain);
        }
        return null;
    }

    /**
     * Deactivated the edit part.
     * 
     * @param self
     *            the edit part to deactivate.
     */
    public static void deactivate(final IDiagramElementEditPart self) {
        if (self.isActive()) {
            final DiagramEventBroker broker = DiagramElementEditPartOperation.getDiagramEventBroker(self);
            List<EObject> resolveAllSemanticElements = self.resolveAllSemanticElements();
            if (resolveAllSemanticElements != null) {
                final Iterator<EObject> iterSemanticElements = resolveAllSemanticElements.iterator();
                removeNavigateDecoratorRefresher(self, broker);
                while (iterSemanticElements.hasNext()) {
                    final EObject semantic = iterSemanticElements.next();
                    DiagramElementEditPartOperation.removeListener(broker, semantic, self.getEAdapterSemanticElements());
                    DiagramElementEditPartOperation.removeListener(broker, semantic, self.getEditModeListener());
                    // remove this try/catch once the offline mode
                    // will be supported
                    try {
                        if (semantic.eContainer() != null) {
                            // Remove listener for container to do some specific
                            // things on remove
                            DiagramElementEditPartOperation.removeListener(broker, semantic.eContainer(), self.getEditModeListener());
                        }
                    } catch (IllegalStateException e) {
                        // An issue has been encountered while connecting to
                        // remote
                        if (SiriusDiagramEditorPlugin.getInstance().isDebugging()) {
                            SiriusDiagramEditorPlugin.getInstance().getLog().log(new Status(IStatus.WARNING, SiriusDiagramEditorPlugin.ID, "Error while connecting to remote CDO server"));
                        }
                    }
                }
                final DStylizable stylizable = self.resolveDiagramElement();
                if (stylizable != null && stylizable.getStyle() != null && self instanceof NotificationListener) {
                    DiagramElementEditPartOperation.removeListener(broker, stylizable.getStyle(), (NotificationListener) self);
                }
                if (stylizable != null) {
                    DiagramElementEditPartOperation.removeListener(broker, stylizable, self.getEAdapterDiagramElement());
                }
            }
        }
    }

    private static void removeListener(final DiagramEventBroker broker, final EObject listened, final NotificationPreCommitListener listener) {
        final boolean oldDeliver = listened.eDeliver();
        listened.eSetDeliver(true);
        broker.removeNotificationListener(listened, listener);
        listened.eSetDeliver(oldDeliver);
    }

    private static void removeListener(final DiagramEventBroker broker, final EObject listened, final NotificationListener listener) {
        final boolean oldDeliver = listened.eDeliver();
        listened.eSetDeliver(true);
        broker.removeNotificationListener(listened, listener);
        listened.eSetDeliver(oldDeliver);
    }

    /**
     * Remove the invisible elements from a list of children.
     * 
     * @param modelChildren
     *            list of children.
     */
    public static void removeInvisibleElements(final List<?> modelChildren) {
        DDiagram parentDiagram = null;
        final Iterator<?> iterModel = modelChildren.iterator();
        while (iterModel.hasNext()) {
            final Object object = iterModel.next();

            if (object instanceof View) {
                final View view = (View) object;
                final EObject element = view.getElement();
                if (element instanceof DDiagramElement) {
                    final DDiagramElement diagramElement = (DDiagramElement) element;
                    /* compute parent diagram only once */
                    if (parentDiagram == null) {
                        parentDiagram = diagramElement.getParentDiagram();
                    }
                    if (isHidden(diagramElement, parentDiagram)) {
                        iterModel.remove();
                    }
                }
            }
        }
    }

    private static boolean isHidden(DDiagramElement diagramElement, DDiagram parentDiagram) {
        final EObject diagramElementContainer = diagramElement.eContainer();
        /*
         * if direct container is null we could in a rollback, so we should not
         * delete the child
         */
        if (diagramElementContainer != null) {
            if (!DisplayServiceManager.INSTANCE.getDisplayService().isDisplayed(parentDiagram, diagramElement)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Returns the icon of the specified diagram element.
     * 
     * @param self
     *            the edit part.
     * @return the icon of the specified diagram element.
     */
    public static Image getLabelIcon(final IDiagramElementEditPart self) {
        if (self.getMetamodelType().isInstance(self.resolveSemanticElement())) {
            final DDiagramElement element = self.resolveDiagramElement();
            final DiagramElementMapping mapping = element.getDiagramElementMapping();
            final Style style = element.getStyle();
            final StyleConfiguration styleConfiguration = IStyleConfigurationRegistry.INSTANCE.getStyleConfiguration(mapping, style);
            final Image image = new StyleConfigurationQuery(styleConfiguration).getLabelIcon(element, self);
            return image;
        }
        return null;
    }

    /**
     * Returns the figure of the style.
     * 
     * @param figure
     *            any Figure.
     * @return a figure corresponding to a {@link Style} element in the
     *         metamodel.
     */
    public static StyledFigure getStyledFigure(final IFigure figure) {
        StyledFigure styledFigure = null;
        if (figure instanceof StyledFigure) {
            styledFigure = (StyledFigure) figure;
        } else {
            for (IFigure childFigure : Iterables.filter(figure.getChildren(), IFigure.class)) {
                styledFigure = DiagramElementEditPartOperation.getStyledFigure(childFigure);
                if (styledFigure != null) {
                    break;
                }
            }
        }
        return styledFigure;
    }

    /**
     * Refresh the font using the eObject.
     * 
     * @param self
     *            the edit part.
     * @param eObj
     *            a {@link DStylizable} eObject.
     * @param figure
     *            figure on which the method should set the font.
     */
    public static void refreshFont(final IDiagramElementEditPart self, final EObject eObj, final IFigure figure) {
        if (eObj instanceof DStylizable) {
            final Style style = ((DStylizable) eObj).getStyle();
            if (style instanceof LabelStyle) {
                final LabelStyle lStyle = (LabelStyle) style;
                figure.setFont(VisualBindingManager.getDefault().getFontFromLabelStyle(lStyle, DiagramElementEditPartOperation.getFontName(self)));
                final FontStyle fontStyle = (FontStyle) self.getNotationView().getStyle(NotationPackage.eINSTANCE.getFontStyle());
                if (fontStyle != null && figure instanceof SiriusWrapLabel) {
                    final SiriusWrapLabel wrap = (SiriusWrapLabel) figure;
                    wrap.setTextUnderline(fontStyle.isUnderline());
                    wrap.setTextStrikeThrough(fontStyle.isStrikeThrough());
                }

                RGBValues labelRGBColor = lStyle.getLabelColor();
                Color labelColor = VisualBindingManager.getDefault().getLabelColorFromRGBValues(labelRGBColor);
                if (!figure.getForegroundColor().equals(labelColor)) {
                    figure.setForegroundColor(labelColor);
                }
            }
        }
    }

    /**
     * Returns the name of the font to use.
     * 
     * @param self
     *            the edit part.
     */
    private static String getFontName(final IDiagramElementEditPart self) {
        final FontStyle fontStyle = DiagramElementEditPartOperation.getFontStyle(self);
        if (fontStyle != null) {
            return fontStyle.getFontName();
        }
        return null;
    }

    private static FontStyle getFontStyle(final IDiagramElementEditPart self) {
        FontStyle fontStyle = (FontStyle) self.getNotationView().getStyle(NotationPackage.eINSTANCE.getFontStyle());
        if (fontStyle == null && self.getParent() instanceof IDiagramElementEditPart) {
            final IDiagramElementEditPart parent = (IDiagramElementEditPart) self.getParent();
            fontStyle = (FontStyle) parent.getNotationView().getStyle(NotationPackage.eINSTANCE.getFontStyle());
        }
        return fontStyle;
    }
}

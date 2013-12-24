/*******************************************************************************
 * Copyright (c) 2008, 2012 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.tools.internal.command;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sirius.business.api.helper.SiriusUtil;
import org.eclipse.sirius.business.api.helper.task.AbstractCommandTask;
import org.eclipse.sirius.business.api.helper.task.ICommandTask;
import org.eclipse.sirius.business.api.helper.task.InitInterpreterVariablesTask;
import org.eclipse.sirius.business.api.helper.task.TaskHelper;
import org.eclipse.sirius.business.api.query.DDiagramElementQuery;
import org.eclipse.sirius.business.api.query.EObjectQuery;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterSiriusVariables;
import org.eclipse.sirius.common.tools.api.util.StringUtil;
import org.eclipse.sirius.ecore.extender.business.api.permission.IPermissionAuthority;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.tools.api.command.AbstractCommandFactory;
import org.eclipse.sirius.tools.api.command.DCommand;
import org.eclipse.sirius.tools.api.command.IDiagramCommandFactory;
import org.eclipse.sirius.tools.api.command.SiriusCommand;
import org.eclipse.sirius.tools.api.command.ui.UICallBack;
import org.eclipse.sirius.tools.api.command.view.CreateDiagramWithInitialOperation;
import org.eclipse.sirius.tools.api.command.view.HideDDiagramElement;
import org.eclipse.sirius.tools.api.command.view.HideDDiagramElementLabel;
import org.eclipse.sirius.tools.api.command.view.JavaActionFromToolCommand;
import org.eclipse.sirius.tools.api.command.view.RefreshSiriusElement;
import org.eclipse.sirius.tools.api.command.view.RevealAllElementsCommand;
import org.eclipse.sirius.tools.api.command.view.RevealDDiagramElements;
import org.eclipse.sirius.tools.api.command.view.RevealDDiagramElementsLabel;
import org.eclipse.sirius.tools.api.interpreter.InterpreterUtil;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.sirius.tools.internal.command.builders.CommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.ContainerCreationCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.DeletionCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.DirectEditCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.DoubleClickCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.DragAndDropCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.EdgeCreationCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.GenericToolCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.NodeCreationCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.PaneBasedSelectionWizardCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.PasteCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.ReconnectionCommandBuilder;
import org.eclipse.sirius.tools.internal.command.builders.SelectionWizardCommandBuilder;
import org.eclipse.sirius.viewpoint.DDiagram;
import org.eclipse.sirius.viewpoint.DDiagramElement;
import org.eclipse.sirius.viewpoint.DDiagramElementContainer;
import org.eclipse.sirius.viewpoint.DEdge;
import org.eclipse.sirius.viewpoint.DLabelled;
import org.eclipse.sirius.viewpoint.DNode;
import org.eclipse.sirius.viewpoint.DRefreshable;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.DragAndDropTarget;
import org.eclipse.sirius.viewpoint.EdgeTarget;
import org.eclipse.sirius.viewpoint.description.DiagramDescription;
import org.eclipse.sirius.viewpoint.description.tool.AbstractVariable;
import org.eclipse.sirius.viewpoint.description.tool.BehaviorTool;
import org.eclipse.sirius.viewpoint.description.tool.ContainerCreationDescription;
import org.eclipse.sirius.viewpoint.description.tool.ContainerDropDescription;
import org.eclipse.sirius.viewpoint.description.tool.DirectEditLabel;
import org.eclipse.sirius.viewpoint.description.tool.DoubleClickDescription;
import org.eclipse.sirius.viewpoint.description.tool.EdgeCreationDescription;
import org.eclipse.sirius.viewpoint.description.tool.ExternalJavaAction;
import org.eclipse.sirius.viewpoint.description.tool.NodeCreationDescription;
import org.eclipse.sirius.viewpoint.description.tool.OperationAction;
import org.eclipse.sirius.viewpoint.description.tool.PaneBasedSelectionWizardDescription;
import org.eclipse.sirius.viewpoint.description.tool.PasteDescription;
import org.eclipse.sirius.viewpoint.description.tool.ReconnectEdgeDescription;
import org.eclipse.sirius.viewpoint.description.tool.RepresentationCreationDescription;
import org.eclipse.sirius.viewpoint.description.tool.SelectionWizardDescription;
import org.eclipse.sirius.viewpoint.description.tool.ToolDescription;
import org.eclipse.sirius.viewpoint.description.validation.ValidationFix;

/**
 * A command factory that creates commands that can be undone.
 * 
 * @author mchauvin
 */
public class UndoRedoCapableEMFCommandFactory extends AbstractCommandFactory implements IDiagramCommandFactory {

    private TaskHelper commandTaskHelper;

    /**
     * Create a new Factory. the autoRefresh is by default deactivated
     * 
     * @param domain
     *            current editing domain.
     */
    public UndoRedoCapableEMFCommandFactory(final TransactionalEditingDomain domain) {
        super(domain);
        commandTaskHelper = new TaskHelper(modelAccessor, uiCallBack);
    }

    private IPermissionAuthority getPermissionAuthority() {
        return modelAccessor.getPermissionAuthority();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildLaunchRuleCommandFromTool(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.sirius.viewpoint.description.tool.BehaviorTool, boolean,
     *      boolean)
     */
    public Command buildLaunchRuleCommandFromTool(final DSemanticDecorator rootObject, final BehaviorTool tool, final boolean executeFromRootContainer, final boolean deepProcess) {

        EObject root = rootObject.getTarget();

        if (root != null) {
            if (executeFromRootContainer) {
                // Let's launch the operation for the entire model.
                root = EcoreUtil.getRootContainer(root);
            }
            final Option<DRepresentation> representation = new EObjectQuery(rootObject).getRepresentation();
            final DCommand result = new SiriusCommand(domain, tool.getName());
            //
            // Current selection.
            if (representation.some() && tool.getDomainClass() == null || StringUtil.isEmpty(tool.getDomainClass().trim()) || this.modelAccessor.eInstanceOf(root, tool.getDomainClass())) {
                if (this.commandTaskHelper.checkPrecondition(root, tool) && tool.getInitialOperation() != null && tool.getInitialOperation().getFirstModelOperations() != null) {
                    //
                    // We append a new task.
                    result.getTasks().add(commandTaskHelper.buildTaskFromModelOperation(representation.get(), root, tool.getInitialOperation().getFirstModelOperations()));
                }
            }
            if (representation.some() && deepProcess) {
                final Iterator<EObject> iterContents = root.eAllContents();
                while (iterContents.hasNext()) {
                    final EObject current = iterContents.next();
                    if (tool.getDomainClass() == null || StringUtil.isEmpty(tool.getDomainClass().trim()) || this.modelAccessor.eInstanceOf(current, tool.getDomainClass())) {
                        if (this.commandTaskHelper.checkPrecondition(current, tool)) {
                            //
                            // We append a new task.
                            result.getTasks().add(commandTaskHelper.buildTaskFromModelOperation(representation.get(), current, tool.getInitialOperation().getFirstModelOperations()));
                        }
                    }
                }
            }
            addRefreshTask(rootObject, result, tool);
            addRemoveDanglingReferencesTask(result, tool, rootObject);
            return result;
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateNodeCommandFromTool(org.eclipse.sirius.viewpoint.DDiagramElementContainer,
     *      org.eclipse.sirius.viewpoint.description.tool.AbstractToolDescription)
     */
    public Command buildCreateNodeCommandFromTool(final DDiagramElementContainer container, final NodeCreationDescription tool) {
        final CommandBuilder builder = new NodeCreationCommandBuilder(tool, container);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateNodeCommandFromTool(org.eclipse.sirius.viewpoint.DNode,
     *      org.eclipse.sirius.viewpoint.description.tool.AbstractToolDescription)
     */
    public Command buildCreateNodeCommandFromTool(final DNode node, final NodeCreationDescription tool) {
        final CommandBuilder builder = new NodeCreationCommandBuilder(tool, node);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateNodeCommandFromTool(org.eclipse.sirius.viewpoint.DDiagram,
     *      org.eclipse.sirius.viewpoint.description.tool.AbstractToolDescription)
     */
    public Command buildCreateNodeCommandFromTool(final DDiagram diagram, final NodeCreationDescription tool) {
        final CommandBuilder builder = new NodeCreationCommandBuilder(tool, diagram);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateContainerCommandFromTool(org.eclipse.sirius.viewpoint.DDiagram,
     *      org.eclipse.sirius.viewpoint.description.tool.ContainerCreationDescription)
     */
    public Command buildCreateContainerCommandFromTool(final DDiagram diagram, final ContainerCreationDescription tool) {
        final CommandBuilder builder = new ContainerCreationCommandBuilder(tool, diagram);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateContainerCommandFromTool(org.eclipse.sirius.viewpoint.DNodeContainer,
     *      org.eclipse.sirius.viewpoint.description.tool.ContainerCreationDescription)
     */
    public Command buildCreateContainerCommandFromTool(final DDiagramElementContainer nodeContainer, final ContainerCreationDescription tool) {
        final CommandBuilder builder = new ContainerCreationCommandBuilder(tool, nodeContainer);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildSelectionWizardCommandFromTool(org.eclipse.sirius.viewpoint.description.tool.SelectionWizardDescription,
     *      org.eclipse.sirius.viewpoint.DSemanticDecorator,
     *      java.util.Collection)
     */
    public Command buildSelectionWizardCommandFromTool(final SelectionWizardDescription tool, final DSemanticDecorator containerView, final Collection<EObject> selectedElement) {
        final CommandBuilder builder = new SelectionWizardCommandBuilder(tool, containerView, selectedElement);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildPaneBasedSelectionWizardCommandFromTool(org.eclipse.sirius.viewpoint.description.tool.PaneBasedSelectionWizardDescription,
     *      org.eclipse.sirius.viewpoint.DSemanticDecorator,
     *      java.util.Collection)
     */
    public Command buildPaneBasedSelectionWizardCommandFromTool(final PaneBasedSelectionWizardDescription tool, final DSemanticDecorator containerView, final Collection<EObject> selectedElement) {
        final CommandBuilder builder = new PaneBasedSelectionWizardCommandBuilder(tool, containerView, selectedElement);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tool.api.command.IDiagramCommandFactory#
     *      buildJavaActionFromTool
     *      (org.eclipse.sirius.description.tool.AbstractToolDescription,
     *      org.eclipse.sirius.DSemanticDecorator,
     *      org.eclipse.sirius.description.tool.JavaActionMenuItem;)
     */
    public Command buildJavaActionFromTool(final ExternalJavaAction tool, final Collection<DSemanticDecorator> containerViews, final IExternalJavaAction javaAction) {
        final EObject anySemantic = containerViews.iterator().next().getTarget();
        final CompoundCommand compoundCommand = new CompoundCommand();
        final DCommand dCommand = new SiriusCommand(this.domain, tool.getName());

        final Command command = buildJavaActionFromTool(tool, anySemantic, containerViews, javaAction);
        compoundCommand.append(command);
        compoundCommand.append(dCommand);

        for (final DSemanticDecorator containerView : containerViews) {
            addRefreshTask(containerView, dCommand, tool);
        }
        addRemoveDanglingReferencesTask(dCommand, tool, containerViews.iterator().next());
        return compoundCommand;
    }

    private Command buildJavaActionFromTool(final ExternalJavaAction tool, final EObject container, final Collection<DSemanticDecorator> containerViews, final IExternalJavaAction javaAction) {
        return new JavaActionFromToolCommand(this.domain, javaAction, tool, containerViews);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tool.api.command.IDiagramCommandFactory#
     *      buildOperationActionFromTool
     *      (org.eclipse.sirius.description.tool.AbstractToolDescription,
     *      org.eclipse.sirius.DSemanticDecorator,
     *      org.eclipse.sirius.description.tool.OperationMenuItem;)
     */
    public Command buildOperationActionFromTool(final OperationAction tool, final Collection<DSemanticDecorator> containerViews) {
        final EObject anySemantic = containerViews.iterator().next().getTarget();
        final DCommand command = buildOperationActionFromTool(tool, anySemantic, containerViews);
        for (final DSemanticDecorator containerView : containerViews) {
            addRefreshTask(containerView, command, tool);
        }
        addRemoveDanglingReferencesTask(command, tool, containerViews.iterator().next());
        return command;
    }

    private DCommand buildOperationActionFromTool(final OperationAction tool, final EObject container, final Collection<DSemanticDecorator> containerViews) {
        final DCommand result = new SiriusCommand(domain, tool.getName());
        final IInterpreter interpreter = InterpreterUtil.getInterpreter(container);
        final Map<AbstractVariable, Object> variables = new HashMap<AbstractVariable, Object>();
        variables.put(tool.getView(), containerViews);
        result.getTasks().add(new InitInterpreterVariablesTask(variables, interpreter, uiCallBack));

        DSemanticDecorator firstContainerView = null;
        if (containerViews != null && containerViews.size() > 0) {
            firstContainerView = containerViews.iterator().next();
            addDiagramVariable(result, firstContainerView, interpreter);
        }

        Option<DRepresentation> representation = new EObjectQuery(firstContainerView).getRepresentation();
        if (representation.some() && tool.getInitialOperation() != null && tool.getInitialOperation().getFirstModelOperations() != null) {
            result.getTasks().add(commandTaskHelper.buildTaskFromModelOperation(representation.get(), container, tool.getInitialOperation().getFirstModelOperations()));
        }
        return result;
    }

    private void addDiagramVariable(final DCommand command, final EObject containerView, final IInterpreter interpreter) {
        final DDiagram diag = SiriusUtil.findDiagram(containerView);
        if (diag != null) {
            command.getTasks().add(new AbstractCommandTask() {

                public String getLabel() {
                    return "Add diagram variable";
                }

                public void execute() {
                    interpreter.setVariable(IInterpreterSiriusVariables.DIAGRAM, diag);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildCreateEdgeCommandFromTool(org.eclipse.sirius.viewpoint.EdgeTarget,
     *      org.eclipse.sirius.viewpoint.EdgeTarget,
     *      org.eclipse.sirius.viewpoint.description.tool.EdgeCreationDescription)
     */
    public Command buildCreateEdgeCommandFromTool(final EdgeTarget source, final EdgeTarget target, final EdgeCreationDescription tool) {
        final CommandBuilder builder = new EdgeCreationCommandBuilder(tool, source, target);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildReconnectEdgeCommandFromTool(org.eclipse.sirius.viewpoint.description.tool.ReconnectEdgeDescription,
     *      org.eclipse.sirius.viewpoint.DEdge,
     *      org.eclipse.sirius.viewpoint.EdgeTarget,
     *      org.eclipse.sirius.viewpoint.EdgeTarget)
     */
    public Command buildReconnectEdgeCommandFromTool(final ReconnectEdgeDescription tool, final DEdge edge, final EdgeTarget source, final EdgeTarget target) {
        final CommandBuilder builder = new ReconnectionCommandBuilder(tool, edge, source, target);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDropInContainerCommandFromTool(org.eclipse.sirius.viewpoint.DragAndDropTarget,
     *      org.eclipse.sirius.viewpoint.DDiagramElement,
     *      org.eclipse.sirius.viewpoint.description.tool.ContainerDropDescription)
     */
    public Command buildDropInContainerCommandFromTool(final DragAndDropTarget container, final DDiagramElement element, final ContainerDropDescription tool) {
        final CommandBuilder builder = new DragAndDropCommandBuilder(tool, container, element);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDropInContainerCommandFromTool(DragAndDropTarget,
     *      EObject, ContainerDropDescription)
     */
    public Command buildDropInContainerCommandFromTool(final DragAndDropTarget container, final EObject droppedElement, final ContainerDropDescription tool) {
        final CommandBuilder builder = new DragAndDropCommandBuilder(tool, container, droppedElement);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDoubleClickOnElementCommandFromTool(DDiagramElement,
     *      DoubleClickOnElementDescription)
     */
    public Command buildDoubleClickOnElementCommandFromTool(DDiagramElement dDiagramElement, DoubleClickDescription tool) {
        final CommandBuilder builder = new DoubleClickCommandBuilder(tool, dDiagramElement);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDoExecuteDetailsOperation(org.eclipse.sirius.viewpoint.DSemanticDecorator,
     *      org.eclipse.sirius.viewpoint.description.tool.RepresentationCreationDescription,
     *      java.lang.String)
     */
    public Command buildDoExecuteDetailsOperation(final DSemanticDecorator target, final RepresentationCreationDescription desc, final String newRepresentationName) {
        final DCommand cmd = new SiriusCommand(domain, "Create new representation");
        final Map<AbstractVariable, Object> variables = new HashMap<AbstractVariable, Object>();
        variables.put(desc.getContainerViewVariable(), target);
        final Map<AbstractVariable, String> stringVariables = new HashMap<AbstractVariable, String>();
        stringVariables.put(desc.getRepresentationNameVariable(), newRepresentationName);
        final ICommandTask initInterpreterVariables = new InitInterpreterVariablesTask(variables, stringVariables, InterpreterUtil.getInterpreter(target), uiCallBack);
        cmd.getTasks().add(initInterpreterVariables);

        Option<DRepresentation> representation = new EObjectQuery(target).getRepresentation();
        if (representation.some() && desc.getInitialOperation() != null && desc.getInitialOperation().getFirstModelOperations() != null) {
            cmd.getTasks().add(commandTaskHelper.buildTaskFromModelOperation(representation.get(), target.getTarget(), desc.getInitialOperation().getFirstModelOperations()));
        }
        addRemoveDanglingReferencesTask(cmd, desc, target);
        return cmd;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDeleteDiagram(org.eclipse.sirius.viewpoint.DDiagram)
     */
    public Command buildDeleteDiagram(final DDiagram dDiagram) {
        final CommandBuilder builder = new DeletionCommandBuilder(dDiagram);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDeleteFromDiagramCommand(org.eclipse.sirius.viewpoint.DDiagramElement)
     */
    public Command buildDeleteFromDiagramCommand(final DDiagramElement element) {
        final CommandBuilder builder = new DeletionCommandBuilder(element, true);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDeleteDiagramElement(org.eclipse.sirius.viewpoint.DDiagramElement)
     */
    public Command buildDeleteDiagramElement(final DDiagramElement element) {
        final CommandBuilder builder = new DeletionCommandBuilder(element, false);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildDirectEditLabelFromTool(org.eclipse.sirius.viewpoint.DLabelled,
     *      org.eclipse.sirius.viewpoint.description.tool.DirectEditLabel,
     *      java.lang.String)
     */
    public Command buildDirectEditLabelFromTool(final DLabelled labeled, final DirectEditLabel directEditTool, final String newValue) {
        final CommandBuilder builder = new DirectEditCommandBuilder(labeled, directEditTool, newValue);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildRefreshCommand(org.eclipse.sirius.viewpoint.DRefreshable)
     */
    public Command buildRefreshCommand(final DRefreshable vpElem) {
        if (getPermissionAuthority().canEditInstance(vpElem)) {
            return new RefreshSiriusElement(domain, vpElem);
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildHideCommand(java.util.Set)
     */
    public Command buildHideCommand(final Set<EObject> elementsToHide) {
        final Set<EObject> filteredSet = new HashSet<EObject>();
        final Iterator<EObject> it = elementsToHide.iterator();
        while (it.hasNext()) {
            final EObject eObj = it.next();
            if (getPermissionAuthority().canEditInstance(eObj)) {
                filteredSet.add(eObj);
            }
        }
        if (filteredSet.size() > 0) {
            return new HideDDiagramElement(domain, filteredSet);
        }

        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildHideLabelCommand(java.util.Set)
     */
    public Command buildHideLabelCommand(Set<EObject> elementsToHide) {
        final Set<EObject> filteredSet = new HashSet<EObject>();
        final Iterator<EObject> it = elementsToHide.iterator();
        while (it.hasNext()) {
            final EObject eObj = it.next();
            if (getPermissionAuthority().canEditInstance(eObj)) {
                filteredSet.add(eObj);
            }
        }
        boolean canHideLabel = true;
        for (Object selectedElement : filteredSet) {
            if (selectedElement instanceof DDiagramElement) {
                canHideLabel = canHideLabel & new DDiagramElementQuery((DDiagramElement) selectedElement).canHideLabel();
            }
        }
        if (canHideLabel) {
            return new HideDDiagramElementLabel(domain, filteredSet);
        }

        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildRevealCommand(org.eclipse.sirius.viewpoint.DDiagramElement)
     */
    public Command buildRevealCommand(final DDiagramElement vpe) {
        if (getPermissionAuthority().canEditInstance(vpe)) {
            return new RevealDDiagramElements(domain, Collections.singleton(vpe));
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.sirius.tools.api.command.IDiagramCommandFactory#buildRevealLabelCommand(org.eclipse.sirius.viewpoint.DDiagramElement)
     */
    public Command buildRevealLabelCommand(DDiagramElement diagramElement) {
        if (getPermissionAuthority().canEditInstance(diagramElement)) {
            return new RevealDDiagramElementsLabel(domain, Collections.singleton(diagramElement));
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public Command buildRevealElementsCommand(final DDiagram viewpoint) {
        if (getPermissionAuthority().canEditInstance(viewpoint)) {
            return new RevealAllElementsCommand(domain, viewpoint);
        }

        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public Command buildRevealElementsCommand(final Set<DDiagramElement> elementsToReveal) {
        final Set<DDiagramElement> filteredSet = new HashSet<DDiagramElement>();
        if (elementsToReveal != null) {
            for (final DDiagramElement diagramElement : elementsToReveal) {
                if (getPermissionAuthority().canEditInstance(diagramElement)) {
                    filteredSet.add(diagramElement);
                }
            }
            if (filteredSet.size() > 0) {
                return new RevealDDiagramElements(domain, filteredSet);
            }
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void setAutoRefreshDView(final boolean autoRefreshDView) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public DCommand buildCreateDiagramFromDescription(final DiagramDescription description, final EObject semanticElement, IProgressMonitor monitor) {
        final DCommand command = new SiriusCommand(domain, "Create new diagram") {
            /**
             * creation of a diagram must not be undoable ! {@inheritDoc}
             */
            @Override
            public boolean canUndo() {
                return false;
            }
        };

        command.getTasks().add(new CreateDiagramWithInitialOperation(description, semanticElement, uiCallBack, monitor));

        return command;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserInterfaceCallBack(final UICallBack newCB) {
        this.uiCallBack = newCB;
        commandTaskHelper = new TaskHelper(modelAccessor, uiCallBack);
    }

    /**
     * {@inheritDoc}
     */
    public Command buildQuickFixOperation(final ValidationFix fix, final EObject fixTarget, final DDiagram diagram) {
        if (fix.getInitialOperation() != null && fix.getInitialOperation().getFirstModelOperations() != null) {
            final DCommand result = new SiriusCommand(domain, "Quick fix");
            result.getTasks().add(commandTaskHelper.buildTaskFromModelOperation(diagram, fixTarget, fix.getInitialOperation().getFirstModelOperations()));
            return result;
        }
        return UnexecutableCommand.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public Command buildGenericToolCommandFromTool(final EObject containerView, final ToolDescription tool) {
        final CommandBuilder builder = new GenericToolCommandBuilder(tool, containerView);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     */
    public Command buildPasteCommandFromTool(DSemanticDecorator dContainer, DSemanticDecorator element, PasteDescription tool) {
        final CommandBuilder builder = new PasteCommandBuilder(tool, dContainer, element);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }

    /**
     * {@inheritDoc}
     */
    public Command buildPasteCommandFromTool(DSemanticDecorator dContainer, EObject droppedElement, PasteDescription tool) {
        final CommandBuilder builder = new PasteCommandBuilder(tool, dContainer, droppedElement);
        builder.init(modelAccessor, domain, uiCallBack);
        return builder.buildCommand();
    }
}

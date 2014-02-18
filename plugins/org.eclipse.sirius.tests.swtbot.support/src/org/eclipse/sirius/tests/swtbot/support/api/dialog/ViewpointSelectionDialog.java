/**
 * Copyright (c) 2009, 2014 THALES GLOBAL SERVICES
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Obeo - Initial API and implementation
 */
package org.eclipse.sirius.tests.swtbot.support.api.dialog;

import java.util.Map;
import java.util.Set;

import org.eclipse.sirius.tests.swtbot.support.utils.SWTBotUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Wrapper for viewpoints selection dialog.
 * 
 * @author dlecan
 */
public class ViewpointSelectionDialog {
    /**
     * The title of the Viewpoints selection dialog.
     */
    public static final String VIEWPOINT_DIALOG_NAME = "Viewpoints Selection";

    private final SWTWorkbenchBot bot;

    /**
     * Create a new instance.
     * 
     * @param bot
     *            the SWT bot
     */
    public ViewpointSelectionDialog(final SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    /**
     * "Select Viewpoints" operation (when creating a new local session, either
     * via wizard or drag and drop of model in local session view).
     * <p>
     * If a viewpoint is provided in both sets, this viewpoint will be selected.
     * Unselection action will be ignored.
     * </p>
     * 
     * @param viewpointToSelect
     *            Viewpoint to select.
     * @param viewpointToDeselect
     *            Viewpoint to deselect.
     */
    public void selectViewpoint(final Set<String> viewpointToSelect, final Set<String> viewpointToDeselect) {
        bot.waitUntil(Conditions.shellIsActive(ViewpointSelectionDialog.VIEWPOINT_DIALOG_NAME));

        final SWTBotShell shellViewpointsSelection = bot.shell(ViewpointSelectionDialog.VIEWPOINT_DIALOG_NAME);

        if (viewpointToSelect != null && viewpointToDeselect != null) {
            final SetView<String> allViewpointNames = Sets.union(viewpointToSelect, viewpointToDeselect);

            final Map<String, Boolean> viewpointSelection = Maps.newHashMap();

            for (final String vpName : allViewpointNames) {
                viewpointSelection.put(vpName, viewpointToSelect.contains(vpName));
            }

            if (!viewpointSelection.isEmpty()) {
                for (int rowPosition = 0; rowPosition < bot.table().rowCount(); rowPosition++) {

                    final SWTBotTableItem item = bot.table().getTableItem(rowPosition);
                    final String text = item.getText(2);

                    if (viewpointSelection.containsKey(text)) {
                        bot.table().click(rowPosition, 0);
                        viewpointSelection.remove(text);
                    }
                }
                failIfMissingViewpoints(viewpointSelection);

                final SWTBotButton okButton = bot.button("OK");

                bot.waitUntil(new DefaultCondition() {

                    @Override
                    public String getFailureMessage() {
                        return "OK button is not enabled";
                    }

                    @Override
                    public boolean test() throws Exception {
                        return okButton.isEnabled();
                    }

                });
            }
        }
        bot.button("OK").click();
        SWTBotUtils.waitProgressMonitorClose("Progress Information");
        bot.waitUntil(Conditions.shellCloses(shellViewpointsSelection));

        SWTBotUtils.waitProgressMonitorClose("Apply new viewpoints selection");
    }

    private void failIfMissingViewpoints(final Map<String, Boolean> remainingViewpoints) {
        final StringBuilder message = new StringBuilder();
        for (final String vpName : remainingViewpoints.keySet()) {
            message.append("\"");
            message.append(vpName);
            message.append("\" ");
        }
        if (message.length() > 0) {
            message.append(" viewpoint(s) was/were not found");
            Assert.fail(message.toString());
        }
    }

}

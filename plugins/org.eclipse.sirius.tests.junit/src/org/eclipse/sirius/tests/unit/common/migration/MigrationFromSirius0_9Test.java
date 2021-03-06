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
package org.eclipse.sirius.tests.unit.common.migration;

import org.osgi.framework.Version;

/**
 * Check that migration is OK for VSM and representations file from Sirius 0.9
 * to the current version.
 * 
 * @author lredor
 */
public class MigrationFromSirius0_9Test extends AbstractMigrationFromTest {

    private static final Version SIRIUS_1_0_0_M5_VERSION = new Version("7.0.0");

    @Override
    protected Version getExpectedVersion() {
        return SIRIUS_1_0_0_M5_VERSION;
    }

    @Override
    protected String getExpectedVersionToString() {
        return "0.9";
    }

    @Override
    protected String getFolderName() {
        return "FromSirius0.9To";
    }
}

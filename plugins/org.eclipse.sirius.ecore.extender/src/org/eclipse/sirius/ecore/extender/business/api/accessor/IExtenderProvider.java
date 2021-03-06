/*******************************************************************************
 * Copyright (c) 2007, 2009 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.ecore.extender.business.api.accessor;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An {@link IExtenderProvider} provides new model extenders considering a given
 * {@link ResourceSet}.
 * 
 * @author cbrun
 * 
 */
public interface IExtenderProvider {
    /**
     * Tell whether the provider want to provide or not.
     * 
     * @param set
     *            the model {@link ResourceSet}.
     * @return true if the provider has something to provides, false otherwise.
     */
    boolean provides(ResourceSet set);

    /**
     * Return the provided {@link IMetamodelExtender}.
     * 
     * @param set
     *            the model {@link ResourceSet}.
     * @return the provided {@link IMetamodelExtender}.
     */
    IMetamodelExtender getExtender(ResourceSet set);
}

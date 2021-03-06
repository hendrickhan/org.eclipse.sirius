/*******************************************************************************
 * Copyright (c) 2009 THALES GLOBAL SERVICES.
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
package org.eclipse.sirius.editor.tools.api.editor;

import org.eclipse.emf.ecore.EModelElement;

/**
 * Interface specifying customization made to the viewpoint specification
 * editor.
 * 
 * @author cbrun
 * 
 */
public interface EditorCustomization {
    /**
     * This method should be implemented to decide whether the given
     * meta-element should be visible in the editor or not.
     * 
     * @param metaElement
     *            metaElement to inspect.
     * @return true if the metaElement should be hidden by the editor.
     */
    boolean isHidden(EModelElement metaElement);

    /**
     * return true if the all tab should be displayed in the editor.
     * 
     * @return true if the all tab should be displayed in the editor.
     */
    boolean showAllTab();
}

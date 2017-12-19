/*******************************************************************************
 * Copyright (c) 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.business.api.query;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;
import org.eclipse.sirius.viewpoint.DRepresentationElement;

/**
 * Allow to retrieve SiriusElement that are directly associated to semantic elements within the scope of a Sirius
 * {@link Session}.</br>
 * * {@link DRepresentation}s or {@link DRepresentationElement}s referencing the provided semantic objects through
 * {@link ViewpointPackage.DSEMANTIC_DECORATOR__TARGET} or
 * {@link ViewpointPackage.DREPRESENTATION_ELEMENT__SEMANTIC_ELEMENTS} {@link EReference}</br>
 * * {@link DRepresentationDescriptor}s which associated DRepresentation is or contains elements that reference the
 * provided semantic object
 * 
 * @author <a href="mailto:laurent.fasani@obeo.fr">Laurent Fasani</a>
 *
 */
public interface SiriusReferenceFinder {

    /**
     * The scope of the research.
     * 
     * @author lfasani
     */
    enum SearchScope {
        /**
         * Only loaded representations in the session are considered in the research scope.
         */
        LOADED_REPRESENTATIONS_SCOPE,
        /**
         * All representations, including non loaded representations, are considered in the research scope.
         */
        ALL_REPRESENTATIONS_SCOPE
    };

    /**
     * Returns the SiriusReferenceFinder of the session a given model element is part of, if any.
     * 
     * @param obj
     *            an {@link EObject}.
     * @return the SiriusReferenceFinder.
     */
    static Optional<SiriusReferenceFinder> of(EObject obj) {
        return Optional.ofNullable(new EObjectQuery(obj).getSiriusReferenceFinder());
    }

    /**
     * Return the {@link DRepresentation}s or {@link DRepresentationElement}s referencing the semanticObjects.</br>
     * In ALL_REPRESENTATIONS_SCOPE case, the DRepresentation containing the found EObjects will be automatically
     * loaded. The search may possibly need to load the DRepresentation resources. But, without guaranteeing it, the
     * implementation will do its best to not load it.</br>
     * Whatever the searchScope is, the DRepresentations containing the found EObject will be automatically loaded.
     * 
     * @param semanticObjects
     *            the semantic objects from which to retrieve the referencing EObjects.
     * @param searchScope
     *            if LOADED_REPRESENTATIONS_SCOPE, the scope of the search is limited to EObjects in loaded
     *            DRepresentation in the Session</br>
     *            if ALL_REPRESENTATIONS_SCOPE, the scope is loaded and not loaded DRepresentations of the Session. The
     *            search may possibly need to load the DRepresentation.</br>
     *            In any case, the DRepresentation containing the found EObject will be automatically loaded.
     * @return the found EObjects that are of type DRepresentation or DRepresentationElement for each of the
     *         semanticObjects.
     */
    Map<EObject, Collection<EObject>> getReferencingSiriusElements(Collection<EObject> semanticObjects, SearchScope searchScope);

    /**
     * Return the {@link DRepresentationDescriptor}s which associated {@link DRepresentation} is or contains a
     * {@link DRepresentationElement} that is referencing the semanticObjects.</br>
     * In ALL_REPRESENTATIONS_SCOPE case, the aim of this method is to avoid loading one of the non loaded
     * representations. Without guaranteeing it, the implementation will do its best to achieve that.</br>
     * 
     * @param semanticObjects
     *            the semantic objects from which to retrieve the referencing EObjects.
     * @param searchScope
     *            if LOADED_REPRESENTATIONS_SCOPE, the scope of the search is limited to EObjects in loaded
     *            DRepresentation in the Session</br>
     *            if ALL_REPRESENTATIONS_SCOPE, the scope is loaded and not loaded DRepresentations of the Session. The
     *            search may possibly need to load the not yet loaded DRepresentation resources.</br>
     * @return the found DRepresentationDescriptors for each of the semanticObjects.
     */
    Map<EObject, Collection<DRepresentationDescriptor>> getImpactedRepresentationDescriptors(Collection<EObject> semanticObjects, SearchScope searchScope);
}

/*******************************************************************************
 * Copyright (c) 2011 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.synchronizer;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * The refresh planner use the input mappings and status of the output model
 * before update to compute the {@link RefreshPlan}
 * 
 * @author Cedric Brun <cedric.brun@obeo.fr>
 * 
 */
public class RefreshPlanner {

    private MappingHiearchyTable mappingTable;

    private SemanticPartitionInvalidator invalidator;

    private PreRefreshStatus pre;

    private SignatureProvider signatureProvider;

    /**
     * Function that returns the Hierarchy of the given Mapping.
     */
    private final Function<Mapping, Collection<MappingHiearchy>> toHierarchy = new Function<Mapping, Collection<MappingHiearchy>>() {

        public Collection<MappingHiearchy> apply(Mapping from) {
            return mappingTable.getHierarchy(from);
        }

    };

    /**
     * Creates a new RefreshPlanner.
     * 
     * @param mappingTable
     *            the MappingHierarchy table to refresh
     * @param invalidator
     *            the semantic invalidator to use during refresh
     * @param pre
     *            the PreRefresh Status
     * @param signatureProvider
     *            the Signature Provider
     */
    public RefreshPlanner(MappingHiearchyTable mappingTable, SemanticPartitionInvalidator invalidator, PreRefreshStatus pre, SignatureProvider signatureProvider) {
        super();
        this.mappingTable = mappingTable;
        this.invalidator = invalidator;
        this.pre = pre;
        this.signatureProvider = signatureProvider;
    }

    /**
     * Computes the RefreshPlan from a CreatedOutput.
     * 
     * @param container
     *            the CreatedOutput to computes the RefreshPlan from
     * @return the RefreshPlan computed from the given CreatedOutput
     */
    public RefreshPlan computePlan(CreatedOutput container) {
        RefreshPlan post = new RefreshPlan(signatureProvider);

        Collection<? extends Mapping> childMappings = container.getChildMappings();

        pre.computeStatus(container, childMappings);
        post.addPreviousStatus(pre.getExistingOutputs());

        Iterable<? extends Mapping> mappingsCreatingElements = Iterables.filter(childMappings, Predicates.not(Mapping.IS_CHECK_ONLY));
        Iterable<Collection<MappingHiearchy>> transformedHiearchy = Iterables.transform(mappingsCreatingElements, toHierarchy);
        Iterable<MappingHiearchy> childHiearchies = Sets.newLinkedHashSet(Iterables.concat(transformedHiearchy));

        for (MappingHiearchy nodeHiearch : childHiearchies) {
            Iterator<Mapping> it = nodeHiearch.fromMostSpecificToMostGeneral();
            while (it.hasNext()) {
                Mapping cur = it.next();
                if (cur.isEnabled() && cur.getCreator().some()) {
                    Maybe<EvaluatedSemanticPartition> par = invalidator.hasFastResult(container.getDescriptor().getSourceElement(), cur.getSemanticPartition());
                    if (!par.some()) {
                        par = MaybeFactory.newSome(cur.getSemanticPartition().evaluate(container.getDescriptor().getSourceElement()));
                    }
                    Collection<? extends OutputDescriptor> allCandidateDescriptors = cur.getCreator().value().computeDescriptors(container, par.value().elements());
                    post.appendOutputDescritorsKeepingTheMostSpecific(allCandidateDescriptors);
                }
            }
        }
        return post;
    }
}

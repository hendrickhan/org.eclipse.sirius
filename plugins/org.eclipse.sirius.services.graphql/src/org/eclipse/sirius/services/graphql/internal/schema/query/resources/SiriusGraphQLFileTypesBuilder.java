/*******************************************************************************
 * Copyright (c) 2018 Obeo.
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
package org.eclipse.sirius.services.graphql.internal.schema.query.resources;

import static org.eclipse.sirius.services.graphql.internal.schema.query.emf.SiriusGraphQLEObjectTypesBuilder.EOBJECT_TYPE;
import static org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLConnectionTypeBuilder.CONNECTION_SUFFIX;
import static org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLEdgeTypeBuilder.EDGE_SUFFIX;
import static org.eclipse.sirius.services.graphql.internal.schema.query.viewpoints.SiriusGraphQLRepresentationTypesBuilder.REPRESENTATION_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.services.common.api.SiriusServicesCommonOptionalUtils;
import org.eclipse.sirius.services.graphql.internal.entities.SiriusGraphQLConnection;
import org.eclipse.sirius.services.graphql.internal.schema.ISiriusGraphQLTypesBuilder;
import org.eclipse.sirius.services.graphql.internal.schema.directives.SiriusGraphQLCostDirective;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLConnectionTypeBuilder;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLEdgeTypeBuilder;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLPaginationArguments;
import org.eclipse.sirius.services.graphql.internal.schema.query.pagination.SiriusGraphQLPaginationDataFetcher;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

/**
 * Used to create the File object of the GraphQL schema.
 *
 * @author sbegaudeau
 */
public class SiriusGraphQLFileTypesBuilder implements ISiriusGraphQLTypesBuilder {
    /**
     * The name of the File type.
     */
    public static final String FILE_TYPE = "File"; //$NON-NLS-1$

    /**
     * The name of the File to Representation connection type.
     */
    public static final String FILE_REPRESENTATION_CONNECTION_TYPE = FILE_TYPE + REPRESENTATION_TYPE + CONNECTION_SUFFIX;

    /**
     * The name of the File to Representation edge type.
     */
    public static final String FILE_REPRESENTATION_EDGE_TYPE = FILE_TYPE + REPRESENTATION_TYPE + EDGE_SUFFIX;

    /**
     * The name of the representations field.
     */
    private static final String REPRESENTATIONS = "representations"; //$NON-NLS-1$

    /**
     * The name of the File to EObject connection type.
     */
    public static final String FILE_EOBJECT_CONNECTION_TYPE = FILE_TYPE + EOBJECT_TYPE + CONNECTION_SUFFIX;

    /**
     * The name of the File to EObject edge type.
     */
    public static final String FILE_EOBJECT_EDGE_TYPE = FILE_TYPE + EOBJECT_TYPE + EDGE_SUFFIX;

    /**
     * The name of the eObjects field.
     */
    private static final String EOBJECTS = "eObjects"; //$NON-NLS-1$

    /**
     * The complexity of the retrieval of a representation.
     */
    private static final int REPRESENTATIONS_COMPLEXITY = 10;

    /**
     * The complexity of the retrieval of an eObject.
     */
    private static final int EOBJECTS_COMPLEXITY = 1;

    @Override
    public Set<GraphQLType> getTypes() {
        GraphQLObjectType representationEdge = new SiriusGraphQLEdgeTypeBuilder(FILE_REPRESENTATION_EDGE_TYPE, REPRESENTATION_TYPE).build();
        GraphQLObjectType representationConnection = new SiriusGraphQLConnectionTypeBuilder(FILE_REPRESENTATION_CONNECTION_TYPE, FILE_REPRESENTATION_EDGE_TYPE).build();
        GraphQLObjectType eObjectEdge = new SiriusGraphQLEdgeTypeBuilder(FILE_EOBJECT_EDGE_TYPE, EOBJECT_TYPE).build();
        GraphQLObjectType eObjectConnection = new SiriusGraphQLConnectionTypeBuilder(FILE_EOBJECT_CONNECTION_TYPE, FILE_EOBJECT_EDGE_TYPE).build();

        // @formatter:off
        GraphQLObjectType file = GraphQLObjectType.newObject()
                .name(FILE_TYPE)
                .field(SiriusGraphQLResourceNameField.build())
                .field(SiriusGraphQLResourcePathField.build())
                .field(SiriusGraphQLResourceContainerField.build())
                .field(SiriusGraphQLResourceProjectField.build())
                .field(this.getRepresentationsField())
                .field(this.getEObjectsField())
                .withInterface(new GraphQLTypeReference(SiriusGraphQLResourceTypesBuilder.RESOURCE_TYPE))
                .build();
        // @formatter:on

        Set<GraphQLType> types = new LinkedHashSet<>();
        types.add(file);
        types.add(representationEdge);
        types.add(representationConnection);
        types.add(eObjectEdge);
        types.add(eObjectConnection);
        return types;
    }

    /**
     * Returns the representations field.
     *
     * @return The representations field.
     */
    private GraphQLFieldDefinition getRepresentationsField() {
        List<String> multipliers = new ArrayList<>();
        multipliers.add(SiriusGraphQLPaginationArguments.FIRST_ARG);
        multipliers.add(SiriusGraphQLPaginationArguments.LAST_ARG);

        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(REPRESENTATIONS)
                .type(new GraphQLTypeReference(FILE_REPRESENTATION_CONNECTION_TYPE))
                .argument(SiriusGraphQLPaginationArguments.build())
                .withDirective(new SiriusGraphQLCostDirective(REPRESENTATIONS_COMPLEXITY, multipliers).build())
                .dataFetcher(this.getRepresentationsDataFetcher())
                .build();
        // @formatter:on
    }

    /**
     * Returns the representations data fetcher.
     *
     * @return The representations data fetcher.
     */
    private DataFetcher<SiriusGraphQLConnection> getRepresentationsDataFetcher() {
        // @formatter:off
        return SiriusGraphQLPaginationDataFetcher.build(environment -> {
            Optional<IFile> optionalFile = Optional.of(environment.getSource())
                    .filter(IFile.class::isInstance)
                    .map(IFile.class::cast);
            
            Optional<Session> optionalSession = optionalFile.map(IFile::getProject)
                    .flatMap(SiriusServicesCommonOptionalUtils::toSession);

            List<DRepresentation> representations = new ArrayList<>();
            if (optionalFile.isPresent() && optionalSession.isPresent()) {
                IFile iFile = optionalFile.get();
                Session session = optionalSession.get();

                Collection<DRepresentationDescriptor> representationDescriptors = DialectManager.INSTANCE.getAllRepresentationDescriptors(session);
                for (DRepresentationDescriptor representationDescriptor : representationDescriptors) {
                    EObject eObject = representationDescriptor.getTarget();
                    URI uri = eObject.eResource().getURI();
                    URI fileUri = URI.createPlatformResourceURI(iFile.getFullPath().toString(), true);
                    if (uri.equals(fileUri)) {
                        representations.add(representationDescriptor.getRepresentation());
                    }
                }
            }

            return representations;
        });
        // @formatter:on
    }

    /**
     * Returns the eObjects field.
     * 
     * @return The eObjects field
     */
    private GraphQLFieldDefinition getEObjectsField() {
        List<String> multipliers = new ArrayList<>();
        multipliers.add(SiriusGraphQLPaginationArguments.FIRST_ARG);
        multipliers.add(SiriusGraphQLPaginationArguments.LAST_ARG);

        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(EOBJECTS)
                .type(new GraphQLTypeReference(FILE_EOBJECT_CONNECTION_TYPE))
                .argument(SiriusGraphQLPaginationArguments.build())
                .withDirective(new SiriusGraphQLCostDirective(EOBJECTS_COMPLEXITY, multipliers).build())
                .dataFetcher(this.getEObjectsDataFetcher())
                .build();
        // @formatter:on
    }

    /**
     * Returns the eObjects data fetcher.
     * 
     * @return The eObjects data fetcher
     */
    private DataFetcher<SiriusGraphQLConnection> getEObjectsDataFetcher() {
        // @formatter:off
        return SiriusGraphQLPaginationDataFetcher.build(environment -> {
            Optional<IFile> optionalFile = Optional.of(environment.getSource())
                    .filter(IFile.class::isInstance)
                    .map(IFile.class::cast);
            
            Optional<Session> optionalSession = optionalFile.map(IFile::getProject)
                    .flatMap(SiriusServicesCommonOptionalUtils::toSession);
            
            Optional<Resource> optionalResource = optionalFile.flatMap(iFile -> {
                return optionalSession.flatMap(session -> SiriusServicesCommonOptionalUtils.toResource(session, iFile));
            });
            
            return optionalResource.map(Resource::getContents).orElseGet(BasicEList::new);
        });
        // @formatter:on
    }
}

/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.neo4j.template;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.springframework.data.neo4j.conversion.QueryResult;
import org.springframework.data.neo4j.core.Property;

import java.util.Map;

/**
 * A template with convenience operations, exception translation and implicit transaction for modifying methods
 * @author mh
 * @since 19.02.11
 */
public interface Neo4jOperations {

    /**
     * Executes the callback in a NON-transactional context.
     * @param callback for executing graph operations NON-transactionally, not null
     * @param <T> return type
     * @return whatever the callback chooses to return
     * @throws org.springframework.dao.DataAccessException subclasses
     */
    <T> T exec(GraphCallback<T> callback);

    /**
     * Delegates to the GraphDatabase
     * @return the reference node of the underlying graph database
     */
    Node getReferenceNode();

    /**
     * Delegates to the GraphDatabase
     * @param id node id
     * @return the requested node of the underlying graph database
     * @throws NotFoundException
     */
    Node getNode(long id);

    /**
     * Transactionally creates the node, sets the properties (if any) and indexes the given fields (if any).
     * Two shortcut means of providing the properties (very short with static imports)
     * <code>template.createNode(Property._("name","value"));</code>
     * <code>template.createNode(Property._("name","value","prop","anotherValue"));</code>
     *
     *
     * @param props properties to be set at node creation might be null
     * @return the newly created node
     */
    Node createNode(Property... props);

    /**
     * Delegates to the GraphDatabase
     * @param id relationship id
     * @return the requested relationship of the underlying graph database
     * @throws NotFoundException
     */
    Relationship getRelationship(long id);

    /**
     * Transactionally creates the relationship, sets the properties (if any) and indexes the given fielss (if any)
     * Two shortcut means of providing the properties (very short with static imports)
     * <code>template.createRelationship(from,to,TYPE, Property._("name","value"));</code>
     * <code>template.createRelationship(from,to,TYPE, Property._("name","value","prop","anotherValue"));</code>
     *
     * @param startNode start-node of relationship
     * @param endNode end-node of relationship
     * @param type relationship type, might by an enum implementing RelationshipType or a DynamicRelationshipType.withName("name")
     * @param props optional initial properties
     * @return  the newly created relationship
     */
    Relationship createRelationship(Node startNode, Node endNode, RelationshipType type, Property... props);

    /**
     * Indexes the given field and value for the element.
     * @param indexName Name of the index, will be checked against existing indexes according to the given element
     * assumes a "node" node index  or "relationship" relationship index for a null value
     * @param element node or relationship to index
     * @param field field to index
     * @param value value to index
     * @param <T> the provided element type
     * @return the provided element for convenience
     */
    <T extends PropertyContainer> T index(String indexName, T element, String field, Object value);

    <T> QueryResult<T> convert(Iterable<T> iterable);

    QueryResult<Map<String, Object>> query(String statement);

    QueryResult<Path> traverse(Node startNode, TraversalDescription traversal);

    <T extends PropertyContainer> QueryResult<T> lookup(String indexName, String field, Object value);

    <T extends PropertyContainer> QueryResult<T> lookup(String indexName, Object valueOrQueryObject);
}
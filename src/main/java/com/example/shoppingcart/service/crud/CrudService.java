package com.example.shoppingcart.service.crud;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD service interface.
 *
 * @param <T> the type of the entity
 * @param <ID> the type of the entity's ID
 * @param <DTO> the type of the DTO for create/update operations
 */
public interface CrudService<T, ID, DTO> {

    /**
     * Creates a new entity.
     *
     * @param dto the DTO containing data for the new entity
     * @return the created entity
     */
    T create(DTO dto);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return an Optional containing the entity, or empty if not found
     */
    Optional<T> getById(ID id);

    /**
     * Updates an existing entity.
     *
     * @param id the ID of the entity to update
     * @param dto the DTO containing updated data for the entity
     * @return the updated entity
     */
    T update(ID id, DTO dto);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     */
    void delete(ID id);

    /**
     * Retrieves all entities. This method is disabled to prevent full table scans.
     *
     * @return a list of all entities
     */
    default List<T> getAll() {
        throw new UnsupportedOperationException("getAll() is disabled to prevent full table scans. Implement specific query methods.");
    }
}
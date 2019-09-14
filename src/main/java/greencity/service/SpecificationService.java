package greencity.service;

import greencity.dto.specification.SpecificationAddDto;
import greencity.entity.Specification;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides the interface to manage {@code Specification} entity.
 */
public interface SpecificationService {
    /**
     * Method for saving new Specification to database.
     *
     * @param dto - dto for Specification entity.
     * @return a Specification.
     */
    Specification save(SpecificationAddDto dto);

    /**
     * Save Specification to DB.
     *
     * @param specification - entity of Specification.
     * @return saved Specification.
     */
    Specification save(Specification specification);

    /**
     * Method for finding Specification by id.
     *
     * @param id - specification's id.
     * @return a specification.
     */
    Specification findById(Long id);

    /**
     * Method for finding all Specifications.
     *
     * @return list of Specifications.
     */
    List<Specification> findAll();

    /**
     * Method for deleting Specification by id.
     *
     * @param id - specification's id.
     * @return id of deleted Specification.
     */
    Long deleteById(Long id);

    /**
     * Finds specification by name.
     *
     * @param name to find by.
     * @return a specification by name.
     */
    Specification findByName(String name);
}

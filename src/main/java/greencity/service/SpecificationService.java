package greencity.service;

import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.Specification;
import java.util.List;
import java.util.Optional;

/**
 * Provides the interface to manage {@code Specification} entity.
 */
public interface SpecificationService {
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
    Optional<Specification> findByName(String name);

    /**
     * Method for finding all SpecificationNameDto.
     *
     * @return list of SpecificationNameDto.
     */
    List<SpecificationNameDto> findAllSpecificationDto();
}

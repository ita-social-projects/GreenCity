package greencity.service;

import greencity.dto.specification.SpecificationValueAddDto;
import greencity.entity.SpecificationValue;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides the interface to manage {@code SpecificationValue} entity.
 */
public interface SpecificationValueService {
    /**
     * Method for saving new SpecificationValue to database.
     *
     * @param dto - dto for SpecificationValue entity.
     * @return a SpecificationValue.
     */
    SpecificationValue save(SpecificationValueAddDto dto);

    /**
     * Save SpecificationValue to DB.
     *
     * @param specificationValue - entity of SpecificationValue.
     * @return saved SpecificationValue.
     */
    SpecificationValue save(SpecificationValue specificationValue);

    /**
     * Method for finding SpecificationValue by id.
     *
     * @param id - specificationValue's id.
     * @return a specificationValue.
     */
    SpecificationValue findById(Long id);

    /**
     * Method for finding all SpecificationValues.
     *
     * @return list of SpecificationValues.
     */
    List<SpecificationValue> findAll();

    /**
     * Method for deleting SpecificationValue by id.
     *
     * @param id - specificationValue's id.
     * @return id of deleted SpecificationValue.
     */
    Long deleteById(Long id);

    /**
     * Finds specificationValue by value.
     *
     * @param value to find by.
     * @return a specificationValue by value.
     */
    SpecificationValue findByValue(String value);
}

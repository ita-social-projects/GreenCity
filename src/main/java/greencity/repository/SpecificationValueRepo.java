package greencity.repository;

import greencity.entity.SpecificationValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationValueRepo extends JpaRepository<SpecificationValue, Long> {
    /**
     * Finds Specification by value.
     *
     * @param value to find by.
     * @return a SpecificationValue by value.
     */
    SpecificationValue findByValue(String value);
}

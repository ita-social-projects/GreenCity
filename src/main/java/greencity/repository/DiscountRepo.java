package greencity.repository;

import greencity.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepo  extends JpaRepository<Discount, Long> {
    Discount findByValue(int value);
}

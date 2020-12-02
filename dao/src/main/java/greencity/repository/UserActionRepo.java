package greencity.repository;

import greencity.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    UserAction findByUserId(Long id);
}

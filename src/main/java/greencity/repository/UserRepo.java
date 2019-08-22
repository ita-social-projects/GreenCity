package greencity.repository;

import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findAllByOrderByEmail(Pageable pageable);
}

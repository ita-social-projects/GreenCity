package greencity.repository;

import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    //zakhar
    boolean existsByEmail(String email);
    @Query("SELECT id from User where email=:email")
    Long findIdByEmail(String email);    //zakhar

}

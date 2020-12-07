package greencity.repository;

import greencity.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    /**
     * Method find {@link UserAction} by id.
     *
     * @param id of {@link UserAction}
     * @return UserAction {@link UserAction}
     * @author Orest Mamchuk
     */
    @Query(value = "select u from UserAction u join fetch u.user as us WHERE us.id = :id")
    UserAction findByUserId(Long id);

    @Query(nativeQuery = true,
            value = " SELECT * FROM public.fn_textsearchcolumn(:category, :userId) ")
    Integer findActionCountAccordToCategory(String category, Long userId);
}

package greencity.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepoCustom{

    Integer findActionCountAccordToCategory(String category, Long userId);
}

package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

}

package greencity.security.eventlisteners;

import greencity.security.events.UpdatePasswordEvent;
import greencity.security.service.OwnSecurityService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener that listens to {@link UpdatePasswordEvent} and delegate password
 * updating logic to {@link OwnSecurityService}.
 */
@Component
public class UpdatePasswordEventListener implements ApplicationListener<UpdatePasswordEvent> {
    private final OwnSecurityService ownSecurityService;

    /**
     * Constructor that is used for {@link OwnSecurityService} injection, which is
     * responsible for updating {@link greencity.entity.User}'s password.
     *
     * @param ownSecurityService service that directly does password update logic
     */
    public UpdatePasswordEventListener(OwnSecurityService ownSecurityService) {
        this.ownSecurityService = ownSecurityService;
    }

    /**
     * Updates user's current password, replacing it for that one that is acquired
     * from the event object.
     *
     * @param event event object from which new password and target user id are
     *              acquired
     */
    @Override
    public void onApplicationEvent(UpdatePasswordEvent event) {
        ownSecurityService.updatePassword(event.getNewPassword(), event.getUserId());
    }
}

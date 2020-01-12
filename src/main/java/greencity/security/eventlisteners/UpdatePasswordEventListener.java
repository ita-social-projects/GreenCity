package greencity.security.eventlisteners;

import greencity.security.events.UpdatePasswordEvent;
import greencity.security.service.OwnSecurityService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdatePasswordEventListener implements ApplicationListener<UpdatePasswordEvent> {
    private final OwnSecurityService ownSecurityService;

    public UpdatePasswordEventListener(OwnSecurityService ownSecurityService) {
        this.ownSecurityService = ownSecurityService;
    }

    @Override
    public void onApplicationEvent(UpdatePasswordEvent event) {
        ownSecurityService.updatePassword(event.getNewPassword(), event.getUserId());
    }
}

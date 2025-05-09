package greencity.aspects;

import greencity.annotations.CheckEmailPreference;
import greencity.client.UserRemoteClient;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.message.EmailMessage;
import greencity.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EmailPreferenceAspect {
    private final UserRemoteClient userRemoteClient;
    private final UserServiceImpl userServiceImpl;
    private final ModelMapper modelMapper;

    @Around("@annotation(checkEmailPreference)")
    public Object checkEmailPreference(ProceedingJoinPoint proceedingJoinPoint,
        CheckEmailPreference checkEmailPreference) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        EmailPreference emailPreference = checkEmailPreference.value();
        String email = extractEmailFromArgs(args);
        UserVO user = userServiceImpl.findByEmail(email);

        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            user.getId(),
            emailPreference,
            EmailPreferencePeriodicity.IMMEDIATELY);

        boolean hasPreference = userRemoteClient.searchUserNotificationPreference(emailPreferenceDto);

        if (hasPreference) {
            return proceedingJoinPoint.proceed();
        } else {
            return null;
        }
    }

    private String extractEmail(Object message) {
        if (message instanceof EmailMessage) {
            return ((EmailMessage) message).getEmail();
        } else if (message instanceof Notification) {
            // TODO
            // return ((Notification) message).getTargetUser().getEmail();
            return "email";
        }
        return null;
    }

    private String extractEmailFromArgs(Object[] args) {
        for (Object arg : args) {
            String email = extractEmail(arg);
            if (email != null) {
                return email;
            }
        }
        return null;
    }
}

package greencity.aspects;

import greencity.annotations.CheckEmailPreference;
import greencity.client.UserRemoteClient;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.message.UserIdMessage;
import greencity.service.UserService;
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
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Around("@annotation(checkEmailPreference)")
    public Object checkEmailPreference(ProceedingJoinPoint proceedingJoinPoint,
        CheckEmailPreference checkEmailPreference) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        EmailPreference emailPreference = checkEmailPreference.value();
        String userEmail = extractUserEmailFromArgs(args);

        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            userEmail,
            emailPreference,
            EmailPreferencePeriodicity.IMMEDIATELY);

        boolean hasPreference = userRemoteClient.searchUserNotificationPreference(emailPreferenceDto);

        if (hasPreference) {
            return proceedingJoinPoint.proceed();
        } else {
            return null;
        }
    }

    private String extractUserEmail(Object message) {
        if (message instanceof UserIdMessage) {
            Long userId = ((UserIdMessage) message).getUserId();
            UserVO user = userService.findById(userId);
            return user.getEmail();
        } else if (message instanceof Notification) {
            return ((Notification) message).getTargetUser().getEmail();
        }
        return null;
    }

    private String extractUserEmailFromArgs(Object[] args) {
        for (Object arg : args) {
            String userEmail = extractUserEmail(arg);
            if (userEmail != null) {
                return userEmail;
            }
        }
        return null;
    }
}

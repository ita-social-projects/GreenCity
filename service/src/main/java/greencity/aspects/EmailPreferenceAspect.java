package greencity.aspects;

import greencity.annotations.CheckEmailPreference;
import greencity.client.UserRemoteClient;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.entity.Notification;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.message.UserIdMessage;
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

    @Around("@annotation(checkEmailPreference)")
    public Object checkEmailPreference(ProceedingJoinPoint proceedingJoinPoint,
        CheckEmailPreference checkEmailPreference) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        EmailPreference emailPreference = checkEmailPreference.value();
        Long userId = extractUserIdFromArgs(args);

        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(
            userId,
            emailPreference,
            EmailPreferencePeriodicity.IMMEDIATELY);

        boolean hasPreference = userRemoteClient.searchUserNotificationPreference(emailPreferenceDto);

        if (hasPreference) {
            return proceedingJoinPoint.proceed();
        } else {
            return null;
        }
    }

    private Long extractUserId(Object message) {
        if (message instanceof UserIdMessage) {
            return ((UserIdMessage) message).getUserId();
        } else if (message instanceof Notification) {
            return ((Notification) message).getTargetUser().getId();
        }
        return null;
    }

    private Long extractUserIdFromArgs(Object[] args) {
        for (Object arg : args) {
            Long userId = extractUserId(arg);
            if (userId != null) {
                return userId;
            }
        }
        return null;
    }
}

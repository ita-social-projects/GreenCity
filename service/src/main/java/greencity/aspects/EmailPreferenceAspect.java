package greencity.aspects;

import greencity.annotations.CheckEmailPreference;
import greencity.dto.user.UserVO;
import greencity.enums.EmailPreference;
import greencity.message.EmailMessage;
import greencity.repository.UserNotificationPreferenceRepo;
import greencity.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EmailPreferenceAspect {
    private final UserNotificationPreferenceRepo userNotificationPreferenceRepo;
    private final UserServiceImpl userServiceImpl;

    @Around("@annotation(checkEmailPreference)")
    public Object checkEmailPreference(ProceedingJoinPoint proceedingJoinPoint,
        CheckEmailPreference checkEmailPreference) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        EmailPreference emailPreference = checkEmailPreference.value();
        String email = extractEmailFromArgs(args);
        UserVO user = userServiceImpl.findByEmail(email);

        boolean hasPreference =
            userNotificationPreferenceRepo.existsByUserIdAndEmailPreference(user.getId(), emailPreference);

        if (hasPreference) {
            return proceedingJoinPoint.proceed();
        } else {
            return null;
        }
    }

    public static String extractEmail(Object message) {
        if (message instanceof EmailMessage) {
            return ((EmailMessage) message).getEmail();
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

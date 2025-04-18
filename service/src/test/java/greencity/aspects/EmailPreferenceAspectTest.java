package greencity.aspects;

import greencity.ModelUtils;
import greencity.annotations.CheckEmailPreference;
import greencity.client.UserRemoteClient;
import greencity.dto.emailpreference.EmailPreferenceDto;
import greencity.dto.user.UserVO;
import greencity.enums.EmailPreference;
import greencity.enums.EmailPreferencePeriodicity;
import greencity.message.ScheduledEmailMessage;
import greencity.service.UserServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailPreferenceAspectTest {
    @Mock
    private UserRemoteClient userRemoteClient;
    @Mock
    private UserServiceImpl userServiceImpl;
    @Mock
    private CheckEmailPreference checkEmailPreference;
    @Mock
    ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    EmailPreferenceAspect emailPreferenceAspect;

    @Test
    void testProceedWhenUserHasEmailPreference() throws Throwable {
        EmailPreference emailPreference = EmailPreference.LIKES;
        when(checkEmailPreference.value()).thenReturn(emailPreference);

        Object[] args =
            {new ScheduledEmailMessage("username", "test@gmail.com", "baselink", "subject", "message", "en", false)};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        UserVO user = ModelUtils.getUserVO();
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(user.getId(), emailPreference, EmailPreferencePeriodicity.IMMEDIATELY);
        when(userServiceImpl.findByEmail("test@gmail.com")).thenReturn(user);

        when(userRemoteClient.searchUserNotificationPreference(emailPreferenceDto))
            .thenReturn(true);

        Object expectedResult = new Object();
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        Object result = emailPreferenceAspect.checkEmailPreference(proceedingJoinPoint, checkEmailPreference);

        verify(proceedingJoinPoint).proceed();

        assertEquals(expectedResult, result);
    }

    @Test
    void testProceedWhenUserDoesNotHaveEmailPreference() throws Throwable {
        EmailPreference emailPreference = EmailPreference.LIKES;
        when(checkEmailPreference.value()).thenReturn(emailPreference);

        Object[] args =
            {new ScheduledEmailMessage("username", "test@gmail.com", "baselink", "subject", "message", "en", false)};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        UserVO user = ModelUtils.getUserVO();
        EmailPreferenceDto emailPreferenceDto = new EmailPreferenceDto(user.getId(), emailPreference, EmailPreferencePeriodicity.IMMEDIATELY);

        when(userServiceImpl.findByEmail("test@gmail.com")).thenReturn(user);

        when(userRemoteClient.searchUserNotificationPreference(emailPreferenceDto))
            .thenReturn(false);

        Object result = emailPreferenceAspect.checkEmailPreference(proceedingJoinPoint, checkEmailPreference);

        verify(proceedingJoinPoint, never()).proceed();

        assertNull(result);
    }
}
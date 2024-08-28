package greencity.aspects;

import greencity.ModelUtils;
import greencity.annotations.CheckEmailPreference;
import greencity.dto.user.UserVO;
import greencity.enums.EmailPreference;
import greencity.message.GeneralEmailMessage;
import greencity.repository.UserNotificationPreferenceRepo;
import greencity.service.UserServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class EmailPreferenceAspectTest {
    @Mock
    private UserNotificationPreferenceRepo userNotificationPreferenceRepo;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private CheckEmailPreference checkEmailPreference;

    @Mock
    ModelUtils modelUtils;
    @Mock
    UserVO userVO;
    @Mock
    ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    EmailPreferenceAspect emailPreferenceAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProceedWhenUserHasEmailPreference() throws Throwable {

        EmailPreference emailPreference = EmailPreference.LIKES;
        when(checkEmailPreference.value()).thenReturn(emailPreference);

        Object[] args = {new GeneralEmailMessage("test@gmail.com", "subject", "message")};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        UserVO user = ModelUtils.getUserVO();
        when(userServiceImpl.findByEmail("test@gmail.com")).thenReturn(user);

        when(userNotificationPreferenceRepo.existsByUserIdAndEmailPreference(user.getId(), emailPreference))
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

        Object[] args = {new GeneralEmailMessage("test@gmail.com", "subject", "message")};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        UserVO user = ModelUtils.getUserVO();

        when(userServiceImpl.findByEmail("test@gmail.com")).thenReturn(user);

        when(userNotificationPreferenceRepo.existsByUserIdAndEmailPreference(user.getId(), emailPreference))
            .thenReturn(false);

        Object result = emailPreferenceAspect.checkEmailPreference(proceedingJoinPoint, checkEmailPreference);

        verify(proceedingJoinPoint, never()).proceed();

        assertNull(result);
    }
}
package greencity.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.ModelUtils;
import greencity.TestConst;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserDeactivatedException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSecurityServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    @Mock
    private JwtTool jwtTool;
    @Mock
    GoogleIdToken googleIdToken;
    @Mock
    ModelMapper modelMapper;
    @Spy
    GoogleIdToken.Payload payload;

    @InjectMocks
    GoogleSecurityServiceImpl googleSecurityService;

    @Test
    void authenticateUserNotNullTest() throws GeneralSecurityException, IOException {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmail("test@mail.com")).thenReturn(userVO);
        SuccessSignInDto result = googleSecurityService.authenticate("1234");
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void authenticateNullUserTest() throws GeneralSecurityException, IOException {
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(null);
        userVO.setName(null);
        when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmail("test@mail.com")).thenReturn(null);
        when(modelMapper.map(any(), eq(UserVO.class))).thenReturn(userVO);
        when(userService.save(userVO)).thenReturn(userVO);
        SuccessSignInDto result = googleSecurityService.authenticate("1234");
        assertNull(result.getUserId());
        assertNull(result.getName());
    }

    @Test
    void authenticationThrowsIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("1234"));
    }

    @Test
    void authenticationThrowsUserDeactivatedExceptionTest() throws GeneralSecurityException, IOException {
        User user = User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.DEACTIVATED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
        UserVO userVO = UserVO.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(ROLE.ROLE_USER)
                .userStatus(UserStatus.DEACTIVATED)
                .lastVisit(LocalDateTime.now())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        when(googleIdTokenVerifier.verify("1234")).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@mail.com");
        when(userService.findByEmail("test@mail.com")).thenReturn(userVO);
        assertThrows(UserDeactivatedException.class,
            () -> googleSecurityService.authenticate("1234"));
    }

    @Test
    void authenticationThrowsIllegalArgumentExceptionInCatchBlockTest() throws GeneralSecurityException, IOException {
        when(googleIdTokenVerifier.verify("1234")).thenThrow(GeneralSecurityException.class);
        assertThrows(IllegalArgumentException.class,
            () -> googleSecurityService.authenticate("1234"));
    }
}

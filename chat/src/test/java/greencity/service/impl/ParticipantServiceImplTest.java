package greencity.service.impl;

import greencity.entity.Participant;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.ParticipantRepo;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceImplTest {
    @InjectMocks
    ParticipantServiceImpl participantServiceImpl;
    @Mock
    ModelMapper modelMapper;
    @Mock
    ParticipantRepo participantRepo;
    private Participant expected;

    @BeforeEach
    void init() {
        expected = Participant.builder()
                .id(1L)
                .name("artur")
                .email("majboroda.artur@mail.com")
                .profilePicture(null)
                .userStatus(UserStatus.ACTIVATED)
                .build();
    }

    @Test
    void findByEmail() {
        String email = "majboroda.artur@mail.com";
        when(participantRepo.findNotDeactivatedByEmail(email)).thenReturn(Optional.of(expected)).thenThrow(UserNotFoundException.class);
        Optional<Participant> actual = Optional.ofNullable(participantServiceImpl.findByEmail(email));
        assertEquals(Optional.of(expected), actual);
    }

    @Test
    void findById() {
        when(participantRepo.findById(1L)).thenReturn(Optional.of(expected)).thenThrow(UserNotFoundException.class);
        Optional<Participant> actual = Optional.ofNullable(participantServiceImpl.findById(1L));
        assertEquals(Optional.of(expected), actual);
    }

    @Test
    void getCurrentParticipantByEmail() {
    }

    @Test
    void findAllExceptCurrentUser() {
    }

    @Test
    void findAllParticipantsByQuery() {
    }
}
package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.Participant;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.ParticipantRepo;
import greencity.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepo participantRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant findByEmail(String email) {
        return participantRepo.findNotDeactivatedByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant findById(Long id) {
        return participantRepo.findById(id)
            .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + id));
    }
}

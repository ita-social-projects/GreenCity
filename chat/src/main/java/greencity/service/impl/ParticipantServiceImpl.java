package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ParticipantDto;
import greencity.entity.Participant;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.ParticipantRepo;
import greencity.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepo participantRepo;
    private final ModelMapper modelMapper;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public ParticipantDto getCurrentParticipantByEmail(String email) {
        return modelMapper.map(
            participantRepo.findNotDeactivatedByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)),
            ParticipantDto.class);
    }

    @Override
    public List<ParticipantDto> findAllExceptCurrentUser(String email) {
        return modelMapper.map(
            participantRepo.findAllExceptCurrentUser(email),
            new TypeToken<List<ParticipantDto>>() {
            }.getType());
    }

    @Override
    public List<ParticipantDto> findAllParticipantsByQuery(String query, String currentUser) {
        return modelMapper.map(
            participantRepo.findAllParticipantsByQuery(query, currentUser),
            new TypeToken<List<ParticipantDto>>() {
            }.getType());
    }
}

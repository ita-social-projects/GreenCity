package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ParticipantDto;
import greencity.exception.exceptions.UserNotFoundException;
import greencity.repository.ParticipantRepo;
import greencity.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepo participantRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public ParticipantDto findByEmail(String email) {
        return modelMapper.map(
            participantRepo.findNotDeactivatedByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email)),
            ParticipantDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParticipantDto findById(Long id) {
        return modelMapper.map(
            participantRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + id)),
            ParticipantDto.class);
    }
}

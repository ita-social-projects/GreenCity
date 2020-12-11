package greencity.service;

import greencity.dto.ParticipantDto;
import greencity.entity.Participant;

public interface ParticipantService {
    /**
     * Method that allow you to find not 'DEACTIVATED' {@link Participant} by email.
     *
     * @param email - {@link Participant}'s email
     * @return {@link Participant} instance.
     */
    ParticipantDto findByEmail(String email);

    /**
     * Method that allow you to find not 'DEACTIVATED' {@link Participant} by id.
     *
     * @param id - {@link Participant} id
     * @return {@link Participant} instance.
     */
    ParticipantDto findById(Long id);
}

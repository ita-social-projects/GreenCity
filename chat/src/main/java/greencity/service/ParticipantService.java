package greencity.service;

import greencity.dto.ParticipantDto;
import greencity.entity.Participant;

import java.util.List;

public interface ParticipantService {
    /**
     * Method to find not 'DEACTIVATED' {@link Participant}/{@code User} by email.
     *
     * @param email - {@link Participant}'s email
     * @return {@link Participant} instance.
     */
    Participant findByEmail(String email);

    /**
     * Method to find not 'DEACTIVATED' {@link Participant}/{@code User} by id.
     *
     * @param id - {@link Participant} id
     * @return {@link Participant} instance.
     */
    Participant findById(Long id);

    /**
     * Method to find current {@link Participant}/{@code User} by username.
     *
     * @param email - {@link Participant} email.
     * @return {@link Participant} instance.
     */
    ParticipantDto getCurrentParticipantByEmail(String email);

    /**
     * {@inheritDoc}
     */
    List<ParticipantDto> findAllExceptCurrentUser(String email);

    /**
     * {@inheritDoc}
     */
    List<ParticipantDto> findAllParticipantsByQuery(String query, String currentUser);
}

package greencity.service;

import greencity.entity.User;

public interface VerifyEmailService {
    void save(User user);

    void verify(String token);
}

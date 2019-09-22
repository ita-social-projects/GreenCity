package greencity.service;

import greencity.entity.Rate;

/**
 * Provides the interface to manage {@code Rate} entity.
 * @author Marian Milian
 * @version 1.0
 */
public interface RateService {
    Rate save(Rate rate);
}

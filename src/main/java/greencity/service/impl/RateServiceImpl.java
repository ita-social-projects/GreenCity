package greencity.service.impl;

import greencity.entity.Rate;
import greencity.repository.RateRepo;
import greencity.service.RateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code RateService}.
 * @author Marian Milian
 * @version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class RateServiceImpl implements RateService {
    private RateRepo rateRepo;


    @Override
    public Rate save(Rate rate) {
        return rateRepo.save(rate);
    }
}

package greencity.service.impl;

import greencity.service.UtilService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceImpl implements UtilService {
    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<Long> getIdsFromString(String ids) {
        return Arrays.stream(ids.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }
}

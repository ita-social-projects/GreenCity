package greencity.service.impl;

import greencity.dto.achievement.AchievementDTO;
import greencity.repository.AchievementRepo;
import greencity.service.AchievementService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AchievementServiceImpl implements AchievementService {
    private AchievementRepo achievementRepo;
    private ModelMapper modelMapper;


    /**
     * dsa.
     * @return
     */
    @Override
    public List<AchievementDTO> findAll() {
        return achievementRepo.findAll()
            .stream()
            .map(achieve -> modelMapper.map(achieve, AchievementDTO.class))
            .collect(Collectors.toList());
    }
}

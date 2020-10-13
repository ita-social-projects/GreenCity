package greencity.service;

import greencity.dto.advice.AdviceTranslationVO;
import greencity.dto.advice.AdvicePostDto;
import java.util.List;

/**
 * AdviceTranslationService interface.
 *
 * @author Vitaliy Dzen
 */
public interface AdviceTranslationService {
    /**
     * Method saves new {@link AdviceTranslationVO}.
     *
     * @param adviceTranslations {@link AdviceTranslationVO}
     * @return instance of {@link AdviceTranslationVO}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslationVO> saveAdviceTranslation(List<AdviceTranslationVO> adviceTranslations);

    /**
     * Method saves new {@link AdviceTranslationVO} and
     * list of new {@link AdviceTranslationVO} with relationship to {@link AdviceTranslationVO}.
     *
     * @param advicePostDTO {@link AdvicePostDto}
     * @return List of {@link AdviceTranslationVO}
     * @author Vitaliy Dzen
     */
    List<AdviceTranslationVO> saveAdviceAndAdviceTranslation(AdvicePostDto advicePostDTO);
}

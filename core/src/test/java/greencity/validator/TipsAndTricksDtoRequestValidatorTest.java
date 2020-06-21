package greencity.validator;

import static greencity.ModelUtils.getTipsAndTricksDtoRequest;
import static greencity.ModelUtils.getUrl;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.TagNotFoundDuringValidation;
import greencity.service.TipsAndTricksTagsService;
import java.net.MalformedURLException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.powermock.api.mockito.PowerMockito.when;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TipsAndTricksDtoRequestValidatorTest {
    @InjectMocks
    private TipsAndTricksDtoRequestValidator validator;
    @Mock
    private TipsAndTricksTagsService tipsAndTricksTagsService;

    @Test
    void isValidTrueTest() throws MalformedURLException {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource(getUrl().toString());
        when(tipsAndTricksTagsService.isValidNumOfUniqueTags(tipsAndTricksDtoRequest.getTipsAndTricksTags()))
            .thenReturn(true);
        when(tipsAndTricksTagsService.isAllValid(tipsAndTricksDtoRequest.getTipsAndTricksTags())).thenReturn(true);
        assertTrue(validator.isValid(tipsAndTricksDtoRequest, null));
    }

    @Test
    void isValidTagNotFoundDuringValidationTest() throws MalformedURLException {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource(getUrl().toString());
        when(tipsAndTricksTagsService.isValidNumOfUniqueTags(tipsAndTricksDtoRequest.getTipsAndTricksTags()))
            .thenReturn(true);
        when(tipsAndTricksTagsService.isAllValid(tipsAndTricksDtoRequest.getTipsAndTricksTags())).thenReturn(false);
        assertThrows(TagNotFoundDuringValidation.class, () -> validator.isValid(tipsAndTricksDtoRequest, null));
    }

    @Test
    void isValidInvalidURLExceptionTest() {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource("ttt://");
        assertThrows(InvalidURLException.class, () -> validator.isValid(tipsAndTricksDtoRequest, null));
    }
}
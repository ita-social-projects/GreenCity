package greencity.validator;

import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.TagNotFoundDuringValidation;
import greencity.service.TagsService;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static greencity.ModelUtils.getTipsAndTricksDtoRequest;
import static greencity.ModelUtils.getUrl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TipsAndTricksDtoRequestValidatorTest {
    @InjectMocks
    private TipsAndTricksDtoRequestValidator validator;
    @Mock
    private TagsService tagService;

    @Test
    void isValidTrueTest() throws MalformedURLException {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource(getUrl().toString());
        when(tagService.isValidNumOfUniqueTags(tipsAndTricksDtoRequest.getTags()))
            .thenReturn(true);
        when(tagService.isAllTipsAndTricksValid(tipsAndTricksDtoRequest.getTags())).thenReturn(true);
        assertTrue(validator.isValid(tipsAndTricksDtoRequest, null));
    }

    @Test
    void isValidTagNotFoundDuringValidationTest() throws MalformedURLException {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource(getUrl().toString());
        when(tagService.isValidNumOfUniqueTags(tipsAndTricksDtoRequest.getTags()))
            .thenReturn(true);
        when(tagService.isAllTipsAndTricksValid(tipsAndTricksDtoRequest.getTags())).thenReturn(false);
        assertThrows(TagNotFoundDuringValidation.class, () -> validator.isValid(tipsAndTricksDtoRequest, null));
    }

    @Test
    void isValidInvalidURLExceptionTest() {
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = getTipsAndTricksDtoRequest();
        tipsAndTricksDtoRequest.setSource("ttt://");
        assertThrows(InvalidURLException.class, () -> validator.isValid(tipsAndTricksDtoRequest, null));
    }
}
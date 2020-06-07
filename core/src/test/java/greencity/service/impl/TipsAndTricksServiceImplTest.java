package greencity.service.impl;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.AppConstant;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.TipsAndTricksRepo;
import greencity.repository.TipsAndTricksTagsRepo;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import static org.powermock.api.mockito.PowerMockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TipsAndTricksServiceImplTest {
    @Mock
    private TipsAndTricksRepo tipsAndTricksRepo;
    @Mock
    private TipsAndTricksTagsRepo tipsAndTricksTagsRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private CloudStorageService fileService;
    @Mock
    private LanguageServiceImpl languageService;
    @Mock
    private NewsSubscriberServiceImpl newsSubscriberService;
    @InjectMocks
    private TipsAndTricksServiceImpl tipsAndTricksService;

    private TipsAndTricksDtoRequest tipsAndTricksDtoRequest = ModelUtils.getTipsAndTricksDtoRequest();
    private TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
    private TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
    private TipsAndTricksTag tipsAndTricksTag = ModelUtils.getTipsAndTricksTag();

    @Test
    public void saveTest() throws MalformedURLException {
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);
        when(newsSubscriberService.findAll()).thenReturn(Collections.emptyList());
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUser());
        when(tipsAndTricksTagsRepo.findByName("tipsAndTricksTag"))
            .thenReturn(Optional.of(tipsAndTricksTag));
        when(fileService.upload(null)).thenReturn(ModelUtils.getUrl());
        when(tipsAndTricksTagsRepo.findAllByNames(tipsAndTricksDtoRequest.getTipsAndTricksTags()))
            .thenReturn(Collections.singletonList(ModelUtils.getTipsAndTricksTag()));
        assertEquals(tipsAndTricks, tipsAndTricksService.save(tipsAndTricksDtoRequest,
            null, TestConst.EMAIL));
    }

    @Test
    public void findAllTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = new PageRequest(0, 2);
        Page<TipsAndTricks> translationPage = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(tipsAndTricksRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(translationPage);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));
        assertEquals(pageableDto, tipsAndTricksService.findAll(pageRequest));
    }

    @Test
    public void findTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = new PageRequest(0, 2);
        Page<TipsAndTricks> translationPage = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));
        when(tipsAndTricksRepo.find(pageRequest, Collections.singletonList(ModelUtils.getTipsAndTricksTag().getName())))
            .thenReturn(translationPage);
        assertEquals(pageableDto, tipsAndTricksService
            .find(pageRequest, Collections.singletonList(ModelUtils.getTipsAndTricksTag().getName())));
    }

    @Test
    public void findDtoByIdTest() {
        TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertEquals(tipsAndTricksDtoResponse, tipsAndTricksService.findDtoById(1L));
    }

    @Test
    public void findDtoByIdFailedTest() {
        TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.empty());
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertThrows(NotFoundException.class, () -> {
            tipsAndTricksService.findDtoById(1L);
        });
    }

    @Test
    public void delete() {
        doNothing().when(tipsAndTricksRepo).deleteById(1L);
        when(tipsAndTricksRepo.findById(anyLong()))
            .thenReturn(Optional.of(ModelUtils.getTipsAndTricks()));
        tipsAndTricksService.delete(1L);
        verify(tipsAndTricksRepo, times(1)).deleteById(1L);
    }
}

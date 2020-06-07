package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.TipsAndTricksRepo;
import greencity.service.TipsAndTricksTagsService;
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
class TipsAndTricksServiceImplTest {
    @Mock
    private TipsAndTricksRepo tipsAndTricksRepo;
    @Mock
    private TipsAndTricksTagsService tipsAndTricksTagsService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private TipsAndTricksServiceImpl tipsAndTricksService;

    private TipsAndTricksDtoRequest tipsAndTricksDtoRequest = ModelUtils.getTipsAndTricksDtoRequest();
    private TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
    private TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
    private TipsAndTricksTag tipsAndTricksTag = ModelUtils.getTipsAndTricksTag();

    @Test
    void saveTest() {
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUser());
        when(tipsAndTricksTagsService.findAllByNames(anyList()))
            .thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertEquals(tipsAndTricksDtoResponse, tipsAndTricksService.save(tipsAndTricksDtoRequest,
            null, ModelUtils.getUser().getEmail()));
    }

    @Test
    void findAllTest() {
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
    void findTest() {
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
    void findDtoByIdTest() {
        TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertEquals(tipsAndTricksDtoResponse, tipsAndTricksService.findDtoById(1L));
    }

    @Test
    void findDtoByIdFailedTest() {
        TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.empty());
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertThrows(NotFoundException.class, () -> {
            tipsAndTricksService.findDtoById(1L);
        });
    }

    @Test
    void delete() {
        doNothing().when(tipsAndTricksRepo).deleteById(1L);
        when(tipsAndTricksRepo.findById(anyLong()))
            .thenReturn(Optional.of(ModelUtils.getTipsAndTricks()));
        tipsAndTricksService.delete(1L);
        verify(tipsAndTricksRepo, times(1)).deleteById(1L);
    }
}

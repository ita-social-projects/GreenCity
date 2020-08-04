package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.TipsAndTricksRepo;
import greencity.service.FileService;
import greencity.service.TagsService;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
class TipsAndTricksServiceImplTest {
    @Mock
    FileService fileService;
    @Mock
    private TipsAndTricksRepo tipsAndTricksRepo;
    @Mock
    private TagsService tagService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private TipsAndTricksServiceImpl tipsAndTricksService;

    private TipsAndTricksDtoRequest tipsAndTricksDtoRequest = ModelUtils.getTipsAndTricksDtoRequest();
    private TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
    private TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
    private Tag tipsAndTricksTag = ModelUtils.getTag();

    @Test
    void saveTest() {
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUser());
        when(tagService.findTipsAndTricksTagsByNames(anyList()))
            .thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertEquals(tipsAndTricksDtoResponse, tipsAndTricksService.save(tipsAndTricksDtoRequest,
            null, ModelUtils.getUser().getEmail()));
    }

    @Test
    void saveFailedTest() {
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUser());
        when(tagService.findTipsAndTricksTagsByNames(anyList()))
            .thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(tipsAndTricksRepo.save(tipsAndTricks)).thenThrow(DataIntegrityViolationException.class);
        assertThrows(NotSavedException.class, () ->
            tipsAndTricksService.save(tipsAndTricksDtoRequest, null, ModelUtils.getUser().getEmail()));
    }

    @Test
    void saveUploadImageTest() throws IOException {
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        tipsAndTricksDtoRequest.setImage(imageToEncode);
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(tipsAndTricksDtoRequest.getImage(), MultipartFile.class)).thenReturn(image);
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());
        when(tagService.findTipsAndTricksTagsByNames(anyList()))
            .thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);
        assertEquals(tipsAndTricksDtoResponse, tipsAndTricksService.save(tipsAndTricksDtoRequest,
            image, ModelUtils.getUser().getEmail()));
    }

    @Test
    void findAllTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(tipsAndTricksRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));
        assertEquals(pageableDto, tipsAndTricksService.findAll(pageRequest));
    }

    @Test
    void findTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));
        when(tipsAndTricksRepo.find(pageRequest, Collections.singletonList(ModelUtils.getTag().getName())))
            .thenReturn(page);
        assertEquals(pageableDto, tipsAndTricksService
            .find(pageRequest, Collections.singletonList(ModelUtils.getTag().getName())));
        dtoList.get(0).setTags(null);
        when((tipsAndTricksRepo.findAllByOrderByCreationDateDesc(pageRequest))).thenReturn(page);
        assertEquals(pageableDto, tipsAndTricksService.find(pageRequest, null));
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
        assertThrows(NotFoundException.class, () -> tipsAndTricksService.findDtoById(1L));
    }

    @Test
    void delete() {
        doNothing().when(tipsAndTricksRepo).deleteById(1L);
        when(tipsAndTricksRepo.findById(anyLong()))
            .thenReturn(Optional.of(ModelUtils.getTipsAndTricks()));
        tipsAndTricksService.delete(1L);
        verify(tipsAndTricksRepo, times(1)).deleteById(1L);
    }

    @Test
    void search() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<SearchTipsAndTricksDto> dtoList = page.stream()
            .map(t -> modelMapper.map(t, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());
        PageableDto<SearchTipsAndTricksDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(tipsAndTricksRepo.searchTipsAndTricks(pageRequest, tipsAndTricks.get(0).getTitle())).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), SearchTipsAndTricksDto.class)).thenReturn(dtoList.get(0));
        assertEquals(pageableDto, tipsAndTricksService.search(tipsAndTricks.get(0).getTitle()));
    }
}

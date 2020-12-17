package greencity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tipsandtricks.TextTranslationVO;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import greencity.dto.tipsandtricks.TipsAndTricksViewDto;
import greencity.dto.tipsandtricks.TitleTranslationVO;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.filters.TipsAndTricksSpecification;
import greencity.repository.TipsAndTricksRepo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Mock
    private LanguageService languageService;
    @InjectMocks
    private TipsAndTricksServiceImpl tipsAndTricksService;
    @Mock
    TipsAndTricksTranslationService tipsAndTricksTranslationService;
    private TipsAndTricksDtoManagement tipsAndTricksDtoManagement = ModelUtils.getTipsAndTricksDtoManagement();
    private TipsAndTricksDtoRequest tipsAndTricksDtoRequest = ModelUtils.getTipsAndTricksDtoRequest();
    private TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
    private TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
    private Tag tipsAndTricksTag = ModelUtils.getTag();
    private TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
    private TipsAndTricksCommentVO tipsAndTricksCommentVO = ModelUtils.getTipsAndTricksCommentVO();
    private User user = ModelUtils.getUser();
    private UserVO userVO = ModelUtils.getUserVO();
    private List<TagTranslationVO> tagTranslationVOList = Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини")
        .languageVO(LanguageVO.builder().id(1L).code("ua").build())
        .build(),
        TagTranslationVO.builder().id(2L).name("News")
            .languageVO(LanguageVO.builder().id(2L).code("en").build())
            .build(),
        TagTranslationVO.builder().id(3L).name("Новины")
            .languageVO(LanguageVO.builder().id(1L).code("ru").build())
            .build());
    private TagVO tagVO = new TagVO(1L, TagType.TIPS_AND_TRICKS, tagTranslationVOList, null, null, null);

    @Test
    void saveTest() {
        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<TagVO>>() {
        }.getType())).thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);

        TipsAndTricksDtoResponse actual =
            tipsAndTricksService.save(tipsAndTricksDtoRequest, null, ModelUtils.getUser().getEmail());

        assertEquals(tipsAndTricksDtoResponse, actual);

        verify(tipsAndTricksTranslationService)
            .saveTitleTranslations(modelMapper.map(tipsAndTricks.getTitleTranslations(),
                new TypeToken<List<TitleTranslationVO>>() {
                }.getType()));
        verify(tipsAndTricksTranslationService)
            .saveTextTranslations(modelMapper.map(tipsAndTricks.getTextTranslations(),
                new TypeToken<List<TextTranslationVO>>() {
                }.getType()));
    }

    @Test
    void saveFailedTest() {
        String email = ModelUtils.getUser().getEmail();

        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<TagVO>>() {
        }.getType())).thenReturn(Collections.singletonList(tipsAndTricksTag));
        when(tipsAndTricksRepo.save(tipsAndTricks)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(NotSavedException.class, () -> tipsAndTricksService.save(tipsAndTricksDtoRequest, null, email));
    }

    @Test
    void saveUploadImageTest() throws IOException {
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        tipsAndTricksDtoRequest.setImage(imageToEncode);

        when(modelMapper.map(tipsAndTricksDtoRequest, TipsAndTricks.class)).thenReturn(tipsAndTricks);
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(modelMapper.map(tipsAndTricksDtoRequest.getImage(), MultipartFile.class)).thenReturn(image);
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);

        TipsAndTricksDtoResponse actual =
            tipsAndTricksService.save(tipsAndTricksDtoRequest, image, ModelUtils.getUser().getEmail());

        assertEquals(tipsAndTricksDtoResponse, actual);
    }

    @Test
    void saveTipsAndTricksWithTranslationsTest() {
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(modelMapper.map(languageService.findByCode(any()), Language.class)).thenReturn(ModelUtils.getLanguage());
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);

        TipsAndTricksDtoManagement actual = tipsAndTricksService.saveTipsAndTricksWithTranslations(
            tipsAndTricksDtoManagement, null, ModelUtils.getUser().getEmail());

        verify(tipsAndTricksRepo).save(any(TipsAndTricks.class));
        verify(tipsAndTricksTranslationService)
            .saveTextTranslations(modelMapper.map(tipsAndTricksDtoManagement.getTextTranslations(),
                new TypeToken<List<TextTranslationVO>>() {
                }.getType()));
        verify(tipsAndTricksTranslationService)
            .saveTitleTranslations(modelMapper.map(tipsAndTricksDtoManagement.getTitleTranslations(),
                new TypeToken<List<TitleTranslationVO>>() {
                }.getType()));

        assertEquals(tipsAndTricksDtoManagement, actual);
    }

    @Test
    void saveTipsAndTricksWithTranslationsUploadImageTest() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();

        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(modelMapper.map(languageService.findByCode(any()), Language.class)).thenReturn(ModelUtils.getLanguage());
        when(userService.findByEmail(TestConst.EMAIL)).thenReturn(ModelUtils.getUserVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());

        TipsAndTricksDtoManagement actual = tipsAndTricksService.saveTipsAndTricksWithTranslations(
            tipsAndTricksDtoManagement, image, ModelUtils.getUser().getEmail());

        assertEquals(tipsAndTricksDtoManagement, actual);
    }

    @Test
    void findAllManagementDtosTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());

        List<TipsAndTricksDtoManagement> dtoManagementList =
            Collections.singletonList(tipsAndTricksDtoManagement);
        PageableDto<TipsAndTricksDtoManagement> pageableManagementDto =
            new PageableDto<>(dtoManagementList, dtoManagementList.size(), 0, 1);

        when(tipsAndTricksRepo.findAllByOrderByCreationDateDesc(pageRequest)).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoManagement.class))
            .thenReturn(dtoManagementList.get(0));
        PageableDto<TipsAndTricksDtoManagement> actual = tipsAndTricksService.findAllManagementDtos(pageRequest);

        assertEquals(pageableManagementDto, actual);
    }

    @Test
    void findAllTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(tipsAndTricksRepo.findByTitleTranslationsLanguageCodeOrderByCreationDateDesc("en", pageRequest))
            .thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));

        PageableDto<TipsAndTricksDtoResponse> actual = tipsAndTricksService.findAll(pageRequest);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findWithoutTagsTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());

        when(tipsAndTricksRepo.findByTitleTranslationsLanguageCodeOrderByCreationDateDesc(
            AppConstant.DEFAULT_LANGUAGE_CODE,
            pageRequest)).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));

        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);
        PageableDto<TipsAndTricksDtoResponse> actual =
            tipsAndTricksService.find(pageRequest, null, AppConstant.DEFAULT_LANGUAGE_CODE);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findWithTagsTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        List<String> tags = Collections.singletonList("tag");
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());

        List<TipsAndTricksDtoResponse> dtoList = Collections.singletonList(ModelUtils.getTipsAndTricksDtoResponse());
        PageableDto<TipsAndTricksDtoResponse> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(tipsAndTricksRepo.find(pageRequest, tags)).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoResponse.class)).thenReturn(dtoList.get(0));

        PageableDto<TipsAndTricksDtoResponse> actual =
            tipsAndTricksService.find(pageRequest, tags, AppConstant.DEFAULT_LANGUAGE_CODE);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findDtoByIdTest() {
        TipsAndTricksDtoResponse tipsAndTricksDtoResponse = ModelUtils.getTipsAndTricksDtoResponse();
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(tipsAndTricksRepo.findByIdAndTitleTranslationsLanguageCode(1L, "en"))
            .thenReturn(Optional.of(tipsAndTricks));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoResponse.class)).thenReturn(tipsAndTricksDtoResponse);

        TipsAndTricksDtoResponse actual = tipsAndTricksService.findDtoById(1L);

        assertEquals(tipsAndTricksDtoResponse, actual);
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
        when(tipsAndTricksRepo.findById(1L))
            .thenReturn(Optional.of(ModelUtils.getTipsAndTricks()));
        tipsAndTricksService.delete(1L);

        verify(tipsAndTricksRepo, times(1)).deleteById(1L);
    }

    @Test
    void findByIdTest() {
        TipsAndTricksVO expected = ModelUtils.getTipsAndTricksVO();

        when(tipsAndTricksRepo.findById(any())).thenReturn(Optional.of(tipsAndTricks));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksVO.class)).thenReturn(expected);

        TipsAndTricksVO actual = tipsAndTricksService.findById(1L);
        assertEquals(expected, actual);
    }

    @Test
    void findByIdFailedTest() {
        Long id = 1L;
        when(tipsAndTricksRepo.findById(id))
            .thenThrow(new NotFoundException(ErrorMessage.TIPS_AND_TRICKS_NOT_FOUND_BY_ID + id));
        assertThrows(NotFoundException.class, () -> tipsAndTricksService.findById(id));
    }

    @Test
    void search() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<SearchTipsAndTricksDto> dtoList = page.stream()
            .map(t -> modelMapper.map(t, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());
        PageableDto<SearchTipsAndTricksDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(tipsAndTricksRepo
            .searchTipsAndTricks(pageRequest, tipsAndTricks.get(0).getTitleTranslations().get(0).getContent(), "en"))
                .thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), SearchTipsAndTricksDto.class)).thenReturn(dtoList.get(0));

        PageableDto<SearchTipsAndTricksDto> actual =
            tipsAndTricksService.search(tipsAndTricks.get(0).getTitleTranslations().get(0).getContent());

        assertEquals(pageableDto, actual);
    }

    @Test
    void update() throws MalformedURLException {
        MultipartFile image = ModelUtils.getFile();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(userService.findByEmail(tipsAndTricksDtoManagement.getAuthorName())).thenReturn(ModelUtils.getUserVO());
        when(languageService.getAllLanguages()).thenReturn(Collections.singletonList(new LanguageDTO(2L, "en")));
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());

        tipsAndTricksService.update(tipsAndTricksDtoManagement, image);
        assertEquals("title content", tipsAndTricks.getTitleTranslations().get(0).getContent());
        verify(tipsAndTricksRepo).save(tipsAndTricks);

    }

    @Test
    void deleteAll() {
        List<Long> listId = Collections.singletonList(1L);
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        tipsAndTricksService.deleteAll(listId);
        verify(tipsAndTricksRepo, times(1)).deleteById(1L);
    }

    @Test
    void findManagementDtoById() {
        Long id = 1L;
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        when(modelMapper.map(tipsAndTricks, TipsAndTricksDtoManagement.class)).thenReturn(tipsAndTricksDtoManagement);
        assertEquals(tipsAndTricksDtoManagement, tipsAndTricksService.findManagementDtoById(id));
    }

    @Test
    void testSearch() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<SearchTipsAndTricksDto> dtoList = page.stream()
            .map(t -> modelMapper.map(t, SearchTipsAndTricksDto.class))
            .collect(Collectors.toList());
        PageableDto<SearchTipsAndTricksDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(tipsAndTricksRepo
            .searchTipsAndTricks(pageRequest, tipsAndTricks.get(0).getTitleTranslations().get(0).getContent(), "en"))
                .thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), SearchTipsAndTricksDto.class)).thenReturn(dtoList.get(0));

        PageableDto<SearchTipsAndTricksDto> actual =
            tipsAndTricksService.search(pageRequest, tipsAndTricks.get(0).getTitleTranslations().get(0).getContent());

        assertEquals(pageableDto, actual);
    }

    @Test
    void searchBy() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());
        List<TipsAndTricksDtoResponse> tipsAndTricksDtoResponses = page.stream()
            .map(t -> modelMapper.map(t, TipsAndTricksDtoResponse.class))
            .collect(Collectors.toList());
        PageableDto<TipsAndTricksDtoResponse> tipsAndTricksDtoResponsePageableDto =
            new PageableDto<>(tipsAndTricksDtoResponses,
                page.getTotalElements(),
                page.getPageable().getPageNumber(),
                page.getTotalPages());

        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(tipsAndTricksRepo
            .searchBy(pageRequest, tipsAndTricks.get(0).getTitleTranslations().get(0).getContent(), "en"))
                .thenReturn(page);

        assertEquals(tipsAndTricksDtoResponsePageableDto, tipsAndTricksService
            .searchBy(pageRequest, tipsAndTricks.get(0).getTitleTranslations().get(0).getContent()));
    }

    @Test
    void getAmountOfWrittenTipsAndTrickByUserId() {
        Long id = 1L;
        when(tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(id)).thenReturn(1L);
        assertEquals(id, tipsAndTricksService.getAmountOfWrittenTipsAndTrickByUserId(id));
    }

    @Test
    void likeComment() {
        TipsAndTricksCommentVO initial = tipsAndTricksCommentVO;
        tipsAndTricksService.likeComment(userVO, initial);
        assertTrue(initial.getUsersLiked().contains(userVO));
    }

    @Test
    void unlikeComment() {
        TipsAndTricksComment initial = tipsAndTricksComment;
        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        tipsAndTricksComment.setUsersLiked(userSet);
        tipsAndTricksService.unlikeComment(ModelUtils.getUserVO(), ModelUtils.getTipsAndTricksCommentVO());
        assertEquals(initial, tipsAndTricksComment);

    }

    @Test
    void searchTipsAndTricksByTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageable, tipsAndTricks.size());
        when(tipsAndTricksRepo.searchTipsAndTricksBy(pageable, "query")).thenReturn(page);
        PageableDto<TipsAndTricksDtoManagement> expected = new PageableDto<>(Collections.emptyList(), 1, 0, 1);
        PageableDto<TipsAndTricksDtoManagement> actual = tipsAndTricksService.searchTipsAndTricksBy(pageable, "query");
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
    }

    @Test
    void getFilteredDataForManagementByPageTest() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricks> page = new PageImpl<>(tipsAndTricks, pageRequest, tipsAndTricks.size());

        List<TipsAndTricksDtoManagement> dtoManagementList =
            Collections.singletonList(tipsAndTricksDtoManagement);
        PageableDto<TipsAndTricksDtoManagement> expectedDto =
            new PageableDto<>(dtoManagementList, dtoManagementList.size(), 0, 1);

        TipsAndTricksViewDto tipsAndTricksViewDto = TipsAndTricksViewDto.builder()
            .id("1")
            .titleTranslations("titleTranslations")
            .author("author")
            .startDate(LocalDate.now().toString())
            .endDate(LocalDate.now().plusDays(1).toString())
            .build();

        when(tipsAndTricksRepo.findAll(any(TipsAndTricksSpecification.class), eq(pageRequest))).thenReturn(page);
        when(modelMapper.map(tipsAndTricks.get(0), TipsAndTricksDtoManagement.class))
            .thenReturn(tipsAndTricksDtoManagement);

        PageableDto<TipsAndTricksDtoManagement> actual =
            tipsAndTricksService.getFilteredDataForManagementByPage(pageRequest, tipsAndTricksViewDto);

        assertEquals(expectedDto, actual);
    }
}

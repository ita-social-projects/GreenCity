package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.dto.PageableDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tipsandtricks.*;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentVO;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.TipsAndTricksRepo;
import java.io.IOException;
import java.util.*;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

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
    @Mock
    UserActionService userActionService;
    @InjectMocks
    private TipsAndTricksServiceImpl tipsAndTricksService;
    @Mock
    TipsAndTricksTranslationService tipsAndTricksTranslationService;
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
    void update() {
        TipsAndTricksDtoManagement tipsAndTricksDtoManagement = TipsAndTricksDtoManagement.builder()
            .id(1L)
            .titleTranslations(Collections.singletonList(new TitleTranslationEmbeddedPostDTO("test", "en")))
            .textTranslations(Collections.singletonList(new TextTranslationDTO("texttexttexttexttexttext", "en")))
            .creationDate(tipsAndTricks.getCreationDate())
            .authorName("orest@gmail.com")
            .tags(tipsAndTricks.getTags()
                .stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName)
                .collect(Collectors.toList()))
            .build();
        when(tipsAndTricksRepo.findById(1L)).thenReturn(Optional.of(tipsAndTricks));
        List<TagVO> tagVOList = Collections.singletonList(tagVO);
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.TIPS_AND_TRICKS)))
            .thenReturn(tagVOList);
        when(userService.findByEmail(tipsAndTricksDtoManagement.getAuthorName())).thenReturn(ModelUtils.getUserVO());
        when(languageService.getAllLanguages()).thenReturn(Collections.singletonList(new LanguageDTO(2L, "en")));

        tipsAndTricksService.update(tipsAndTricksDtoManagement, null);
        assertEquals("test", tipsAndTricks.getTitleTranslations().get(0).getContent());
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
        List<String> tags = Collections.singletonList("test");
        TipsAndTricksDtoManagement tipsAndTricksDtoManagement = TipsAndTricksDtoManagement.builder()
            .id(id)
            .titleTranslations(Collections.singletonList(new TitleTranslationEmbeddedPostDTO("test", "en")))
            .textTranslations(Collections.singletonList(new TextTranslationDTO("texttexttexttexttexttext", "en")))
            .tags(tags)
            .authorName("orest@gmail.com")
            .build();
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
    void calculateTipsAndTricksLikes() {
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionService.findUserActionByUserId(1L)).thenReturn(userActionVO);
        when(userActionService.updateUserActions(userActionVO)).thenReturn(userActionVO);
        tipsAndTricksService.calculateTipsAndTricksLikes(userVO);
    }
}

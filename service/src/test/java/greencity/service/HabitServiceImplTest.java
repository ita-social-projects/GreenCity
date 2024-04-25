package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.habit.CustomHabitDtoRequest;
import greencity.dto.habit.CustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.HabitAssign;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.mapping.CustomShoppingListResponseDtoMapper;
import greencity.mapping.HabitTranslationDtoMapper;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitTranslationRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.mapping.CustomHabitMapper;
import greencity.mapping.CustomShoppingListMapper;
import greencity.mapping.HabitTranslationMapper;
import greencity.repository.CustomShoppingListItemRepo;
import greencity.repository.LanguageRepo;
import greencity.repository.TagsRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class HabitServiceImplTest {

    @InjectMocks
    private HabitServiceImpl habitService;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private HabitTranslationRepo habitTranslationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CustomHabitMapper customHabitMapper;

    @Mock
    private HabitTranslationMapper habitTranslationMapper;

    @Mock
    private CustomShoppingListMapper customShoppingListMapper;

    @Mock
    private CustomShoppingListResponseDtoMapper customShoppingListResponseDtoMapper;

    @Mock
    private HabitTranslationDtoMapper habitTranslationDtoMapper;

    @Mock
    FileService fileService;

    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TagsRepo tagsRepo;

    @Mock
    private LanguageRepo languageRepo;

    @Mock
    private CustomShoppingListItemRepo customShoppingListItemRepo;

    @Mock
    private HabitAssignService habitAssignService;

    @Test()
    void getByIdAndLanguageCodeIsCustomHabitFalse() {
        Habit habit = ModelUtils.getHabit();
        habit.setIsCustomHabit(false);
        HabitDto habitDto = ModelUtils.getHabitDto();
        habitDto.setIsCustomHabit(false);
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, "en"))
            .thenReturn(Optional.of(habitTranslation));
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        assertEquals(habitDto, habitService.getByIdAndLanguageCode(1L, "en"));
        verify(habitRepo).findById(1L);
        verify(habitTranslationRepo).findByHabitAndLanguageCode(habit, "en");
        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
    }

    @Test()
    void getByIdAndLanguageCodeIsCustomHabitTrue() {
        Habit habit = ModelUtils.getHabit();
        habit.setIsCustomHabit(true);
        habit.setCustomShoppingListItems(List.of(ModelUtils.getCustomShoppingListItem()));
        HabitDto habitDto = ModelUtils.getHabitDto();
        habitDto.setIsCustomHabit(true);
        habitDto.setCustomShoppingListItems(List.of(ModelUtils.getCustomShoppingListItemResponseDto()));
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, "en"))
            .thenReturn(Optional.of(habitTranslation));
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        assertEquals(habitDto, habitService.getByIdAndLanguageCode(1L, "en"));
        verify(habitRepo).findById(1L);
        verify(habitTranslationRepo).findByHabitAndLanguageCode(habit, "en");
        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
    }

    @Test
    void getByIdAndLanguageCodeNotFoundException() {
        assertThrows(NotFoundException.class, () -> habitService.getByIdAndLanguageCode(1L, "en"));
    }

    @Test
    void getByIdAndLanguageCodeHabitTranslationNotFoundException2() {
        Habit habit = ModelUtils.getHabit();
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        assertThrows(NotFoundException.class, () -> habitService.getByIdAndLanguageCode(1L, "en"));
    }

    @Test
    void getAllHabitsByLanguageCode() {
        Pageable pageable = PageRequest.of(0, 2);
        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationUa();
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        Habit habit = ModelUtils.getHabit();
        habit.setIsCustomHabit(true);
        habit.setUserId(1L);
        HabitDto habitDto = ModelUtils.getHabitDto();
        habitDto.setIsCustomHabit(true);
        UserVO userVO = ModelUtils.getUserVO();
        List<Long> requestedCustomHabitIds = List.of(1L);
        when(habitAssignRepo.findAllHabitIdsByUserIdAndStatusIsRequested(1L)).thenReturn(requestedCustomHabitIds);
        when(habitTranslationRepo.findAllByLanguageCodeAndHabitAssignIdsRequestedAndUserId(pageable,
            requestedCustomHabitIds, userVO.getId(), "en")).thenReturn(habitTranslationPage);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitRepo.findById(1L)).thenReturn(Optional.ofNullable(habit));
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(habitTranslationRepo.getHabitTranslationByUaLanguage(habit.getId())).thenReturn(habitTranslationUa);
        when(userRepo.findUserLanguageCodeByUserId(userVO.getId())).thenReturn("en");
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());
        assertEquals(pageableDto, habitService.getAllHabitsByLanguageCode(userVO, pageable));

        verify(habitTranslationRepo).findAllByLanguageCodeAndHabitAssignIdsRequestedAndUserId(any(Pageable.class),
            anyList(), anyLong(), anyString());
        verify(habitTranslationRepo).getHabitTranslationByUaLanguage(anyLong());
        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
        verify(habitAssignRepo).findByHabitIdAndUserId(anyLong(), anyLong());
        verify(habitAssignRepo).findAllHabitIdsByUserIdAndStatusIsRequested(anyLong());
        verify(habitRepo).findById(1L);
        verify(userRepo).findUserLanguageCodeByUserId(anyLong());
    }

    @Test
    void getAllHabitsByLanguageCodeWhenRequestedCustomHabitIdsIsEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationUa();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        Habit habit = ModelUtils.getHabit();
        habit.setIsCustomHabit(true);
        habit.setUserId(1L);
        HabitDto habitDto = ModelUtils.getHabitDto();
        habitDto.setIsCustomHabit(true);
        UserVO userVO = ModelUtils.getUserVO();
        List<Long> requestedCustomHabitIds = new ArrayList<>();
        when(habitAssignRepo.findAllHabitIdsByUserIdAndStatusIsRequested(1L)).thenReturn(requestedCustomHabitIds);
        when(habitTranslationRepo.findAllByLanguageCodeAndHabitAssignIdsRequestedAndUserId(pageable,
            requestedCustomHabitIds, userVO.getId(), "ua")).thenReturn(habitTranslationPage);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitRepo.findById(1L)).thenReturn(Optional.ofNullable(habit));
        when(habitAssignRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(habitTranslationRepo.getHabitTranslationByUaLanguage(habit.getId())).thenReturn(habitTranslationUa);
        when(userRepo.findUserLanguageCodeByUserId(userVO.getId())).thenReturn("ua");
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());
        assertEquals(pageableDto, habitService.getAllHabitsByLanguageCode(userVO, pageable));

        verify(habitTranslationRepo).findAllByLanguageCodeAndHabitAssignIdsRequestedAndUserId(any(Pageable.class),
            anyList(), anyLong(), anyString());
        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
        verify(habitAssignRepo).findByHabitIdAndUserId(anyLong(), anyLong());
        verify(habitAssignRepo).findAllHabitIdsByUserIdAndStatusIsRequested(anyLong());
        verify(habitTranslationRepo).getHabitTranslationByUaLanguage(anyLong());
        verify(habitRepo).findById(1L);
        verify(userRepo).findUserLanguageCodeByUserId(anyLong());
    }

    @Test
    void getAllByTagsAndLanguageCode() {
        Pageable pageable = PageRequest.of(0, 2);
        String tag = "ECO_NEWS";
        List<String> tags = Collections.singletonList(tag);
        List<String> lowerCaseTags = Collections.singletonList(tag.toLowerCase());
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        HabitDto habitDto = ModelUtils.getHabitDto();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitTranslationRepo.findAllByTagsAndLanguageCode(pageable, lowerCaseTags, "en"))
            .thenReturn(habitTranslationPage);
        assertEquals(pageableDto, habitService.getAllByTagsAndLanguageCode(pageable, tags, "en"));
    }

    private static Stream<Arguments> getAllByDifferentParametersArguments() {
        return Stream.of(
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.of(true), Optional.of(List.of(1))),
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.of(false), Optional.of(List.of(1))),
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.of(true), Optional.empty()),
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.of(false), Optional.empty()),
            arguments(Optional.empty(), Optional.of(true), Optional.of(List.of(1))),
            arguments(Optional.empty(), Optional.of(false), Optional.of(List.of(1))),
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.empty(), Optional.of(List.of(1))),
            arguments(Optional.empty(), Optional.of(true), Optional.empty()),
            arguments(Optional.empty(), Optional.of(false), Optional.empty()),
            arguments(Optional.of(Collections.singletonList("HABIT")), Optional.empty(), Optional.empty()),
            arguments(Optional.empty(), Optional.empty(), Optional.of(List.of(1))),
            arguments(Optional.empty(), Optional.of(false), Optional.empty()));
    }

    @ParameterizedTest
    @MethodSource("getAllByDifferentParametersArguments")
    void getAllByDifferentParameters(Optional<List<String>> tags, Optional<Boolean> isCustomHabit,
        Optional<List<Integer>> complexities) {
        Pageable pageable = PageRequest.of(0, 2);
        String tag = "HABIT";
        Long userId = ModelUtils.getUser().getId();
        List<String> lowerCaseTags = Collections.singletonList(tag.toLowerCase());
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslationWithCustom();
        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationUa();
        HabitDto habitDto = ModelUtils.getHabitDto();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());

        List<Long> requestedCustomHabitIds = List.of(1L);
        when(habitAssignRepo.findAllHabitIdsByUserIdAndStatusIsRequested(userId)).thenReturn(requestedCustomHabitIds);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitRepo.findById(1L)).thenReturn(Optional.ofNullable(ModelUtils.getHabitWithCustom()));

        when(habitTranslationRepo.findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(any(Pageable.class),
            anyList(), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByDifferentParametersIsCustomHabitFalse(any(Pageable.class), anyList(), any(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(
            any(Pageable.class), anyList(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndIsCustomHabitFalseAndLanguageCode(any(Pageable.class), anyList(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(
            any(Pageable.class), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(any(Pageable.class), any(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(
            any(Pageable.class), anyList(), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(any(Pageable.class),
            anyString(), anyList(), anyLong()))
            .thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByIsCustomFalseHabitAndLanguageCode(any(Pageable.class), anyString()))
            .thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(any(Pageable.class), any(),
            anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(
            any(Pageable.class), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.getHabitTranslationByUaLanguage(anyLong())).thenReturn(habitTranslationUa);

        if (isCustomHabit.isPresent() && tags.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(pageable,
                    lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByDifferentParametersIsCustomHabitFalse(pageable, lowerCaseTags,
                    complexities, "en");
            }
        } else if (isCustomHabit.isPresent() && tags.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                    lowerCaseTags, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByTagsAndIsCustomHabitFalseAndLanguageCode(pageable, lowerCaseTags,
                    "en");
            }
        } else if (isCustomHabit.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                    complexities, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(pageable, complexities,
                    "en");
            }
        } else if (complexities.isPresent() && tags.isPresent()) {
            habitTranslationRepo.findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
        } else if (isCustomHabit.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(pageable,
                    "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByIsCustomFalseHabitAndLanguageCode(pageable, "en");
            }
        } else if (tags.isPresent()) {
            habitTranslationRepo.findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(pageable,
                lowerCaseTags, "en", requestedCustomHabitIds, userId);
        } else if (complexities.isPresent()) {
            habitTranslationRepo.findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                complexities, "en", requestedCustomHabitIds, userId);
        }

        assertEquals(pageableDto, habitService.getAllByDifferentParameters(ModelUtils.getUserVO(), pageable, tags,
            isCustomHabit, complexities, "en"));

        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
        verify(habitRepo).findById(1L);
        verify(habitTranslationRepo).getHabitTranslationByUaLanguage(anyLong());

        if (isCustomHabit.isPresent() && tags.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(pageable,
                        lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByDifferentParametersIsCustomHabitFalse(pageable, lowerCaseTags, complexities,
                        "en");
            }
        } else if (isCustomHabit.isPresent() && tags.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                        lowerCaseTags, "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByTagsAndIsCustomHabitFalseAndLanguageCode(pageable, lowerCaseTags, "en");
            }
        } else if (isCustomHabit.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable, complexities,
                        "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(pageable, complexities,
                        "en");
            }
        } else if (complexities.isPresent() && tags.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(pageable, lowerCaseTags,
                    complexities, "en", requestedCustomHabitIds, userId);
        } else if (isCustomHabit.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(pageable, "en",
                        requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByIsCustomFalseHabitAndLanguageCode(pageable, "en");
            }
        } else if (tags.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(pageable,
                    lowerCaseTags, "en", requestedCustomHabitIds, userId);
        } else if (complexities.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                    complexities, "en", requestedCustomHabitIds, userId);
        }
    }

    @ParameterizedTest
    @MethodSource("getAllByDifferentParametersArguments")
    void getAllByDifferentParametersWhenRequestedCustomHabitIdsIsEmpty(Optional<List<String>> tags,
        Optional<Boolean> isCustomHabit, Optional<List<Integer>> complexities) {
        Pageable pageable = PageRequest.of(0, 2);
        String tag = "HABIT";
        Long userId = ModelUtils.getUser().getId();
        List<String> lowerCaseTags = Collections.singletonList(tag.toLowerCase());
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslationWithCustom();
        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationUa();
        HabitDto habitDto = ModelUtils.getHabitDto();
        Page<HabitTranslation> habitTranslationPage =
            new PageImpl<>(Collections.singletonList(habitTranslation), pageable, 10);
        List<HabitDto> habitDtoList = Collections.singletonList(habitDto);
        PageableDto pageableDto = new PageableDto(habitDtoList, habitTranslationPage.getTotalElements(),
            habitTranslationPage.getPageable().getPageNumber(), habitTranslationPage.getTotalPages());

        List<Long> requestedCustomHabitIds = new ArrayList<>();
        when(habitAssignRepo.findAllHabitIdsByUserIdAndStatusIsRequested(userId)).thenReturn(requestedCustomHabitIds);
        when(modelMapper.map(habitTranslation, HabitDto.class)).thenReturn(habitDto);
        when(habitAssignRepo.findAmountOfUsersAcquired(anyLong())).thenReturn(5L);
        when(habitRepo.findById(1L)).thenReturn(Optional.ofNullable(ModelUtils.getHabitWithCustom()));

        when(habitTranslationRepo.findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(any(Pageable.class),
            anyList(), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByDifferentParametersIsCustomHabitFalse(any(Pageable.class), anyList(), any(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(
            any(Pageable.class), anyList(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndIsCustomHabitFalseAndLanguageCode(any(Pageable.class), anyList(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(
            any(Pageable.class), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(any(Pageable.class), any(),
            anyString())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(
            any(Pageable.class), anyList(), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(any(Pageable.class),
            anyString(), anyList(), anyLong()))
            .thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByIsCustomFalseHabitAndLanguageCode(any(Pageable.class), anyString()))
            .thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(any(Pageable.class), any(),
            anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(
            any(Pageable.class), any(), anyString(), anyList(), anyLong())).thenReturn(habitTranslationPage);
        when(habitTranslationRepo.getHabitTranslationByUaLanguage(anyLong())).thenReturn(habitTranslationUa);

        if (isCustomHabit.isPresent() && tags.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(pageable,
                    lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByDifferentParametersIsCustomHabitFalse(pageable, lowerCaseTags,
                    complexities, "en");
            }
        } else if (isCustomHabit.isPresent() && tags.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                    lowerCaseTags, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByTagsAndIsCustomHabitFalseAndLanguageCode(pageable, lowerCaseTags,
                    "en");
            }
        } else if (isCustomHabit.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                    complexities, "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(pageable, complexities,
                    "en");
            }
        } else if (complexities.isPresent() && tags.isPresent()) {
            habitTranslationRepo.findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
        } else if (isCustomHabit.isPresent()) {
            if (isCustomHabit.get()) {
                habitTranslationRepo.findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(pageable,
                    "en", requestedCustomHabitIds, userId);
            } else {
                habitTranslationRepo.findAllByIsCustomFalseHabitAndLanguageCode(pageable, "en");
            }
        } else if (tags.isPresent()) {
            habitTranslationRepo.findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(pageable,
                lowerCaseTags, "en", requestedCustomHabitIds, userId);
        } else if (complexities.isPresent()) {
            habitTranslationRepo.findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                complexities, "en", requestedCustomHabitIds, userId);
        }

        assertEquals(pageableDto, habitService.getAllByDifferentParameters(ModelUtils.getUserVO(), pageable, tags,
            isCustomHabit, complexities, "en"));

        verify(modelMapper).map(habitTranslation, HabitDto.class);
        verify(habitAssignRepo).findAmountOfUsersAcquired(anyLong());
        verify(habitRepo).findById(1L);
        verify(habitTranslationRepo).getHabitTranslationByUaLanguage(anyLong());

        if (isCustomHabit.isPresent() && tags.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByDifferentParametersByUserIdAndStatusRequested(pageable,
                        lowerCaseTags, complexities, "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByDifferentParametersIsCustomHabitFalse(pageable, lowerCaseTags, complexities,
                        "en");
            }
        } else if (isCustomHabit.isPresent() && tags.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByTagsAndLanguageCodeAndByUserIdAndStatusRequested(pageable,
                        lowerCaseTags, "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByTagsAndIsCustomHabitFalseAndLanguageCode(pageable, lowerCaseTags, "en");
            }
        } else if (isCustomHabit.isPresent() && complexities.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable, complexities,
                        "en", requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByIsCustomHabitFalseAndComplexityAndLanguageCode(pageable, complexities,
                        "en");
            }
        } else if (complexities.isPresent() && tags.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByTagsAndComplexityAndLanguageCodeAndByUserIdAndStatusRequested(pageable, lowerCaseTags,
                    complexities, "en", requestedCustomHabitIds, userId);
        } else if (isCustomHabit.isPresent()) {
            if (isCustomHabit.get()) {
                verify(habitTranslationRepo, times(2))
                    .findCustomHabitsByLanguageCodeAndByUserIdAndStatusRequested(pageable, "en",
                        requestedCustomHabitIds, userId);
            } else {
                verify(habitTranslationRepo, times(2))
                    .findAllByIsCustomFalseHabitAndLanguageCode(pageable, "en");
            }
        } else if (tags.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByTagsAndLanguageCodeAndByUserIdAndRequestedStatus(pageable,
                    lowerCaseTags, "en", requestedCustomHabitIds, userId);
        } else if (complexities.isPresent()) {
            verify(habitTranslationRepo, times(2))
                .findAllByComplexityAndLanguageCodeAndUserIdAndStatusRequested(pageable,
                    complexities, "en", requestedCustomHabitIds, userId);
        }
    }

    @Test
    void getShoppingListForHabit() {
        ShoppingListItemTranslation shoppingListItemTranslation = ModelUtils.getShoppingListItemTranslation();
        List<ShoppingListItemTranslation> shoppingListItemTranslations =
            Collections.singletonList(shoppingListItemTranslation);
        ShoppingListItemDto shoppingListItemDto = new ShoppingListItemDto(1L, "test", "ACTIVE");
        List<ShoppingListItemDto> shoppingListItemDtos = Collections.singletonList(shoppingListItemDto);
        when(modelMapper.map(shoppingListItemTranslation, ShoppingListItemDto.class)).thenReturn(shoppingListItemDto);
        when(shoppingListItemTranslationRepo.findShoppingListByHabitIdAndByLanguageCode("en", 1L))
            .thenReturn(shoppingListItemTranslations);
        assertEquals(shoppingListItemDtos, habitService.getShoppingListForHabit(1L, "en"));
    }

    @Test
    void addShoppingListItemToHabitTest() {
        doNothing().when(habitRepo).addShopingListItemToHabit(1L, 1L);
        habitService.addShoppingListItemToHabit(1L, 1L);
        verify(habitRepo).addShopingListItemToHabit(1L, 1L);
    }

    @Test
    void deleteShoppingListItemTest() {
        doNothing().when(habitRepo).upadateShopingListItemInHabit(1L, 1L);
        habitService.deleteShoppingListItem(1L, 1L);
        verify(habitRepo).upadateShopingListItemInHabit(1L, 1L);
    }

    @Test
    void addAllShoppingListItemToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.getFirst(), 1L);
        habitService.addAllShoppingListItemsByListOfId(1L, listID);
        verify(habitRepo, times(1)).addShopingListItemToHabit(listID.getFirst(), 1L);
    }

    @Test
    void deleteAllShoppingListItemToHabitTest() {
        List<Long> listID = Collections.singletonList(1L);
        doNothing().when(habitRepo).addShopingListItemToHabit(listID.getFirst(), 1L);
        habitService.deleteAllShoppingListItemsByListOfId(1L, listID);
        verify(habitRepo, times(1)).upadateShopingListItemInHabit(listID.getFirst(), 1L);
    }

    @Test
    void addCustomHabitTestWithImagePathInDto() throws IOException {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Language languageUa = ModelUtils.getLanguageUa();
        Language languageEn = ModelUtils.getLanguage();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDtoForServiceTest();
        CustomShoppingListItem customShoppingListItem = ModelUtils.getCustomShoppingListItemForServiceTest();

        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        addCustomHabitDtoRequest.setImage(imageToEncode);
        CustomHabitDtoResponse addCustomHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();
        addCustomHabitDtoResponse.setImage(imageToEncode);

        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();

        List<HabitTranslationDto> habitTranslationDtoList = List.of(
            habitTranslationDto.setLanguageCode("en"),
            habitTranslationDto.setLanguageCode("ua"));

        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationForServiceTest();
        List<HabitTranslation> habitTranslationList = List.of(
            habitTranslationUa.setLanguage(languageEn),
            habitTranslationUa.setLanguage(languageUa));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)))
            .thenReturn(List.of(habitTranslationUa));
        when(languageRepo.findByCode("ua")).thenReturn(Optional.of(languageUa));
        when(languageRepo.findByCode("en")).thenReturn(Optional.of(languageEn));
        when(customShoppingListItemRepo.findAllByUserIdAndHabitId(1L, 1L)).thenReturn(List.of(customShoppingListItem));
        when(customShoppingListMapper.mapAllToList(List.of(customShoppingListItemResponseDto)))
            .thenReturn(List.of(customShoppingListItem));
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(addCustomHabitDtoResponse);
        when(customShoppingListResponseDtoMapper.mapAllToList(List.of(customShoppingListItem)))
            .thenReturn(List.of(customShoppingListItemResponseDto));
        when(habitTranslationRepo.findAllByHabit(habit)).thenReturn(habitTranslationList);
        when(habitTranslationDtoMapper.mapAllToList(habitTranslationList)).thenReturn(habitTranslationDtoList);

        when(fileService.upload(image)).thenReturn(imageToEncode);

        assertEquals(addCustomHabitDtoResponse,
            habitService.addCustomHabit(addCustomHabitDtoRequest, null, "taras@gmail.com"));

        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findById(20L);
        verify(habitTranslationMapper, times(2)).mapAllToList(List.of(habitTranslationDto));
        verify(languageRepo, times(2)).findByCode(anyString());
        verify(customShoppingListItemRepo).findAllByUserIdAndHabitId(1L, 1L);
        verify(customShoppingListMapper).mapAllToList(anyList());
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(customShoppingListResponseDtoMapper).mapAllToList(List.of(customShoppingListItem));
        verify(habitTranslationRepo).findAllByHabit(habit);
        verify(habitTranslationDtoMapper).mapAllToList(habitTranslationList);
        verify(fileService).convertToMultipartImage(any());
    }

    @Test
    void addCustomHabitTestWithImageFile() throws IOException {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Language languageUa = ModelUtils.getLanguageUa();
        Language languageEn = ModelUtils.getLanguage();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDtoForServiceTest();
        CustomShoppingListItem customShoppingListItem = ModelUtils.getCustomShoppingListItemForServiceTest();

        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        CustomHabitDtoResponse addCustomHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();
        addCustomHabitDtoResponse.setImage(imageToEncode);

        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();

        List<HabitTranslationDto> habitTranslationDtoList = List.of(
            habitTranslationDto.setLanguageCode("en"),
            habitTranslationDto.setLanguageCode("ua"));

        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationForServiceTest();
        List<HabitTranslation> habitTranslationList = List.of(
            habitTranslationUa.setLanguage(languageEn),
            habitTranslationUa.setLanguage(languageUa));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)))
            .thenReturn(List.of(habitTranslationUa));
        when(languageRepo.findByCode("ua")).thenReturn(Optional.of(languageUa));
        when(languageRepo.findByCode("en")).thenReturn(Optional.of(languageEn));
        when(customShoppingListItemRepo.findAllByUserIdAndHabitId(1L, 1L)).thenReturn(List.of(customShoppingListItem));
        when(customShoppingListMapper.mapAllToList(List.of(customShoppingListItemResponseDto)))
            .thenReturn(List.of(customShoppingListItem));
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(addCustomHabitDtoResponse);
        when(customShoppingListResponseDtoMapper.mapAllToList(List.of(customShoppingListItem)))
            .thenReturn(List.of(customShoppingListItemResponseDto));
        when(habitTranslationRepo.findAllByHabit(habit)).thenReturn(habitTranslationList);
        when(habitTranslationDtoMapper.mapAllToList(habitTranslationList)).thenReturn(habitTranslationDtoList);

        when(fileService.upload(image)).thenReturn(imageToEncode);

        assertEquals(addCustomHabitDtoResponse,
            habitService.addCustomHabit(addCustomHabitDtoRequest, image, "taras@gmail.com"));

        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findById(20L);
        verify(habitTranslationMapper, times(2)).mapAllToList(List.of(habitTranslationDto));
        verify(languageRepo, times(2)).findByCode(anyString());
        verify(customShoppingListItemRepo).findAllByUserIdAndHabitId(1L, 1L);
        verify(customShoppingListMapper).mapAllToList(anyList());
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(customShoppingListResponseDtoMapper).mapAllToList(List.of(customShoppingListItem));
        verify(habitTranslationRepo).findAllByHabit(habit);
        verify(habitTranslationDtoMapper).mapAllToList(habitTranslationList);
        verify(fileService).upload(any());
    }

    @Test
    void addCustomHabitTest2() throws IOException {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Language languageUa = ModelUtils.getLanguageUa();
        Language languageEn = ModelUtils.getLanguage();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDtoForServiceTest();
        CustomShoppingListItem customShoppingListItem = ModelUtils.getCustomShoppingListItemForServiceTest();

        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        CustomHabitDtoResponse addCustomHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();
        addCustomHabitDtoResponse.setImage(imageToEncode);

        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();

        List<HabitTranslationDto> habitTranslationDtoList = List.of(
            habitTranslationDto.setLanguageCode("en"),
            habitTranslationDto.setLanguageCode("ua"));

        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationForServiceTest();
        List<HabitTranslation> habitTranslationList = List.of(
            habitTranslationUa.setLanguage(languageEn),
            habitTranslationUa.setLanguage(languageUa));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)))
            .thenReturn(List.of(habitTranslationUa));
        when(languageRepo.findByCode("ua")).thenReturn(Optional.of(languageUa));
        when(languageRepo.findByCode("en")).thenReturn(Optional.of(languageEn));
        when(customShoppingListItemRepo.findAllByUserIdAndHabitId(1L, 1L)).thenReturn(List.of(customShoppingListItem));
        when(customShoppingListMapper.mapAllToList(List.of(customShoppingListItemResponseDto)))
            .thenReturn(List.of(customShoppingListItem));
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(addCustomHabitDtoResponse);
        when(customShoppingListResponseDtoMapper.mapAllToList(List.of(customShoppingListItem)))
            .thenReturn(List.of(customShoppingListItemResponseDto));
        when(habitTranslationRepo.findAllByHabit(habit)).thenReturn(habitTranslationList);
        when(habitTranslationDtoMapper.mapAllToList(habitTranslationList)).thenReturn(habitTranslationDtoList);

        when(fileService.upload(image)).thenReturn(imageToEncode);

        assertEquals(addCustomHabitDtoResponse,
            habitService.addCustomHabit(addCustomHabitDtoRequest, null, "taras@gmail.com"));

        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findById(20L);
        verify(habitTranslationMapper, times(2)).mapAllToList(List.of(habitTranslationDto));
        verify(languageRepo, times(2)).findByCode(anyString());
        verify(customShoppingListItemRepo).findAllByUserIdAndHabitId(1L, 1L);
        verify(customShoppingListMapper).mapAllToList(anyList());
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(customShoppingListResponseDtoMapper).mapAllToList(List.of(customShoppingListItem));
        verify(habitTranslationRepo).findAllByHabit(habit);
        verify(habitTranslationDtoMapper).mapAllToList(habitTranslationList);
    }

    @Test
    void addCustomHabitNoSuchElementExceptionWithNotExistingLanguageCodeTestUa() throws IOException {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        addCustomHabitDtoRequest.setImage(imageToEncode);
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        habitTranslationDto.setLanguageCode("ua");
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslationForServiceTest();

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)))
            .thenReturn(List.of(habitTranslation));
        when(languageRepo.findByCode("ua")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
            () -> habitService.addCustomHabit(addCustomHabitDtoRequest, image, "taras@gmail.com"));

        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findById(20L);
        verify(habitTranslationMapper).mapAllToList(addCustomHabitDtoRequest.getHabitTranslations());
        verify(languageRepo).findByCode(anyString());
    }

    @Test
    void addCustomHabitNoSuchElementExceptionWithNotExistingLanguageCodeEn() throws IOException {
        User user = ModelUtils.getUser();
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Language languageUa = ModelUtils.getLanguageUa();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();
        habitTranslationDto.setLanguageCode("ua");
        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationForServiceTest();

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitTranslationMapper.mapAllToList(List.of(habitTranslationDto)))
            .thenReturn(List.of(habitTranslationUa));
        when(languageRepo.findByCode("ua")).thenReturn(Optional.of(languageUa));
        when(languageRepo.findByCode("en")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
            () -> habitService.addCustomHabit(addCustomHabitDtoRequest, image, "taras@gmail.com"));

        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(customHabitMapper.convert(addCustomHabitDtoRequest));
        verify(customHabitMapper, times(3)).convert(addCustomHabitDtoRequest);
        verify(tagsRepo).findById(20L);

        verify(habitTranslationMapper, times(2)).mapAllToList(addCustomHabitDtoRequest.getHabitTranslations());
        verify(languageRepo, times(2)).findByCode(anyString());
    }

    @Test
    void addCustomHabitThrowUserNotFoundException() {
        CustomHabitDtoRequest addCustomHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        when(userRepo.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(habitRepo.save(customHabitMapper.convert(addCustomHabitDtoRequest))).thenReturn(nullable(Habit.class));

        assertThrows(WrongEmailException.class,
            () -> habitService.addCustomHabit(addCustomHabitDtoRequest, image, "user@gmail.com"));

        verify(userRepo).findByEmail("user@gmail.com");
        verify(customHabitMapper).convert(addCustomHabitDtoRequest);
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesTest() {
        Long habitId = 1L;
        Long userId = 2L;
        Long friendId = 3L;
        User friend = ModelUtils.getUser();
        friend.setId(friendId);
        friend.setProfilePicturePath("test");
        UserProfilePictureDto friendProfilePicture = UserProfilePictureDto.builder()
            .id(friend.getId())
            .name(friend.getName())
            .profilePicturePath(friend.getProfilePicturePath())
            .build();

        when(userRepo.existsById(userId)).thenReturn(true);
        when(habitRepo.existsById(habitId)).thenReturn(true);
        when(userRepo.getFriendsAssignedToHabit(userId, habitId)).thenReturn(List.of(friend));
        when(modelMapper.map(friend, UserProfilePictureDto.class)).thenReturn(friendProfilePicture);

        List<UserProfilePictureDto> list = habitService.getFriendsAssignedToHabitProfilePictures(habitId, userId);
        assertFalse(list.isEmpty());
        assertEquals(friendProfilePicture, list.getFirst());

        verify(userRepo).existsById(userId);
        verify(habitRepo).existsById(habitId);
        verify(userRepo).getFriendsAssignedToHabit(userId, habitId);
        verify(modelMapper).map(friend, UserProfilePictureDto.class);
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesWhenUserNotFoundTest() {
        Long habitId = 1L;
        Long userId = 2L;

        when(userRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> habitService.getFriendsAssignedToHabitProfilePictures(habitId, userId));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_ID + userId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(habitRepo, never()).existsById(anyLong());
        verify(userRepo, never()).getFriendsAssignedToHabit(anyLong(), anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesWhenHabitNotFoundTest() {
        Long habitId = 1L;
        Long userId = 2L;

        when(userRepo.existsById(userId)).thenReturn(true);
        when(habitRepo.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> habitService.getFriendsAssignedToHabitProfilePictures(habitId, userId));

        assertEquals(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId, exception.getMessage());

        verify(userRepo).existsById(userId);
        verify(habitRepo).existsById(habitId);
        verify(userRepo, never()).getFriendsAssignedToHabit(anyLong(), anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateCustomHabitTest() throws IOException {
        User user = ModelUtils.getUser();
        user.setRole(Role.ROLE_ADMIN);
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Language languageUa = ModelUtils.getLanguageUa();
        Language languageEn = ModelUtils.getLanguage();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setTags(Set.of(tag));
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto =
            ModelUtils.getCustomShoppingListItemResponseDtoForServiceTest();
        CustomShoppingListItem customShoppingListItem = ModelUtils.getCustomShoppingListItemForServiceTest();

        CustomHabitDtoRequest customHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestWithImage();
        CustomHabitDtoResponse customHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();
        customHabitDtoResponse.setImage(imageToEncode);

        HabitTranslationDto habitTranslationDto = ModelUtils.getHabitTranslationDto();

        List<HabitTranslationDto> habitTranslationDtoList = List.of(
            habitTranslationDto.setLanguageCode("en"),
            habitTranslationDto.setLanguageCode("ua"));

        HabitTranslation habitTranslationUa = ModelUtils.getHabitTranslationForServiceTest();
        List<HabitTranslation> habitTranslationList = List.of(
            habitTranslationUa.setLanguage(languageEn),
            habitTranslationUa.setLanguage(languageUa));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepo.save(customHabitMapper.convert(customHabitDtoRequest))).thenReturn(habit);
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(customShoppingListItemRepo.findAllByUserIdAndHabitId(anyLong(), anyLong()))
            .thenReturn(List.of(customShoppingListItem));
        when(customShoppingListMapper.mapAllToList(List.of(customShoppingListItemResponseDto)))
            .thenReturn(List.of(customShoppingListItem));
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(customHabitDtoResponse);
        when(habitTranslationRepo.findAllByHabit(habit)).thenReturn(habitTranslationList);
        when(habitTranslationDtoMapper.mapAllToList(habitTranslationList)).thenReturn(habitTranslationDtoList);
        when(habitRepo.save(habit)).thenReturn(habit);
        when(fileService.upload(image)).thenReturn(imageToEncode);

        assertEquals(customHabitDtoResponse,
            habitService.updateCustomHabit(customHabitDtoRequest, 1L, "taras@gmail.com", image));

        verify(habitRepo).findById(anyLong());
        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(any());
        verify(customHabitMapper).convert(customHabitDtoRequest);
        verify(tagsRepo).findById(20L);
        verify(customShoppingListItemRepo, times(2)).findAllByUserIdAndHabitId(anyLong(), anyLong());
        verify(customShoppingListMapper).mapAllToList(anyList());
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(customShoppingListResponseDtoMapper).mapAllToList(List.of(customShoppingListItem));
        verify(habitTranslationRepo, times(2)).findAllByHabit(habit);
        verify(habitTranslationDtoMapper).mapAllToList(habitTranslationList);
    }

    @Test
    void updateCustomHabitThrowsUserNotFoundException() {
        CustomHabitDtoRequest customHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestForServiceTest();
        when(userRepo.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(habitRepo.save(customHabitMapper.convert(customHabitDtoRequest))).thenReturn(nullable(Habit.class));

        assertThrows(WrongEmailException.class,
            () -> habitService.updateCustomHabit(customHabitDtoRequest, 1L, "user@gmail.com", null));

        verify(userRepo).findByEmail("user@gmail.com");
        verify(customHabitMapper).convert(customHabitDtoRequest);
    }

    @Test
    void updateCustomHabitThrowsUserHasNoPermissionToAccessException() {
        CustomHabitDtoRequest customHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestWithImage();
        User user = ModelUtils.getUser();
        String email = user.getEmail();
        user.setRole(Role.ROLE_USER);

        Habit habit = ModelUtils.getCustomHabitForServiceTest();

        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> habitService.updateCustomHabit(customHabitDtoRequest, 1L, email, null));

        verify(userRepo).findByEmail(anyString());
        verify(habitRepo).findById(anyLong());
    }

    @Test
    void updateCustomHabitWithNewCustomShoppingListItemToUpdateTest() throws IOException {
        User user = ModelUtils.getTestUser();
        user.setRole(Role.ROLE_ADMIN);
        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        CustomShoppingListItem newItem = ModelUtils.getCustomShoppingListItemForUpdate();
        newItem.setId(null);

        CustomHabitDtoRequest customHabitDtoRequest = ModelUtils
            .getСustomHabitDtoRequestWithNewCustomShoppingListItem();
        CustomHabitDtoResponse customHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();
        when(customShoppingListMapper.mapAllToList(any()))
            .thenReturn(List.of(newItem));
        when(customShoppingListItemRepo.save(any())).thenReturn(ModelUtils.getCustomShoppingListItemForUpdate());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(tagsRepo.findById(20L)).thenReturn(Optional.of(tag));
        when(habitRepo.save(habit)).thenReturn(habit);
        when(fileService.upload(image)).thenReturn(imageToEncode);
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(customHabitDtoResponse);

        assertEquals(customHabitDtoResponse,
            habitService.updateCustomHabit(customHabitDtoRequest, 1L, "user@email.com", image));

        verify(customShoppingListItemRepo, times(2)).findAllByUserIdAndHabitId(2L, 1L);
        verify(customShoppingListItemRepo).save(any());
        verify(habitRepo).findById(anyLong());
        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(any());
        verify(tagsRepo).findById(20L);
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(habitTranslationRepo).findAllByHabit(habit);
    }

    @Test
    void updateCustomHabitWithComplexityToUpdateTest() throws IOException {
        User user = ModelUtils.getTestUser();
        user.setRole(Role.ROLE_ADMIN);

        Tag tag = ModelUtils.getTagHabitForServiceTest();
        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        MultipartFile image = ModelUtils.getFile();
        String imageToEncode = Base64.getEncoder().encodeToString(image.getBytes());
        habit.setUserId(1L);
        habit.setImage(imageToEncode);
        habit.setTags(Set.of(tag));

        CustomHabitDtoRequest customHabitDtoRequest = ModelUtils.getСustomHabitDtoRequestWithComplexityAndDuration();
        CustomHabitDtoResponse customHabitDtoResponse = ModelUtils.getAddCustomHabitDtoResponse();

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepo.save(customHabitMapper.convert(customHabitDtoRequest))).thenReturn(habit);
        when(modelMapper.map(habit, CustomHabitDtoResponse.class)).thenReturn(customHabitDtoResponse);
        when(habitRepo.save(habit)).thenReturn(habit);

        assertEquals(customHabitDtoResponse,
            habitService.updateCustomHabit(customHabitDtoRequest, 1L, "user@email.com", null));

        verify(habitRepo).findById(anyLong());
        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).save(any());
        verify(modelMapper).map(habit, CustomHabitDtoResponse.class);
        verify(habitTranslationRepo).findAllByHabit(habit);
    }

    @Test
    void updateCustomHabitThrowsUserHasNoPermissionToAccessExceptionWithDiffrentUserId() {
        CustomHabitDtoRequest customHabitDtoRequest =
            ModelUtils.getAddCustomHabitDtoRequestWithImage();
        User user = ModelUtils.getTestUser();
        String email = user.getEmail();
        user.setRole(Role.ROLE_USER);

        Habit habit = ModelUtils.getCustomHabitForServiceTest();
        habit.setUserId(1L);

        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> habitService.updateCustomHabit(customHabitDtoRequest, 1L, email, null));

        assertNotEquals(user.getId(), habit.getUserId());
        verify(userRepo).findByEmail(anyString());
        verify(habitRepo).findById(anyLong());
    }

    @Test
    void deleteCustomHabitSuccessTest() {
        Long customHabitId = 1L;
        Habit toDelete = ModelUtils.getHabitWithCustom();
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        User user = ModelUtils.getUser();
        toDelete.setUserId(1L);
        when(habitRepo.findByIdAndIsCustomHabitIsTrue(customHabitId))
            .thenReturn(Optional.of(toDelete));
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(habitRepo.findHabitAssignByHabitIdAndHabitOwnerId(customHabitId, 1L))
            .thenReturn(List.of(habitAssign.getId()));

        habitService.deleteCustomHabit(customHabitId, user.getEmail());

        verify(habitRepo).findByIdAndIsCustomHabitIsTrue(customHabitId);
        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo).findHabitAssignByHabitIdAndHabitOwnerId(customHabitId, 1L);
    }

    @Test
    void deleteCustomHabitThrowsNotFoundExceptionTest() {
        Long customHabitId = 1L;
        String userEmail = "email@gmail.com";

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> habitService.deleteCustomHabit(customHabitId, userEmail));

        assertEquals(ErrorMessage.CUSTOM_HABIT_NOT_FOUND + customHabitId, exception.getMessage());
        verify(habitRepo, times(0)).save(any(Habit.class));
    }

    @Test
    void deleteCustomHabitThrowsWrongEmailExceptionTest() {
        Long customHabitId = 1L;
        String userEmail = "email@gmail.com";
        Habit toDelete = ModelUtils.getHabitWithCustom();
        toDelete.setUserId(1L);
        when(habitRepo.findByIdAndIsCustomHabitIsTrue(customHabitId))
            .thenReturn(Optional.of(toDelete));

        WrongEmailException exception = assertThrows(WrongEmailException.class,
            () -> habitService.deleteCustomHabit(customHabitId, userEmail));

        assertEquals(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail, exception.getMessage());
        verify(habitRepo, times(0)).save(any(Habit.class));
        verify(habitRepo).findByIdAndIsCustomHabitIsTrue(customHabitId);
    }

    @Test
    void checkAccessOfOwnerToCustomHabitThrowsUserHasNoPermissionToAccessExceptionTest() {
        Long customHabitId = 1L;
        Habit toDelete = ModelUtils.getHabitWithCustom();
        User user = ModelUtils.getUser();
        String userEmail = user.getEmail();
        toDelete.setUserId(4L);
        when(habitRepo.findByIdAndIsCustomHabitIsTrue(customHabitId))
            .thenReturn(Optional.of(toDelete));
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserHasNoPermissionToAccessException exception = assertThrows(UserHasNoPermissionToAccessException.class,
            () -> habitService.deleteCustomHabit(customHabitId, userEmail));

        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, exception.getMessage());

        verify(habitRepo).findByIdAndIsCustomHabitIsTrue(customHabitId);
        verify(userRepo).findByEmail(user.getEmail());
        verify(habitRepo, times(0)).save(any(Habit.class));
    }
}

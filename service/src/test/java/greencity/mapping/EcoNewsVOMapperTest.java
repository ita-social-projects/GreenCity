package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsVOMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    EcoNewsVOMapper ecoNewsVOMapper;

    @Test
    void convert() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        User author = ecoNews.getAuthor();
        UserVO authorVO = mock(UserVO.class);
        Role role = Role.ROLE_USER;

        EcoNewsVO expected = EcoNewsVO.builder()
            .id(ecoNews.getId())
            .author(UserVO.builder()
                .id(ecoNews.getAuthor().getId())
                .name(ecoNews.getAuthor().getName())
                .email(ecoNews.getAuthor().getEmail())
                .role(role)
                .build())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .source(ecoNews.getSource())
            .text(ecoNews.getText())
            .title(ecoNews.getTitle())
            .tags(ecoNews.getTags().stream()
                .map(tag -> TagVO.builder()
                    .id(tag.getId())
                    .build())
                .collect(Collectors.toList()))
            .usersLikedNews(ecoNews.getUsersLikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .usersDislikedNews(ecoNews.getUsersDislikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .build();

        when(modelMapper.map(author, UserVO.class))
            .thenReturn(authorVO);
        when(authorVO.getRole())
            .thenReturn(role);

        assertEquals(expected, ecoNewsVOMapper.convert(ecoNews));
    }
}

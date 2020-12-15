package greencity.mapping;

import greencity.dto.photo.PhotoVO;
import greencity.entity.Comment;
import greencity.entity.Photo;
import greencity.entity.Place;
import greencity.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PhotoVOMapperTest {
    @InjectMocks
    private PhotoVOMapper mapper;

    @Test
    void convert() {
        Photo sourse = Photo.builder()
            .name("name")
            .id(13L)
            .user(User.builder().id(1L).build())
            .comment(Comment.builder().id(1L).build())
            .place(Place.builder().id(1L).build())
            .build();

        PhotoVO expected = new PhotoVO(13L, "name", 1L, 1L, 1L);

        assertEquals(expected, mapper.convert(sourse));
    }
}
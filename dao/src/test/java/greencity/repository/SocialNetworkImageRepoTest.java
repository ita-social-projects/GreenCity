package greencity.repository;

import greencity.entity.SocialNetworkImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/social_network_image.sql")
class SocialNetworkImageRepoTest {

    @Autowired
    SocialNetworkImageRepo socialNetworkImageRepo;

    @Test
    void findByHostPathTest() {
        SocialNetworkImage socialNetworkImage = socialNetworkImageRepo.findByHostPath("first_host_path").get();
        assertEquals("first_image_path", socialNetworkImage.getImagePath());
    }

    @Test
    void searchBy() {
        Pageable pageable = PageRequest.of(0, 2);
         SocialNetworkImage socialNetworkImage = socialNetworkImageRepo.searchBy(pageable, "second")
             .get()
             .findFirst()
             .get();
         assertEquals("second_host_path", socialNetworkImage.getHostPath());
    }
}

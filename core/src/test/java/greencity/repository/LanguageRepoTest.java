package greencity.repository;

import greencity.GreenCityApplication;
import greencity.IntegrationTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class LanguageRepoTest extends IntegrationTestBase {
    @Autowired
    private LanguageRepo languageRepo;

    @Test
    void findById() {
        List<Object> expected = new ArrayList<>();
        Assertions.assertEquals(expected, languageRepo.findAllLanguageCodes());
    }
}

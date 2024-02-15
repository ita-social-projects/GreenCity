package greencity.controller;

import greencity.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileServiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    FileService fileService;

    @InjectMocks
    FileServiceController fileServiceController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileServiceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void uploadImageTest() throws Exception {
        MockMultipartFile file =
            new MockMultipartFile("image", new byte[1]);
        mockMvc.perform(multipart("/files/image")
            .file(file))
            .andExpect(status().isOk());

        verify(fileService).upload(file);
    }
}

package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.logs.LogFileMetadataDto;
import greencity.service.DotenvService;
import greencity.service.LogFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LogFileControllerTest {

    private static final String GET_LOG_FILES_LIST_LINK = "/logs";
    private static final String VIEW_LOG_FILE_LINK = "/logs/view/{filename}";
    private static final String DOWNLOAD_LOG_FILE_LINK = "/logs/download/{filename}";
    private static final String DELETE_DOTENV_FILE_LINK = "/logs/delete-dotenv";

    private MockMvc mockMvc;

    @InjectMocks
    private LogFileController controller;

    @Mock
    private LogFileService logFileService;

    @Mock
    private DotenvService dotenvService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * ✅ Test: Should return 200 OK when request is valid
     */
    @Test
    void shouldReturnOkWhenRequestIsValid() throws Exception {
        String REQUEST_BODY = """
        {
            "secretKey": "validSecret",
            "filterDto": {
              "fileNameQuery": "string",
              "fileContentQuery": "string",
              "byteSizeRange": {
                "from": 0,
                "to": 0
              },
              "dateRange": {
                "from": "2025-01-01T00:00:00.000Z",
                "to": "2025-01-01T00:00:00.000Z"
              },
              "logLevel": "INFO"
            }
        }
        """;

        PageableDto<LogFileMetadataDto> mockResponse = new PageableDto<>(List.of(), 0, 0, 0);
        Mockito.when(logFileService.getLogFilesList(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post(GET_LOG_FILES_LIST_LINK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

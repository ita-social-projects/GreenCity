package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.exception.helper.EndpointValidationHelper;
import greencity.service.DotenvService;
import greencity.service.LogFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LogFileControllerTest {

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

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EndpointValidationHelper endpointValidationHelper;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper, endpointValidationHelper))
            .build();
    }

    @Test
    void listLogFilesShouldReturnOkWhenRequestIsValidTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable page = PageRequest.of(pageNumber, pageSize);
        LogFileFilterDto filterDto = ModelUtils.getLogFileFilterDto();
        String secretKey = "validSecret";
        String requestBody = """
            {
              "secretKey": "validSecret",
              "filterDto": {
                "fileNameQuery": "filename",
                "fileContentQuery": "fileContent",
                "logLevel": "INFO"
              }
            }
            """;

        mockMvc.perform(post(GET_LOG_FILES_LIST_LINK + "?page=5&size=20")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(logFileService).listLogFiles(page, filterDto, secretKey);
    }

    @Test
    void getLogFileShouldReturnOkWhenRequestIsValidTest() throws Exception {
        String filename = "logfile.log";
        String secretKey = "validSecret";
        String fileContent = "Log file content";

        when(logFileService.viewLogFileContent(logFileService.sanitizeFilename(filename), secretKey))
            .thenReturn(fileContent);

        mockMvc.perform(post(VIEW_LOG_FILE_LINK, filename)
            .contentType(MediaType.TEXT_PLAIN)
            .content(secretKey))
            .andExpect(status().isOk())
            .andExpect(content().string(fileContent));
    }

    @Test
    void downloadLogFileShouldReturnOkWhenFileExistsTest() throws Exception {
        String filename = "logfile.log";
        String secretKey = "validSecret";
        byte[] fileContent = "Log file content".getBytes();
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        when(logFileService.generateDownloadLogFileUrl(logFileService.sanitizeFilename(filename), secretKey))
            .thenReturn(resource);

        mockMvc.perform(post(DOWNLOAD_LOG_FILE_LINK, filename)
            .contentType(MediaType.TEXT_PLAIN)
            .content(secretKey))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + logFileService.sanitizeFilename(filename) + "\""))
            .andExpect(content().bytes(fileContent));
    }

    @Test
    void deleteDotenvFileShouldReturnOkWhenFileIsDeletedTest() throws Exception {
        String secretKey = "validSecret";

        doNothing().when(dotenvService).deleteDotenvFile(secretKey);

        mockMvc.perform(post(DELETE_DOTENV_FILE_LINK)
            .contentType(MediaType.TEXT_PLAIN)
            .content(secretKey))
            .andExpect(status().isOk());
    }
}

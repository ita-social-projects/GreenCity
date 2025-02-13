package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.logs.filter.LogFileFilterDto;
import greencity.exception.handler.CustomExceptionHandler;
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
import java.security.Principal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LogFileControllerTest {

    private static final String GET_LOG_FILES_LIST_LINK = "/logs";
    private static final String VIEW_LOG_FILE_LINK = "/logs/view/{filename}";
    private static final String DOWNLOAD_LOG_FILE_LINK = "/logs/download/{filename}";
    private static final String DELETE_DOTENV_FILE_LINK = "/logs/delete-dotenv";

    private static final Principal principal = ModelUtils.getPrincipal();

    private MockMvc mockMvc;

    @InjectMocks
    private LogFileController controller;

    @Mock
    private LogFileService logFileService;

    @Mock
    private DotenvService dotenvService;

    @Mock
    private ObjectMapper objectMapper;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void getLogFilesListShouldReturnOkWhenRequestIsValid() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable page = PageRequest.of(pageNumber, pageSize);
        LogFileFilterDto filterDto = ModelUtils.getLogFileFilterDto();
        String secretKey  = "validSecret";
         String REQUEST_BODY = """
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
                .content(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
         verify(logFileService).getLogFilesList(page, filterDto, secretKey);
    }

    @Test
    void getLogFileShouldReturnOkWhenRequestIsValid() throws Exception {
        String filename = "logfile.log";
        String secretKey = "validSecret";
        String fileContent = "Log file content";

        when(logFileService.getLogFileContent(filename, secretKey)).thenReturn(fileContent);

        mockMvc.perform(post(VIEW_LOG_FILE_LINK, filename)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(secretKey))
                .andExpect(status().isOk())
                .andExpect(content().string(fileContent));
    }

    @Test
    void shouldReturnOkWhenFileExists() throws Exception {
        String filename = "logfile.log";
        String secretKey = "validSecret";
        byte[] fileContent = "Log file content".getBytes();
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        when(logFileService.getDownloadLogFileUrl(filename, secretKey))
                .thenReturn(resource);

        mockMvc.perform(post(DOWNLOAD_LOG_FILE_LINK, filename)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(secretKey))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void shouldReturnOkWhenFileIsDeleted() throws Exception {
        String secretKey = "validSecret";

        doNothing().when(dotenvService).deleteDotenvFile(secretKey);

        mockMvc.perform(post(DELETE_DOTENV_FILE_LINK)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(secretKey))
                .andExpect(status().isOk());
    }
}

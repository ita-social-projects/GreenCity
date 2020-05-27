package greencity.service.impl;

import greencity.mapping.MultipartFileResource;
import greencity.service.FileService;
import java.net.URL;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    final String serverUrl;

    /**
     * Constructor.
     *
     * @param serverUrl localhost constant URL.
     */
    public FileServiceImpl(@Value("${STORAGE_LOCAL_HOST}") final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Method upload multipartfile to storage microservice.
     *
     * @param multipartFile image file to save.
     * @return photo's URL.
     */
    @SneakyThrows
    @Override
    public URL upload(MultipartFile multipartFile) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartFileResource(multipartFile));
        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        return new URL(response.getBody());
    }
}

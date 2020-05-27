package greencity.mapping;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Converts MultipartFile to MultipartFileResource.
 */
public class MultipartFileResource extends ByteArrayResource {
    private final String filename;

    /**
     * Constructor.
     *
     * @param multipartFile converted to MultipartFileResource.
     *
     */
    public MultipartFileResource(MultipartFile multipartFile) throws IOException {
        super(multipartFile.getBytes());
        this.filename = multipartFile.getOriginalFilename();
    }

    /**
     * Method return file name.
     *
     * @return file name.
     */
    @Override
    public String getFilename() {
        return this.filename;
    }
}

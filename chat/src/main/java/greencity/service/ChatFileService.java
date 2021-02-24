package greencity.service;

import java.io.IOException;

public interface ChatFileService {
    /**
     * {@inheritDoc}
     */
    String save(String encodedString) throws IOException;

    /**
     * {@inheritDoc}
     */
    byte[] getByteArrayFromFile(String fileName) throws IOException;

    /**
     * {@inheritDoc}
     */
    String getUniqueName();
}

package greencity;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageUploader {

    /**
     * Upload file to Google Cloud Storage.
     *
     * <p>This class is based from <a
     * href="https://github.com/googleapis/google-cloud-java/blob/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/StorageExample.java">
     * StorageExample.java</a>.
     * <p>See the <a
     * href="https://github.com/googleapis/google-cloud-java/blob/master/google-cloud-examples/README.md">
     * README</a> for compilation instructions. Run this code with
     *
     * @param projectId       if empty or null it fails back to projectId from json file specified in environment variable GOOGLE_APPLICATION_CREDENTIALS.
     * @param filePath        full file path on local file system.
     * @param bucketName      from <a
     *                        href="https://console.cloud.google.com/storage/browser?project=greencity-c5a3a&authuser=0">
     *                        Storage browser</a>.
     * @param destinationName destination file name in storage bucket.
     * @return public image url.
     * @throws Exception
     */
    public static URL upload(final String projectId,
                             final String filePath,
                             final String bucketName,
                             final String destinationName) throws Exception {
        final StorageOptions.Builder optionsBuilder = StorageOptions.newBuilder();

        if (projectId != null && !projectId.isEmpty()) {
            optionsBuilder.setProjectId(projectId);
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path is null");
        }
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("Bucket name is null");
        }
        if (destinationName == null || destinationName.isEmpty()) {
            throw new IllegalArgumentException("Destination name is null");
        }

        final Storage storage = optionsBuilder.build().getService();
        try {
            final Path path = Paths.get(filePath);
            final String contentType = Files.probeContentType(path);
            final String blob = path.getFileName().toString();
            final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blob).setContentType(contentType).build();

            return run(storage, path, blobInfo);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static URL run(final Storage storage, final Path uploadFrom, final BlobInfo blobInfo) throws IOException {
        if (Files.size(uploadFrom) > 1_000_000) {
            // When content is not available or large (1MB or more) it is recommended
            // to write it in chunks via the blob's channel writer.
            try (WriteChannel writer = storage.writer(blobInfo)) {
                final byte[] buffer = new byte[1024];
                try (InputStream input = Files.newInputStream(uploadFrom)) {
                    int limit;
                    while ((limit = input.read(buffer)) >= 0) {
                        try {
                            writer.write(ByteBuffer.wrap(buffer, 0, limit));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } else {
            final byte[] bytes = Files.readAllBytes(uploadFrom);
            // create the blob in one request.
            final Blob blob = storage.create(blobInfo, bytes);
        }

        final URL url = new URL("https://storage.cloud.google.com/" + blobInfo.getBucket() + "/" + blobInfo.getName() + "?authuser=0");
        return url;
    }

    public static void main(final String... args) throws Exception {
        final URL url = upload("", "/home/julia/Pictures/Screenshot.png", "staging.greencity-c5a3a.appspot.com", "busy.png");
        System.out.println(url);
    }
}

package greencity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service implementation for handling database backup operations.
 */
@Service
@Slf4j
public class DatabaseBackupServiceImpl implements DatabaseBackupService {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUser;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    private static final String BACKUP_FILE_PATH = "C:\\backup\\backup.sql";

    /**
     * Creates a backup of the database by executing the pg_dump command. Extracts
     * database connection details from the datasource URL and uses them to run the
     * backup.
     */
    @Override
    public void backupDatabase() {
        String host = "localhost";
        String port = "5432";
        String dbName = "";

        // Extract hostname, port, and database name from the datasource URL
        Pattern pattern = Pattern.compile("jdbc:postgresql://([^:/]+)(?::(\\d+))?/([^?]+)");
        Matcher matcher = pattern.matcher(datasourceUrl);
        if (matcher.find()) {
            host = matcher.group(1);
            if (matcher.group(2) != null) {
                port = matcher.group(2);
            }
            dbName = matcher.group(3);
        }

        File backupFile = new File(BACKUP_FILE_PATH);
        File backupDir = backupFile.getParentFile();
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
            "pg_dump", "-h", host, "-p", port, "-U", datasourceUser, "-b", "-v", "-f", BACKUP_FILE_PATH, dbName);
        processBuilder.environment().put("PGPASSWORD", datasourcePassword);

        try {
            Process process = processBuilder.start();
            ExecutorService executorService = Executors.newFixedThreadPool(2);

            // Read standard output
            executorService.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Read error output
            executorService.submit(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            int exitCode = process.waitFor();
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            if (exitCode == 0) {
                System.out.println("Backup completed successfully");
            } else {
                System.err.println("Backup failed");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

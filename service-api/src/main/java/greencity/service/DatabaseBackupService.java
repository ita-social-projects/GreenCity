package greencity.service;

/**
 * Interface for the service that handles database backup operations.
 */
public interface DatabaseBackupService {
    /**
     * Creates a backup of the database. This method should initiate the process of
     * backing up the entire database.
     */
    void backupDatabase();
}

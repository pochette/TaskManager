import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class FileBackendTaskManagerTest extends TaskManagerTest <FileBackendTasksManager> {
    @BeforeEach
    void setUp() throws IOException {
        taskManager = new FileBackendTasksManager(Manager.getDefaultHistory(), Path.of("src/Backend/Backend.csv"));
        super.setUp();
    }

    @Test
    void shouldSave() {

    }
}

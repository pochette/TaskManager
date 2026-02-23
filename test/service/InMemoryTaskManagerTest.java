import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager> {

    @BeforeEach
    @Override
    void setUp() throws IOException {
        super.setUp();
        taskManagerReadAndWrite = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}

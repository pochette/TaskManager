import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackendTaskManagerTest extends TaskManagerTest<FileBackendTasksManager> {
    private static final String FILE_PATH = "src/Backend/Backend.csv";

    @Override
    @BeforeEach
    void setUp() throws IOException {

        Path filePath = Path.of(FILE_PATH);
        if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        HistoryManager historyManager = new InMemoryHistoryManager();

        this.taskManager = new FileBackendTasksManager(historyManager, Path.of("src/Backend/Backend.csv"));

        super.setUp();
    }


    @Test
    void shouldCreateAllOfTypesOfTasks() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        assertEquals(task1, taskManager.getTaskById(task1.getIdTask()));
        assertEquals(epic, taskManager.getEpicById(epic.getIdTask()));
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getIdTask()));

        String fileContent;
        try {
            fileContent = Files.readString(Paths.get(FILE_PATH));
            String[] lines = fileContent.split("\n");

            assertEquals(6, lines.length, "File should contain  lines (header + 3 tasks + 1 line of history + 1 empty line)");

            List<Task> loadedTasks = Arrays.stream(lines, 1, 4)
                    .map(taskManager::taskFromLoad)
                    .sorted(Comparator.comparingInt(Task::getIdTask))
                    .toList();
            Iterator<Task> iterator = loadedTasks.iterator();
            assertEquals(task1, iterator.next(), "Loaded task should match the original task");
            assertEquals(epic, iterator.next(), "Loaded epic should match the original epic");
            assertEquals(subtask1, iterator.next(), "Loaded subtask should match the original subtask");
            assertEquals(task1, taskManager.taskFromLoad(lines[1]), "Loaded task should match the original task");
            assertEquals(subtask1, taskManager.taskFromLoad(lines[2]), "Loaded subtask should match the original subtask");
            assertEquals(epic, taskManager.taskFromLoad(lines[3]), "Loaded epic should match the original epic");
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final Path FILE_PATH = Path.of("src/Backend/Backend.csv");
    private static final Path TEST_FILE_PATH = Path.of("src/Backend/TestBackend.csv");
    private final Function<Task, String> csvSerializer = new Manager.TaskCSVTransformer()::taskToCSV;
    private final Function<String, Task> csvDeserializer = new Manager.TaskCSVTransformer()::taskFromLoad;
    FileTaskStorage fileTaskStorage2 = new FileTaskStorage(
            TEST_FILE_PATH,
            new Manager.TaskCSVTransformer()::taskToCSV,
            new Manager.TaskCSVTransformer()::taskFromLoad);
    FileTaskStorage fileTaskStorage1 = new FileTaskStorage(FILE_PATH, csvSerializer, csvDeserializer);

    void clearCSVFile() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
        Files.createFile(FILE_PATH);
        Files.write(FILE_PATH, "id,type,name,status,description,duration,startTime,epic\n".getBytes());

    }

//    void clearHistoryManager() {
//
//    }

    @Override
    @BeforeEach
    void setUp() throws IOException {
        clearCSVFile();

        taskManager = new FileBackedTaskManager(Manager.getDefaultHistory(), fileTaskStorage1);
        super.setUp();
    }

    @Test
    void test1_shouldCreateAndLoadFromFileEpicWithoutSubtask() throws IOException {
        taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getIdTask()));

        List<String> lines = Files.readAllLines(FILE_PATH);
//        String[] lines = fileContent.split("\n");

        assertEquals(4, lines.size(), "File should contain  lines (header + 1 tasks + 1 line of history + 1 empty line)");

        Epic epicFromLoad = (Epic) csvDeserializer.apply(lines.get(1));

        assertEquals(epic, epicFromLoad, "Loaded epic should match the original epic");

    }

    @Test
    void test2_shouldCreateAndLoadFromFileSubtask() throws IOException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getIdTask()));

        List<String> lines = Files.readAllLines(FILE_PATH);

        assertEquals(5, lines.size(), "File should contain  lines (header + 1 epic + 1 subtask + 1 line of history + 1 empty line)");
        List<Task> loadedTasks = new ArrayList<>();
        Subtask loadedSubtask = (Subtask) csvDeserializer.apply(lines.get(2));

        assertEquals(subtask1, loadedSubtask, "Loaded subtask should match the original subtask");

    }

    @Test
    void test3_shouldCreateAndLoadFromFileTask() throws IOException {
        taskManager.createTask(task1);

        assertEquals(task1, taskManager.getTaskById(task1.getIdTask()));

        List<String> lines = Files.readAllLines(FILE_PATH);

        assertEquals(4, lines.size(), "File should contain  lines (header + 1 tasks + 1 line of history + 1 empty line)");
        List<Task> loadedTasks = new ArrayList<>();
        Task loadedTask = csvDeserializer.apply(lines.get(1));

        assertEquals(task1, loadedTask, "Loaded task should match the original task");
    }

    @Test
    void test4_shouldLoadAndDeleteHistoryFromFile() throws IOException {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        // Access tasks to add them to history
        taskManager.getTaskById(task1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());

        List<String> lines = Files.readAllLines(FILE_PATH);

        // The last non-empty line should be the history line
        String historyLine = lines.getLast();
        String[] historyIds = historyLine.split(",");

        HistoryManager historyManager = taskManager.getHistoryManager();

        assertEquals(3, historyManager.getHistory().size(), "History should contain 3 tasks");

        assertEquals(task1.getIdTask(), Integer.parseInt(historyIds[0]), "First history ID should match task1 ID");
        assertEquals(epic.getIdTask(), Integer.parseInt(historyIds[1]), "Second history ID should match epic ID");
        assertEquals(subtask1.getIdTask(), Integer.parseInt(historyIds[2]), "Third history ID should match subtask1 ID");

        historyManager.remove(task1.getIdTask());
        assertEquals(2, historyManager.getHistory().size(), "History should contain 2 tasks after removal");
        assertFalse(historyManager.getHistory().contains(task1.getIdTask()), "History should not contain task1 after removal");

        historyManager.remove(epic.getIdTask());
        assertEquals(1, historyManager.getHistory().size(), "History should contain 1 task after removal");
        assertFalse(historyManager.getHistory().contains(epic.getIdTask()));

        taskManager.removeEpicById(epic.getIdTask());
        assertEquals(0, historyManager.getHistory().size(), "History should be empty after removing epic and its subtask");
        assertFalse(historyManager.getHistory().contains(subtask1.getIdTask()));
    }

    //
    @Test
    void test5_shouldTestLoadingFromFileAndRemovingAllOfTypeTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        //0,1,3,4,2
        StringBuilder idForTest = new StringBuilder();
        taskManager.getTaskById(task1.getIdTask());
        taskManager.getTaskById(task2.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getSubtaskById(subtask2.getIdTask());

        HistoryManager historyManager = taskManager.getHistoryManager();
        try {
            List<String> linesFromFile = Files.readAllLines(FILE_PATH);
            assertEquals(8, linesFromFile.size(), "File should contain 8 lines");
            assertEquals(String.format("%d,%d,%d,%d,%d",
                    task1.getIdTask(),
                    task2.getIdTask(),
                    epic.getIdTask(),
                    subtask1.getIdTask(),
                    subtask2.getIdTask()), linesFromFile.getLast(), "History line should be the last line");

            //Удаление таска1

            taskManager.removeTaskById(task1.getIdTask());
            linesFromFile = Files.readAllLines(FILE_PATH);
            assertEquals(7, linesFromFile.size(), "File should contain 7 lines after removing task1");
            assertEquals(String.format("%d,%d,%d,%d",
                    task2.getIdTask(),
                    epic.getIdTask(),
                    subtask1.getIdTask(),
                    subtask2.getIdTask()), linesFromFile.getLast(), "История не совпадает с запросами");

            //Remove allSubtask

            taskManager.removeAllSubtasks();
            linesFromFile = Files.readAllLines(FILE_PATH);
            assertEquals(5, linesFromFile.size(), "File should contain 6 lines after removing all subtasks");
            assertEquals(String.format("%d,%d",
                    task2.getIdTask(),
                    epic.getIdTask()), linesFromFile.getLast(), "History line should be equals 1,2");

            //Remove allEpic
            taskManager.removeAllEpics();
            linesFromFile = Files.readAllLines(FILE_PATH);
            assertEquals(4, linesFromFile.size(), "File should contain 3 lines after removing all epics");
            assertEquals(String.format("%d",
                    task2.getIdTask()), linesFromFile.getLast(), "History line should be equals 1");

            //Remove allTask
            taskManager.removeAllTasks();
            linesFromFile = Files.readAllLines(FILE_PATH);
            assertEquals(2, linesFromFile.size(), "Should left only one line with values");
            assertEquals("id,type,name,status,description,duration,startTime,epic", linesFromFile.getFirst());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void test6_shouldToLoadFromFile() {
        LegacyFileBackedTaskManager taskManager2 = new LegacyFileBackedTaskManager(Manager.getDefaultHistory(), TEST_FILE_PATH);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager2.loadFromFile(TEST_FILE_PATH);

        assertEquals("561,TASK,Task 561 title,NEW,Task 561 Description,PT72H,2026-01-04T10:00",
                taskManager2.getTaskById(561).toString(taskManager2.getTaskById(561)));

        assertEquals("9782,EPIC,Epic 9782 title,NEW,Epic 9782 description",
                taskManager2.getEpicById(9782).toString(taskManager2.getEpicById(9782)));

        assertEquals("983,SUBTASK,Subtask 983 title,NEW,Subtask 983 description,PT72H,2026-01-25T10:00,2",
                taskManager2.getSubtaskById(983).toString(taskManager2.getSubtaskById(983)));

    }

    @Test
    void test7_shouldThrowManagerSaveException() {
        assertThrows(LegacyFileBackedTaskManager.ManagerSaveException.class, () -> {
            LegacyFileBackedTaskManager failedTaskManager = new LegacyFileBackedTaskManager(Manager.getDefaultHistory(), Paths.get("..."));
            failedTaskManager.createTask(task1);
        });

        assertThrows(LegacyFileBackedTaskManager.ManagerSaveException.class, () -> {
            LegacyFileBackedTaskManager failedTaskManager = new LegacyFileBackedTaskManager(Manager.getDefaultHistory(), Paths.get("..."));
            failedTaskManager.loadFromFile(Paths.get("..."));
        });
        //createFileIfNotExist() test
    }
}

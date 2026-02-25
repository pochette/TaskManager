import org.junit.jupiter.api.AfterEach;
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
  private final Function<Task, String> csvSerializer =
      new Managers.CSV_TRANSFORMER()::serializeTask;
  private final Function<String, Task> csvDeserializer =
      new Managers.CSV_TRANSFORMER()::getTaskFromLoad;
  FileTaskStorage fileTaskStorageReadAndWrite = new FileTaskStorage(FILE_PATH, csvSerializer, csvDeserializer);
  FileTaskStorage fileTaskStorageOnlyRead =
      new FileTaskStorage(TEST_FILE_PATH, null, new Managers.CSV_TRANSFORMER()::getTaskFromLoad);

  @Override
  @BeforeEach
  void setUp() throws IOException {
//    clearCSVFile();

    taskManagerReadAndWrite = new FileBackedTaskManager(Managers.getDefaultHistory(), fileTaskStorageReadAndWrite);
    taskManagerOnlyRead = new FileBackedTaskManager(Managers.getDefaultHistory(), fileTaskStorageOnlyRead);

    super.setUp();
  }

  void clearCSVFile() throws IOException {
      if (Files.exists(FILE_PATH)) {
          Files.delete(FILE_PATH);
   } else Files.createFile(FILE_PATH);
// Files.write(FILE_PATH, "id,type,name,status,description,duration,startTime,epic\n".getBytes());

  }

  @AfterEach
  void tearDown() throws IOException {
      clearCSVFile();

    HistoryManager historyManager = Managers.getDefaultHistory();
    historyManager.getHistory()
        .forEach(task -> historyManager.remove(task.getIdTask()));
  }

  @Test
  void test1_shouldCreateAndLoadFromFileEpicWithoutSubtask() throws IOException {
    taskManagerReadAndWrite.createTask(epic);

    assertEquals(epic, taskManagerReadAndWrite.getTaskById(epic.getIdTask()));

    List<String> lines = Files.readAllLines(FILE_PATH);

    assertEquals(
        4,
        lines.size(),
        "File should contain  lines (header + 1 tasks + 1 line of history + 1 empty line)");

    Epic epicFromLoad = (Epic) csvDeserializer.apply(lines.get(1));

    assertEquals(epic, epicFromLoad, "Loaded epic should match the original epic");
  }

  @Test
  void test2_shouldCreateAndLoadFromFileSubtask() throws IOException {
    taskManagerReadAndWrite.createTask(epic);
    taskManagerReadAndWrite.createTask(subtask1);

    assertEquals(subtask1, taskManagerReadAndWrite.getTaskById(subtask1.getIdTask()));

    List<String> lines = Files.readAllLines(FILE_PATH);

    assertEquals(
        5,
        lines.size(),
        "File should contain  lines (header + 1 epic + 1 subtask + 1 line of history + 1 empty line)");
    List<Task> loadedTasks = new ArrayList<>();
    Subtask loadedSubtask = (Subtask) csvDeserializer.apply(lines.get(2));

    assertEquals(subtask1, loadedSubtask, "Loaded subtask should match the original subtask");
  }

  @Test
  void test3_shouldCreateAndLoadFromFileTask() throws IOException {
    taskManagerReadAndWrite.createTask(task1);

    assertEquals(task1, taskManagerReadAndWrite.getTaskById(task1.getIdTask()));

    List<String> lines = Files.readAllLines(FILE_PATH);

    assertEquals(
        4,
        lines.size(),
        "File should contain  lines (header + 1 tasks + 1 line of history + 1 empty line)");
    List<Task> loadedTasks = new ArrayList<>();
    Task loadedTask = csvDeserializer.apply(lines.get(1));

    assertEquals(task1, loadedTask, "Loaded task should match the original task");
  }

  @Test
  void test4_shouldLoadAndDeleteHistoryFromFile() throws IOException {
    taskManagerReadAndWrite.createTask(task1);
    taskManagerReadAndWrite.createTask(epic);
    taskManagerReadAndWrite.createTask(subtask1);

    // Access tasks to add them to history
    taskManagerReadAndWrite.getTaskById(task1.getIdTask());
    taskManagerReadAndWrite.getTaskById(epic.getIdTask());
    taskManagerReadAndWrite.getTaskById(subtask1.getIdTask());

    List<String> lines = Files.readAllLines(FILE_PATH);

    // The last non-empty line should be the history line
    String historyLine = lines.getLast();
    String[] historyIds = historyLine.split(",");

    HistoryManager historyManager = Managers.getDefaultHistory();

    assertEquals(
        historyIds.length, historyManager.getHistory().size(), "History should contain 3 tasks");

    assertEquals(
        task1.getIdTask(),
        Integer.parseInt(historyIds[0]),
        "First history ID should match task1 ID");
    assertEquals(
        epic.getIdTask(),
        Integer.parseInt(historyIds[1]),
        "Second history ID should match epic ID");
    assertEquals(
        subtask1.getIdTask(),
        Integer.parseInt(historyIds[2]),
        "Third history ID should match subtask1 ID");

    historyManager.remove(task1.getIdTask());
    assertEquals(
        2, historyManager.getHistory().size(), "History should contain 2 tasks after removal");
    assertFalse(
        historyManager.getHistory().contains(task1.getIdTask()),
        "History should not contain task1 after removal");

    historyManager.remove(epic.getIdTask());
    assertEquals(
        1, historyManager.getHistory().size(), "History should contain 1 task after removal");
    assertFalse(historyManager.getHistory().contains(epic.getIdTask()));

    taskManagerReadAndWrite.removeTaskById(epic.getIdTask());
    assertEquals(
        0,
        historyManager.getHistory().size(),
        "History should be empty after removing epic and its subtask");
    assertFalse(historyManager.getHistory().contains(subtask1.getIdTask()));
  }

  //
  @Test
  void test5_shouldTestLoadingFromFileAndRemovingAllOfTypeTask() {
    taskManagerReadAndWrite.createTask(task1);
    taskManagerReadAndWrite.createTask(task2);
    taskManagerReadAndWrite.createTask(epic);
    taskManagerReadAndWrite.createTask(subtask1);
    taskManagerReadAndWrite.createTask(subtask2);

    // 0,1,3,4,2
    StringBuilder idForTest = new StringBuilder();
    taskManagerReadAndWrite.getTaskById(task1.getIdTask());
    taskManagerReadAndWrite.getTaskById(task2.getIdTask());
    taskManagerReadAndWrite.getTaskById(epic.getIdTask());
    taskManagerReadAndWrite.getTaskById(subtask1.getIdTask());
    taskManagerReadAndWrite.getTaskById(subtask2.getIdTask());

    HistoryManager historyManager = Managers.getDefaultHistory();
    try {
      List<String> linesFromFile = Files.readAllLines(FILE_PATH);
      assertEquals(8, linesFromFile.size(), "File should contain 8 lines");
      assertEquals(
          String.format(
              "%d,%d,%d,%d,%d",
              task1.getIdTask(),
              task2.getIdTask(),
              epic.getIdTask(),
              subtask1.getIdTask(),
              subtask2.getIdTask()),
          linesFromFile.getLast(),
          "History line should be the last line");

      // Удаление таска1

      taskManagerReadAndWrite.removeTaskById(task1.getIdTask());
      linesFromFile = Files.readAllLines(FILE_PATH);
      assertEquals(7, linesFromFile.size(), "File should contain 7 lines after removing task1");
      assertEquals(
          String.format(
              "%d,%d,%d,%d",
              task2.getIdTask(), epic.getIdTask(), subtask1.getIdTask(), subtask2.getIdTask()),
          linesFromFile.getLast(),
          "История не совпадает с запросами");

      // Remove allSubtask

      taskManagerReadAndWrite.removeAllSubtasks();
      linesFromFile = Files.readAllLines(FILE_PATH);
      assertEquals(
          5, linesFromFile.size(), "File should contain 6 lines after removing all subtasks");
      assertEquals(
          String.format("%d,%d", task2.getIdTask(), epic.getIdTask()),
          linesFromFile.getLast(),
          "History line should be equals 1,2");

      // Remove allEpic
      taskManagerReadAndWrite.removeAllEpics();
      linesFromFile = Files.readAllLines(FILE_PATH);
      assertEquals(4, linesFromFile.size(), "File should contain 3 lines after removing all epics");
      assertEquals(
          String.format("%d", task2.getIdTask()),
          linesFromFile.getLast(),
          "History line should be equals 1");

      // Remove allTask
      taskManagerReadAndWrite.removeAllOrdinaryTasks();
      linesFromFile = Files.readAllLines(FILE_PATH);
      assertEquals(2, linesFromFile.size(), "Should left only one line with values");
      assertEquals(
          "id,type,name,status,description,duration,startTime,epic", linesFromFile.getFirst());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void test6_shouldToLoadFromFile() {

    taskManagerReadAndWrite.createTask(task1);
    taskManagerReadAndWrite.createTask(task2);
    taskManagerReadAndWrite.createTask(epic);
    taskManagerReadAndWrite.createTask(subtask1);
    taskManagerReadAndWrite.createTask(subtask2);

    List<Task> loadedTasks = fileTaskStorageReadAndWrite.loadTasksFromFile();
  }

  @Test
  void test7_shouldThrowManagerReadException() {
    assertThrows(
        ManagerReadException.class,
        () -> {
          FileBackedTaskManager failedTaskManager =
              new FileBackedTaskManager(
                  Managers.getDefaultHistory(),
                  new FileTaskStorage(Paths.get("dvdsa"), csvSerializer, csvDeserializer));
          failedTaskManager.createTask(task1);

        });

    // createFileIfNotExist() test
  }
}

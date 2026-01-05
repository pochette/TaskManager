import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private InMemoryTaskManager taskManager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;


    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        epic = new Epic("Epic title", Task.Status.NEW, "Epic Description");
        taskManager.createTask(epic);
        subtask1 = new Subtask("Subtask 1", "Description 1", Task.Status.NEW, Duration.ofDays(43),
                LocalDateTime.of(2026,1,4,4,56), epic.getIdTask());
        subtask2 = new Subtask("Subtask 2 title", "Description Subtask2", Task.Status.NEW, Duration.ofHours(56),
                LocalDateTime.of(2026,1,5,6,34), epic.getIdTask());
    }
    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
    }

    @MethodSource("sourceCreateEpicWithGivenParameters")
    @ParameterizedTest(name = "{index} Create epic with status of subtasks = {0} and {1} should return epic status = {2}")
    @DisplayName("GIVEN a new instance of TaskManager " +
            "WHEN a new epic is created with subtasks " +
            "THEN epic status should be calculated correctly based on subtasks statuses.")

    void shouldCreateEpicWithGivenParameters(Task.Status statusSubtask1,
                                             Task.Status statusSubtask2,
                                             Task.Status expectedEpicStatus) {
        //Given
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        //When
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "updated Subtask1", "updated description Subtask1", statusSubtask1, Duration.ofHours(56), LocalDateTime.of(2026,1,6,13,56), epic.getIdTask()), subtask1.getIdTask());


        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "updated Subtask2", "updated description Subtask2", statusSubtask2, Duration.ofHours(5), LocalDateTime.of(2026,2,3,14,57), epic.getIdTask()), subtask2.getIdTask());

        Task.Status actualEpicStatus = taskManager.getTaskById(epic.getIdTask()).getStatus();

        //Then
        assertEquals(expectedEpicStatus, actualEpicStatus, "Epic status is not calculated correctly.");
    }

    private static Stream<Arguments> sourceCreateEpicWithGivenParameters() {
        return Stream.of(
                Arguments.of("NEW", "NEW", "NEW"),
                Arguments.of("DONE", "DONE", "DONE"),
                Arguments.of("IN_PROGRESS", "IN_PROGRESS", "IN_PROGRESS"),
                Arguments.of("NEW", "DONE", "IN_PROGRESS"),
                Arguments.of("NEW", "IN_PROGRESS", "IN_PROGRESS"),
                Arguments.of("DONE", "IN_PROGRESS", "IN_PROGRESS")
        );
    }

//    @MethodSource("sourceSubtasksListForEpic")
//    @ParameterizedTest(name = "{index} Create epic with status of subtasks = {0} and {1} should return epic status = {2}")
//
//
//    void shouldCalculateStatusOfEpicCorreclty(List<Subtask> subtasks, Task.Status expectedEpicStatus) {
//        //Given
//        taskManager.createEpic(epic);
//        //When
//        for (Subtask subtask : subtasks) {
//            taskManager.createSubtask(subtask);
//        }
//        Task.Status actualEpicStatus = taskManager.getTaskById(epic.getIdTask()).getStatus();
//
//        //Then
//        assertEquals(expectedEpicStatus, actualEpicStatus, "Epic status is not calculated correctly.");
//
//        //
//    }
//
//    private Stream <Arguments> sourceSubtasksListForEpic() {
//        return Stream.of(
//                Arguments.of(Collections.emptyList()),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.NEW, Task.Status.NEW), Task.Status.NEW
//                )),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.DONE, Task.Status.DONE), Task.Status.DONE
//                )),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.NEW, Task.Status.DONE), Task.Status.IN_PROGRESS
//                )),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.IN_PROGRESS, Task.Status.IN_PROGRESS), Task.Status.IN_PROGRESS
//                )),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.NEW, Task.Status.IN_PROGRESS), Task.Status.IN_PROGRESS
//                )),
//                Arguments.of(List.of(
//                        createSubtasksListForEpic(epic, Task.Status.DONE, Task.Status.IN_PROGRESS), Task.Status.IN_PROGRESS
//                ))
//        );
//    }
//
//
//    private static List<Subtask> createSubtasksListForEpic(Epic epic, Task.Status statusSubtask1, Task.Status statusSubtask2) {
//        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getIdTask(), statusSubtask1);
//        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getIdTask(), statusSubtask2);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//        return List.of(subtask1, subtask2);
//    }
}





//
//@MethodSource("test2MethodSource")
//@ParameterizedTest(name = "{index} Create order with weight = {0} should return {1}")
//@DisplayName("GIVEN a new instance of OrderManager " +
//        "WHEN a new order is created " +
//        "THEN order creation method should return true " +
//        "if weight is allowed.")
//
//
//void test2_orderWeightValidation(double weight, boolean expectedResult) {
//    //Given
//    OrderManager orderManager = new OrderManager();
//    //Совершаемое действие, Когда (When)
//    List<Item> items = List.of(
//            new Item("apple", 2),
//            new Item("banana", 3)
//    );
//    long price = 12345;
//
//    boolean isCreated = orderManager.createOrder(new Order(items, price, weight));
//    //Проверяемое действие, Тогда (Then)
//    assertEquals(expectedResult, isCreated);
//}
//
//private Stream<Arguments> test2MethodSource() {
//    return Stream.of(
//            Arguments.of(-1.0, false),
//            Arguments.of(-0.001, false),
//            Arguments.of(0.0, false),
//            Arguments.of(0.001, true),
//            Arguments.of(1.0, true),
//            Arguments.of(19.999, true),
//            Arguments.of(20.0, false),
//            Arguments.of(20.001, false),
//            Arguments.of(25.0, false)
//    );
//}

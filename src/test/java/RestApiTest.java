import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RestApiTest {
        private static final String BASE_URL = "http://localhost:4567/";

        private Process proc;

        private final String listOfTodosJSON = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";

        @BeforeEach
        // TODO: set a base of todos, projects and categories that will represent the
        // previous state
        public void setUp() throws InterruptedException {
                try {

                        proc = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
                } catch (IOException e) {
                        e.printStackTrace();
                }
                // Make sure application is running
                System.out.println("Starting tests in...\n");
                for (int i = 3; i > 0; i--) {
                        System.out.println(i);
                        Thread.sleep(1000);
                }
        }

        @AfterEach
        // Todo: Delete all todos, projects and categories before the next test
        void setdown() {
                proc.destroy();
        }

        // todos
        @Test
        void testTodoGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println(response.body());
                assertEquals(200, response.statusCode());
                assertEquals(listOfTodosJSON, response.body());
        }

        @Test
        void testTodoHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testTodoPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());

        }

        // todos/:id
        @Test
        void testSpecificTodoGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoPutRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // todos/:id/categories

        @Test
        void testSpecificTodoCategoriesGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/categories"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoCategoriesHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/categories"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoCategoriesPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // todos/:id/categories/:id
        @Test
        void testSpecificTodoSpecificCategoryDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/categories/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // todos/:id/tasksof

        @Test
        void testSpecificTodoTasksOfGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/tasksof"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoTasksOfHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/tasksof"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificTodoTasksOfPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/tasksof"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // todos/:id/tasksof/:id

        @Test
        void testSpecificTodoSpecificTaskOfDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1/tasksof/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // ======================================================================================
        // ======================================================================================
        // ======================================================================================

        // projects

        @Test
        void testProjectsGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testProjectsHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testProjectsPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"completed\": true, \"active\": false, \"description\": \"Testing POST request for project\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // projects/:id

        @Test
        void testSpecificProjectGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectPutRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/25"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

        }

        // projects/:id/tasks
        @Test
        void testSpecificProjectTasksGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/tasks"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectTasksHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/tasks"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectTasksPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/tasks"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // projects/:id/tasks/:id

        @Test
        void testSpecificProjectSpecificTaskDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/tasks/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // projects/:id/categories

        @Test
        void testSpecificProjectCategoriesGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/categories"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectCategoriesHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/categories"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificProjectCategoriesPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // projects/:id/categories/:id

        @Test
        void testSpecificProjectSpecificCategoryDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/categories/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // ======================================================================================
        // ======================================================================================
        // ======================================================================================

        // categories

        @Test
        void testCategoriesGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testCategoriesHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testCategoriesPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // categories/:id

        @Test
        void testSpecificCategoryGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // categories/:id/todos

        @Test
        void testSpecificCategoryTodosGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/todos"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryTodosHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/todos"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryTodosPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/todos"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // categories/:id/todos/:id

        @Test
        void testSpecificCategorySpecificTodoDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/todos/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        // categories/:id/projects

        @Test
        void testSpecificCategoryProjectsGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/projects"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryProjectsHeadRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/projects"))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        @Test
        void testSpecificCategoryProjectsPostRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/projects"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
        }

        // categories/:id/projects/:id

        @Test
        void testSpecificCategorySpecificProjectDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories/1/projects/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
        }

        void createAllBeforeEntites() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest requestTodo1 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Todo 1\", \"doneStatus\": false, \"description\": \"Created Before Each Test\"}"))
                                .build();
                HttpResponse<String> response = client.send(requestTodo1, BodyHandlers.ofString());

                HttpRequest requestTodo2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Todo 2\", \"doneStatus\": false, \"description\": \"Created Before Each Test\"}"))
                                .build();
                HttpResponse<String> response2 = client.send(requestTodo2, BodyHandlers.ofString());

                HttpRequest requestProject1 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Project 1\", \"completed\": true, \"active\": false, \"description\": \"Base Project 1\"}"))
                                .build();

                HttpResponse<String> response3 = client.send(requestProject1, BodyHandlers.ofString());

                HttpRequest requestProject2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Project 2\", \"completed\": true, \"active\": false, \"description\": \"Base Project 2\"}"))
                                .build();

                HttpResponse<String> response4 = client.send(requestProject2, BodyHandlers.ofString());

                HttpRequest requestCategories1 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Category 1\", \"Description\": \"Base Category 1\"}"))
                                .build();

                HttpResponse<String> response5 = client.send(requestCategories1, BodyHandlers.ofString());

                HttpRequest requestCategories2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "categories"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Created Category 2\", \"Description\": \"Base Category 2\"}"))
                                .build();

                HttpResponse<String> response6 = client.send(requestCategories2, BodyHandlers.ofString());

        }
}

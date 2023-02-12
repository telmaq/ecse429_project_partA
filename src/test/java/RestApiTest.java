import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.management.ObjectName;

import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class RestApiTest {
        private static final String BASE_URL = "http://localhost:4567/";

        private Process runTodoManagerRestApi;
        ObjectMapper mapper = new ObjectMapper();

        // Initial State JSON Strings
        // Todos JSON
        private final String listOfTodosJSON = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoPOST = "{\"id\":\"3\",\"title\":\"Test1\",\"doneStatus\":\"false\",\"description\":\"Test description\"}";
        private final String TodoPOSTAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"3\",\"title\":\"Test1\",\"doneStatus\":\"false\",\"description\":\"Test description\"}]}";
        private final String TodoGETId = "{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoPOSTId = "{\"id\":\"1\",\"title\":\"Title change\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}";
        private final String TodoPOSTIdAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoPUTIdBug = "{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}";
        private final String TodoPUTId = "{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\"}";
        private final String TodoPUTIdAfterStateBug = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoPUTIdAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\"},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoDELETEIdAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String TodoGETIdCategory = "{\"categories\":[{\"id\":\"1\",\"title\":\"Office\",\"description\":\"\"}]}";
        private final String TodoPOSTIdCategory = "{\"id\":\"3\",\"title\":\"Test\",\"description\":\"Test Description\"}";
        private final String TodoPOSTIdCategoryAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"},{\"id\":\"3\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";

        private final String listOfProjectsJSON = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]}]}";
        private final String listOfCategoriesJSON = "{\"categories\":[{\"id\":\"1\",\"title\":\"Office\",\"description\":\"\"},{\"id\":\"2\",\"title\":\"Home\",\"description\":\"\"}]}";

        @BeforeEach
        // TODO: set a base of todos, projects and categories that will represent the
        // previous state
        public void setUp() throws InterruptedException {
                try {
                        runTodoManagerRestApi = Runtime.getRuntime().exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
                } catch (IOException e) {
                        e.printStackTrace();
                }
                // Make sure application is running
                System.out.println("Starting tests in...\n");
                for (int i = 3; i > 0; i--) {
                        System.out.println(i);
                        Thread.sleep(500);
                }
        }

        @AfterEach
        // Todo: Delete all todos, projects and categories before the next test
        void setdown() {
                runTodoManagerRestApi.destroy();
        }

        // todos
        @Test
        void testTodoGetRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                try {
                        JSONAssert.assertEquals(listOfTodosJSON, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                                                "{\"title\": \"Test1\", \"doneStatus\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
                assertEquals(TodoPOST, response.body());

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(TodoPOSTAfterState, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                try {
                        JSONAssert.assertEquals(TodoGETId, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                                                "{\"title\": \"Title change\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(TodoPOSTId, response.body());

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(TodoPOSTIdAfterState, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
        }

        // Unexpected side effect detected. Response body does not display categories
        // and project of the todo. Different from the
        // Response body of the POST request above.
        @Test
        void testSpecificTodoPutRequestBug() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Title change again\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(TodoPUTIdBug, response.body());
        }

        // Unexpected side effect detected. Because the payload of the PUT request
        // ommitted categories and projects, it changed the todo with id 1 object
        // to not have any associations with categories and projects.
        @Test
        void testSpecificTodoPutRequestBug2() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Title change again\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(TodoPUTId, response.body());

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(TodoPUTIdAfterStateBug, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
        }

        @Test
        void testSpecificTodoPutRequestCorrect() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Title change again\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(TodoPUTId, response.body());

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());

                try {
                        JSONAssert.assertEquals(TodoPUTIdAfterState, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(TodoDELETEIdAfterState, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                try {
                        JSONAssert.assertEquals(TodoGETIdCategory, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"Description\": \"Test Description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
                assertEquals(TodoPOSTIdCategory, response.body());

                HttpRequest request2 = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> response2 = client.send(request2, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(TodoPOSTIdCategoryAfterState, response2.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        System.out.println("JSON objects are not equal.");
                }
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
                assertEquals(listOfProjectsJSON, response.body());
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
                assertEquals(listOfCategoriesJSON, response.body());
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

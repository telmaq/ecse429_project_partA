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
        private final String todoPOST = "{\"id\":\"3\",\"title\":\"Test1\",\"doneStatus\":\"false\",\"description\":\"Test description\"}";
        private final String todoPOSTAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"3\",\"title\":\"Test1\",\"doneStatus\":\"false\",\"description\":\"Test description\"}]}";
        private final String todoGETId = "{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoPOSTId = "{\"id\":\"1\",\"title\":\"Title change\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}";
        private final String todoPOSTIdAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoPUTIdBug = "{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]}";
        private final String todoPUTId = "{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\"}";
        private final String todoPUTIdAfterStateBug = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoPUTIdAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"Title change again\",\"doneStatus\":\"false\",\"description\":\"\"},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoDELETEIdAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoGETIdCategory = "{\"categories\":[{\"id\":\"1\",\"title\":\"Office\",\"description\":\"\"}]}";
        private final String todoPOSTIdCategory = "{\"id\":\"3\",\"title\":\"Test\",\"description\":\"Test Description\"}";
        private final String todoPOSTIdCategoryAfterState = "{\"todos\":[{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"},{\"id\":\"3\"}],\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoDELETEIdCategoryAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String todoGETIdTasksOf = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}]}";
        private final String todoPOSTIdTasksOf = "{\"id\":\"2\",\"title\":\"Test\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"Test description\",\"tasks\":[{\"id\":\"1\"}]}";
        private final String todoPOSTIdTasksOfAfterState = "{\"projects\":[{\"id\":\"2\",\"title\":\"Test\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"Test description\",\"tasks\":[{\"id\":\"1\"}]},{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}]}";
        private final String todoDELETEIdTasksOfAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"}]}]}";

        private final String listOfProjectsJSON = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]}]}";
        private final String projectPOST = "{\"id\":\"2\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing POST request for project\"}";
        private final String projectPOSTAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing POST request for project\"}]}";
        private final String projectGETId = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}]}]}";
        private final String projectPOSTId = "{\"id\":\"1\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing POST request for project\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}";
        private final String projectPOSTIdAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing POST request for project\",\"tasks\":[{\"id\":\"1\"},{\"id\":\"2\"}]}]}";
        private final String projectPUTId = "{\"id\":\"1\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing PUT request for project\"}";
        private final String projectPUTIdAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Test\",\"completed\":\"true\",\"active\":\"false\",\"description\":\"Testing PUT request for project\"}]}";
        private final String projectDELETEIdAfterState = "{\"errorMessages\":[\"Could not find an instance with projects/1\"]}";
        private final String projectGETIdTasksOf = "{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String projectPOSTIdTasksOf = "{\"id\":\"3\",\"title\":\"Test\",\"doneStatus\":\"false\",\"description\":\"Test description\",\"tasksof\":[{\"id\":\"1\"}]}";
        private final String projectPOSTIdTasksOfAfterState = "{\"todos\":[{\"id\":\"1\",\"title\":\"scan paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"categories\":[{\"id\":\"1\"}],\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"3\",\"title\":\"Test\",\"doneStatus\":\"false\",\"description\":\"Test description\",\"tasksof\":[{\"id\":\"1\"}]},{\"id\":\"2\",\"title\":\"file paperwork\",\"doneStatus\":\"false\",\"description\":\"\",\"tasksof\":[{\"id\":\"1\"}]}]}";
        private final String projectDELETEIdTasksOfAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"}]}]}";
        private final String projectGETIdCategories = "{\"categories\":[]}";
        private final String projectPOSTIdCategories = "{\"id\":\"3\",\"title\":\"Test\",\"description\":\"Test description\"}";
        private final String projectPOSTIdCategoriesAfterState = "{\"projects\":[{\"id\":\"1\",\"title\":\"Office Work\",\"completed\":\"false\",\"active\":\"false\",\"description\":\"\",\"tasks\":[{\"id\":\"2\"},{\"id\":\"1\"}],\"categories\":[{\"id\":\"3\"}]}]}";
        private final String projectDELETEIdCategoriesAfterState = "";

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
                assertEquals(todoPOST, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoPOSTAfterState, getResponse.body(), false);
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
                        JSONAssert.assertEquals(todoGETId, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
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
                assertEquals(todoPOSTId, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoPOSTIdAfterState, getResponse.body(), false);
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
                assertEquals(todoPUTIdBug, response.body());
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
                assertEquals(todoPUTId, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoPUTIdAfterStateBug, getResponse.body(), false);
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
                assertEquals(todoPUTId, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());

                try {
                        JSONAssert.assertEquals(todoPUTIdAfterState, getResponse.body(), false);
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

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoDELETEIdAfterState, getResponse.body(), false);
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
                        JSONAssert.assertEquals(todoGETIdCategory, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
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
                assertEquals(todoPOSTIdCategory, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoPOSTIdCategoryAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
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

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "todos"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoDELETEIdCategoryAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                try {
                        JSONAssert.assertEquals(todoGETIdTasksOf, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                                                "{\"title\": \"Test\", \"completed\": false, \"active\": false, \"description\": \"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
                assertEquals(todoPOSTIdTasksOf, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoPOSTIdTasksOfAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(todoDELETEIdTasksOfAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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

                try {
                        JSONAssert.assertEquals(listOfProjectsJSON, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                assertEquals(projectPOST, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(projectPOSTAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                try {
                        JSONAssert.assertEquals(projectGETId, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"completed\": true, \"active\": false, \"description\": \"Testing POST request for project\"}"))

                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                try {
                        JSONAssert.assertEquals(projectPOSTId, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(projectPOSTIdAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }

        }

        @Test
        void testSpecificProjectPutRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"completed\": true, \"active\": false, \"description\": \"Testing PUT request for project\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                assertEquals(projectPUTId, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());

                try {
                        JSONAssert.assertEquals(projectPUTIdAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }

        }

        @Test
        void testSpecificProjectDeleteRequest() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .DELETE()
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                assertEquals(projectDELETEIdAfterState, getResponse.body());

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
                try {
                        JSONAssert.assertEquals(projectGETIdTasksOf, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                assertEquals(projectPOSTIdTasksOf, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/tasks"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(projectPOSTIdTasksOfAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }

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

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(projectDELETEIdTasksOfAfterState, getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }

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

                try {
                        JSONAssert.assertEquals(projectGETIdCategories, response.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                                .POST(HttpRequest.BodyPublishers.ofString(
                                                "{\"title\": \"Test\", \"Description\":\"Test description\"}"))
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                assertEquals(201, response.statusCode());
                assertEquals(projectPOSTIdCategories, response.body());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                try {
                        JSONAssert.assertEquals(projectPOSTIdCategoriesAfterState,
                                        getResponse.body(), false);
                        System.out.println("JSON objects are equal.");
                } catch (JSONException e) {
                        throw new AssertionError("JSON objects are not equal.");
                }
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
                assertEquals(404, response.statusCode());

                HttpRequest getRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "projects/1/categories/1"))
                                .build();

                HttpResponse<String> getResponse = client.send(getRequest, BodyHandlers.ofString());
                assertEquals(projectDELETEIdCategoriesAfterState, getResponse.body());
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
}

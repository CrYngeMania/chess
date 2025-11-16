package facade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.GameData;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public HashMap<String, Object> register(String username, String password, String email) throws ResponseException {
        HashMap<String, Object> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        request.put("email", email);

        var httpRequest = buildRequest("POST", "/user", request);
        var response = sendRequest(httpRequest);
        HashMap<String, Object> reg =  handleResponse(response);
        assert reg != null;
        authToken = (String) reg.get("authToken");
        return reg;
    }

    public HashMap<String, Object> login(String username, String password) throws ResponseException {
        HashMap<String, Object> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);

        var httpRequest = buildRequest("POST", "/session", request);
        var response = sendRequest(httpRequest);
        HashMap<String, Object> login =  handleResponse(response);
        assert login != null;
        authToken = (String) login.get("authToken");
        return login;
    }

    public HashMap<String, Object> logout() throws ResponseException {
        var httpRequest = buildRequest("DELETE", "/session",null);
        var response = sendRequest(httpRequest);
        return handleResponse(response);
    }

    public HashMap<String, Object> joinGame(String playerColor, Integer gameID) throws ResponseException {
        HashMap<String, Object> request = new HashMap<>();
        request.put("playerColor", playerColor);
        request.put("gameID", gameID);
        var httpRequest = buildRequest("PUT", "/game", request);
        var response = sendRequest(httpRequest);
        HashMap<String, Object> result = handleResponse(response);
        assert result != null;
        if (result.containsKey("gameID") && result.get("gameID") instanceof Double d){
            result.put("gameID", d.intValue());
        }
        return result;
    }

    public HashMap<String, Object> createGame(String gameName) throws ResponseException {
        HashMap<String, Object> request = new HashMap<>();
        request.put("gameName", gameName);
        var httpRequest = buildRequest("POST", "/game", request);
        var response = sendRequest(httpRequest);
        HashMap<String, Object> result = handleResponse(response);
        assert result != null;
        if (result.containsKey("gameID") && result.get("gameID") instanceof Double d){
            result.put("gameID", d.intValue());
        }
        return result;
    }

    public HashMap<String, Object> listGame() throws ResponseException {
        var httpRequest = buildRequest("GET", "/game", null);
        var response = sendRequest(httpRequest);

        HashMap<String, Object> result = handleResponse(response);

        Object rawGames = result.get("games");
        if (rawGames instanceof List<?> rawList) {
            List<GameData> games = new ArrayList<>();
            for (Object obj : rawList) {
                // Convert each map to GameData
                GameData game = new Gson().fromJson(new Gson().toJson(obj), GameData.class);
                games.add(game);
            }
            result.put("games", games);  // replace the raw list with typed list
        }

        return result;
    }

    public void delete() throws ResponseException {
        var httpRequest = buildRequest("DELETE", "/db", null);
        var response = sendRequest(httpRequest);
        handleResponse(response);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null && !body.isEmpty()) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Bad response");
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status),
                    "No response body received for status: " + status);
        }

        if (response.body() != null) {
            Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
            return new Gson().fromJson(response.body(), mapType);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

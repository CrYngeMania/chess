package facade;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;


    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegistrationResult register(RegistrationRequest request) throws DataAccessException {
        var httpRequest = buildRequest("POST", "/user", request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, RegistrationResult.class);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        var httpRequest = buildRequest("POST", "/session", request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout() throws DataAccessException {
        var httpRequest = buildRequest("DELETE", "/session",null);
        var response = sendRequest(httpRequest);
        return handleResponse(response, LogoutResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        var httpRequest = buildRequest("PUT", "/game", request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, JoinGameResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        var httpRequest = buildRequest("POST", "/game", request);
        var response = sendRequest(httpRequest);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGameResult listGame() throws DataAccessException {
        var httpRequest = buildRequest("GET", "/game", null);
        var response = sendRequest(httpRequest);
        return handleResponse(response, ListGameResult.class);
    }

    public DeleteResult delete() throws DataAccessException {
        var httpRequest = buildRequest("DELETE", "/db", null);
        var response = sendRequest(httpRequest);
        return handleResponse(response, DeleteResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw DataAccessException.fromJson(body);
            }

            throw new DataAccessException(DataAccessException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

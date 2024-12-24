package heusc.API;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Callable;

import heusc.Utility.Http;

// https://icanhazdadjoke.com/api
public class DadJokeAPI {
    private static final String endpoint = "https://icanhazdadjoke.com";
    private static final Map<String, String> headers = Map.of(
        "Accept", "text/plain",
        "User-Agent", "heusc discord@squishhy"
    );
    private static final Callable<HttpResponse<String>> jokeRequest = Http.createRequest(Http.GET(endpoint, headers));

    public static String getDadJoke() {
        try {
            HttpResponse<String> response = jokeRequest.call();
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

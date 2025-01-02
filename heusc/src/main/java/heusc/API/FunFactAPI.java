package heusc.API;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.tinylog.Logger;

import com.squareup.moshi.JsonAdapter;

import heusc.App;
import heusc.Utility.Http;

// https://uselessfacts.jsph.pl/
public class FunFactAPI {
    private static final String endpoint = "https://uselessfacts.jsph.pl/api/v2/facts/random";
    
    @SuppressWarnings("unchecked")
    private static final Callable<HttpResponse<String>> factRequest = Http.createRequest(Http.GET(endpoint, Collections.EMPTY_MAP));
    private static final JsonAdapter<FunFact> factAdapter = App.MOSHI.adapter(FunFact.class);


    public static String getFunFact() {
        try {
            HttpResponse<String> response = factRequest.call();
            FunFact funFact = factAdapter.fromJson(response.body());
            return funFact.toString();
        } catch (Exception e) {
            Logger.error(e);
            return e.getMessage();
        }
    }

    private static class FunFact {
        String text;

        @Override
        public String toString() {
            return text;
        }
    }
}

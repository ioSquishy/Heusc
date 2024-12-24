package heusc.API;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Types;

import heusc.App;
import heusc.Utility.Http;

// https://zenquotes.io/
public class ZenQuoteAPI {
    private static final String endpoint = "https://zenquotes.io/api/quotes";
    @SuppressWarnings("unchecked")
    private static final Callable<HttpResponse<String>> quoteRequest = Http.createRequest(Http.GET(endpoint, Collections.EMPTY_MAP));
    private static final JsonAdapter<List<Zenquote>> quoteAdapter = App.MOSHI.adapter(Types.newParameterizedType(List.class, Zenquote.class));

    @SuppressWarnings("unchecked")
    private static List<Zenquote> quotes = Collections.EMPTY_LIST;

    public static String pullQuote() {
        if (quotes.isEmpty()) {
            try {
                refreshQuotes();
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return quotes.remove(quotes.size()-1).toString();
    }

    private static void refreshQuotes() throws Exception {
        HttpResponse<String> response = quoteRequest.call();
        quotes = quoteAdapter.fromJson(response.body());
    }

    private static class Zenquote {
        String q; // quote
        String a; // author

        @Override
        public String toString() {
            return '"' + q + "\" - " + a;
        }
    }
}

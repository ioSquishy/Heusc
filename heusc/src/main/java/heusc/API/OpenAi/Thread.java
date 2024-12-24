package heusc.API.OpenAi;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import com.squareup.moshi.JsonAdapter;

import heusc.App;
import heusc.Utility.Http;

public class Thread {
    private static final String postURL = "https://api.openai.com/v1/threads";
    private static Callable<HttpResponse<String>> requestThreadCreation = Http.createRequest(Http.POST(postURL, OpenAi.headers, ""));
    private static final JsonAdapter<Thread> threadAdapter = App.MOSHI.adapter(Thread.class);
    public final String id;
    private boolean deleted = false;

    private Thread(String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public static Thread createThread() {
        try {
            HttpResponse<String> response = requestThreadCreation.call();
            if (response.statusCode() == 200) {
                return threadAdapter.fromJson(response.body());
            } else {
                System.err.println("requestThreadCreation failed: " + response.body());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<oaMessage> retrieveLatestMessages(int numMessages) {
        if (numMessages < 1 || numMessages > 100) {
            System.err.println("numMessages must be between 1-100 (inclusive)");
            return null;
        }

        final String getUrl = "https://api.openai.com/v1/threads/" + this.id + "/messages?limit=" + numMessages;
        HttpRequest request = Http.GET(getUrl, OpenAi.headers);

        try {
            HttpResponse<String> response = Http.createRequest(request).call();
            if (response.statusCode() == 200) {
                System.out.println(response.body());
                MessageRetrievalResponse pojoResponse = MessageRetrievalResponse.mrrAdapter.fromJson(response.body());
                return pojoResponse.data;
            } else {
                System.err.println("retrieveLatestMessages() failed: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static class MessageRetrievalResponse {
        public static final JsonAdapter<MessageRetrievalResponse> mrrAdapter = App.MOSHI.adapter(MessageRetrievalResponse.class);

        public final List<oaMessage> data;
        public final String first_id;
        public final String last_id;
        public final boolean has_more;

        public MessageRetrievalResponse(List<oaMessage> data, String first_id, String last_id, boolean has_more) {
            this.data = data;
            this.first_id = first_id;
            this.last_id = last_id;
            this.has_more = has_more;
        }
    }

    public boolean deleteThread() {
        HttpRequest request = Http.DELETE(postURL + "/" + this.id, OpenAi.headers);
        try {
            HttpResponse<String> response = Http.createRequest(request).call();
            if (response.statusCode() == 200) {
                Thread responseThread = threadAdapter.fromJson(response.body());
                this.deleted = responseThread.deleted;
                return this.deleted;
            } else {
                System.err.println("deleteThread() failed: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

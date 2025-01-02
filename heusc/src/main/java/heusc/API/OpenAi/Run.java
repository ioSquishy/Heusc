package heusc.API.OpenAi;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import com.squareup.moshi.JsonAdapter;

import heusc.App;
import heusc.Utility.Http;
import heusc.Utility.Http.CallableRequest;

public class Run {
    private static final JsonAdapter<Run> runAdapter = App.MOSHI.adapter(Run.class);
    private enum Status {
        queued, in_progress, requires_action, cancelling, cancelled, failed, completed, incomplete, expired
    };

    public final String id;
    public final String thread_id;
    public final String assistant_id;
    private Status status;
    private IncompleteDetails incomplete_details;

    private Run(String id, String threadID, String assistantID, Status status, IncompleteDetails incompleteDetails) {
        this.id = id;
        this.assistant_id = assistantID;
        this.thread_id = threadID;
        this.status = status;
        this.incomplete_details = incompleteDetails;
    }

    public boolean isCompleted() {
        return status == Status.completed;
    }

    public boolean requiresAction() {
        return status == Status.requires_action;
    }

    public boolean isPending() {
        return status == Status.queued
            || status == Status.in_progress
            || status == Status.requires_action;
    }

    public boolean isIncomplete() {
        return status == Status.cancelling 
            || status == Status.cancelled
            || status == Status.failed
            || status == Status.incomplete
            || status == Status.expired;
    }

    public String getIncompleteReason() {
        return incomplete_details.reason;
    }

    public Status getStatus() {
        return status;
    }

    public static Run createRun(String threadID, String assistantID, List<oaMessage> additionalMessages) {
        return new RunRequest(threadID, assistantID, additionalMessages).createRun();
    }

    public CompletableFuture<Run> pollCompletion(int timeout, TimeUnit timeoutUnit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int pollDurationMillis = 500;
                int maxPolls = (int) timeoutUnit.toMillis(pollDurationMillis) / pollDurationMillis;
                int currentPoll = 0;

                while (this.isPending() && !this.requiresAction() && currentPoll < maxPolls) {
                    currentPoll++;

                    try {
                        java.lang.Thread.sleep(pollDurationMillis);
                    } catch (InterruptedException e) {
                        java.lang.Thread.currentThread().interrupt();
                        Logger.debug("Polling of run {} interrupted.\nReason: {}", this.id, e);
                        throw new RuntimeException("The polling was interrupted", e);
                    }

                    this.status = retrieveRun(this.thread_id, this.id).status;
                }

                if (currentPoll >= maxPolls) {
                    Logger.debug("Poll timed out, max polls reached.");
                    throw new RuntimeException("Poll timed out, max polls reached.");
                }

                if (this.isIncomplete()) {
                    Logger.debug("Run incomplete.Reason: {}", this.getIncompleteReason());
                    throw new IncompleteException(this.getIncompleteReason());
                }

                return this;
            } catch (Exception e) {
                Logger.error(e);
                throw new RuntimeException("Failed to poll the run", e);
            }
        });
    }

    public static CompletableFuture<Run> createRunAndPoll(String threadID, String assistantID, List<oaMessage> additionalMessages) {
        return createRun(threadID, assistantID, additionalMessages).pollCompletion(20, TimeUnit.SECONDS);
    }

    public static Run retrieveRun(String threadID, String runID) {
        final String getUrl = "https://api.openai.com/v1/threads/" + threadID + "/runs/" + runID;
        HttpRequest request = Http.GET(getUrl, OpenAi.headers);
        try {
            HttpResponse<String> response = Http.createRequest(request).call();
            if (response.statusCode() == 200) {
                return runAdapter.fromJson(response.body());
            } else {
                Logger.error("Status code: {}\nResponse Message: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    private static class IncompleteDetails {
        String reason;
    }

    private static class RunRequest {
        private static final JsonAdapter<RunRequest> runRequestAdapter = App.MOSHI.adapter(RunRequest.class).nullSafe();
        // path parameter
        public final transient String thread_id;
        // body parameters
        @SuppressWarnings("unused")
        public final String assistant_id;
        @SuppressWarnings("unused")
        public final List<oaMessage> additional_messages;

        public RunRequest(String threadID, String assistantID, List<oaMessage> messages) {
            this.thread_id = threadID;
            this.assistant_id = assistantID;
            this.additional_messages = messages;
        }

        private String toJson() {
            return runRequestAdapter.toJson(this);
        }

        public Run createRun() {
            String postUrl = "https://api.openai.com/v1/threads/" + this.thread_id + "/runs";
            CallableRequest requestRunCreation = Http.createRequest(Http.POST(postUrl, OpenAi.headers, this.toJson()));
            try {
                HttpResponse<String> response = requestRunCreation.call();
                if (response.statusCode() == 200) {
                    return runAdapter.fromJson(response.body());
                } else {
                    Logger.error("Status code: {}\nRequest Json: {}\nResponse Message: {}", response.statusCode(), this.toJson(), response.body());
                }
            } catch (Exception e) {
                Logger.error(e);
            }
            return null;
        }
    }
}

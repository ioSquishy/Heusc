package heusc.API.OpenAi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.javacord.api.entity.message.Message;
import org.tinylog.Logger;

import heusc.API.OpenAi.oaMessage.Content;
import heusc.API.OpenAi.oaMessage.ImageUrlContent;
import heusc.API.OpenAi.oaMessage.TextContent;

public class Conversation {
    private Thread thread;
    private List<oaMessage> additionalMessages = new ArrayList<oaMessage>();
    private boolean pollingResponse = false;

    public Conversation() {
        thread = Thread.createThread();
    }

    public boolean deleteThread() {
        return thread.deleteThread();
    }

    public boolean clearThread() {
        boolean success = deleteThread();
        if (success) {
            thread = Thread.createThread();
        }
        return success;
    }

    public void appendDiscordMessageToThread(Message message) {
        
        List<ImageUrlContent> imageUrlContent = message.getAttachments().stream()
            .filter(attachment -> attachment.isImage())
            .map(attachment -> attachment.getUrl().toString())
            .map(url -> ImageUrlContent.of(url))
            .collect(Collectors.toList());
        
        List<Content> content = new ArrayList<Content>(imageUrlContent.size() + 1);

        if (!message.getContent().isBlank()) {
            content.add(TextContent.of(message.getContent()));
        }
        content.addAll(imageUrlContent);

        additionalMessages.add(oaMessage.userMessageOf(content));
    }

    public CompletableFuture<oaMessage> pollResponse() {
        if (pollingResponse)  {
            Logger.debug("Attempted to run a pending thread.");
            throw new RuntimeException("Attempted to run a pending thread.");
        }

        pollingResponse = true;
        return CompletableFuture.supplyAsync(() -> {
            try (BlockOverlappingPolls block = new BlockOverlappingPolls()) {
                Run run = Run.createRun(thread.id, OpenAi.assistantID, additionalMessages);
                run.pollCompletion(20, TimeUnit.SECONDS).join();
                oaMessage latestMessage = thread.retrieveLatestMessages(1).get(0);
                return latestMessage;
            } catch (Exception e) {
                Logger.error(e);
                throw new RuntimeException("Failed to create or poll response", e);
            }
        });
    }

    private class BlockOverlappingPolls implements AutoCloseable {
        @Override
        public void close() {
            pollingResponse = false;
        }
    }
}

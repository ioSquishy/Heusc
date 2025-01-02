package heusc.API.OpenAi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.NonThrowingAutoCloseable;
import org.tinylog.Logger;

import heusc.Utility.MessageSplitter;
import heusc.Utility.TextFile;
import heusc.Utility.MessageSplitter.UnsplittableException;

public class ConversationManager {
    private static HashMap<Long /*channel id*/, Conversation> conversations = new HashMap<Long, Conversation>();
    private static final int discordMessageCharLimit = 2000;

    public static MessageCreateListener getMessageCreateListener() {
        return new MessageCreateListener() {
            @Override
            public void onMessageCreate(MessageCreateEvent event) {
                handleMessageCreateEvent(event);
            }
        };
    }

    public static void handleMessageCreateEvent(MessageCreateEvent messageEvent) {
        if (!userIsWhitelisted(messageEvent.getMessageAuthor().getId())) {
            return;
        }

        java.lang.Thread responseThread = new java.lang.Thread(() -> {
            try {
                java.lang.Thread.sleep(2000);
                replyToMessage(messageEvent);
            } catch (InterruptedException e) {
                Logger.debug("Thread interrupted. (expected)");
                // expected, do nothing
            } catch (Exception e) {
                Logger.error(e);
                messageEvent.getChannel().sendMessage(e.getMessage());
            }
        });
        responseThread.setDaemon(true);
        responseThread.start();

        messageEvent.getChannel().addUserStartTypingListener(typingEvent -> {
            responseThread.interrupt();
        }).removeAfter(2100, TimeUnit.MILLISECONDS);
    }

    private static void replyToMessage(MessageCreateEvent event) {
        try (NonThrowingAutoCloseable typingIndicator = event.getChannel().typeContinuously()) {
            Conversation convo = getConversation(event.getChannel().getId());
            convo.appendDiscordMessageToThread(event.getMessage());
            convo.pollResponse().thenAccept(response -> {
                String responseText = response.getText();
                if (responseText.length() > discordMessageCharLimit) {
                    try {
                        List<String> messageParts = MessageSplitter.splitMessageByCharLimit(responseText, discordMessageCharLimit);
                        messageParts.forEach(message -> {
                            event.getChannel().sendMessage(message).join();
                        });
                    } catch (UnsplittableException e) {
                        sendResponseAsTxt(String.valueOf(event.getMessageId()), responseText, event.getChannel());
                    }
                } else {
                    event.getChannel().sendMessage(responseText);
                }
                typingIndicator.close();
            });
        } catch (Exception e) {
            Logger.error(e);
            event.getChannel().sendMessage(e.getMessage());
        }
    }

    private static void sendResponseAsTxt(String fileName, String response, TextChannel channel) {
        CompletableFuture.runAsync(() -> {
            TextFile.create(fileName, false);
            TextFile.setContent(fileName, response);
            TextFile.getFile(fileName).ifPresent(file -> {
                channel.sendMessage(file).join();
                file.delete();
            });
        });
    }

    /**
     * Creates/returns conversation of a user with chat gpt
     */
    public static Conversation getConversation(long channelID) {
        Conversation convo = conversations.get(channelID);
        if (convo == null) {
            convo = new Conversation();
            conversations.put(channelID, convo);
        }

        return convo;
    }

    public static boolean deleteConversation(long channelID) {
        Conversation removedConvo = conversations.remove(channelID);
        return removedConvo != null ? removedConvo.deleteThread() : false;
    }

    private static final HashSet<Long> whitelistedIds = new HashSet<Long>();
    private static final String whitelistFileName = "openaiWhitelist";
    public static void initializeWhitelist() {
        TextFile.create(whitelistFileName, true);
        TextFile.readContent(whitelistFileName).ifPresent(ids -> {
            whitelistedIds.addAll(
                List.of(ids.split("\n")).stream()
                    .map(id -> Long.parseLong(id.strip()))
                    .collect(Collectors.toList())
            );
        });
    }

    public static boolean userIsWhitelisted(long userID) {
        return whitelistedIds.contains(userID);
    }

    public static void addUserToWhitelist(long userID) {
        if (whitelistedIds.add(userID)) {
            TextFile.appendContent(whitelistFileName, userID + "\n");
        }
    }

    public static void removeUserFromWhitelist(long userID) {
        if (whitelistedIds.remove(userID)) {
            String idList = whitelistedIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
            TextFile.setContent(whitelistFileName, idList);
        }
    }
}

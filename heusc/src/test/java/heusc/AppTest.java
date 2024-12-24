package heusc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;

import heusc.API.OpenAi.Conversation;
import heusc.API.OpenAi.ConversationManager;
import heusc.API.OpenAi.oaMessage;
import heusc.API.OpenAi.OpenAi;
import heusc.API.OpenAi.Run;
import heusc.API.OpenAi.Thread;
import heusc.API.OpenAi.oaMessage.TextContent;
import heusc.Utility.TextFile;
import heusc.API.OpenAi.Thread.MessageRetrievalResponse;

@SuppressWarnings("unused")
public class AppTest {
    public static void main(String[] args) throws Exception {
        idWhitelistTest();
    }

    public static void idWhitelistTest() {
        ConversationManager.initializeWhitelist();

        long id = 263049275196309506L;
        System.out.println("whitelisting id");
        
        ConversationManager.addUserToWhitelist(id);
        System.out.println("is whitelisted?: " + ConversationManager.userIsWhitelisted(id));

        // ConversationManager.removeUserFromWhitelist(id);
        // System.out.println("is whitelisted?: " + ConversationManager.userIsWhitelisted(id));
    }

    public static void textFileTest() {
        TextFile.create("test", true);
        TextFile.setContent("test", "a");
        TextFile.appendContent("test", "b");
        System.out.println("test content: " + TextFile.readContent("test").get());
        TextFile.setContent("test", "c");
        System.out.println("test content: " + TextFile.readContent("test").get());
    }

    public static void pollTest() {
        Thread thread = Thread.createThread();
        System.out.println("thread id: " + thread.id);

        try {
            List<oaMessage> newMessages = new ArrayList<oaMessage>();
            newMessages.add(
                oaMessage.userMessageOf(
                    TextContent.of("what happens if i send two message content parts in one request?"),
                    TextContent.of("is this text content part appended to the other?")
                )
            );

            // Run run = Run.createRun(thread.id, OpenAi.assistantID, newMessages);
            // run.pollCompletion(10, TimeUnit.SECONDS).join();
            Run run = Run.createRunAndPoll(thread.id, OpenAi.assistantID, newMessages).join();
            System.out.println("run status: " + run.getStatus());

            List<oaMessage> retrievedMessages = thread.retrieveLatestMessages(2);
            for (oaMessage message : retrievedMessages) {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean success = thread.deleteThread();
        System.out.println("deleted thread: " + success);
    }

    public static void manualTest() {
        Thread thread = Thread.createThread();
        System.out.println("thread id: " + thread.id);

        try {
            List<oaMessage> newMessages = new ArrayList<oaMessage>();
            newMessages.add(oaMessage.userMessageOf(TextContent.of("hello")));

            Run run = Run.createRun(thread.id, OpenAi.assistantID, newMessages);
            System.out.println("run id: " + run.id);

            System.out.println("\nsleeping...");
            java.lang.Thread.sleep(1000);
            System.out.println("slept\n");

            run = Run.retrieveRun(thread.id, run.id);
            System.out.println("run status: " + run.getStatus());

            List<oaMessage> retrievedMessages = thread.retrieveLatestMessages(2);
            for (oaMessage message : retrievedMessages) {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean success = thread.deleteThread();
        System.out.println("deleted thread: " + success);
    }

    public static void messageResponseTest() throws IOException {
        String json = "{\r\n" + //
                        "  \"object\": \"list\",\r\n" + //
                        "  \"data\": [\r\n" + //
                        "    {\r\n" + //
                        "      \"id\": \"msg_aEtAf74NeVxHldOBclhXH9MG\",\r\n" + //
                        "      \"object\": \"thread.message\",\r\n" + //
                        "      \"created_at\": 1734752248,\r\n" + //
                        "      \"assistant_id\": \"asst_EHgEfVVKb87WVPTq5gzuyxaE\",\r\n" + //
                        "      \"thread_id\": \"thread_6g8p0oAHC4zLNBCywlBhACGr\",\r\n" + //
                        "      \"run_id\": \"run_bXpyvbBHXuwWl8H82S4O31aS\",\r\n" + //
                        "      \"role\": \"assistant\",\r\n" + //
                        "      \"content\": [\r\n" + //
                        "        {\r\n" + //
                        "          \"type\": \"text\",\r\n" + //
                        "          \"text\": {\r\n" + //
                        "            \"value\": \"Hello! How can I assist you today?\",\r\n" + //
                        "            \"annotations\": []\r\n" + //
                        "          }\r\n" + //
                        "        }\r\n" + //
                        "      ],\r\n" + //
                        "      \"attachments\": [],\r\n" + //
                        "      \"metadata\": {}\r\n" + //
                        "    },\r\n" + //
                        "    {\r\n" + //
                        "      \"id\": \"msg_Z7OUhwKqhk7XeolKQH6trJPB\",\r\n" + //
                        "      \"object\": \"thread.message\",\r\n" + //
                        "      \"created_at\": 1734752247,\r\n" + //
                        "      \"assistant_id\": null,\r\n" + //
                        "      \"thread_id\": \"thread_6g8p0oAHC4zLNBCywlBhACGr\",\r\n" + //
                        "      \"run_id\": null,\r\n" + //
                        "      \"role\": \"user\",\r\n" + //
                        "      \"content\": [\r\n" + //
                        "        {\r\n" + //
                        "          \"type\": \"text\",\r\n" + //
                        "          \"text\": {\r\n" + //
                        "            \"value\": \"hello\",\r\n" + //
                        "            \"annotations\": []\r\n" + //
                        "          }\r\n" + //
                        "        }\r\n" + //
                        "      ],\r\n" + //
                        "      \"attachments\": [],\r\n" + //
                        "      \"metadata\": {}\r\n" + //
                        "    }\r\n" + //
                        "  ],\r\n" + //
                        "  \"first_id\": \"msg_aEtAf74NeVxHldOBclhXH9MG\",\r\n" + //
                        "  \"last_id\": \"msg_Z7OUhwKqhk7XeolKQH6trJPB\",\r\n" + //
                        "  \"has_more\": false\r\n" + //
                        "}";
        System.out.println(json);

        MessageRetrievalResponse mmr = MessageRetrievalResponse.mrrAdapter.fromJson(json);

        mmr.data.forEach(message -> {
            TextContent content = (TextContent) message.content.get(0);
            System.out.println(content.getText());
        });
    }
}

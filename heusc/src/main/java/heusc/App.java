package heusc;

import heusc.API.OpenAi.ConversationManager;
import heusc.API.OpenAi.oaMessage.Content;
import heusc.API.OpenAi.oaMessage.ImageUrlContent;
import heusc.API.OpenAi.oaMessage.MessageTextContentAdapter;
import heusc.API.OpenAi.oaMessage.TextContent;
import heusc.Commands.*;
import heusc.Commands.CommandLine.*;
import heusc.Events.*;

import java.util.Scanner;
import java.util.Set;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.tinylog.Logger;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static final DiscordApi api = new DiscordApiBuilder().setToken(Dotenv.load().get("DISCORD_TOKEN")).setAllNonPrivilegedIntentsAnd(Intent.DIRECT_MESSAGES, Intent.DIRECT_MESSAGE_REACTIONS, Intent.GUILD_MESSAGES, Intent.MESSAGE_CONTENT).login().join();
    public static final Moshi MOSHI = new Moshi.Builder()
        .add(PolymorphicJsonAdapterFactory.of(Content.class, "type")
            .withSubtype(TextContent.class, "text")
            .withSubtype(ImageUrlContent.class, "image_url"))
        .add(new MessageTextContentAdapter()).build();
    
    public static void main(String[] args) {
        // initialize stuff
        ConversationManager.initializeWhitelist();
        startCommandLineCommandHandler();

        // create commands
        api.bulkOverwriteGlobalApplicationCommands(Set.of(
            Ping.createCommand(),
            EndCall.createCommand(),
            InspireMe.createCommand(),
            DadJoke.createCommand(),
            FunFact.createCommand(),
            NewThread.createCommand(),
            UserInfo.createUserContextMenu()
        ));

        // register listeners
        SlashCommandCreate.registerListener();
        UserContextMenuCommand.registerListener();
        MessageCreate.registerListener();
        ReactionAdd.registerListener();

        Logger.tag("sysout").info("App initialized!");
    }

    public static void startCommandLineCommandHandler() {
        new Thread(() -> {
            try (Scanner sysin = new Scanner(System.in)) {
                String input = "";
                while (!input.equals("stop")) {
                    input = sysin.nextLine();
                    String[] commandParts = input.split(" ");

                    if (commandParts.length != 3) {
                        System.err.println("format: [command] [action] [parameter]");
                        continue;
                    }

                    switch (commandParts[0]) {
                        case "whitelist": Whitelist.handleCommand(commandParts); continue;
                        default:
                            System.err.println("command does not exist");
                            continue;
                    }
                }
                System.out.println("stopped");
                App.api.disconnect();
            }
        }).start();
    }
}
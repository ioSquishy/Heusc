package heusc;

import heusc.API.OpenAi.ConversationManager;
import heusc.API.OpenAi.oaMessage.Content;
import heusc.API.OpenAi.oaMessage.ImageUrlContent;
import heusc.API.OpenAi.oaMessage.MessageTextContentAdapter;
import heusc.API.OpenAi.oaMessage.TextContent;
import heusc.Commands.*;
import heusc.Events.*;

import java.util.Set;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

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

        // create commands
        api.bulkOverwriteGlobalApplicationCommands(Set.of(
            Ping.createCommand(),
            EndCall.createCommand(),
            InspireMe.createCommand(),
            DadJoke.createCommand(),
            FunFact.createCommand()
        ));

        // register listeners
        SlashCommandCreate.registerListener();
        MessageCreate.registerListener();
        ReactionAdd.registerListener();
        // ButtonClick.registerListener();
    }
}
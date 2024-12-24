package heusc.Commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import heusc.API.ZenQuoteAPI;

public class InspireMe {
    public static SlashCommandBuilder createCommand () {
        return new SlashCommandBuilder()
            .setName("inspireme")
            .setDescription("Get a random inspirational quote.");
    }

    public static void runCommand(SlashCommandInteraction interaction) {
        interaction.createImmediateResponder().setContent(ZenQuoteAPI.pullQuote()).respond();
    }
}

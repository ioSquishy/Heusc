package heusc.Commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import heusc.API.FunFactAPI;

public class FunFact {
    public static SlashCommandBuilder createCommand () {
        return new SlashCommandBuilder()
            .setName("funfact")
            .setDescription("Get a random fun fact.");
    }

    public static void runCommand(SlashCommandInteraction interaction) {
        interaction.createImmediateResponder().setContent(FunFactAPI.getFunFact()).respond();
    }
}

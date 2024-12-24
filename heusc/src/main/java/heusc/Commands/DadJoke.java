package heusc.Commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import heusc.API.DadJokeAPI;

public class DadJoke {
    public static SlashCommandBuilder createCommand () {
        return new SlashCommandBuilder()
            .setName("dadjoke")
            .setDescription("Get a random dad joke.");
    }

    public static void runCommand(SlashCommandInteraction interaction) {
        interaction.createImmediateResponder().setContent(DadJokeAPI.getDadJoke()).respond();
    }
}

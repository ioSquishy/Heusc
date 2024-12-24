package heusc.Events;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;
import heusc.Commands.DadJoke;
import heusc.Commands.EndCall;
import heusc.Commands.FunFact;
import heusc.Commands.InspireMe;
import heusc.Commands.NewThread;
import heusc.Commands.Ping;

public class SlashCommandCreate {
    public static ListenerManager<SlashCommandCreateListener> registerListener() {
        return App.api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            switch (interaction.getCommandName().toString()) {
                case "ping" : Ping.runCommand(interaction); break;
                case "endcall" : EndCall.runCmd(interaction); break;
                case "inspireme" : InspireMe.runCommand(interaction); break;
                case "dadjoke" : DadJoke.runCommand(interaction); break;
                case "funfact" : FunFact.runCommand(interaction); break;
                case "newthread" : NewThread.runCommand(interaction);
            }
        });
    }
}

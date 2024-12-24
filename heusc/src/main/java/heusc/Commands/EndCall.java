package heusc.Commands;

import java.time.Instant;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import heusc.App;
import heusc.API.DadJokeAPI;
import heusc.API.FunFactAPI;
import heusc.API.ZenQuoteAPI;


public class EndCall {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static HashMap<Long, EndCallEntry> pendingEnds = new HashMap<Long, EndCallEntry>();

    private static final Runnable refreshPending = new Runnable() {
        public void run() {
            HashMap<Long, EndCallEntry> refreshedPendingEnds = new HashMap<Long, EndCallEntry>();
            // iterate all pending ends and run the runnable if it is time
            for (Entry<Long, EndCallEntry> entry : pendingEnds.entrySet()) {
                if (entry.getValue().endTime <= Instant.now().getEpochSecond()) {
                    entry.getValue().deleteCall.run();
                } else {
                    refreshedPendingEnds.put(entry.getKey(), entry.getValue());
                }
            }
            // override pendingEnds with still pending ones
            pendingEnds = refreshedPendingEnds;
        }
    };

    public static void runCmd(SlashCommandInteraction interaction) {
        ServerVoiceChannel vc = interaction.getArgumentChannelValueByName("vc").get().asServerVoiceChannel().get();
        int minutes = interaction.getArgumentLongValueByName("minutes").get().intValue();
        long endTime = Instant.now().getEpochSecond() + (minutes*60);

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    vc.getServer().createVoiceChannelBuilder().setName(vc.getName()).setRawPosition(vc.getRawPosition()).create().get();
                    vc.delete().get();
                } catch(InterruptedException | ExecutionException e) {
                    App.api.getOwner().get().join().sendMessage("Failed to end <#" + vc.getId() + ">\nMessage: " + e.getMessage() + "\nCause: " + e.getCause()).join();
                }
            }
        };

        pendingEnds.put(vc.getId(), new EndCallEntry(endTime, runnable));
        scheduler.schedule(refreshPending, minutes, TimeUnit.MINUTES);
        interaction.createImmediateResponder().setContent("Call will end <t:" + endTime + ":R>\n" + getSpecialMessage()).respond().join();
    }

    private static final Random random = new Random();
    private static String getSpecialMessage() {
        switch (random.nextInt(3)) {
            case 0 : return ZenQuoteAPI.pullQuote();
            case 1 : return DadJokeAPI.getDadJoke();
            case 2 : return FunFactAPI.getFunFact();
        }
        return "";
    }

    public static SlashCommandBuilder createCommand () {
        return new SlashCommandBuilder()
            .setName("endcall")
            .setDescription("Ends call after x minutes.")
            .addOption(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "vc", "voice channel to end", true))
            .addOption(SlashCommandOption.create(SlashCommandOptionType.LONG, "minutes", "number of minutes until call ends", true))
            .setEnabledInDms(false);
    }

    private static class EndCallEntry {
        public final long endTime;
        public final Runnable deleteCall;

        public EndCallEntry(long endTime, Runnable deleteCall) {
            this.endTime = endTime;
            this.deleteCall = deleteCall;
        }
    }
}

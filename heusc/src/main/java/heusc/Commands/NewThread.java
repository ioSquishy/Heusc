package heusc.Commands;

import org.javacord.api.entity.channel.AutoArchiveDuration;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.API.OpenAi.Conversation;
import heusc.API.OpenAi.ConversationManager;

public class NewThread {
    public static SlashCommandBuilder createCommand() {
        return new SlashCommandBuilder()
            .setName("newthread")
            .setDescription("Start a new AI thread.");
    }

    public static void runCommand(SlashCommandInteraction interaction) {
        if (!ConversationManager.userIsWhitelisted(interaction.getUser().getId())) {
            return;
        }

        interaction.getChannel().ifPresent(channel -> {
            if (channel.getType() == ChannelType.PRIVATE_CHANNEL) {
                Conversation convo = ConversationManager.getConversation(channel.getId());
                if (convo.clearThread()) {
                    interaction.createImmediateResponder().setContent("Thread cleared.").respond();
                } else {
                    interaction.createImmediateResponder().setContent("Unable to clear thread.").respond();
                }
                return;
            }

            // create thread in server text channel if command was not sent in dms
            channel.asServerTextChannel().ifPresent(serverChannel -> {
                serverChannel.createThread(ChannelType.SERVER_PUBLIC_THREAD, "New Thread", AutoArchiveDuration.ONE_DAY).thenAccept(thread -> {
                    ListenerManager<MessageCreateListener> listener = thread.addMessageCreateListener(ConversationManager.getMessageCreateListener());
                    thread.addServerThreadChannelDeleteListener(event -> {
                        ConversationManager.deleteConversation(thread.getId());
                        listener.remove();
                    });
                    interaction.createImmediateResponder().setContent("Thread created.").setFlags(MessageFlag.EPHEMERAL).respond();
                    return;
                });
            });
        });

        interaction.createImmediateResponder().setContent("Unable to create new thread.").setFlags(MessageFlag.EPHEMERAL).respond();
    }
}

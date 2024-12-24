package heusc.Events;

import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;
import heusc.API.OpenAi.ConversationManager;

public class MessageCreate {
    public static ListenerManager<MessageCreateListener> registerListener() {
        return App.api.addMessageCreateListener(event -> {
            if (event.getChannel().getType() == ChannelType.PRIVATE_CHANNEL) {
                ConversationManager.handleMessageCreateEvent(event);
            }
        });
    }
}

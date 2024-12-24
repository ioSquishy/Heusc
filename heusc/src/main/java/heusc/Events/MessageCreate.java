package heusc.Events;

import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;
import heusc.API.OpenAi.ConversationManager;

public class MessageCreate {
    public static ListenerManager<MessageCreateListener> registerListener() {
        return App.api.addMessageCreateListener(event -> {
            ConversationManager.handleMessageCreateEvent(event);
        });
    }
}

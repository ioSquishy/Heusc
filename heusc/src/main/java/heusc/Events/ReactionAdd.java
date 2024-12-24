package heusc.Events;

import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;

public class ReactionAdd {
    public static ListenerManager<ReactionAddListener> registerListener() {
        return App.api.addReactionAddListener(event -> {
            if (event.getChannel().getType().equals(ChannelType.PRIVATE_CHANNEL)) {
                event.deleteMessage();
                return;
            }
        });
    }
}

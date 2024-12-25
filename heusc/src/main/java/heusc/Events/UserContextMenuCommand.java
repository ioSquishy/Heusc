package heusc.Events;

import org.javacord.api.interaction.UserContextMenuInteraction;
import org.javacord.api.listener.interaction.UserContextMenuCommandListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;

public class UserContextMenuCommand {
    public static ListenerManager<UserContextMenuCommandListener> registerListener() {
        return App.api.addUserContextMenuCommandListener(event -> {
            UserContextMenuInteraction interaction = event.getUserContextMenuInteraction();
            System.out.println("invoking: " + interaction.getUser().getName());
            System.out.println("target: " + interaction.getTarget().getName());
        });
    }
}

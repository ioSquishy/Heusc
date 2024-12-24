package heusc.Events;

import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.util.event.ListenerManager;

import heusc.App;

public class ButtonClick {
    public static ListenerManager<ButtonClickListener> registerListener() {
        return App.api.addButtonClickListener(event -> {
            
        });
    }
}

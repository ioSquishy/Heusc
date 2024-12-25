package heusc.Commands;

import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.UserContextMenuBuilder;

public class UserInfo {
    public static SlashCommandBuilder createCommand () {
        return new SlashCommandBuilder()
            .setName("userinfo")
            .setDescription("Returns info about a user.")
            .addOption(
                SlashCommandOption.create(SlashCommandOptionType.USER, "user", "user to get info on", true)
            );
    }

    public static UserContextMenuBuilder createUserContextMenu() {
        return new UserContextMenuBuilder()
            .setName("userinfo")
            .setDescription("Returns info about a user.");
    }
}

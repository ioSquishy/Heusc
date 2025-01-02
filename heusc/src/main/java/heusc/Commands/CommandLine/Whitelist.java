package heusc.Commands.CommandLine;

import heusc.API.OpenAi.ConversationManager;

public class Whitelist {
    public static void handleCommand(String[] commandParts) {
        try {
            long whitelistParam = Long.parseLong(commandParts[2]);
            switch (commandParts[1]) {
                case "add": 
                    ConversationManager.addUserToWhitelist(whitelistParam);
                case "remove":
                    ConversationManager.removeUserFromWhitelist(whitelistParam);
                default:
                    System.err.println("action does not exist");
            }
        } catch (NumberFormatException e) {
            System.err.println("Not a valid user ID.");
        }
    }
}

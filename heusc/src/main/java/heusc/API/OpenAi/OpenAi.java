package heusc.API.OpenAi;

import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenAi {
    private static final String apiKey = Dotenv.load().get("OPENAI_API_KEY");

    protected static final Map<String, String> headers = Map.of(
        "Content-Type", "application/json",
        "Authorization", "Bearer " + apiKey,
        "OpenAI-Beta", "assistants=v2",
        "OpenAI-Organization", "org-H3RmQBHZoOkdEgoxLocQ5W3w",
        "OpenAI-Project", "proj_O6PNYeqaXqT4U7EsrXCtHhGb"
    );

    public static final String assistantID = "asst_EHgEfVVKb87WVPTq5gzuyxaE";
}

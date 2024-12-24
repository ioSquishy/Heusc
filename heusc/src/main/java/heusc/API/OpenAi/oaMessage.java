package heusc.API.OpenAi;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;

import heusc.App;

public class oaMessage {
    protected static final JsonAdapter<oaMessage> messageAdapter = App.MOSHI.adapter(oaMessage.class);

    // request params
    private String id; // message id
    private String thread_id;
    public final String role; // "user" or "assistant";
    public final List<Content> content;
    
    private oaMessage(String id, String thread_id, String role, List<Content> content) {
        this.id = id;
        this.thread_id = thread_id;
        this.role = role;
        this.content = content;
    }

    private oaMessage(String role, List<Content> content) {
        this.role = role;
        this.content = content;
    }

    public static oaMessage userMessageOf(Content... content) {
        return new oaMessage("user", List.of(content));
    }

    public static oaMessage userMessageOf(List<Content> content) {
        return new oaMessage("user", content);
    }

    public String getMessageId() {
        return id;
    }

    public String getThreadId() {
        return thread_id;
    }

    public String getText() {
        return getTextContent().get(0).getText();
    }

    public List<TextContent> getTextContent() {
        return content.stream()
            .filter(content -> content instanceof TextContent)
            .map(content -> (TextContent) content)
            .collect(Collectors.toList());
    }

    public List<ImageUrlContent> getImageUrlContent() {
        return content.stream()
            .filter(content -> content instanceof ImageUrlContent)
            .map(content -> (ImageUrlContent) content)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return messageAdapter.toJson(this);
    }

    public static class MessageTextContentAdapter {
        @FromJson TextContent fromJson(Map<String, Object> rawJson) {
            @SuppressWarnings("unchecked")
            Map<String, Object> textObj = (Map<String, Object>) rawJson.get("text");
            return new TextContent((String) textObj.get("value"));
        }
    }
    
    public abstract static class Content {
        public final transient String type;

        public Content(String type) {
            this.type =  type;
        }

        public static List<Content> of(Content... content) {
            return List.of(content);
        }
    }

    public static class TextContent extends Content {
        private final String text;

        public TextContent(String text) {
            super("text");
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static TextContent of(String text) {
            return new TextContent(text);
        }
    }

    public static class ImageUrlContent extends Content {
        private final ImageUrl image_url;

        public ImageUrlContent(String url) {
            super("image_url");
            this.image_url = new ImageUrl(url);
        }

        public String getUrl() {
            return image_url.url;
        }

        public static ImageUrlContent of(String imageUrl) {
            return new ImageUrlContent(imageUrl);
        }

        private static class ImageUrl {
            final String url;
            public ImageUrl(String url) {
                this.url = url;
            }
        }
    }
}

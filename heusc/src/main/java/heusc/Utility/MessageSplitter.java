package heusc.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSplitter {
    public static List<String> splitMessageByCharLimit(String originalMessage, int charLimit) throws UnsplittableException {
        List<String> messageSegments = segmentMessage(originalMessage);
        List<String> recombinedSegments = new ArrayList<String>();

        StringBuilder combinedString = new StringBuilder();
        for (String segment : messageSegments) {
            String seg = segment;
            
            if (seg.length() > charLimit) {
                throw new UnsplittableException();
            }

            // add code blocks separately
            if (seg.startsWith("```") && seg.endsWith("```")) {
                recombinedSegments.add(combinedString.toString());
                combinedString.setLength(0);
                recombinedSegments.add(seg);
                continue;
            }

            if ((combinedString.length() + seg.length()) <= charLimit) {
                combinedString.append(seg);
            } else {
                recombinedSegments.add(combinedString.toString());
                combinedString.setLength(0);
                combinedString.append(seg);
            }
        }
        recombinedSegments.add(combinedString.toString());

        return recombinedSegments;
    }

    private static final Pattern codeBlockPattern = Pattern.compile("(```.*?```\\n)", Pattern.DOTALL);
    private static List<String> segmentMessage(String originalMessage) {
        String message = originalMessage + "\n";
        Matcher matcher = codeBlockPattern.matcher(message);
        List<String> segments = new ArrayList<>();

        int lastIndex = 0;
        while (matcher.find()) {
            // Process text before the code block
            if (lastIndex < matcher.start()) {
                String nonCodeSegment = message.substring(lastIndex, matcher.start());
                segments.addAll(splitAndPreserveNewlines(nonCodeSegment));
            }

            // Add the entire code block as a single segment
            segments.add(matcher.group(1));

            // Update lastIndex to the end of the current match
            lastIndex = matcher.end();
        }

        // Process any remaining text after the last code block
        if (lastIndex < message.length()) {
            String nonCodeSegment = message.substring(lastIndex);
            segments.addAll(splitAndPreserveNewlines(nonCodeSegment));
        }

        return segments;
    }

    private static List<String> splitAndPreserveNewlines(String text) {
        String[] lines = text.split("(?<=\n)");  // Lookbehind to split but keep newline segments
        return List.of(lines);
    }

    public static class UnsplittableException extends Exception {
        public UnsplittableException() {
            super();
        }
        public UnsplittableException(String message) {
            super(message);
        }
    }
}

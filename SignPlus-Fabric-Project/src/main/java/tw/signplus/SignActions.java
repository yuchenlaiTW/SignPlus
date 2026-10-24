package tw.signplus;

import java.net.URI;
import java.util.Locale;

/**
 * Shared parser for SignPlus sign formats.
 * Hook this into a verified sign-click event after reading the sign's front text.
 */
public final class SignActions {
    private SignActions() {}

    public enum Kind { WARP, TP, HTTPS, HOME, NONE }

    public record Action(Kind kind, String value) {}

    public static Action parse(String firstLine, String remainingLines) {
        String tag = firstLine == null ? "" : firstLine.trim().toLowerCase(Locale.ROOT);
        String value = remainingLines == null ? "" : remainingLines.trim();

        return switch (tag) {
            case "[warp]" -> new Action(Kind.WARP, value);
            case "[tp]" -> new Action(Kind.TP, value);
            case "[https]" -> new Action(Kind.HTTPS, value.replaceAll("\\s+", ""));
            case "[home]" -> new Action(Kind.HOME, value);
            default -> new Action(Kind.NONE, "");
        };
    }

    /** Only accept HTTPS links. Call only in response to an explicit user click. */
    public static URI safeHttpsUri(String value) {
        try {
            URI uri = URI.create(value);
            if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null) {
                return uri;
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid URL.
        }
        return null;
    }
}

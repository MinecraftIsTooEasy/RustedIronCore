package moddedmite.rustedironcore.client.api.chat;

import java.util.List;

/**
 * Finds click targets in plain chat text. Indices refer to visible characters, without formatting codes.
 */
@FunctionalInterface
public interface ChatClickTextProvider {
    List<ChatClickTextRange> findClickTargets(String plainText);
}

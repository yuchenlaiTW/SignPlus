package tw.signplus;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.network.chat.Component;

/** Isolated sign-text adapter for easier version-specific maintenance. */
public final class SignTextReader {
    private SignTextReader() {}
    public static String[] read(SignBlockEntity sign) {
        String[] lines = new String[4];
        try {
            var text = sign.getFrontText();
            for (int i = 0; i < 4; i++) {
                Component component = text.getMessage(i, false);
                lines[i] = component.getString();
            }
        } catch (Throwable ignored) { return new String[0]; }
        return lines;
    }
}

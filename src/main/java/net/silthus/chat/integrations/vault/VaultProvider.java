package net.silthus.chat.integrations.vault;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.Identity;
import org.jetbrains.annotations.NotNull;

public class VaultProvider {

    private final Chat chat;

    public VaultProvider(Chat chat) {
        this.chat = chat;
    }

    public VaultProvider() {
        this.chat = null;
    }

    public Component getPrefix(Identity identity) {
        if (chat == null || !identity.isPlayer())
            return Component.empty();

        String prefix = chat.getPlayerPrefix(identity.getPlayer());
        return validateAndDeserialize(prefix);
    }

    public Component getSuffix(Identity identity) {
        if (chat == null || !identity.isPlayer())
            return Component.empty();

        String suffix = chat.getPlayerSuffix(identity.getPlayer());
        return validateAndDeserialize(suffix);
    }

    @NotNull
    private Component validateAndDeserialize(String prefix) {
        if (prefix == null || prefix.isBlank())
            return Component.empty();

        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
    }
}

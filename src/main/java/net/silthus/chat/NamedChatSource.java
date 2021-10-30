package net.silthus.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
final class NamedChatSource implements ChatSource {

    private final String identifier;
    private String displayName;

    NamedChatSource(String identifier) {
        this(identifier, identifier);
    }
}

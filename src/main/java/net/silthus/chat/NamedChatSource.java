package net.silthus.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
class NamedChatSource implements ChatSource {

    private final String identifier;
    private String name;

    NamedChatSource(String identifier) {
        this(identifier, identifier);
    }
}

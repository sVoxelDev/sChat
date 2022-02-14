package net.silthus.schat.message;

import java.util.Optional;
import net.silthus.schat.channel.Channel;
import org.jetbrains.annotations.NotNull;

public final class TargetsFilter {

    public static @NotNull Optional<Channel> firstChannel(Targets targets) {
        return targets.stream()
            .filter(target -> target instanceof Channel)
            .map(target -> (Channel) target)
            .findFirst();
    }

    private TargetsFilter() {
    }
}

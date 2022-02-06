package net.silthus.schat.command;

import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public abstract class CommandBuilder<B extends Command.Builder<B, C>, C extends Command> implements Command.Builder<B, C> {

    private Function<B, ? extends C> command;

    protected CommandBuilder(Function<B, ? extends C> command) {
        this.command = command;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B use(Function<B, ? extends C> command) {
        this.command = command;
        return (B) this;
    }
}

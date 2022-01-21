package net.silthus.schat.ui;

import java.util.function.Consumer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;

public interface ViewProvider {

    static ViewProvider simpleViewProvider() {
        return new SimpleViewProvider();
    }

    default void updateView(Chatter chatter, Consumer<Component> renderedView) {
        renderedView.accept(getView(chatter).render());
    }

    View getView(@NonNull Chatter chatter);
}

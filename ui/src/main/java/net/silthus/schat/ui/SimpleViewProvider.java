package net.silthus.schat.ui;

import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import net.silthus.schat.chatter.Chatter;

import static net.silthus.schat.ui.ViewModel.of;
import static net.silthus.schat.ui.views.Views.tabbedChannels;

final class SimpleViewProvider implements ViewProvider {
    private final Map<Chatter, View> views = new WeakHashMap<>();

    @Override
    public View getView(@NonNull Chatter chatter) {
        return views.computeIfAbsent(chatter, c -> tabbedChannels(of(c)));
    }
}

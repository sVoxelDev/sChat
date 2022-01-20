package net.silthus.schat.ui.views;

import lombok.NonNull;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewModel;

public final class Views {

    public static View tabbedChannels(@NonNull ViewModel viewModel) {
        return new TabbedChannelsView(viewModel);
    }

    private Views() {
    }
}

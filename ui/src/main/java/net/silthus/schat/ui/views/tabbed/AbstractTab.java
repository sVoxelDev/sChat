/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.ui.views.tabbed;

import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.view.View;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.silthus.schat.util.Iterators.lastN;

@Data
@Accessors(fluent = true)
public abstract class AbstractTab implements Tab {
    private final TabbedChannelsView view;
    private Format messageFormat;

    protected AbstractTab(TabbedChannelsView view, Format messageFormat) {
        this.view = view;
        this.messageFormat = messageFormat;
    }

    @Override
    public Component renderContent() {
        return renderBlankLines()
            .append(join(newlines(), renderMessages()));
    }

    protected abstract boolean isMessageDisplayed(Message message);

    protected List<Component> renderMessages() {
        return messages().stream()
            .map(message -> messageFormat().format(view, message))
            .toList();
    }

    protected @NotNull Collection<Message> messages() {
        return view.viewModel().messages().stream()
            .filter(this::isMessageDisplayed)
            .collect(lastN(100));
    }

    protected final Component renderBlankLines() {
        final int blankLineAmount = Math.max(0, view.get(View.VIEW_HEIGHT) - messages().size());
        return renderBlankLines(blankLineAmount);
    }

    protected final Component renderBlankLines(int amount) {
        final TextComponent.Builder builder = text();
        for (int i = 0; i < amount; i++) {
            builder.append(newline());
        }
        return builder.build();
    }
}

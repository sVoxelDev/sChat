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
package net.silthus.schat.ui.view;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;

import static net.silthus.schat.pointer.Setting.setting;

public interface View extends Configured.Modifiable<View> {

    Key VIEW_MARKER_KEY = Key.key("schat", "view");
    Component VIEW_MARKER = net.kyori.adventure.text.Component.storageNBT(VIEW_MARKER_KEY.asString(), VIEW_MARKER_KEY);

    Setting<Integer> VIEW_HEIGHT = setting(Integer.class, "height", 100); // minecraft chat box height in lines

    static View empty() {
        return net.kyori.adventure.text.Component::empty;
    }

    Component render();

    default boolean isRenderedView(Component render) {
        return render.contains(VIEW_MARKER) || render.children().contains(VIEW_MARKER);
    }
}

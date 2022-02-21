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
package net.silthus.schat.platform.config.adapter;

import java.io.File;
import java.io.Serial;
import java.nio.file.Path;

public interface ConfigurationAdapter extends ConfigurationSection {

    Path configPath();

    void save() throws SaveFailed;

    void load() throws LoadFailed;

    interface Factory {
        ConfigurationAdapter create(File config);
    }

    class LoadFailed extends RuntimeException {
        @Serial private static final long serialVersionUID = 5479783390057437837L;

        public LoadFailed(Throwable cause) {
            super(cause);
        }
    }

    class SaveFailed extends RuntimeException {
        @Serial private static final long serialVersionUID = -2373407050480274400L;

        public SaveFailed(Throwable cause) {
            super(cause);
        }
    }
}

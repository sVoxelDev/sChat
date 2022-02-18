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
package net.silthus.schat.commands;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.command.Command;
import net.silthus.schat.command.Result;

public class SetActiveChannelCommand extends JoinChannelCommand implements Command {

    public static Result setActiveChannel(Chatter chatter, Channel channel) {
        return setActiveChannelBuilder(chatter, channel).execute();
    }

    public static JoinChannelCommand.Builder setActiveChannelBuilder(Chatter chatter, Channel channel) {
        return joinChannelBuilder(chatter, channel).use(SetActiveChannelCommand::new);
    }

    protected SetActiveChannelCommand(JoinChannelCommand.Builder builder) {
        super(builder);
    }

    @Override
    public Result execute() {
        final Result joinChannelResult = super.execute();
        if (joinChannelResult.wasSuccessful())
            return setActiveChannelAndUpdateView();
        else
            return joinChannelResult;
    }

    private Result setActiveChannelAndUpdateView() {
        chatter().activeChannel(channel());
        return Result.success();
    }
}

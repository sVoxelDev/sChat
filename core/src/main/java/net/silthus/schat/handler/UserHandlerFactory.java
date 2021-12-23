package net.silthus.schat.handler;

import net.silthus.schat.User;

public interface UserHandlerFactory<H extends Handler> {

    H create(User user);
}

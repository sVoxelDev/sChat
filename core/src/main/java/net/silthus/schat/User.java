package net.silthus.schat;

import lombok.Getter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;

@Getter
public class User implements Identified {

    private final Identity identity;

    public User(Identity identity) {
        this.identity = identity;
    }

    public boolean hasPermission(String permission) {
        return false;
    }
}

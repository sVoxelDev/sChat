package net.silthus.schat.cucumber;

import io.cucumber.java.ParameterType;

public class UserSteps {

    @ParameterType("[a-zA-Z0-9]+")
    public User user(String user) {
        return new User();
    }
}

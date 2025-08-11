package com.premuda;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

public class MyAuthenticatedWebSession extends AuthenticatedWebSession {

    private String username;
    private Roles roles = new Roles();

    public MyAuthenticatedWebSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if ("konobar".equals(username) && "konobar".equals(password)) {
            this.username = username;
            roles.add("KONOBAR");
            return true;
        } else if ("vlasnik".equals(username) && "vlasnik".equals(password)) {
            this.username = username;
            roles.add("VLASNIK");
            return true;
        }
        return false;
    }

    @Override
    public Roles getRoles() {
        return roles;
    }

    public static MyAuthenticatedWebSession get() {
        return (MyAuthenticatedWebSession) AuthenticatedWebSession.get();
    }

    public String getUsername() {
        return username;
    }
}

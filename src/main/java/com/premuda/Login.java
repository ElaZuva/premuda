package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

public class Login extends WebPage {
    private static final long serialVersionUID = 1L;

    public Login() {
        add(new HeaderPanelPocetni("headerPanel"));

        SignInPanel signInPanel = new SignInPanel("signInPanel") {
            @Override
            protected void onSignInSucceeded() {
                Roles roles = MyAuthenticatedWebSession.get().getRoles();
                if (roles.hasRole("VLASNIK")) {
                    setResponsePage(ProsjecnaPotrosnja.class);
                } else if (roles.hasRole("KONOBAR")) {
                    setResponsePage(PregledRezervacija.class);
                } else {
                    setResponsePage(HomePage.class);
                }
            }
            @Override
            protected void onSignInFailed() {
                error("Neispravni podaci. Pokušaj ponovno.");
            }
        };

        add(signInPanel);
    }
}
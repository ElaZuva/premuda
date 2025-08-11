package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.link.Link;
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
                // fallback ako nešto pođe po zlu
                setResponsePage(HomePage.class);
            }
            
        }

        @Override
        protected void onSignInFailed() {
            // Možeš dodati poruku ili log
            error("Neispravni podaci. Pokušaj ponovno.");
        }
    };

    add(signInPanel);
    }
	
}
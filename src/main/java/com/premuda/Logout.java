package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

public class Logout extends WebPage {
    private static final long serialVersionUID = 1L;

    public Logout() {
        // Odjavi korisnika
        AuthenticatedWebSession.get().signOut();

        // Očisti sve destination URL-ove ako postoje
        AuthenticatedWebSession.get().invalidateNow();

        // Preusmjeri na login stranicu
        setResponsePage(HomePage.class);
    }
}

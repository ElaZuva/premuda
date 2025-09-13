package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {

        add(new Image("pocetna", new PackageResourceReference(HomePage.class, "pocetna.jpeg")));
        add(new Image("strelica", new PackageResourceReference(HomePage.class, "strelica.png")));
        add(new Image("jelovnik", new PackageResourceReference(HomePage.class, "jelovnik.jpeg")));
        add(new Image("hobotnica", new PackageResourceReference(HomePage.class, "hobotnica.jpeg")));
        add(new Image("informacije", new PackageResourceReference(HomePage.class, "informacije.jpeg")));
        add(new BookmarkablePageLink<Void>("rezervacijaLink", NovaRezervacijaGost.class));
        add(new BookmarkablePageLink<Void>("rezervacijaLinkDva", NovaRezervacijaGost.class));
        add(new BookmarkablePageLink<Void>("jelovnikLink", PrikazJelovnikaGost.class));
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("prijavaLink", Login.class);
        link.add(new Image("masarine", new PackageResourceReference(HomePage.class, "rak-bili.png")));
        add(link);

        add(new Label("dobrodosli", "MASARINE - PREMUDA"));
    }
	
}

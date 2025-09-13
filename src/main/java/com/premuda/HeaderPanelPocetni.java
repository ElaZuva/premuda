package com.premuda;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class HeaderPanelPocetni extends Panel {
    private static final long serialVersionUID = 1L;
    private Service service = new Service();
    public HeaderPanelPocetni(String id) {
        super(id);


        WebMarkupContainer navContainer = new WebMarkupContainer("navContainer");
        navContainer.setOutputMarkupId(true);
        add(navContainer);

        

        navContainer.add(new Link<Void>("novaRezervacija") {
            @Override
            public void onClick() {
                setResponsePage(NovaRezervacijaGost.class);
            }
        });

        navContainer.add(new Link<Void>("jelovnik") {
            @Override
            public void onClick() {
                setResponsePage(PrikazJelovnikaGost.class);
            }
        });
        navContainer.add(new Link<Void>("pocetna") {
            @Override
            public void onClick() {
                setResponsePage(HomePage.class);
            }
        });
    }
}

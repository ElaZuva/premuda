package com.premuda;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import java.time.LocalDate;

public class HeaderPanelKonobar extends Panel {
    private static final long serialVersionUID = 1L;

    public HeaderPanelKonobar(String id) {
        super(id);


        WebMarkupContainer navContainer = new WebMarkupContainer("navContainer");
        navContainer.setOutputMarkupId(true);
        add(navContainer);


        navContainer.add(new Link<Void>("pregledStolova") {
            @Override
            public void onClick() {
                setResponsePage(PregledStolova.class);
            }
        });

        navContainer.add(new Link<Void>("novaRezervacija") {
            @Override
            public void onClick() {
                setResponsePage(NovaRezervacija.class);
            }
        });

        navContainer.add(new Link<Void>("pregledRezervacija") {
            @Override
            public void onClick() {
                setResponsePage(PregledRezervacija.class);
            }
        });

        navContainer.add(new Link<Void>("logout") {
            @Override
            public void onClick() {
                setResponsePage(Logout.class);
            }
        });

    }
}

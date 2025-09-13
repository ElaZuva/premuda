package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class PregledStolova extends WebPage {
    private static final long serialVersionUID = 1L;

    private Service service = new Service();

    public PregledStolova() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        add(new Link<Void>("dodajStol") {
            @Override
            public void onClick() {
                setResponsePage(DodajStol.class);
            }
        });

        List<Stol> stolovi = service.getAllStolovi();

        ListView<Stol> stolListView = new ListView<Stol>("stolovi", stolovi) {
            @Override
            protected void populateItem(final ListItem<Stol> item) {
                final Stol stol = item.getModelObject();

                item.add(new Label("imeStola", stol.getImeStola()));
                item.add(new Label("brojLjudi", String.valueOf(stol.getBrojLjudi())));
                item.add(new Label("pozicija", stol.getPozicija() ? "Unutra" : "Vani"));
                
            }
        };

        add(stolListView);
    }
}

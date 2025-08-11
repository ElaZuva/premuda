package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.*;
import org.apache.wicket.model.*;
import java.time.LocalDate;
import java.util.*;

public class ProsjecnaPotrosnja extends WebPage {

    private LocalDate datum = LocalDate.of(2025, 7, 20);
    private int brojDana = 7;

    private List<Object[]> rezultat = new ArrayList<>();

    public ProsjecnaPotrosnja() {
        Service service = new Service();


        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }
        Form<Void> form = new Form<Void>("forma") {
            @Override
            protected void onSubmit() {
                rezultat = service.prosjecnaPotrosnjaSastojka(datum, brojDana);
            }
        };

        form.add(new TextField<>("datum", new PropertyModel<>(this, "datum")));
        form.add(new TextField<>("brojDana", new PropertyModel<>(this, "brojDana")));
        add(form);

        ListView<Object[]> listView = new ListView<Object[]>("rezultat", new PropertyModel<>(this, "rezultat")) {
            @Override
            protected void populateItem(ListItem<Object[]> item) {
                Object[] red = item.getModelObject();
                item.add(new Label("naziv", red[0].toString()));
                item.add(new Label("prosjek", String.format("%.2f", (Double) red[1])));
                item.add(new Label("stanje", String.format("%.2f", (double) red[2])));
                item.add(new Label("razlika", String.format("%.2f", (double) red[3])));
            }
        };
        add(listView);
    }
}

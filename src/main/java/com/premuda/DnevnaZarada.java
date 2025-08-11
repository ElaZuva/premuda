package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.AttributeModifier;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DnevnaZarada extends WebPage {

    private List<Object[]> rezultat = new ArrayList<>();
    private List<Object[]> buduci = new ArrayList<>();

    public DnevnaZarada() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }
        Service service = new Service();
        rezultat = service.getProsjekDnevneZarade();
        buduci = service.PredvidanjeZaradeUBuducnosti();

        ListView<Object[]> listView = new ListView<Object[]>("rezultat", new PropertyModel<>(this, "rezultat")) {
            @Override
            protected void populateItem(ListItem<Object[]> item) {
                Object[] red = item.getModelObject();
                LocalDate datum = (LocalDate) red[0];
                Double prosjekProslih = (Double) red[1];
                Double iznosOve = (Double) red[2];
                Double predvidanje = (Double) red[3];
                Double razlika = (Double) red[4];

                String datumStr = datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
                String iznosOveStr = String.format("%.2f €", iznosOve);
                String prosjekProslihStr = String.format("%.2f €", prosjekProslih);
                String predvidanjeStr = String.format("%.2f €", predvidanje);
                String razlikaStr = String.format("%.2f €", razlika);

                item.add(new Label("datum", datumStr));
                item.add(new Label("prosle", prosjekProslihStr));
                item.add(new Label("iznosOve", iznosOveStr));
                item.add(new Label("predvidanje", predvidanjeStr));
                Label razlikaLabel = new Label("razlika", razlikaStr);
                String color = (razlika != null && razlika < 0) ? "red" : "green";
                razlikaLabel.add(new AttributeModifier("style", "color:" + color));
                item.add(razlikaLabel);
            }
        };

        add(listView);
        ListView<Object[]> buduciListView = new ListView<Object[]>("buduci", new PropertyModel<>(this, "buduci")) {
            @Override
            protected void populateItem(ListItem<Object[]> item) {
                Object[] red = item.getModelObject();
                LocalDate datum = (LocalDate) red[0];
                Integer brojLjudi = (Integer) red[2];
                Double predvidanje = (Double) red[1];

                String datumStr = datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
                String brojLjudiStr = String.format("%d", brojLjudi);
                String predvidanjeStr = String.format("%.2f €", predvidanje);

                item.add(new Label("datumBuduci", datumStr));
                item.add(new Label("brojLjudi", brojLjudiStr));
                item.add(new Label("predvidanjeBuduci", predvidanjeStr));
                
            }
        };

        add(buduciListView);
    }
}

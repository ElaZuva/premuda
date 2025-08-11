package com.premuda;

import com.premuda.Service;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class StatistikaPage extends WebPage {

    public StatistikaPage() {
        Service service = new Service();
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        List<Object[]> rezervacijePoDanu = service.getRezervacijaCountByDayOfWeek();

        add(new ListView<Object[]>("rowsRezervacije", rezervacijePoDanu) {
            @Override
            protected void populateItem(ListItem<Object[]> item) {
                Object[] row = item.getModelObject();

                DayOfWeek dayOfWeek = (DayOfWeek) row[0];
                Long count = (Long) row[1];

                item.add(new Label("dayRezervacije", dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())));
                item.add(new Label("countRezervacije", count.toString()));
            }
        });

        List<Object[]> ljudiPoDanu = service.getProsjecanBrojLjudiPoDanuUTjednu();

        add(new ListView<Object[]>("rowsLjudi", ljudiPoDanu) {
            @Override
            protected void populateItem(ListItem<Object[]> item) {
                Object[] row = item.getModelObject();

                DayOfWeek dayOfWeek = (DayOfWeek) row[0];
                Double count = (Double) row[1];

                item.add(new Label("dayLjudi", dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())));
                item.add(new Label("countLjudi", String.format("%.2f", count)));
            }
        });
    }
}

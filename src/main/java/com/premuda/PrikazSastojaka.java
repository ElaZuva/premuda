package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.validation.validator.RangeValidator;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrikazSastojaka extends WebPage {
    private static final long serialVersionUID = 1L;

    private final Service service = new Service();

    public PrikazSastojaka() {
        super();
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }
        add(new FeedbackPanel("feedback"));


        add(new Link<Void>("noviSastojak") {
            @Override
            public void onClick() {
                setResponsePage(DodajSastojak.class);
            }
        });

        List<Sastojak> sastojci = service.getAllSastojci();

        ListView<Sastojak> sastojciListView = new ListView<Sastojak>("sastojci", sastojci) {
            @Override
            protected void populateItem(ListItem<Sastojak> item) {
                Sastojak sastojak = item.getModelObject();

                item.add(new Label("imeSastojka", sastojak.getImeSastojka()));
                item.add(new Label("unit", sastojak.getUnit()));
                item.add(new Label("trenutnoStanje", String.valueOf(sastojak.getStanje())));
                item.add(new Label("minimalnoStanje", String.valueOf(service.minimalnoStanje(sastojak))));

                Form<Sastojak> formKolicina = new Form<>("promijeniKolicinuForm", new CompoundPropertyModel<>(sastojak));
                item.add(formKolicina);

                // polje s validatorom koji ne dozvoljava vrijednosti manje od 0
                TextField<Integer> stanjeField = new TextField<>("stanje", Integer.class);
                stanjeField.setRequired(true);
                stanjeField.add(RangeValidator.minimum(0)); 

                formKolicina.add(stanjeField);

                formKolicina.add(new Button("submitKolicina") {
                    @Override
                    public void onSubmit() {
                        service.updateSastojak(sastojak);
                        setResponsePage(PrikazSastojaka.class);
                    }
                });

                item.add(new Link<Void>("ukloniSastojak") {
                    @Override
                    public void onClick() {
                        service.deleteSastojak(sastojak.getId());
                        setResponsePage(PrikazSastojaka.class);
                    }
                });
            }
        };

        add(sastojciListView);
    }
}

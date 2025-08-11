package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class DodajStol extends WebPage {

    private Stol stol = new Stol();
    private Service service = new Service();

    public DodajStol() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        Form<Stol> form = new Form<>("dodajStolForm", new CompoundPropertyModel<>(stol));

        form.add(new TextField<>("imeStola").setRequired(true));
        form.add(new TextField<>("brojLjudi").setRequired(true));

        RadioGroup<Boolean> pozicijaGroup = new RadioGroup<>("pozicija");
        pozicijaGroup.add(new Radio<>("unutra", Model.of(true)).setLabel(Model.of("Unutra")));
        pozicijaGroup.add(new Radio<>("vani", Model.of(false)).setLabel(Model.of("Vani")));
        form.add(pozicijaGroup);

        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                stol.setSakriven(false);
                service.dodajStol(stol);
                setResponsePage(PregledStolova.class); // Preusmjeri na stranicu sa svim stolovima
            }
        });

        add(form);
    }    

}

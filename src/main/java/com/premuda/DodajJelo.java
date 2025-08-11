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

//Dodaje novo jelo u bazu podataka

public class DodajJelo extends WebPage {

    private Jelovnik jelo = new Jelovnik();
    private Service service = new Service();

    public DodajJelo() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        // Forma za unos novog stola
        Form<Jelovnik> form = new Form<>("dodajJeloForm", new CompoundPropertyModel<>(jelo));

        form.add(new TextField<>("imeArtikla").setRequired(true));
        form.add(new TextField<>("kategorija").setRequired(true));
        form.add(new TextField<>("potkategorija").setRequired(true));
        form.add(new TextField<>("unit").setRequired(false));
        form.add(new TextField<>("cijena").setRequired(true));

        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                jelo.setSakriven(false);
                jelo.setKolicina(0);
                service.dodajJelo(jelo);
                setResponsePage(PrikazCilogJelovnika.class); 
            }
        });

        add(form);
    }    

}

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

//Dodaje novi sastojak u bazu podataka

public class DodajSastojak extends WebPage {

    private Sastojak sastojak = new Sastojak();
    private Service service = new Service();

    public DodajSastojak() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        // Forma za unos novog stola
        Form<Sastojak> form = new Form<>("dodajSastojakForm", new CompoundPropertyModel<>(sastojak));

        form.add(new TextField<>("imeSastojka").setRequired(true));
        form.add(new TextField<>("unit").setRequired(false));
        form.add(new TextField<>("stanje").setRequired(true));

        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                sastojak.setSakriven(false);
                service.dodajSastojak(sastojak);
                setResponsePage(PrikazSastojaka.class); 
            }
        });

        add(form);
    }    

}

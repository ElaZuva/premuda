package com.premuda;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class StatistikaJelaPage extends WebPage {

    private static final long serialVersionUID = 1L;

    private Long jeloId;
    private String datumStr; 
    private int prodanoJela; 
    private double prosjekProdajeNaDan;
    private Service service = new Service();

    public StatistikaJelaPage(PageParameters parameters) {
        super(parameters);
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        jeloId = parameters.get("jeloId").toOptionalLong();
        datumStr = parameters.get("datum").toOptionalString();

        if (datumStr == null) {
            datumStr = "";
        }

        Form<Void> form = new Form<Void>("form") {
            @Override
            protected void onSubmit() {
                super.onSubmit();
                try {
                    LocalDate parsedDate = LocalDate.parse(datumStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    prodanoJela = service.prodanoJelaUDanu(jeloId, parsedDate);
                    prosjekProdajeNaDan = service.prosjekProdajeJelaUDanu(jeloId, parsedDate);
                } catch (DateTimeParseException e) {
                    prodanoJela = 0;
                }
            }
        };
        add(form);

        TextField<String> datumField = new TextField<>("datum", new PropertyModel<>(this, "datumStr"));
        form.add(datumField);

        form.add(new Button("submit"));

        Label danasLabel = new Label("danasRezultat", new PropertyModel<>(this, "prodanoJela"));
        add(danasLabel);
        Label prosjekDanaLabel = new Label("prosjekDanaRezultat", new PropertyModel<>(this, "prosjekProdajeNaDan"));
        add(prosjekDanaLabel);
    }
}

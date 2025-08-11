package com.premuda;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.convert.IConverter;

    
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.markup.html.form.TextField;
import java.time.LocalDate;
import java.time.LocalTime;

public class NovaRezervacija extends WebPage {

    private Rezervacija rezervacija = new Rezervacija();
    private Service service = new Service();

    public NovaRezervacija() {
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        Form<Rezervacija> form = new Form<>("novaRezervacija", new CompoundPropertyModel<>(rezervacija));

        form.add(new TextField<>("imeRezervacije").setRequired(true));

        form.add(new TextField<>("brojLjudi", Integer.class).setRequired(true));

        TextField<LocalDate> dateField = new TextField<LocalDate>("datum", LocalDate.class) {
            private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

            @Override
            @SuppressWarnings("unchecked")
            public <C> IConverter<C> getConverter(Class<C> type) {
                if (LocalDate.class.equals(type)) {
                    return (IConverter<C>) new IConverter<LocalDate>() {
                        @Override
                        public LocalDate convertToObject(String value, Locale locale) {
                            return LocalDate.parse(value, fmt);
                        }

                        @Override
                        public String convertToString(LocalDate date, Locale locale) {
                            return date.format(fmt);
                        }
                    };
                }
                return super.getConverter(type);
            }
        };
        dateField.setRequired(true);
        dateField.add(AttributeModifier.replace("type", "date"));
        form.add(dateField);


        List<String> vrijemeOpcije = IntStream.rangeClosed(17, 22)
                .mapToObj(sat -> String.format("%02d:00", sat))
                .collect(Collectors.toList());
        DropDownChoice<String> vrijemeChoice = new DropDownChoice<>("vrijeme", vrijemeOpcije);
        vrijemeChoice.setRequired(true);
        form.add(vrijemeChoice);

        form.add(new Button("rezerviraj") {
            @Override
            public void onSubmit() {
                super.onSubmit();

                try {
                    List<Stol> slobodniStolovi = service.getSlobodneStolove(rezervacija);

                    Stol stol = service.nadiPrviStol(slobodniStolovi, rezervacija);
                    if (stol == null) {
                        error("Nema slobodnih stolova za odabrane parametre.");
                        return;
                    }

                    rezervacija.setStol(stol);
                    service.dodajRezervaciju(rezervacija);

                    setResponsePage(PregledRezervacija.class);
                } catch (Exception e) {
                    error("Greška pri stvaranju rezervacije: " + e.getMessage());
                }
            }
        });

        add(form);
    }

    
}

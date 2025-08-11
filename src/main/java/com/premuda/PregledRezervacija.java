package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class PregledRezervacija extends WebPage {
    private static final long serialVersionUID = 1L;

    private final Service service = new Service();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public PregledRezervacija() {
        super();

        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        add(new Link<Void>("novaRezervacija") {
            @Override
            public void onClick() {
                setResponsePage(NovaRezervacija.class);
            }
        });

        add(new Link<Void>("danas") {
            @Override
            public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "dan");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });

        add(new Link<Void>("sutra") {
            @Override
            public void onClick() {
                LocalDate sutra = LocalDate.now().plusDays(1);
                PageParameters pp = new PageParameters();
                pp.add("datum", sutra.toString());
                pp.add("period", "dan");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });

        add(new Link<Void>("prosle") {
            @Override
            public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "prosle");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });

        add(new Link<Void>("buduce") {
            @Override
            public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "buduce");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });

        add(new Link<Void>("sve") {
            @Override
            public void onClick() {
                setResponsePage(PregledRezervacija.class);
            }
        });

        List<Rezervacija> sveRez = service.getAllRezervacije();
        add(buildListView(sveRez));
    }

   
    public PregledRezervacija(PageParameters params) {
        super(params);

        add(new HeaderPanel("headerPanel"));

        add(new Link<Void>("novaRezervacija") {
            @Override public void onClick() { setResponsePage(NovaRezervacija.class); }
        });
        add(new Link<Void>("danas") {
            @Override public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "dan");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });
        add(new Link<Void>("sutra") {
            @Override public void onClick() {
                LocalDate sutra = LocalDate.now().plusDays(1);
                PageParameters pp = new PageParameters();
                pp.add("datum", sutra.toString());
                pp.add("period", "dan");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });
        add(new Link<Void>("prosle") {
            @Override public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "prosle");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });
        add(new Link<Void>("buduce") {
            @Override public void onClick() {
                LocalDate danas = LocalDate.now();
                PageParameters pp = new PageParameters();
                pp.add("datum", danas.toString());
                pp.add("period", "buduce");
                setResponsePage(PregledRezervacija.class, pp);
            }
        });
        add(new Link<Void>("sve") {
            @Override public void onClick() {
                setResponsePage(PregledRezervacija.class);
            }
        });

        String datumString = params.get("datum").toOptionalString();
        String period      = params.get("period").toOptionalString();

        LocalDate datum;
        if (datumString == null || datumString.isEmpty()) {
            datum = LocalDate.now();
        } else {
            datum = LocalDate.parse(datumString);
        }

        List<Rezervacija> filtrirane = filtriraneRezervacije(period, datum);
        add(buildListView(filtrirane));
    }

   
    private ListView<Rezervacija> buildListView(List<Rezervacija> lista) {
        return new ListView<Rezervacija>("rezervacije", lista) {
            @Override
            protected void populateItem(ListItem<Rezervacija> item) {
                Rezervacija r = item.getModelObject();
                Stol stol = r.getStol();

                item.add(new Label("imeRezervacije", r.getImeRezervacije()));
                item.add(new Label("imeStola", stol != null ? stol.getImeStola() : "Nema"));
                item.add(new Label("brojLjudi", String.valueOf(r.getBrojLjudi())));
                item.add(new Label("pozicija",
                    (stol != null && stol.getPozicija()) ? "Unutra" : "Vani"));

                item.add(new Label("datum", r.getDatum().format(DATE_FORMATTER)));
                item.add(new Label("vrijeme", r.getVrijeme().format(TIME_FORMATTER)));

                item.add(new Link<Void>("dodajNarudzbu") {
                    @Override public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.add("rezervacijaId", r.getId());
                        setResponsePage(PrikazJelovnika.class, pp);
                    }
                });
                item.add(new Link<Void>("prikaziNarudzbu") {
                    @Override public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.add("rezervacijaId", r.getId());
                        setResponsePage(PrikazNarudzbePage.class, pp);
                    }
                });
                item.add(new Link<Void>("ukloniNarudzbu") {
                    @Override public void onClick() {
                        service.deleteRezervacija(r.getId());
                        setResponsePage(PregledRezervacija.class);
                    }
                });
            }
        };
    }

    private List<Rezervacija> filtriraneRezervacije(String period, LocalDate datum) {
        if ("dan".equals(period)) {
            return service.getRezervacijePoDatumu(datum);
        } else if ("prosle".equals(period)) {
            return service.getRezervacijePrijeDatuma(datum);
        } else if ("buduce".equals(period)) {
            return service.getRezervacijePoslijeDatuma(datum);
        } else {
            return Collections.emptyList();
        }
    }
}

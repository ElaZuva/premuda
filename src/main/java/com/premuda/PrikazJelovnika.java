package com.premuda;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
public class PrikazJelovnika extends WebPage {

    private static final long serialVersionUID = 1L;

    private String odabranaKategorija = "predjelo"; 
    private List<Jelovnik> narudzba = new ArrayList<>();
    private Rezervacija rezervacija;

    private transient Service service = new Service();

    private ListView<Jelovnik> stavkeView;

    public PrikazJelovnika(PageParameters params) {
        super(params);

        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        Long rezervacijaId = params.get("rezervacijaId").toLongObject();
        this.rezervacija = service.findRezervacijaById(rezervacijaId);

        add(new Label("imeRezervacije", rezervacija != null ? rezervacija.getImeRezervacije() : "N/A"));
        add(new Label("brojLjudi", rezervacija != null ? String.valueOf(rezervacija.getBrojLjudi()) : "0"));
        add(new Link<Void>("predjeloLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "predjelo";
                updateJelovnik();
            }
        });

        add(new Link<Void>("glavnoJeloLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "glavno jelo";
                updateJelovnik();
            }
        });

        add(new Link<Void>("desertLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "desert";
                updateJelovnik();
            }
        });

        add(new Link<Void>("piceLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "pice";
                updateJelovnik();
            }
        });

        stavkeView = new ListView<Jelovnik>("jelovnik", new PropertyModel<>(this, "jelovnik")) {
            @Override
            protected void populateItem(ListItem<Jelovnik> item) {
                Jelovnik jelo = item.getModelObject();
                
                item.add(new Label("naziv", jelo.getImeArtikla()));
                item.add(new Label("cijena", String.valueOf(jelo.getCijena())));

                Label kolicinaLabel = new Label("kolicina", new PropertyModel<>(jelo, "kolicina"));
                kolicinaLabel.setOutputMarkupId(true);
                item.add(kolicinaLabel);

                item.add(new AjaxLink<Void>("smanjiKolicinu") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (jelo.getKolicina() > 0) {
                            jelo.setKolicina(jelo.getKolicina() - 1);
                            if (jelo.getKolicina() == 0) {
                                narudzba.remove(jelo);
                            }
                            target.add(kolicinaLabel);
                        }
                    }
                });

                item.add(new AjaxLink<Void>("povecajKolicinu") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        jelo.setKolicina(jelo.getKolicina() + 1);
                        if (!narudzba.contains(jelo)) {
                            narudzba.add(jelo);
                        }
                        target.add(kolicinaLabel);
                    }
                });
            }
        };
        stavkeView.setOutputMarkupId(true);
        add(stavkeView);

        Form<Void> form = new Form<>("form");
        add(form);
        form.add(new org.apache.wicket.markup.html.form.Button("zavrsiNarudzbu") {
            @Override
            public void onSubmit() {
                for (Jelovnik j : narudzba) {
                    if (j.getKolicina() > 0) {
                        Narudzba n = new Narudzba();
                        n.setRezervacija(rezervacija);  
                        n.setJelo(j);
                        n.setKolicina(j.getKolicina());
                        service.dodajNarudzba(n);
                    }
                }
                PageParameters pp = new PageParameters();
                pp.add("rezervacijaId", rezervacija.getId());
                setResponsePage(PrikazNarudzbePage.class, pp);
            }
        });

        updateJelovnik();
    }

    private void updateJelovnik() {
        List<Jelovnik> jelovnik = service.getStavkeKategorije(odabranaKategorija);
        stavkeView.setList(jelovnik);
    }

    public List<Jelovnik> getJelovnik() {
        return service.getStavkeKategorije(odabranaKategorija);
    }
}

package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.link.Link;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrikazNarudzbePage extends WebPage {

    private static final long serialVersionUID = 1L;

    private transient Service service = new Service();
    private int brojac = 0;

    public PrikazNarudzbePage() {

        super();

    }
    public PrikazNarudzbePage(PageParameters params) {
        super(params);
        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

        Long rezervacijaId = params.get("rezervacijaId").toLongObject();

        Rezervacija rezervacija = service.findRezervacijaById(rezervacijaId);

        List<Narudzba> narudzbe = service.findNarudzbeByRezervacijaId(rezervacijaId);

        double ukupnaCijena = service.ukupnaCijenaRezervacije(rezervacijaId);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        add(new Label("imeRezervacije", rezervacija != null ? rezervacija.getImeRezervacije() : "N/A"));
        add(new Label("brojLjudi", rezervacija != null ? String.valueOf(rezervacija.getBrojLjudi()) : "0"));
        add(new Label("datum", rezervacija != null ? rezervacija.getDatum().format(dateFormatter) : "-"));
        add(new Label("vrijeme", rezervacija != null ? rezervacija.getVrijeme().format(timeFormatter) : "-"));
        add(new Link<Void>("dodajNarudzbu") {
            @Override public void onClick() {
                PageParameters pp = new PageParameters();
                pp.add("rezervacijaId", rezervacijaId);
                setResponsePage(PrikazJelovnika.class, pp);
            }
        });

        ListView<Narudzba> narudzbeList = new ListView<Narudzba>("narudzbeList", narudzbe) {
            @Override
            protected void populateItem(ListItem<Narudzba> item) {
                Narudzba n = item.getModelObject();
                Jelovnik jelo = n.getJelo();
                
                item.add(new Label("nazivJela", jelo != null ? jelo.getImeArtikla() : "-"));
                Label kolicinaLabel = new Label("kolicina", new PropertyModel<>(n, "kolicina"));
                kolicinaLabel.setOutputMarkupId(true);
                item.add(kolicinaLabel);
                item.add(new AjaxLink<Void>("smanjiKolicinu") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (n.getKolicina() > 0) {
                            n.setKolicina(n.getKolicina() - 1);
                            target.add(kolicinaLabel);
                            brojac = brojac - 1;
                        }
                    }
                });

                item.add(new AjaxLink<Void>("povecajKolicinu") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if(service.dostupnoJela(jelo)>brojac){
                            n.setKolicina(n.getKolicina() + 1);
                            target.add(kolicinaLabel);
                            brojac = brojac + 1;
                        }
                        
                    }
                });
                Form<Narudzba> formNarudzba = new Form<>("promijeniKolicinuForm", new CompoundPropertyModel<>(n));
                item.add(formNarudzba);
                formNarudzba.add(new Button("submitKolicina"){
                    @Override
                    public void onSubmit(){
                        service.updateNarudzba(n, brojac);
                        PageParameters pp = new PageParameters().add("rezervacijaId", rezervacijaId);
                        setResponsePage(PrikazNarudzbePage.class, pp);
                    }
                });
                String cijenaString = jelo != null ? String.valueOf(jelo.getCijena()) : "0.0";
                item.add(new Label("cijena", cijenaString));

                double cijena = jelo != null ? jelo.getCijena() : 0.0;
                double ukupno = cijena * n.getKolicina();
                item.add(new Label("ukupno", String.valueOf(ukupno)));
            }
        };
        add(narudzbeList);

        add(new Label("cijenaNarudzbe", rezervacija != null ? ukupnaCijena : "N/A"));
    }
}

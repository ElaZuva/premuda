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
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
public class PrikazCilogJelovnika extends WebPage {

    private static final long serialVersionUID = 1L;

    private String odabranaKategorija = "predjelo"; 

    private transient Service service = new Service();

    private ListView<Jelovnik> stavkeView;

    public PrikazCilogJelovnika() {

        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }

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
                item.add(new Label("unit", jelo.getUnit()));
                item.add(new Label("cijena", String.valueOf(jelo.getCijena())));

                item.add(new Link<Void>("urediCijenu") {
                    @Override
                    public void onClick() {
                        PageParameters pp = new PageParameters();
                        pp.add("jeloId", jelo.getId());
                        
                        setResponsePage(UrediJelo.class, pp);
                    }
                });
                item.add(new Link<Void>("sakrijArtikl") {
                    @Override
                    public void onClick() {
                        jelo.setSakriven(true);
                        setResponsePage(PrikazCilogJelovnika.class);
                    }
                });
                
                
            }
        };
        stavkeView.setOutputMarkupId(true);
        add(stavkeView);

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

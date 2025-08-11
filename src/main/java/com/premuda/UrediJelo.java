package com.premuda;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
public class UrediJelo extends WebPage {

    private static final long serialVersionUID = 1L;

    private transient Service service = new Service();
    private Jelovnik jelo;
    private List<Sastojak> recept = new ArrayList<>();
    private Recept noviRecept = new Recept();

    public UrediJelo(PageParameters params) {
        super(params);

        if (MyAuthenticatedWebSession.get().getRoles().hasRole("VLASNIK")) {
            add(new HeaderPanel("headerPanel"));
        } else {
            add(new HeaderPanelKonobar("headerPanel"));
        }
        Long jeloId = params.get("jeloId").toLongObject();
        this.jelo = service.findJeloById(jeloId);

        Form<Jelovnik> form = new Form<>("promijeniJeloForm", new CompoundPropertyModel<>(jelo));
        add(form);

        form.add(new TextField<>("imeArtikla").setRequired(true));
        form.add(new TextField<>("kategorija").setRequired(true));
        form.add(new TextField<>("potkategorija").setRequired(true));
        form.add(new TextField<>("cijena").setRequired(true));
        form.add(new TextField<>("unit").setRequired(true));


        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                service.updateJelo(jelo.getId(), jelo.getImeArtikla(), jelo.getKategorija(), jelo.getPotkategorija(), jelo.getCijena(), jelo.getUnit());
                setResponsePage(PrikazCilogJelovnika.class);
            }
        });

        List<Recept> recept = service.findReceptByJeloId(jeloId);

        ListView<Recept> receptList = new ListView<Recept>("receptList", recept) {
            @Override
            protected void populateItem(ListItem<Recept> item) {
                Recept r = item.getModelObject();
                Sastojak sastojak = r.getSastojak();

                item.add(new Label("nazivSastojkaRecept", sastojak != null ? sastojak.getImeSastojka() : "-"));
                item.add(new Label("kolicinaRecept", r.getKolicina()));

                Form<Recept> formKolicina = new Form<>("promijeniKolicinuForm", new CompoundPropertyModel<>(r));
                item.add(formKolicina);
                formKolicina.add(new TextField<>("kolicina").setRequired(true));
                formKolicina.add(new Button("submitKolicina") {
                    @Override
                    public void onSubmit() {
                        service.updateRecept(r);
                        PageParameters pp = new PageParameters().add("jeloId", jeloId);
                        setResponsePage(UrediJelo.class, pp);
                    }
                });


            }
        };
        add(receptList);

        List<Sastojak> sastojak = service.getAllSastojci();

        ListView<Sastojak> sastojakList = new ListView<Sastojak>("sastojakList", sastojak){
            @Override
            protected void populateItem(ListItem<Sastojak> item){
                Sastojak s = item.getModelObject();
                item.add(new Label("nazivSastojka", s != null ? s.getImeSastojka() : "-"));
                item.add(new Label("unit", String.valueOf(s.getUnit())));
                item.add(new Link<Void>("dodajSastojak") {
                    @Override public void onClick() {
                        noviRecept.setSastojak(s);
                        noviRecept.setJelo(jelo);
                        noviRecept.setKolicina(0.0);
                        service.dodajRecept(noviRecept);
                        PageParameters pp = new PageParameters();
                        pp.add("jeloId", jelo.getId());
                        setResponsePage(UrediJelo.class, pp);
                    }
                });
            }
        };
        add(sastojakList);

    }



}

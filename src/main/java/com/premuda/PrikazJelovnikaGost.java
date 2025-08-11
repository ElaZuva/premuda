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
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import java.util.ArrayList;
import java.util.List;
public class PrikazJelovnikaGost extends WebPage {

    private static final long serialVersionUID = 1L;

    private String odabranaKategorija = "predjelo"; 

    private transient Service service = new Service();

    private ListView<Jelovnik> stavkeView;

    public PrikazJelovnikaGost() {
        
        
        
        
        add(new HeaderPanelPocetni("headerPanel"));

        Link<Void> predjeloLink = new Link<Void>("predjeloLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "predjelo";
                updateJelovnik();
            }
        };
        predjeloLink.add(new Image("predjeloImg", new PackageResourceReference(PrikazJelovnikaGost.class, "jelovnik.jpeg")));
        add(predjeloLink);

        Link<Void> glavnoJeloLink = new Link<Void>("glavnoJeloLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "glavno jelo";
                updateJelovnik();
            }
        };
        glavnoJeloLink.add(new Image("glavnoJeloImg", new PackageResourceReference(PrikazJelovnikaGost.class, "hobotnica.jpeg")));
        add(glavnoJeloLink);

        Link<Void> desertLink = new Link<Void>("desertLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "desert";
                updateJelovnik();
            }
        };
        desertLink.add(new Image("desertImg", new PackageResourceReference(PrikazJelovnikaGost.class, "jelovnik.jpeg")));
        add(desertLink);

        Link<Void> piceLink = new Link<Void>("piceLink") {
            @Override
            public void onClick() {
                odabranaKategorija = "pice";
                updateJelovnik();
            }
        };
        piceLink.add(new Image("piceImg", new PackageResourceReference(PrikazJelovnikaGost.class, "informacije.jpeg")));
        add(piceLink);

        stavkeView = new ListView<Jelovnik>("jelovnik", new PropertyModel<>(this, "jelovnik")) {
            @Override
            protected void populateItem(ListItem<Jelovnik> item) {
                Jelovnik jelo = item.getModelObject();
                
                item.add(new Label("naziv", jelo.getImeArtikla()));
                item.add(new Label("unit", jelo.getUnit()));
                item.add(new Label("cijena", String.valueOf(jelo.getCijena())));

                
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

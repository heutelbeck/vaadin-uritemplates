package org.apples.views;

import java.util.Map;

import org.heutelbeck.vaadin.navigation.UriTemplateResolver;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Core extends VerticalLayout implements View {

    public static final String NAME = "apples/{appleId}/cores/{coreId}";
    private static final UriTemplateResolver RESOLVER = new UriTemplateResolver(
            NAME);

    Label appleId = new Label();
    Label coreId = new Label();

    public Core() {
        addComponent(new Label(
                "This views shows an individual core in a specific apple  Fragment: '"
                        + UI.getCurrent().getPage().getUriFragment() + "'"));
        addComponent(appleId);
        appleId.setCaption("Apple ID:");
        addComponent(coreId);
        coreId.setCaption("Core ID:");
    }

    @Override
    public void enter(
            ViewChangeEvent event
    ) {
        Map<String, String> parameters = RESOLVER
                .resolveViewName(event.getViewName());
        appleId.setValue(parameters.get("appleId"));
        coreId.setValue(parameters.get("coreId"));
    }

}

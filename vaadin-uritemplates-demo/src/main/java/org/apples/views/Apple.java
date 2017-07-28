package org.apples.views;

import java.util.Map;

import org.heutelbeck.vaadin.navigation.UriTemplateResolver;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Apple extends VerticalLayout implements View {

    public static final String NAME = "apples/{appleId}";
    private static final UriTemplateResolver RESOLVER = new UriTemplateResolver(
            NAME);
    Label appleId = new Label();

    public Apple() {
        addComponent(new Label(
                "This views shows the apple with a specific identifier  Fragment: '"
                        + UI.getCurrent().getPage().getUriFragment() + "'"));
        addComponent(appleId);
        appleId.setCaption("Apple ID:");
    }

    @Override
    public void enter(
            ViewChangeEvent event
    ) {
        Map<String, String> parameters = RESOLVER
                .resolveViewName(event.getViewName());
        appleId.setValue(parameters.get("appleId"));
    }

}

package org.apples.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Apples extends VerticalLayout implements View {

    public static final String NAME = "apples";

    public Apples() {
        addComponent(new Label("This view lists all the apples. Fragment: '"
                + UI.getCurrent().getPage().getUriFragment() + "'"));
    }

    @Override
    public void enter(
            ViewChangeEvent event
    ) {
        // NOP
    }

}

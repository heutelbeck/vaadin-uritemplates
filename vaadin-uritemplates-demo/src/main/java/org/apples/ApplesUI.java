package org.apples;

import org.apples.views.Apple;
import org.apples.views.Apples;
import org.apples.views.Core;
import org.apples.views.Cores;
import org.apples.views.Default;
import org.heutelbeck.vaadin.navigation.UriTemplateNavigator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI
@SuppressWarnings("serial")
public class ApplesUI extends UI {

    UriTemplateNavigator navigator;

    Panel viewDisplay;

    @Override
    protected void init(
            VaadinRequest request
    ) {

        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        setContent(root);

        final CssLayout navigationBar = new CssLayout();
        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        navigationBar.addComponent(
                createNavigationButton("List all apples", Apples.NAME));
        navigationBar.addComponent(
                createNavigationButton("Show one apple", "apples/12345"));
        navigationBar.addComponent(createNavigationButton("List cores of apple",
                "apples/123sf45Xd5/cores"));
        navigationBar.addComponent(createNavigationButton(
                "Show one core of apple", "apples/1124621Xd5/cores/382"));

        root.addComponent(navigationBar);

        viewDisplay = new Panel();
        viewDisplay.setSizeFull();
        root.addComponent(viewDisplay);
        root.setExpandRatio(viewDisplay, 1.0f);

        navigator = new UriTemplateNavigator(this, viewDisplay);

        navigator.addView(Default.NAME, new Default());
        navigator.addView(Apple.NAME, new Apple());
        navigator.addView(Apples.NAME, new Apples());
        navigator.addView(Core.NAME, new Core());
        navigator.addView(Cores.NAME, new Cores());
    }

    private Button createNavigationButton(
            String caption,
            final String viewPath
    ) {
        Button button = new Button(caption);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(
                event -> getUI().getNavigator().navigateTo(viewPath));
        return button;
    }

}

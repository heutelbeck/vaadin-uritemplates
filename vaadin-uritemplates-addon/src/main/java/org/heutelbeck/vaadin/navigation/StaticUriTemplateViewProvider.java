package org.heutelbeck.vaadin.navigation;

import com.vaadin.navigator.View;

@SuppressWarnings("serial")
public class StaticUriTemplateViewProvider
        extends AbstractUriTemplateViewProvider {

    private final View view;

    /**
     * Create a new view provider which creates new view instances based on a view class.
     *
     * @param viewName
     *            name of the views to create (not null)
     * @param view
     *            the view is requested (not null)
     */
    public StaticUriTemplateViewProvider(
            String viewName,
            View view
    ) {
        super(viewName);
        if (null == view) {
            throw new IllegalArgumentException("View should not be null");
        }
        this.view = view;
    }

    @Override
    public View getView(
            String viewName
    ) {
        if (getViewName().equals(viewName)) {
            return view;
        }
        return null;
    }

}

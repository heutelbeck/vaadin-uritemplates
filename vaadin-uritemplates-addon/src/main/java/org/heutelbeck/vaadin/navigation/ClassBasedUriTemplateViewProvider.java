package org.heutelbeck.vaadin.navigation;

import com.vaadin.navigator.View;

@SuppressWarnings("serial")
public class ClassBasedUriTemplateViewProvider
        extends AbstractUriTemplateViewProvider {

    private final Class<? extends View> viewClass;

    /**
     * Create a new view provider which creates new view instances based on a view class.
     *
     * @param viewName
     *            name of the views to create (not null)
     * @param viewClass
     *            class to instantiate when a view is requested (not null)
     */
    public ClassBasedUriTemplateViewProvider(
            String viewName,
            Class<? extends View> viewClass
    ) {
        super(viewName);
        if (null == viewClass) {
            throw new IllegalArgumentException("View class should not be null");
        }
        this.viewClass = viewClass;
    }

    @Override
    public View getView(
            String viewName
    ) {
        if (getViewName().equals(viewName)) {
            try {
                return viewClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Get the view class for this provider.
     *
     * @return {@link View} class
     */
    public Class<? extends View> getViewClass() {
        return viewClass;
    }

}

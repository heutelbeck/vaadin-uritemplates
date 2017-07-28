package org.heutelbeck.vaadin.navigation;

import org.springframework.web.util.UriTemplate;

@SuppressWarnings("serial")
public abstract class AbstractUriTemplateViewProvider
        implements UriTemplateViewProvider {

    private final String viewName;
    private UriTemplate template;
    private int pathLength;

    protected AbstractUriTemplateViewProvider(
            String viewName
    ) {
        if (null == viewName) {
            throw new IllegalArgumentException("View name should not be null");
        }
        this.viewName = viewName;
        pathLength = viewName.split("/").length;
        if (!viewName.isEmpty()) {
            template = new UriTemplate(viewName);
        }
    }

    @Override
    public String getViewName(
            String viewAndParameters
    ) {
        // look for exact match, pattern match, and lastly for prefix match
        if (viewName.equals(viewAndParameters)
                || (!viewName.isEmpty() && template.matches(viewAndParameters))
                || viewAndParameters.startsWith(viewName + "/")) {
            return viewName;
        }
        return null;
    }

    /**
     * Get the view name for this provider.
     *
     * @return view name for this provider
     */
    public String getViewName() {
        return viewName;
    }

    @Override
    public int getPathLength() {
        return pathLength;
    }

}

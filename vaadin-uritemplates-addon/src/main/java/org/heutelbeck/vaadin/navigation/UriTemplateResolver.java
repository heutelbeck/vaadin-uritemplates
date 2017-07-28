package org.heutelbeck.vaadin.navigation;

import java.util.Map;

import org.springframework.web.util.UriTemplate;

public class UriTemplateResolver {
    private final UriTemplate template;

    public UriTemplateResolver(
            String viewName
    ) {
        if (viewName == null || viewName.isEmpty()) {
            throw new IllegalArgumentException(
                    "template may not be null or empty.");
        }
        template = new UriTemplate(viewName);
    }

    public Map<String, String> resolveViewName(
            String viewName
    ) {
        return template.match(viewName);
    }
}

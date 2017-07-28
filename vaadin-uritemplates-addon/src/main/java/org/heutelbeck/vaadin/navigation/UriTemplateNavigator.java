package org.heutelbeck.vaadin.navigation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class UriTemplateNavigator extends Navigator {

    private List<UriTemplateViewProvider> providers = new LinkedList<UriTemplateViewProvider>();
    private ViewProvider errorProvider;
    private String currentNavigationState = null;

    /**
     * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
     * UI and replacing the contents of a {@link ComponentContainer} with the active view.
     * <p>
     * All components of the container are removed each time before adding the active {@link View}. Views must implement
     * {@link Component} when using this constructor.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()}Â if a navigator was created. If at a later point
     * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
     * called to ensure the current view matches the navigation state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param container
     *            The ComponentContainer whose contents should be replaced with the active view on view change
     */
    public UriTemplateNavigator(
            UI ui,
            ComponentContainer container
    ) {
        super(ui, new ComponentContainerViewDisplay(container));
    }

    /**
     * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
     * UI and replacing the contents of a {@link SingleComponentContainer} with the active view.
     * <p>
     * Views must implement {@link Component} when using this constructor.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()}Â if a navigator was created. If at a later point
     * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
     * called to ensure the current view matches the navigation state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param container
     *            The SingleComponentContainer whose contents should be replaced with the active view on view change
     */
    public UriTemplateNavigator(
            UI ui,
            SingleComponentContainer container
    ) {
        super(ui, new SingleComponentContainerViewDisplay(container));
    }

    /**
     * Creates a navigator that is tracking the active view using URI fragments of the {@link Page} containing the given
     * UI.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()}Â if a navigator was created. If at a later point
     * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
     * called to ensure the current view matches the navigation state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param display
     *            The ViewDisplay used to display the views.
     */
    public UriTemplateNavigator(
            UI ui,
            ViewDisplay display
    ) {
        super(ui, new UriFragmentManager(ui.getPage()), display);
    }

    /**
     * Creates a navigator.
     * <p>
     * When a custom navigation state manager is not needed, use one of the other constructors which use a URI fragment
     * based state manager.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()}Â if a navigator was created. If at a later point
     * changes are made to the navigator, {@code navigator.navigateTo(navigator.getState())} may need to be explicitly
     * called to ensure the current view matches the navigation state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param stateManager
     *            The NavigationStateManager keeping track of the active view and enabling bookmarking and direct
     *            navigation or null to use the default implementation
     * @param display
     *            The ViewDisplay used to display the views handled by this navigator
     */
    public UriTemplateNavigator(
            UI ui,
            NavigationStateManager stateManager,
            ViewDisplay display
    ) {
        init(ui, stateManager, display);
    }

    /**
     * Navigates to a view and initialize the view with given parameters.
     * <p>
     * The view string consists of a view name optionally followed by a slash and a parameters part that is passed as-is
     * to the view. ViewProviders are used to find and create the correct type of view.
     * <p>
     * If multiple providers return a matching view, the view with the longest name is selected. This way, e.g.
     * hierarchies of subviews can be registered like "admin/", "admin/users", "admin/settings" and the longest match is
     * used.
     * <p>
     * If the view being deactivated indicates it wants a confirmation for the navigation operation, the user is asked
     * for the confirmation.
     * <p>
     * Registered {@link ViewChangeListener}s are called upon successful view change.
     *
     * @param navigationState
     *            view name and parameters
     *
     * @throws IllegalArgumentException
     *             if <code>navigationState</code> does not map to a known view and no error view is registered
     */
    @Override
    public void navigateTo(
            String navigationState
    ) {
        ViewProvider longestViewNameProvider = getViewProvider(navigationState);
        String longestViewName = longestViewNameProvider == null ? null
                : longestViewNameProvider.getViewName(navigationState);
        View viewWithLongestName = null;

        if (longestViewName != null) {
            viewWithLongestName = longestViewNameProvider
                    .getView(longestViewName);
        }

        if (viewWithLongestName == null && errorProvider != null) {
            longestViewName = errorProvider.getViewName(navigationState);
            viewWithLongestName = errorProvider.getView(longestViewName);
        }

        if (viewWithLongestName == null) {
            throw new IllegalArgumentException(
                    "Trying to navigate to an unknown state '" + navigationState
                            + "' and an error view provider not present");
        }
        //
        // String parameters = "";
        // if (navigationState.length() > longestViewName.length() + 1) {
        // parameters = navigationState
        // .substring(longestViewName.length() + 1);
        // } else
        //
        if (navigationState.endsWith("/")) {
            navigationState = navigationState.substring(0,
                    navigationState.length() - 1);
        }
        if (getCurrentView() == null
                || !SharedUtil.equals(getCurrentView(), viewWithLongestName)
                || !SharedUtil.equals(currentNavigationState,
                        navigationState)) {
            navigateTo(viewWithLongestName, navigationState, "");
        } else {
            updateNavigationState(new ViewChangeEvent(this, getCurrentView(),
                    viewWithLongestName, navigationState, ""));
        }
    }

    /**
     * Registers a static, pre-initialized view instance for a view name.
     * <p>
     * Registering another view with a name that is already registered overwrites the old registration of the same type.
     * <p>
     * Note that a view should not be shared between UIs (for instance, it should not be a static field in a UI
     * subclass).
     *
     * @param viewName
     *            String that identifies a view (not null nor empty string)
     * @param view
     *            {@link View} instance (not null)
     */
    @Override
    public void addView(
            String viewName,
            View view
    ) {

        // Check parameters
        if (viewName == null || view == null) {
            throw new IllegalArgumentException(
                    "view and viewName must be non-null");
        }

        removeView(viewName);
        addProvider(new StaticUriTemplateViewProvider(viewName, view));
    }

    /**
     * Registers a view class for a view name.
     * <p>
     * Registering another view with a name that is already registered overwrites the old registration of the same type.
     * <p>
     * A new view instance is created every time a view is requested.
     *
     * @param viewName
     *            String that identifies a view (not null nor empty string)
     * @param viewClass
     *            {@link View} class to instantiate when a view is requested (not null)
     */
    @Override
    public void addView(
            String viewName,
            Class<? extends View> viewClass
            ) {
        // Check parameters
        if (viewName == null || viewClass == null) {
            throw new IllegalArgumentException(
                    "view and viewClass must be non-null");
        }

        removeView(viewName);
        addProvider(new ClassBasedUriTemplateViewProvider(viewName, viewClass));
    }

    /**
     * Get view provider that handles the given {@code state}.
     *
     * @param state
     *            state string
     * @return suitable provider
     */
    @Override
    protected ViewProvider getViewProvider(
            String state
        ) {
        String longestViewName = null;
        int longestPath = 0;
        UriTemplateViewProvider longestPathProvider = null;
        for (UriTemplateViewProvider provider : providers) {
            String viewName = provider.getViewName(state);
            if (null != viewName && (longestViewName == null
                    || provider.getPathLength() > longestPath)) {
                longestViewName = viewName;
                longestPathProvider = provider;
                longestPath = provider.getPathLength();
            }
        }
        return longestPathProvider;
    }

    /**
     * Removes a view from navigator.
     * <p>
     * This method only applies to views registered using {@link #addView(String, View)} or
     * {@link #addView(String, Class)}.
     *
     * @param viewName
     *            name of the view to remove
     */
    @Override
    public void removeView(
            String viewName
        ) {
        Iterator<UriTemplateViewProvider> it = providers.iterator();
        while (it.hasNext()) {
            ViewProvider provider = it.next();
            if (provider instanceof StaticUriTemplateViewProvider) {
                StaticUriTemplateViewProvider staticProvider = (StaticUriTemplateViewProvider) provider;
                if (staticProvider.getViewName().equals(viewName)) {
                    it.remove();
                }
            } else if (provider instanceof ClassBasedUriTemplateViewProvider) {
                ClassBasedUriTemplateViewProvider classBasedProvider = (ClassBasedUriTemplateViewProvider) provider;
                if (classBasedProvider.getViewName().equals(viewName)) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Registers a view provider (factory).
     * <p>
     * Providers are called in order of registration until one that can handle the requested view name is found.
     *
     * @param provider
     *            provider to register, not <code>null</code>
     * @throws IllegalArgumentException
     *             if the provided view provider is <code>null</code>
     */
    @Override
    public void addProvider(
            ViewProvider provider
        ) {
        if (provider == null
                || !(provider instanceof UriTemplateViewProvider)) {
            throw new IllegalArgumentException(
                    "Cannot add a null or non-UriTemplateViewProvider view provider");
        }
        providers.add((UriTemplateViewProvider) provider);
    }

    /**
     * Unregister a view provider (factory).
     *
     * @param provider
     *            provider to unregister
     */
    @Override
    public void removeProvider(
            ViewProvider provider
        ) {
        providers.remove(provider);
    }

    /**
     * Registers a view provider that is queried for a view when no other view matches the navigation state. An error
     * view provider should match any navigation state, but could return different views for different states. Its
     * <code>getViewName(String navigationState)</code> should return <code>navigationState</code>.
     *
     * @param provider
     *            the view provider
     */
    @Override
    public void setErrorProvider(
            ViewProvider provider
        ) {
        errorProvider = provider;
    }

    /**
     * Revert the changes to the navigation state. When navigation fails, this method can be called by
     * {@link #navigateTo(View, String, String)} to revert the URL fragment to point to the previous view to which
     * navigation succeeded.
     *
     * This method should only be called by {@link #navigateTo(View, String, String)}. Normally it should not be
     * overridden, but can be by frameworks that need to hook into view change cancellations of this type.
     *
     * @since 7.6
     */
    @Override
    protected void revertNavigation() {
        if (currentNavigationState != null) {
            getStateManager().setState(currentNavigationState);
        }
    }

    /**
     * Update the internal state of the navigator (parameters, previous successful URL fragment navigated to) when
     * navigation succeeds.
     *
     * Normally this method should not be overridden nor called directly from application code, but it can be called by
     * a custom implementation of {@link #navigateTo(View, String, String)}.
     *
     * @since 7.6
     * @param event
     *            a view change event with details of the change
     */
    @Override
    protected void updateNavigationState(
            ViewChangeEvent event
        ) {
        String viewName = event.getViewName();
        String parameters = event.getParameters();
        if (null != viewName && getStateManager() != null) {
            String navigationState = viewName;
            if (!parameters.isEmpty()) {
                navigationState += "/" + parameters;
            }
            if (!navigationState.equals(getStateManager().getState())) {
                getStateManager().setState(navigationState);
            }
            currentNavigationState = navigationState;
        }
    }
}

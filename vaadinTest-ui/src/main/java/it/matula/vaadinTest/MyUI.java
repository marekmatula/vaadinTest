package it.matula.vaadinTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import it.matula.vaadinTest.samples.MainScreen;
import it.matula.vaadinTest.samples.authentication.AccessControl;
import it.matula.vaadinTest.samples.authentication.BasicAccessControl;
import it.matula.vaadinTest.samples.authentication.LoginScreen;
import it.matula.vaadinTest.samples.authentication.LoginScreen.LoginListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.Responsive;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * 
 */
@Theme("mytheme")
@Widgetset("it.matula.vaadinTest.MyAppWidgetset")
public class MyUI extends UI {

    private AccessControl accessControl = new BasicAccessControl();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("My");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        setContent(new MainScreen(MyUI.this));
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static MyUI get() {
        return (MyUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            /*
             * Configure the viewport meta tags appropriately on mobile devices.
             * Instead of device based scaling (default), using responsive
             * layouts.
             * 
             * If using Vaadin TouchKit, this is done automatically and it is
             * sufficient to have an empty servlet class extending
             * TouchKitServlet.
             */
            getService().addSessionInitListener(
                    new ViewPortSessionInitListener());
        }
    }
}

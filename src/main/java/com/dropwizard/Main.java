package com.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.jersey.sessions.HttpSessionProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.Path;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.codahale.metrics.health.HealthCheck;

public class Main extends Application<DropwizardConfiguration> {
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "dropwizard-engine";
    }

    @Override
    public void initialize(Bootstrap<DropwizardConfiguration> bootstrap) {
        // bootstrap.addBundle(new AssetsBundle("/webapp", "/"));
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/content/"));
    }

    protected Class getSpringConfigurationClass() {
        return SpringConfiguration.class;
    }

    private void doSpringConfiguration(DropwizardConfiguration configuration, Environment environment) {
        // init Spring context
        // before we init the app context, we have to create a parent context
        // with all the config objects others rely on to get initialized
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

        parent.refresh();
        parent.getBeanFactory().registerSingleton("configuration", configuration);
        parent.registerShutdownHook();
        parent.start();

        // the real main app context has a link to the parent context
        ctx.setParent(parent);
        ctx.register(getSpringConfigurationClass());
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        // now that Spring is started, let's get all the beans that matter into
        // DropWizard

        // health checks
        Map<String, HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
        for (Map.Entry<String, HealthCheck> entry : healthChecks.entrySet()) {
            environment.healthChecks().register("template", entry.getValue());
        }

        // resources
        Map<String, Object> resources = ctx.getBeansWithAnnotation(Path.class);
        for (Map.Entry<String, Object> entry : resources.entrySet()) {
            environment.jersey().register(entry.getValue());
        }

        // last, but not least,let's link Spring to the embedded Jetty in
        // Dropwizard
        environment.servlets().addServletListeners(new SpringContextLoaderListener(ctx));
    }

    @Override
    public void run(DropwizardConfiguration configuration, Environment environment) {
        configuration.init();
        doSpringConfiguration(configuration, environment);
        environment.jersey().register(HttpSessionProvider.class);
        environment.servlets().setSessionHandler(new SessionHandler());

        // Enable Cross-origin resource sharing (CORS)
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");
    }

}
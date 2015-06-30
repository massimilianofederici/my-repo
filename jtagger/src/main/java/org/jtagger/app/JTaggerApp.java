package org.jtagger.app;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jtagger.app.configuration.JTaggerConfiguration;
import org.jtagger.app.healthchecks.DataSourceHealthCheck;
import org.jtagger.app.resources.LibraryResource;
import org.jtagger.core.sources.ImslpOrgDataSource;
import org.jtagger.core.sources.MetaDataSource;

public class JTaggerApp extends Application<JTaggerConfiguration> {

	public static void main(final String[] args) throws Exception {
		new JTaggerApp().run(args);
	}

	@Override
	public void run(final JTaggerConfiguration configuration,
			final Environment environment) throws Exception {
		final MetaDataSource imslp = new ImslpOrgDataSource();
		final DataSourceHealthCheck imslpHealthCheck = new DataSourceHealthCheck(
				imslp, configuration.getDataSources().getImslp().getPingId());
		environment.healthChecks().register(imslp.getClass().getSimpleName(),
				imslpHealthCheck);
		environment.jersey().register(new LibraryResource());
		configureCors(environment);
	}

	private void configureCors(final Environment environment) {
		final Dynamic filter = environment.servlets().addFilter("CORS",
				CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
				true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,
				"GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(
				CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders",
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}
}

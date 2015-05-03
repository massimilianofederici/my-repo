package org.jtagger.app;

import org.jtagger.app.configuration.JTaggerConfiguration;
import org.jtagger.app.healthchecks.DataSourceHealthCheck;
import org.jtagger.core.sources.ImslpOrgDataSource;
import org.jtagger.core.sources.MetaDataSource;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

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
	}

}

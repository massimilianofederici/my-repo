package org.jtagger.app.healthchecks;

import org.jtagger.core.sources.MetaDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

public class DataSourceHealthCheck extends HealthCheck {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MetaDataSource dataSource;

	private final String pingId;

	public DataSourceHealthCheck(final MetaDataSource dataSource,
			final String pingId) {
		this.dataSource = dataSource;
		this.pingId = pingId;
	}

	@Override
	protected Result check() throws Exception {
		logger.info("Checking connection with MetaDataSource {}", dataSource);
		try {
			dataSource.fetchMetaData(pingId);
			logger.info("Connection is OK");
			return Result.healthy();
		} catch (final Exception e) {
			logger.error("Connection is not available", e);
			return Result.unhealthy(e);
		}
	}

}

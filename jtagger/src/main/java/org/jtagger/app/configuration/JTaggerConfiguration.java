package org.jtagger.app.configuration;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JTaggerConfiguration extends Configuration {

	@NotNull
	@Valid
	private final MetaDataSourcesConfiguration dataSources;

	public JTaggerConfiguration(
			@JsonProperty("data-sources") final MetaDataSourcesConfiguration dataSources) {
		super();
		this.dataSources = dataSources;
	}

	public MetaDataSourcesConfiguration getDataSources() {
		return dataSources;
	}

}

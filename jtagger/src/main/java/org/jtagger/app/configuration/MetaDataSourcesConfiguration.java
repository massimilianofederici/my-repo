package org.jtagger.app.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaDataSourcesConfiguration {

	@NotNull
	@Valid
	private final MetaDataSourceConfiguration imslp;

	public MetaDataSourcesConfiguration(
			@JsonProperty("imslp") final MetaDataSourceConfiguration imslp) {
		this.imslp = imslp;
	}

	public MetaDataSourceConfiguration getImslp() {
		return imslp;
	}
}

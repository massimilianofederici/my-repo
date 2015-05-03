package org.jtagger.app.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaDataSourceConfiguration {

	@NotEmpty
	private final String pingId;

	public MetaDataSourceConfiguration(
			@JsonProperty("ping-id") final String pingId) {
		super();
		this.pingId = pingId;
	}

	public String getPingId() {
		return pingId;
	}
}

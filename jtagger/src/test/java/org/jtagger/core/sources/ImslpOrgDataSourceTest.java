package org.jtagger.core.sources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ImslpOrgDataSourceTest {

	private final String composer = "Beethoven";

	private final String workName = "Symphony";

	@Mock
	private Client webClient;

	@Mock
	private WebTarget webTarget;

	@Mock
	private Invocation.Builder builder;

	private ImslpOrgDataSource instanceUnderTest;

	@Before
	public void setup() {
		instanceUnderTest = new ImslpOrgDataSource(webClient);
	}

	@Test
	public void lookup_WithMatchingWork_ShouldPresentMultipleTitles()
			throws IOException {
		final String json = "{\"query\":{\"search\":[{\"title\":\"A\"},{\"title\":\"B\"}]}}";
		when(webClient.target(anyString())).thenReturn(webTarget);
		when(webTarget.request()).thenReturn(builder);
		when(builder.get(String.class)).thenReturn(json);

		final List<String> lookup = instanceUnderTest
				.lookup(composer, workName);

		assertThat(lookup).contains("A", "B");
	}

	@Test
	public void lookup_WithNonMatchingWork_ShouldPresentEmptyTitleList()
			throws IOException {
		final String json = "{\"query\":{\"search\":[]}}";
		when(webClient.target(anyString())).thenReturn(webTarget);
		when(webTarget.request()).thenReturn(builder);
		when(builder.get(String.class)).thenReturn(json);

		final List<String> lookup = instanceUnderTest
				.lookup(composer, workName);

		assertThat(lookup).isEmpty();
	}

	@Test
	public void fetchMetaData_WithMatchingKey_ShouldReturnMultipleKeys()
			throws IOException {
		final String pageTitle = "Symphony No.9, Op.125 (Beethoven, Ludwig van)";
		final Map<String, String> metaData = new ImslpOrgDataSource()
				.fetchMetaData(pageTitle);
		assertThat(metaData).isNotEmpty();
	}
}

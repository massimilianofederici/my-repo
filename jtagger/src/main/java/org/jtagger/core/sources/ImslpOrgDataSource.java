package org.jtagger.core.sources;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Massimiliano
 *         http://imslp.org/api.php?action=query&list=search&srsearch
 *         =Mendelssohn%20String%20Quartet&srprop=timestamp&format=json
 */
public class ImslpOrgDataSource implements MetaDataSource {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String BASE_URL = "http://imslp.org/api.php";

	private final Client client;

	ImslpOrgDataSource(final Client client) {
		this.client = client;
	}

	public ImslpOrgDataSource() {
		this(new JerseyClientBuilder().build());
	}

	/**
	 * http://imslp.org/api.php?action=query&titles=Symphony_No.8,_Op
	 * .93_(Beethoven,_Ludwig_van)&format=json&prop=revisions&rvprop=content
	 * 
	 * @see org.jtagger.core.sources.MetaDataSource#fetchMetaData(java.lang.String)
	 */
	@Override
	public Map<String, String> fetchMetaData(final String id)
			throws IOException {
		final String pageTitle = URLEncoder.encode(id, "UTF-8");
		final String url = String
				.format("%s?action=query&titles=%s&format=json&prop=revisions&rvprop=content",
						BASE_URL, pageTitle);
		logger.info("IMSLP fetchMetaData {}", url);
		final String result = client.target(url).request().get(String.class);
		return processWorkInfo(result);
	}

	@Override
	public List<String> lookup(final String composer, final String workName)
			throws IOException {
		final String query = new StringBuilder().append(composer).append(" ")
				.append(workName).toString();
		final String url = String
				.format("%s?action=query&list=search&srsearch=%s&srprop=timestamp&format=json",
						BASE_URL, URLEncoder.encode(query, "UTF-8"));
		logger.info("IMSLP lookup {}", url);
		final String result = client.target(url).request().get(String.class);
		final JsonNode searchResults = new ObjectMapper().readTree(result)
				.get("query").get("search");
		final Builder<String> builder = ImmutableList.builder();
		searchResults.elements().forEachRemaining(node -> {
			final String pageTitle = node.get("title").asText();
			logger.info("Matching page title {}", pageTitle);
			builder.add(pageTitle);
		});
		return builder.build();
	}

	private Map<String, String> processWorkInfo(final String json)
			throws IOException {
		final JsonNode node = new ObjectMapper().readTree(json);
		final Entry<String, JsonNode> pageEntry = node.get("query")
				.get("pages").fields().next();
		final JsonNode revision = pageEntry.getValue().get("revisions")
				.elements().next();
		final String content = revision.toString();
		String workInfo = StringUtils.substringBetween(content,
				"*****WORK INFO*****", "*****");
		workInfo = StringUtils.remove(workInfo, "\\n");
		workInfo = StringUtils.remove(workInfo, "<br>");
		workInfo = workInfo.trim();
		logger.info("WorkInfo {}", workInfo);
		final String[] tags = StringUtils.split(workInfo, "|");
		final com.google.common.collect.ImmutableMap.Builder<String, String> builder = ImmutableMap
				.builder();
		Arrays.stream(tags).forEach(tag -> {
			final String key = StringUtils.substringBefore(tag, "=");
			final String value = StringUtils.substringAfter(tag, "=");
			logger.info("Found {}={}", key, value);
			if (StringUtils.isNotBlank(key)) {
				builder.put(key, value);
			}
		});
		return builder.build();
	}

	@Override
	public String toString() {
		return "ImslpOrg MetaDataSource " + BASE_URL;
	}
}

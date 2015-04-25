package org.jtagger.core.sources;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;

public class Imslp {

	public static void main(final String[] args) throws IOException {
		final String string = new Client()
				.resource(
						"http://imslp.org/api.php?action=query&titles=String%20Quartet%20No.1,%20Op.12%20(Mendelssohn,%20Felix)&format=json&prop=revisions&rvprop=content")
				.get(String.class);
		// System.out.println(string);
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode node = mapper.readTree(string);
		final Entry<String, JsonNode> pageEntry = node.get("query")
				.get("pages").getFields().next();
		final JsonNode revision = pageEntry.getValue().get("revisions")
				.getElements().next();
		final String content = revision.toString();
		// System.out.println(content);
		String workInfo = StringUtils.substringBetween(content,
				"*****WORK INFO*****", "*****");
		workInfo = StringUtils.remove(workInfo, "\\n");
		final String[] tags = StringUtils.split(workInfo, "|");
		for (final String tag : tags) {
			System.out.println(tag);
		}
	}
}

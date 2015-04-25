package org.jtagger.core;

import java.util.Iterator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Tags implements Iterable<String> {

	private final ImmutableMap<String, String> tagMap;

	Tags(final ImmutableMap<String, String> map) {
		tagMap = map;
	}

	public String get(final String key) {
		return tagMap.get(key);
	}

	@Override
	public Iterator<String> iterator() {
		return tagMap.keySet().iterator();
	}

	public static class TagsBuilder {
		private final Builder<String, String> builder = new Builder<>();

		public TagsBuilder withTag(final String key, final String value) {
			builder.put(key, value);
			return this;
		}

		public Tags build() {
			return new Tags(builder.build());
		}
	}
}

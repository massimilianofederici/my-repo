package org.jtagger.core;

import static org.assertj.core.api.Assertions.*;

import org.jtagger.core.Tags;
import org.jtagger.core.Tags.TagsBuilder;
import org.junit.Test;

public class TagsTest {

	private final String tagName = "key";
	private final String tagValue = "value";

	@Test
	public void get_WithExistingKey_ShouldReturnValue() {
		final Tags tags = new TagsBuilder().withTag(tagName, tagValue).build();
		assertThat(tags.get("key")).isEqualTo(tagValue);
	}

	@Test
	public void get_WithNonExistingKey_ShouldReturnNull() {
		final Tags tags = new TagsBuilder().withTag(tagName, tagValue).build();
		assertThat(tags.get("invalid")).isNull();
	}

	@Test
	public void withTag_WithNullKey_ShouldThrowException() {
		assertThatThrownBy(() -> new TagsBuilder().withTag(null, tagValue));
	}

	@Test
	public void iterator_Always_IsImmutable() {
		final Tags tags = new TagsBuilder().withTag(tagName, tagValue).build();
		assertThatThrownBy(() -> tags.iterator().remove());
	}
}

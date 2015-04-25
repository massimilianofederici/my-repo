package org.jtagger.core;

import java.util.Optional;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class CopyFlacTestWatcher extends TestWatcher {

	private CopyFlac marker;

	@Override
	protected void starting(final Description description) {
		marker = description.getAnnotation(CopyFlac.class);
	}

	public Optional<String> getFlacFileName() {
		if (marker != null) {
			return Optional.of(marker.value());
		}
		return Optional.empty();
	}

}

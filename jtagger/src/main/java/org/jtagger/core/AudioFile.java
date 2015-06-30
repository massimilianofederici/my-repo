package org.jtagger.core;

public class AudioFile {

	private final String fileName;

	private final Tags tags;

	public AudioFile(final String fileName, final Tags tags) {
		this.fileName = fileName;
		this.tags = tags;
	}

	public String getFileName() {
		return fileName;
	}

	public Tags getTags() {
		return tags;
	}
}

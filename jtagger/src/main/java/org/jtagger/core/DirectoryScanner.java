package org.jtagger.core;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jtagger.core.Tags.TagsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class DirectoryScanner {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private void validateInputDirectory(final File directory) {
		Preconditions.checkNotNull(directory,
				"input directory expected not to be null");
		Preconditions.checkArgument(directory.isDirectory(),
				"input directory expected not to be a file");
	}

	public void scan(final File directory,
			final DirectoryScannerListener listener) {
		validateInputDirectory(directory);
		Preconditions
				.checkNotNull(listener, "listener expected not to be null");
		final File[] listFiles = FileUtils
				.convertFileCollectionToFileArray(FileUtils.listFiles(
						directory, new String[] { "flac" }, true));
		for (int i = 0; i < listFiles.length; i++) {
			final File file = listFiles[i];
			try {
				final AudioFile audioFile = AudioFileIO.read(file);
				final Tag tag = audioFile.getTag();
				final Iterator<TagField> fields = tag.getFields();
				final TagsBuilder builder = new TagsBuilder();
				while (fields.hasNext()) {
					final TagField tagField = fields.next();
					final String id = tagField.getId();
					final String value = tagField.toString();
					logger.info("Found tag {}={}", id, value);
					builder.withTag(id, value);
				}
				listener.metaData(builder.build(), file);
			} catch (final Exception e) {
				logger.warn("Could not process file {}", file.getName(), e);
				listener.fileProcessingError(file, e);
			}
		}
	}
}

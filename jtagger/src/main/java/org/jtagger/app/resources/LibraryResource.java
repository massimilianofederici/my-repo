package org.jtagger.app.resources;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import jersey.repackaged.com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jtagger.core.AudioFile;
import org.jtagger.core.Library;
import org.jtagger.core.Tags;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList.Builder;

@Path("library")
public class LibraryResource {

	private Optional<Library> library = Optional.empty();

	@GET
	public LibraryDto getLibrary() throws IOException {
		return library.map(t -> {
			try {
				return LibraryDto.fromLibrary(t);
			} catch (final Exception e) {
				return null;
			}
		}).orElse(null);
	}

	@POST()
	@Path("/initialise")
	public void initialie(final String fullPath) throws IOException {
		final Library newLibrary = new Library(new File(fullPath));
		newLibrary.scan();
		library = Optional.of(newLibrary);
	}

	@POST()
	@Path("/refresh")
	public void refresh() throws IOException {
		Preconditions.checkState(library.isPresent(), "No library initialised");
		library.get().scan();
	}

	@POST
	@Path("/update")
	public void update(final AudioFileDto[] files) throws Exception {
		for (final AudioFileDto audioFileDto : files) {
			final org.jaudiotagger.audio.AudioFile audioFile = AudioFileIO
					.read(new File(audioFileDto.fileName));
			final FlacTag tag = (FlacTag) audioFile.getTag();
			for (final MetaDataDto metaDataDto : audioFileDto.tags) {
				final TagField tagField = tag.createField(metaDataDto.key,
						metaDataDto.value);
				tag.setField(tagField);
				// tag.setField(FieldKey.valueOf(metaDataDto.key),
				// metaDataDto.value);
				// Iterator<TagField> fields = tag.getFields();
				// while (fields.hasNext()) {
				// TagField tagField = (TagField) fields.next();
				// if (tagField.getId().equals(metaDataDto.key)) {
				// tag.getFields(tagField);
				// }
				// }
			}
			audioFile.commit();
		}
	}

	static class LibraryDto {

		private final String path;

		private final List<AudioFileDto> files;

		private LibraryDto(final String path, final List<AudioFileDto> files) {
			super();
			this.path = path;
			this.files = files;
		}

		static LibraryDto fromLibrary(final Library library) throws IOException {
			final List<AudioFileDto> files = Arrays.stream(library.files())
					.map(e -> AudioFileDto.fromAudioFile(e))
					.collect(Collectors.toList());
			return new LibraryDto(library.getBaseDirectory().getAbsolutePath(),
					files);
		}

		public String getPath() {
			return path;
		}

		public List<AudioFileDto> getFiles() {
			return files;
		}
	}

	static class AudioFileDto {

		private final String fileName;

		private final List<MetaDataDto> tags;

		private final String path;

		private AudioFileDto(@JsonProperty("fileName") final String fileName,
				@JsonProperty("tags") final List<MetaDataDto> tags) {
			super();
			this.fileName = fileName;
			this.tags = tags;
			this.path = StringUtils.substringBeforeLast(fileName,
					File.separator);
		}

		static AudioFileDto fromAudioFile(final AudioFile file) {
			final Tags tags = file.getTags();
			final Builder<MetaDataDto> metaDatas = new Builder<>();
			tags.forEach(key -> metaDatas.add(new MetaDataDto(key, tags
					.get(key))));
			return new AudioFileDto(file.getFileName(), metaDatas.build());
		}

		public String getFileName() {
			return fileName;
		}

		public List<MetaDataDto> getTags() {
			return tags;
		}

		public String getPath() {
			return path;
		}
	}

	static class MetaDataDto {

		private final String key;

		private final String value;

		MetaDataDto(@JsonProperty("key") final String key,
				@JsonProperty("value") final String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}
}

package org.jtagger.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LibraryTest {

	@Rule
	public TemporaryFolder libraryBaseDirectory = new TemporaryFolder();

	@Rule
	public CopyFlacTestWatcher copyFlacTestWatcher = new CopyFlacTestWatcher();

	private final String testFileName = "test1.flac";

	private String testFilePath;

	private Library theLibrary;

	@Before
	public void setup() throws IOException {
		theLibrary = new Library(libraryBaseDirectory.getRoot());
		final Optional<String> optionalFileName = copyFlacTestWatcher
				.getFlacFileName();
		if (optionalFileName.isPresent()) {
			final String fileName = optionalFileName.get();
			FileUtils.copyFile(new File("data", fileName), new File(
					libraryBaseDirectory.getRoot(), fileName));
		}
		testFilePath = libraryBaseDirectory.getRoot().getAbsolutePath()
				+ File.separator + testFileName;
		theLibrary.scan();
	}

	@Test
	public void query_OnLibraryNotInitialised_ShouldThrowException()
			throws IOException {
		assertThatThrownBy(
				() -> new Library(libraryBaseDirectory.getRoot()).query("test"))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void isEmpty_OnLibraryNotInitialised_ShouldThrowException() {
		assertThatThrownBy(
				() -> new Library(libraryBaseDirectory.getRoot()).isEmpty())
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void files_OnLibraryNotInitialised_ShouldThrownException() {
		assertThatThrownBy(
				() -> new Library(libraryBaseDirectory.getRoot()).files())
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	public void isInitialised_AfterScan_ShouldReturnTrue() throws IOException {
		assertThat(theLibrary.isInitialised()).as("initialised flag").isTrue();
	}

	@Test
	public void isInitialised_BeforeScan_ShouldReturnFalse() throws IOException {
		assertThat(new Library(libraryBaseDirectory.getRoot()).isInitialised())
				.as("initialised flag").isFalse();
	}

	@Test
	public void scan_WithEmptyDirectory_ShouldNotPopulateIndex()
			throws IOException {
		assertThat(theLibrary.isEmpty()).as("empty flag").isTrue();
	}

	@Test
	@CopyFlac("test1.flac")
	public void scan_WithSingleFile_ShouldPopulateIndex() throws IOException {
		assertThat(theLibrary.isEmpty()).as("empty flag").isFalse();
	}

	@Test
	@CopyFlac("test1.flac")
	public void files_WithSingleFile_ShouldReturnOneFile() throws IOException {
		assertThat(theLibrary.files()).as("library files")
				.extracting("fileName").containsExactly(testFilePath);
	}

	@Test
	@CopyFlac("test1.flac")
	public void query_WithMatchingTitle_ShouldReturnOneFile()
			throws IOException {
		final String title = getTestFile().getTags().get("TITLE");

		assertThat(theLibrary.query(title)).extracting("fileName")
				.containsExactly(testFilePath);
	}

	@Test
	@CopyFlac("test1.flac")
	public void query_WithMatchingArtist_ShouldReturnOneFile()
			throws IOException {
		final String artist = getTestFile().getTags().get("ARTIST");

		assertThat(theLibrary.query("ARTIST:" + artist)).extracting("fileName")
				.containsExactly(testFilePath);
	}

	@Test
	@CopyFlac("test1.flac")
	public void query_WithMatchingDate_ShouldReturnOneFile() throws IOException {
		final String date = getTestFile().getTags().get("DATE");

		assertThat(theLibrary.query("DATE:" + date)).extracting("fileName")
				.containsExactly(testFilePath);
	}

	@Test
	public void query_WithInvalidQueryString_ShouldThrowException()
			throws IOException {
		assertThatThrownBy(() -> theLibrary.query("*"))
				.hasCauseExactlyInstanceOf(ParseException.class);
	}

	@Test
	@CopyFlac("test1.flac")
	public void scan_AfterScanWithSameFile_ShouldUpdateIndex()
			throws IOException {
		theLibrary.scan();
		assertThat(theLibrary.files()).hasSize(1);
	}

	@Test
	@CopyFlac("test1.flac")
	public void scan_AfterScanWithAddedFile_ShouldUpdateIndex()
			throws IOException {
		FileUtils.copyFile(new File("data", testFileName), new File(
				libraryBaseDirectory.getRoot(), "test2.flac"));
		theLibrary.scan();
		assertThat(theLibrary.files()).hasSize(2);
	}

	@Test
	@CopyFlac("invalid.flac")
	public void scan_WithInvalidFile_ShouldReportError() throws IOException {
		assertThat(theLibrary.scan().getNumberOfErrors()).isEqualTo(1);
	}

	@Test
	@CopyFlac("invalid.flac")
	public void scan_WithInvalidFile_ShouldNotReportFileProcessed()
			throws IOException {
		assertThat(theLibrary.scan().getNumberOfFilesProcessed()).isEqualTo(0);
	}

	private AudioFile getTestFile() throws IOException {
		return theLibrary.files()[0];
	}
}
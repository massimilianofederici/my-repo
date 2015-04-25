package org.jtagger.core;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jtagger.core.DirectoryScanner;
import org.jtagger.core.DirectoryScannerListener;
import org.jtagger.core.Tags;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryScannerTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public CopyFlacTestWatcher copyFlacTestWatcher = new CopyFlacTestWatcher();

	private final DirectoryScanner theScanner = new DirectoryScanner();

	@Mock
	private File baseDirectory;

	@Mock
	private DirectoryScannerListener scannerListener;

	@Before
	public void setup() throws IOException {
		final Optional<String> optionalFileName = copyFlacTestWatcher
				.getFlacFileName();
		if (optionalFileName.isPresent()) {
			final String fileName = optionalFileName.get();
			logger.info("Copying test resource {} to {}", fileName,
					testFolder.getRoot());
			FileUtils.copyFile(new File("data", fileName),
					new File(testFolder.getRoot(), fileName));
		}
	}

	@Test
	public void scan_GivenNullDirectory_ShouldThrowException() {
		assertThatThrownBy(() -> theScanner.scan(null, scannerListener))
				.hasMessageContaining("not to be null");
	}

	@Test
	public void scan_GivenNullListener_ShouldThrowException() {
		Mockito.when(baseDirectory.isDirectory()).thenReturn(true);
		assertThatThrownBy(() -> theScanner.scan(baseDirectory, null))
				.hasMessageContaining("not to be null");
	}

	@Test
	public void scan_GivenFile_ShouldThrowException() {
		Mockito.when(baseDirectory.isDirectory()).thenReturn(false);
		assertThatThrownBy(
				() -> theScanner.scan(baseDirectory, scannerListener))
				.hasMessageContaining("not to be a file");
	}

	@Test
	@CopyFlac("test1.flac")
	public void scan_GivenSingleFile_ShouldReportSingleFileToProcess() {
		theScanner.scan(testFolder.getRoot(), scannerListener);
		final ArgumentCaptor<Tags> tagsCapture = ArgumentCaptor
				.forClass(Tags.class);
		Mockito.verify(scannerListener).metaData(tagsCapture.capture(),
				Mockito.any());
	}

	@Test
	@CopyFlac("test1.flac")
	public void scan_GivenSingleFile_ShouldReportMetaData() {
		theScanner.scan(testFolder.getRoot(), scannerListener);
		final ArgumentCaptor<Tags> metaDataCaptor = ArgumentCaptor
				.forClass(Tags.class);
		Mockito.verify(scannerListener, Mockito.atLeastOnce()).metaData(
				metaDataCaptor.capture(), Mockito.any());
	}

	@Test
	public void scan_GivenMultipleDirectories_ShouldReportMultipleFiles()
			throws IOException {
		final File dir1 = testFolder.newFolder("dir1");
		final File dir2 = testFolder.newFolder("dir2");
		copyTestFile(dir1);
		copyTestFile(dir2);

		theScanner.scan(testFolder.getRoot(), scannerListener);

		final ArgumentCaptor<Tags> tagsCaptor = ArgumentCaptor
				.forClass(Tags.class);
		Mockito.verify(scannerListener, Mockito.times(2)).metaData(
				tagsCaptor.capture(), Mockito.any());
	}

	@Test
	@CopyFlac("invalid.flac")
	public void scan_GivenInvalidFile_ShouldReportError() {
		theScanner.scan(testFolder.getRoot(), scannerListener);
		Mockito.verify(scannerListener).fileProcessingError(Mockito.any(),
				Mockito.any());
	}

	private void copyTestFile(final File parentDirectory) throws IOException {
		final String fileName = "test1.flac";
		FileUtils.copyFile(new File("data", fileName), new File(
				parentDirectory, fileName));
	}
}

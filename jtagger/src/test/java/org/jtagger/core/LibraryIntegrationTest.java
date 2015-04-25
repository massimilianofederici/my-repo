package org.jtagger.core;

import java.io.File;
import java.io.IOException;

import org.jtagger.core.Library;
import org.jtagger.core.ScanReport;
import org.junit.Test;

public class LibraryIntegrationTest {

	@Test
	public void largeLibrary() throws IOException {
		final Library library = new Library(new File(
				"C:\\Users\\Massimiliano\\Downloads"));
		final ScanReport report = library.scan();
		System.out.println(report.toString());
	}
}

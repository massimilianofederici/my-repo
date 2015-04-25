package org.jtagger.core;

import java.io.File;

public interface DirectoryScannerListener {

	void metaData(Tags tags, File file);

	void fileProcessingError(File file, Exception e);
}

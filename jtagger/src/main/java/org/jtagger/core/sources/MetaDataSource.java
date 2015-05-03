package org.jtagger.core.sources;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MetaDataSource {

	Map<String, String> fetchMetaData(String id) throws IOException;

	List<String> lookup(String composer, String workName) throws IOException;
}

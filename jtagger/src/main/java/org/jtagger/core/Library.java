package org.jtagger.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jaudiotagger.tag.FieldKey;
import org.jtagger.core.Tags.TagsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Library {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final File baseDirectory;

	private final Analyzer analyzer;

	private Directory index;

	public Library(final File baseDirectory) {
		this.analyzer = new StandardAnalyzer();
		this.baseDirectory = baseDirectory;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public AudioFile[] files() throws IOException {
		validateIndex();
		final DirectoryReader reader = DirectoryReader.open(index);
		final int maxDoc = reader.maxDoc();
		final AudioFile[] files = new AudioFile[maxDoc];
		for (int i = 0; i < maxDoc; i++) {
			final Document document = reader.document(i);
			final String fileName = document.get("file");
			files[i] = new AudioFile(fileName, getMetaData(document));
		}
		return files;
	}

	public AudioFile[] query(final String text) throws IOException {
		validateIndex();
		logger.info("Quering library with input {}", text);
		try {
			final Query query = new QueryParser(FieldKey.TITLE.name(), analyzer)
					.parse(text);
			final DirectoryReader reader = DirectoryReader.open(index);
			final IndexSearcher searcher = new IndexSearcher(reader);
			final TopScoreDocCollector collector = TopScoreDocCollector
					.create(10);
			searcher.search(query, collector);
			final ScoreDoc[] hits = collector.topDocs().scoreDocs;
			final AudioFile[] files = new AudioFile[hits.length];
			for (int i = 0; i < hits.length; i++) {
				final ScoreDoc hit = hits[i];
				final int docId = hit.doc;
				final Document document = searcher.doc(docId);
				final String fileName = document.get("file");
				files[i] = new AudioFile(fileName, getMetaData(document));
			}
			return files;
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}

	private Tags getMetaData(final Document document) throws IOException {
		final List<IndexableField> fields = document.getFields();
		final TagsBuilder builder = new TagsBuilder();
		for (int i = 0; i < fields.size(); i++) {
			final IndexableField field = fields.get(i);
			final String name = field.name();
			if (!name.equals("file")) {
				final String value = field.stringValue();
				builder.withTag(name, value);
			}
		}
		return builder.build();
	}

	public boolean isEmpty() throws IOException {
		validateIndex();
		return numberOfDocuments() == 0;
	}

	private int numberOfDocuments() throws IOException {
		final int numDocs = DirectoryReader.open(index).numDocs();
		logger.debug("Found {} documents in the index", numDocs);
		return numDocs;
	}

	private void validateIndex() {
		Preconditions.checkState(index != null,
				"Library has not been initialised");
	}

	public boolean isInitialised() {
		return index != null;
	}

	IndexWriter newIndexWriter() throws IOException {
		return new IndexWriter(index, new IndexWriterConfig(analyzer));
	}

	public ScanReport scan() throws IOException {
		index = new RAMDirectory();
		final IndexWriter writer = newIndexWriter();
		final ScanReport report = new ScanReport();
		report.startScan();
		try {
			new DirectoryScanner().scan(baseDirectory,
					new DirectoryScannerListener() {

						@Override
						public void metaData(final Tags tags, final File file) {
							final Document currentDocument = new Document();
							currentDocument.add(new TextField("file", file
									.getAbsolutePath(), Store.YES));
							for (final String key : tags) {
								final IndexableField field = new TextField(key,
										tags.get(key), Store.YES);
								currentDocument.add(field);
							}
							try {
								writer.addDocument(currentDocument);
								report.fileProcessed();
							} catch (final IOException e) {
								logger.warn("Could not index document", e);
							}
						}

						@Override
						public void fileProcessingError(final File file,
								final Exception e) {
							report.processingError();
						}
					});
			writer.commit();
			report.scanComplete();
			return report;
		} finally {
			writer.close();
		}
	}
}

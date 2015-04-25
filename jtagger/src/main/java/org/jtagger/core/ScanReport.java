package org.jtagger.core;

import java.time.Duration;
import java.time.LocalDateTime;

public class ScanReport {

	private int numberOfFilesProcessed;

	private int numberOfErrors;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	public int getNumberOfFilesProcessed() {
		return numberOfFilesProcessed;
	}

	public void startScan() {
		this.startTime = LocalDateTime.now();
	}

	public void scanComplete() {
		this.endTime = LocalDateTime.now();
	}

	public void fileProcessed() {
		this.numberOfFilesProcessed++;
	}

	public void processingError() {
		this.numberOfErrors++;
	}

	@Override
	public String toString() {
		return String
				.format("%d files were successfully processed in %d milliseconds, with {} errors",
						numberOfFilesProcessed,
						Duration.between(startTime, endTime).toMillis(),
						numberOfErrors);
	}
}

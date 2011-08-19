package main;

import java.io.File;
import java.util.Vector;

import ArithmeticCoderPackage.AdaptiveModel_One;

public class Archiver {
	private final static String fileSignature = ".jyg";
	private final static String separatorSignature = "\t";
	private final String ArchiveName;
	private final Vector<File> fileList;

	// Finds all of the files in the hierarchy and makes a list of files
	public Archiver(final String inputArchiveName, final String[] args) {
		ArchiveName = inputArchiveName;
		this.fileList = new Vector<File>(0);
		for (String arg : args) {
			createFileList(new File(arg));
		}
	}

	// Recursively makes sure to go inside a folder but to add all files
	private void createFileList(final File inputFile) {
		if (inputFile.isDirectory()) {
			for (File tempFile : inputFile.listFiles()) {
				createFileList(tempFile);
			}
		} else {
			fileList.add(inputFile);
		}
	}

	public void createArchive() {

		OutputFileHandler outputFileHandler = new OutputFileHandler(
				ArchiveName, ".jyg");
		// System.out.print(outputFileHandler.getFilePath() + "\n");
		for (File nextFile : fileList) {

			InputFileHandler inputFileHandler = new InputFileHandler(nextFile);
			// inputFileHandler.openDataStream();

			// Notice we're not dealing with FileLength anymore. During my
			// debugging
			// I figured out how to encode an EOF character and so now life
			// is good
			outputFileHandler.write(fileSignature.getBytes());
			outputFileHandler.write(separatorSignature.getBytes());
			outputFileHandler.write(inputFileHandler.getFilePath().getBytes());
			outputFileHandler.write(separatorSignature.getBytes());
			if (!nextFile.getPath().endsWith(".jyg")) {
				new AdaptiveModel_One(inputFileHandler, outputFileHandler)
						.encode();
			}
			// I write so many new lines because I realized the decoder has
			// a
			// tendency to go and pick up a few bytes it shouldn't be
			// picking
			// up. This was the quickest fix.
			outputFileHandler.write("\n\n\n\n\n\n\n".getBytes());
			inputFileHandler.closeDataStream();

		}
		outputFileHandler.closeDataStream();
	}

	public void unArchive() {

		InputFileHandler inputFileHandler = new InputFileHandler(new File(
				ArchiveName));
		System.out.print("Reading: " + inputFileHandler.getFilePath() + "\n");
		while (inputFileHandler.readUntil(separatorSignature).endsWith(".jyg")) {
			OutputFileHandler outputFileHandler = new OutputFileHandler("out_"
					+ inputFileHandler.readUntil(separatorSignature));
			new AdaptiveModel_One(inputFileHandler, outputFileHandler).decode();
			outputFileHandler.closeDataStream();
		}
		inputFileHandler.closeDataStream();
	}
}

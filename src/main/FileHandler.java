package main;

import java.io.File;
import java.io.IOException;

public class FileHandler {
	private final String FileName;
	private final String Extension;
	protected final File newFile;

	public FileHandler(File inputFile) {
		if (inputFile.getPath().contains(".")) {
			Extension = inputFile.getPath().substring(
					inputFile.getPath().indexOf("."),
					inputFile.getPath().length());
			FileName = inputFile.getPath().substring(0,
					inputFile.getPath().indexOf("."));
		} else {
			FileName = inputFile.getPath();
			Extension = "";
		}
		
		newFile = inputFile;
	}

	public FileHandler(final String inputFilePath, final String inputExtension) {
		if (inputFilePath.contains(".")) {
			Extension = inputFilePath.substring(inputFilePath.indexOf('.'),
					inputFilePath.length())
					+ inputExtension;
			FileName = inputFilePath.substring(0, inputFilePath.indexOf('.'));
		} else {
			FileName = inputFilePath;
			Extension = inputExtension;
		}
		newFile = createFile(FileName, Extension);
	}

	private final static File createFile(String FileName, String Extension) {
		File tempFile = new File(FileName + Extension);
		
		if (FileName.contains("/")){
			String ParentDirectory = FileName.substring(0, FileName.lastIndexOf("/"));
			new File(ParentDirectory).mkdir();
		}
		
		try {
			for (int attempts = 1; !tempFile.createNewFile(); attempts++) {
				tempFile = new File(FileName + "_" + attempts + Extension);
			}
		} catch (IOException e) {
			System.out.print("Could Not Open File");
			return null;
		}
		return tempFile;
	}

	public final String getExtension() {
		return Extension;
	}

	public final String getFileName() {
		return FileName;
	}

	public final String getFilePath() {
		return newFile.getPath();
	}

	public final long getFileLength() {
		return newFile.length();
	}

	public final void fileDelete() {
		newFile.delete();
	}
}
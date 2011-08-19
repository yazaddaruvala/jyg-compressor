package main;

import java.io.FileOutputStream;
import java.io.IOException;

public class OutputFileHandler extends FileHandler {
	private final static int BufferSize = 100000;
	private byte[] OutputByteArray = new byte[BufferSize];
	private int BufferIndex = 0;

	private FileOutputStream DataStream;

	public OutputFileHandler(String inputFileName) {
		super(inputFileName, "");
		try {
			DataStream = new FileOutputStream(newFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputFileHandler(String inputFileName, String inputExtension) {
		super(inputFileName, inputExtension);
		try {
			DataStream = new FileOutputStream(newFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final boolean openDataStream() {
		return openDataStream(false);
	}

	public final boolean openDataStream(boolean append) {
		try {
			DataStream = new FileOutputStream(newFile, append);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public final boolean closeDataStream() {
		try {
			DataStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public final boolean write(byte[] bytes) {
		flush();
		try {
			DataStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean write(char Char) {
		return write((byte) Char);
	}

	public boolean write(byte inputByte) {
		OutputByteArray[BufferIndex] = inputByte;
		BufferIndex++;
		if (BufferIndex == BufferSize) {
			return flush();
		}
		return false;
	}

	public boolean flush() {
		try {
			DataStream.write(OutputByteArray, 0, BufferIndex);
			BufferIndex=0;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

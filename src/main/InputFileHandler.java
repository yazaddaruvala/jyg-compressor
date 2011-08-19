package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InputFileHandler extends FileHandler {
	private final static int BufferSize = 100000;
	private byte[] InputByteArray = new byte[BufferSize];
	private int BufferIndex = 100000;

	private FileInputStream DataStream;

	public InputFileHandler(File inputFile) {
		super(inputFile);
		openDataStream();
	}

	public final boolean openDataStream() {
		try {
			if (DataStream.equals(null));
				
		} catch (NullPointerException e1) {
			try {
				DataStream = new FileInputStream(newFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public final boolean closeDataStream() {
		try {
			DataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public final boolean isAvailable() {
		try {
			if (DataStream.available() == 0
					&& BufferIndex == InputByteArray.length) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String readUntil(String inputString) {
		String tempString = "";
		byte[] bytes = new byte[inputString.length()];
		try {
			while (!tempString.endsWith(inputString) && isAvailable()) {
				bytes = read(bytes.length);
				for (byte b : bytes) {
					tempString += (char) b;
				}
			}
		} catch (IOException e) {
			return "";
		}
		if (inputString.length() > tempString.length()) {
			return "";
		} else {
			return tempString.substring(0, tempString.length()
					- inputString.length());
		}
	}

	public byte[] read(int byteArrayLength) throws IOException {
		byte[] bytes = new byte[byteArrayLength];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) getUnsignedByte();
		}
		return bytes;
	}

	public byte[] read() throws IOException {
		return read(BufferSize);
	}

	public int getUnsignedByte() {
		try {
			if (BufferIndex == InputByteArray.length) {
				DataStream.read(InputByteArray);
				BufferIndex = 0;
			}
			byte next = InputByteArray[BufferIndex];
			BufferIndex++;
			return (int) next & 0xFF;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

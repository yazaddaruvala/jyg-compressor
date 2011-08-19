package main;

public class Compressor {
	private byte [] original, altered;

	Compressor(final byte [] fileByteArray) {
			this.original = fileByteArray;
	}

	public byte [] getOriginalBytes(){
		return original;
	}
	
	public byte [] getAlteredBytes(){
		return altered;
	}
	
	//Unimplemented Compress;
	public boolean Compress(){
		//some compression should occur
		altered = original;
		return true;
	}
	
	private static void compressionStats(byte[] original, byte[] compressed, byte[] un_compressed){
		int error_count = 0;
		for(int index = 0; index < original.length; index++){
			if(un_compressed[index] != original[index]){
				error_count++;
			}
			System.out.print(un_compressed[index]);
		}
		System.out.print("\n");
		System.out.print(( (double)(compressed.length)/original.length) + "%\n");
		System.out.print("Error Count: " + error_count + "\n");
	}
}

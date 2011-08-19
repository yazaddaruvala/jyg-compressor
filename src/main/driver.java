package main;

public class driver {

	public static void main(String[] args) {
		final long starttime = System.currentTimeMillis();
//		String FileName = "out_folder/2.txt";
//		if (FileName.contains("/")){
//			String ParentDirectory = FileName.substring(0, FileName.lastIndexOf("/"));
//			new File(ParentDirectory).mkdir();
//		}
//		
//		File tempFile = new File(FileName);
//		try {
//			tempFile.createNewFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		if (args.length > 1) {
			new Archiver("Archive", args).createArchive();
		} else {
			if (args[0].endsWith(".jyg")) {
				new Archiver(args[0], args).unArchive();
			} else {
				new Archiver(args[0], args).createArchive();

			}
		}
		System.out.print("Done: " + (System.currentTimeMillis() - starttime));
	}
}
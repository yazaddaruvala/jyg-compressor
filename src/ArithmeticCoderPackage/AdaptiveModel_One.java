package ArithmeticCoderPackage;

import main.InputFileHandler;
import main.OutputFileHandler;


public class AdaptiveModel_One {

	// For each possible symbol, what is the probability that the next symbol
	// occurs
	private int[][] mCumCount = new int[257][257];
	// Since we're counting frequency as our probability, we need to keep track
	// of the total frequency
	private int mTotal[] = new int[257];
	private final ArithmeticCoder coder;

	public AdaptiveModel_One(final InputFileHandler inputInputFileHandler,
			final OutputFileHandler inputOutputFileHandler) {

		coder = new ArithmeticCoder(inputInputFileHandler,
				inputOutputFileHandler);

		// Initialize the probabilities at 1 for every possible symbol in every
		// possible context
		for (int i = 0; i < 257; i++) {
			mTotal[i] = 257;
			for (int j = 0; j < 257; j++) {
				mCumCount[i][j] = 1;
			}
		}
	}

	public void decode() {
		int previous = 0;
		int symbol = 0;
		coder.DecodeStart();
		do {
			// This for loop figures out which symbol corresponds to the
			// probability we've determined
			symbol = 0;
			int low_count = 0;
			for (long value = coder.DecodeTarget(mTotal[previous]); low_count+ mCumCount[previous][symbol] <= value; symbol++){
				low_count += mCumCount[previous][symbol];
			}


			// Here you run the probability values you determined through
			// Decode, which will basically update your decoder, and get
			// more bytes, so you're following the steps the encoder took.
			coder.decode(low_count, low_count + mCumCount[previous][symbol]);
			
			// update model, so increment the probabilities you're storing
			mCumCount[previous][symbol]++;
			mTotal[previous]++;
			previous = symbol;

			// Write symbol
			if (symbol < 256) {
				coder.outputFileHandler.write((byte) symbol);

			}

		}while (symbol < 256);
		coder.outputFileHandler.flush();

	}

	public void encode() {
		int previous = 0;
		for (int i = 0; i< coder.inputFileHandler.getFileLength();i++) {
			// Read a byte
			int symbol = coder.inputFileHandler.getUnsignedByte();
			int low_count = 0;
			char j;
			// Determine the probability value for that symbol
			for (j = 0; j < symbol; j++) {
				low_count += mCumCount[previous][j];
			}
			// Encode that in
			coder.encode(low_count, low_count + mCumCount[previous][j],
					mTotal[previous]);
			// Update your model
			mCumCount[previous][symbol]++;
			mTotal[previous]++;
			previous = symbol;
		}

		// This just deals with whatever values still haven't been written
		// to
		// the file
		coder.encode(mTotal[previous]-1,mTotal[previous], mTotal[previous]);
		coder.EncodeFinish();
	}
}

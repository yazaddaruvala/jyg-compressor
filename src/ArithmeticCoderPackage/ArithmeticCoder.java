package ArithmeticCoderPackage;

import main.InputFileHandler;
import main.OutputFileHandler;

public class ArithmeticCoder {
	private static int g_FirstQuarter = 0x20000000;
	private static int g_ThirdQuarter = 0x60000000;
	private static int g_Half = 0x40000000;

	protected InputFileHandler inputFileHandler;
	protected OutputFileHandler outputFileHandler;

	private int mBitCount = 0;
	private int mBitBuffer = 0;
	protected long mLow = 0;
	protected long mHigh = 0x7FFFFFFF;
	protected long mScale = 0;
	protected int mBuffer = 0;
	protected long mStep = 0;

	public ArithmeticCoder(final InputFileHandler inputInputFileHandler,
			final OutputFileHandler inputOutputFileHandler) {
		inputFileHandler = inputInputFileHandler;
		outputFileHandler = inputOutputFileHandler;
	}

	// Stores bits one at a time until it fills the byte and writes the byte to
	// the output-file's handler
	private void SetBit(int bit) {
		mBitBuffer = (mBitBuffer << 1) | bit;
		mBitCount++;

		if (mBitCount >= 8) {
			outputFileHandler.write((byte) mBitBuffer);
			mBitCount = 0;
			mBitBuffer = 0;
		}
	}

	// Retrieves bits one at a time until it empties the byte and reads a new
	// byte from the input-file's handler
	private int GetBit() {
		if (mBitCount == 0) {
			mBitBuffer = inputFileHandler.getUnsignedByte();
			mBitCount = 8;
		}
		int bit = mBitBuffer >> 7;
		mBitBuffer = mBitBuffer << 1 & 0x000000FF;
		mBitCount--;
		return bit;
	}

	// At the very end, when you need to get rid of any data still being
	// buffered, just fill it with zeroes
	private void SetBitFlush() {
		while (mBitCount != 0)
			SetBit(0);
	}

	// This is the actual encoding algorithm
	public void encode(final int low_count, final int high_count,
			final int total) {
		// Adjust values for probability ( high and low bounds, and step value
		// between them
		mStep = (mHigh - mLow + 1) / total;
		mHigh = mLow + mStep * high_count - 1;
		mLow = mLow + mStep * low_count;

		// This is called e1/e2 scaling, it's like.. oh what if the most
		// significant bits of mLow and mHigh are the same? So it looks at the
		// two possibilities, and then just writes either the 0 or the 1 until
		// they're different
		while ((mHigh < g_Half) || (mLow >= g_Half)) {
			if (mHigh < g_Half) {
				SetBit(0);
				mLow = mLow * 2;
				mHigh = mHigh * 2 + 1;

				// perform e3 mappings
				for (; mScale > 0; mScale--)
					SetBit(1);
			} else if (mLow >= g_Half) {
				SetBit(1);
				mLow = 2 * (mLow - g_Half);
				mHigh = 2 * (mHigh - g_Half) + 1;

				// perform e3 mappings
				for (; mScale > 0; mScale--)
					SetBit(0);
			}
		}

		// e3 is that underflow thing.. So it's like.. mHigh and mLow are
		// approaching the same value, and they'll keep getting to a similar
		// value even though they have different MSBs.. so it causes a problem.
		// The way they deal with it is kind of weird because they just remember
		// they scaled it and then do some voodoo magic up there
		while ((g_FirstQuarter <= mLow) && (mHigh < g_ThirdQuarter)) {
			// keep necessary e3 mappings in mind
			mScale++;
			mLow = 2 * (mLow - g_FirstQuarter);
			mHigh = 2 * (mHigh - g_FirstQuarter) + 1;
		}
	}

	void EncodeFinish() {
		// There are two possibilities of how mLow and mHigh can be distributed,
		// which means that two bits are enough to distinguish them.

		if (mLow < g_FirstQuarter) // mLow < FirstQuarter < Half <= mHigh
		{
			SetBit(0);
			for (int i = 0; i < mScale + 1; i++)
				// perform e3-skaling
				SetBit(1);
		} else // mLow < Half < ThirdQuarter <= mHigh
		{
			SetBit(1); // zeros added automatically by the decoder; no need to
			// send them
		}

		// empty the output buffer
		SetBitFlush();
		outputFileHandler.flush();
	}

	void DecodeStart() {
		// Fill buffer with bits from the input stream
		for (int i = 0; i < 31; i++)
			// just use the 31 least significant bits
			mBuffer = (mBuffer << 1) | GetBit();
	}

	long DecodeTarget(int total) {
		// split number space into single steps
		mStep = (mHigh - mLow + 1) / total;
		// return current value
		return (mBuffer - mLow) / mStep;
	}

	// Decoding algorithm.. It's actually rather similar to the encoding
	// algorithm
	void decode(int low_count, int high_count) {
		mHigh = mLow + mStep * high_count - 1;
		mLow = mLow + mStep * low_count;
		// e1/e2 mapping
		while ((mHigh < g_Half) || (mLow >= g_Half)) {
			if (mHigh < g_Half) {
				mLow = mLow * 2;
				mHigh = mHigh * 2 + 1;
				mBuffer = 2 * mBuffer + GetBit();
			} else if (mLow >= g_Half) {
				mLow = 2 * (mLow - g_Half);
				mHigh = 2 * (mHigh - g_Half) + 1;
				mBuffer = 2 * (mBuffer - g_Half) + GetBit();
			}
			mScale = 0;
		}

		// e3 mapping
		while ((g_FirstQuarter <= mLow) && (mHigh < g_ThirdQuarter)) {
			mScale++;
			mLow = 2 * (mLow - g_FirstQuarter);
			mHigh = 2 * (mHigh - g_FirstQuarter) + 1;
			mBuffer = 2 * (mBuffer - g_FirstQuarter) + GetBit();
		}
	}
}

package ca.bc.gov.nrs.wfone.common.utils;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils
{
	public static byte[] longToBytes(long x)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		((Buffer) buffer).flip();// need flip
		long result = buffer.getLong();

		return result;
	}

	public static byte[] getBytes(UUID guid)
	{
		byte[] result = null;

		byte[] mostSigBytes = ByteUtils.longToBytes(guid.getMostSignificantBits());
		byte[] leastSigBytes = ByteUtils.longToBytes(guid.getLeastSignificantBits());

		result = new byte[] { mostSigBytes[0], mostSigBytes[1], mostSigBytes[2], mostSigBytes[3], mostSigBytes[4], mostSigBytes[5], mostSigBytes[6],
				mostSigBytes[7], leastSigBytes[0], leastSigBytes[1], leastSigBytes[2], leastSigBytes[3], leastSigBytes[4], leastSigBytes[5], leastSigBytes[6],
				leastSigBytes[7] };

		return result;
	}

	public static UUID getUUID(byte[] bytes)
	{
		UUID result = null;

		if (bytes.length != 16)
		{
			throw new IllegalArgumentException("Unexpected length " + bytes.length + " found where 16 was expected.");
		}

		byte[] mostSigBytes = new byte[] { bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7] };
		long mostSigBits = ByteUtils.bytesToLong(mostSigBytes);

		byte[] leastSigBytes = new byte[] { bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15] };
		long leastSigBits = ByteUtils.bytesToLong(leastSigBytes);

		result = new UUID(mostSigBits, leastSigBits);

		return result;
	}
}
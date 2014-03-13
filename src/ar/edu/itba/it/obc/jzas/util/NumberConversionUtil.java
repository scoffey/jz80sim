package ar.edu.itba.it.obc.jzas.util;

public class NumberConversionUtil {
	public static String mostSignificativeHex(Integer i) {
		return bin2Hex(mostSignificativeBin(i));
	}

	public static String leastSignificativeHex(Integer i) {
		return bin2Hex(leastSignificativeBin(i));
	}

	public static String mostSignificativeBin(Integer i) {
		return toBin(i, 16).substring(0, 8);
	}

	public static String leastSignificativeBin(Integer i) {
		return toBin(i, 16).substring(8, 16);
	}

	public static String littleEndianBin(int i) {
		return leastSignificativeBin(i) + mostSignificativeBin(i);
	}

	public static String littleEndianHex(int i) {
		return leastSignificativeHex(i) + mostSignificativeHex(i);
	}
	
	public static String bigEndianHex(int i) {
		return mostSignificativeHex(i) + leastSignificativeHex(i);
	}

	public static String bigEndianBin(int i) {
		return mostSignificativeBin(i) + leastSignificativeBin(i);
	}

	
	public static int parseHex(String hex) {
		try {
			return (int) Long.parseLong("0" + hex, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int parseBin(String bin) {
		try {
			return (int) Long.parseLong("0" + bin, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

	public static String bin2Hex(String bin) {
		int intVal = parseBin(bin);
		return normalizeNumber(Integer.toHexString(intVal), bin.length() / 4);
	}

	public static String hex2Bin(String hex) {
		String ret = "";
		for (int i = 0; i < hex.length(); i++) {
			int intVal = parseHex(hex.substring(i, i + 1));
			ret += normalizeNumber(Integer.toBinaryString(intVal), 4);
		}
		return ret;
	}

	public static String toBin(int bin, int size) {
		String ret = Integer.toBinaryString(bin);
		return normalizeNumber(ret, size);
	}

	public static String toHex(int i, int size) {
		String ret = Integer.toHexString(i);
		return normalizeNumber(ret, size);
	}

	public static String toHex(float i, int size) {
		return toHex(Float.floatToRawIntBits(i), 4);

		// IEEE 754 de doble presición
		// long codification = Double.doubleToRawLongBits(i);
		// return toHex((int) (codification >> 32), 8) + toHex((int)
		// (codification & 0x00000000FFFFFFFF), 8);
	}

	public static int parseLittleEndianHex(String hex) {
		int lowPart = (int) Long.parseLong("0" + hex.substring(0, 2), 16);
		int highPart = (int) Long.parseLong("0" + hex.substring(2, 4), 16);
		return lowPart + (highPart << 8);
	}

	public static int parseBigEndianHex(String hex) {
		return (int) Long.parseLong("0" + hex, 16);
	}

	private static String normalizeNumber(String ret, int size) {
		int addCount = size - ret.length();
		if (addCount > 0) {
			for (int i = 0; i < addCount; i++) {
				ret = "0" + ret;
			}
		} else {
			ret = ret.substring(ret.length() - size, ret.length());
		}
		return ret;
	}
}

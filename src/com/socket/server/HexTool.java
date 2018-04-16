package com.socket.server;

/**
 * 
 * @ClassName HexTool
 * @Description 十六进制、byte数组等相关转换工具
 * @author ouArea
 * @date 2013-11-12 下午3:47:58
 * 
 */
public class HexTool {
	public synchronized static byte[] shortToByteArray(short s) {
		// byte[] shortBuf = new byte[2];
		// for (int i = 0; i < 2; i++) {
		// int offset = (shortBuf.length - 1 - i) * 8;
		// shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		// }
		// return shortBuf;

		byte[] b = new byte[2];
		b[1] = (byte) (s & 0xff);
		b[0] = (byte) (s >> 8 & 0xff);
		return b;
	}

	// /**
	// * 注释：字节数组到short的转换！
	// *
	// * @param b
	// * @return
	// */
	// public synchronized static short byteToShort(byte[] b) {
	// try {
	// short s = 0;
	// short s0 = (short) (b[0] & 0xff);// 最低位
	// short s1 = (short) (b[1] & 0xff);
	// s1 <<= 8;
	// s = (short) (s0 | s1);
	// return s;
	// } catch (Exception e) {
	// return 0;
	// }
	// }

	/**
	 * 
	 * @Title: byteToShort
	 * @Description: byte数组 转 short
	 * @param bytes
	 * @return
	 * @author: ouArea
	 * @return short
	 * @throws
	 */
	public static short byteToShort(byte[] bytes, int offset) {
		return (short) (bytes[offset] + 256 * bytes[offset + 1]);
	}

	/**
	 * byteToInt大小端
	 * 
	 * @Title: byteToIntDX
	 * @Description: TODO
	 * @param bytes
	 * @param offset
	 * @return
	 * @author: ouArea
	 * @return int
	 * @throws
	 */
	public static int byteToIntDX(byte[] bytes, int offset) {
		return (bytes[offset] & 0xff) + ((bytes[offset + 1] & 0xff) * 256);
	}

	// public static byte[] longToByte(long l) {
	// byte[] b = new byte[8];
	// for (int i = 0; i < b.length; i++) {
	// b[i] = new Long(l).byteValue();
	// l = l >> 8;
	// }
	// return b;
	// }

	// public static long byteToLong(byte[] b) {
	// long l = 0;
	// l |= (((long) b[7] & 0xff) << 56);
	// l |= (((long) b[6] & 0xff) << 48);
	// l |= (((long) b[5] & 0xff) << 40);
	// l |= (((long) b[4] & 0xff) << 32);
	// l |= (((long) b[3] & 0xff) << 24);
	// l |= (((long) b[2] & 0xff) << 16);
	// l |= (((long) b[1] & 0xff) << 8);
	// l |= ((long) b[0] & 0xff);
	// return l;
	// }

	/**
	 * 
	 * @Title bytes2HexString
	 * @Description byte数组转为十六进制显示的字符串
	 * @author ouArea
	 * @date 2013-11-16 上午1:08:23
	 * @param b
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String bytes2HexString(byte[] b, int offset, int len) {
		StringBuffer ret = new StringBuffer();
		for (int i = offset; i < len; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				ret.append("0").append(hex);
			} else {
				ret.append(hex);
			}
		}
		return ret.toString().toUpperCase();
	}

	// /**
	// * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	// *
	// * @param src0
	// * byte
	// * @param src1
	// * byte
	// * @return byte
	// */
	// public static byte uniteBytes(byte src0, byte src1) {
	// byte _b0 = Byte.decode("0x" + new String(new byte[] { src0
	// })).byteValue();
	// _b0 = (byte) (_b0 << 4);
	// byte _b1 = Byte.decode("0x" + new String(new byte[] { src1
	// })).byteValue();
	// byte ret = (byte) (_b0 ^ _b1);
	// return ret;
	// }

	// /**
	// * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	// * 0xD9}
	// *
	// * @param src
	// * String
	// * @return byte[]
	// */
	// public static byte[] hexString2Bytes(String src) {
	// byte[] ret = new byte[8];
	// byte[] tmp = src.getBytes();
	// for (int i = 0; i < 8; i++) {
	// ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
	// }
	// return ret;
	// }
	/**
	 * 
	 * @Title hexStringToBytes
	 * @Description 十六进制显示的字符串转化为原生byte数组
	 * @author ouArea
	 * @date 2013-11-16 上午1:08:44
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 
	 * @Title charToByte
	 * @Description char转化为byte
	 * @author ouArea
	 * @date 2013-11-16 上午1:09:37
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
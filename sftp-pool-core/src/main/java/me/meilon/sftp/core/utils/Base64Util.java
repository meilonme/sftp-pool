package me.meilon.sftp.core.utils;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class Base64Util {

	/**
	 * InputStream 转换成 Base64 编码
	 * @param imgFileIo 入参必须是读取图片文件的 IO 流
	 * @return Base64 编码字串
	 */
	public static String generateBase64(InputStream imgFileIo) {
		if (null == imgFileIo){
			return null;
		}
		try {
			byte[] bytes = IOUtils.toByteArray(imgFileIo);
			return Base64.encodeBase64String(bytes);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				imgFileIo.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * base64编码转InputStream
	 * @param base64String base64编码
	 */
	public static InputStream baseToInputStream(String base64String) {
		byte[] bytes1 = Base64.decodeBase64(base64String);
		return new ByteArrayInputStream(bytes1);
	}

}

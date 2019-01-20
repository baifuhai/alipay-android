package com.alipay.sdk.pay.demo.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class SignUtil {

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	public static String sign(String content, String privateKey, String signType) throws Exception {
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		String s = signType.equals("RSA2") ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
		java.security.Signature signature = java.security.Signature.getInstance(s);

		signature.initSign(priKey);
		signature.update(content.getBytes(DEFAULT_CHARSET));

		byte[] signed = signature.sign();

		return Base64.encode(signed);
	}

}

package com.alipay.sdk.pay.demo.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PayUtil {

	/**
	 * 外部订单号，必须唯一
	 *
	 * @return
	 */
	public static String getOutTradeNo() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date()) + new Random().nextInt(1000);
	}

	/**
     * 签名
	 *
	 * @param map
     * @param privateKey
     * @param signType
     * @return
     * @throws Exception
	 */
	public static String sign(Map<String, String> map, String privateKey, String signType) throws Exception {
		List<String> keys = new ArrayList<>(map.keySet());
		Collections.sort(keys);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = map.get(key);
			if (i > 0) {
				sb.append("&");
			}
			sb.append(key + "=" + value);
		}

		String sign = SignUtil.sign(sb.toString(), privateKey, signType);
		map.put("sign", sign);

		keys.add("sign");

		sb.setLength(0);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = map.get(key);
			if (i > 0) {
				sb.append("&");
			}
			sb.append(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		return sb.toString();
	}

}

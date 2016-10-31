package org.demo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

public class WechatJsSign {

	private final static Logger LOG = Logger.getLogger(WechatJsSign.class);
	private String url = "";
	private String jsapiTicket = "";
	private String appId = "";
	private Map<String, String> ret;

	public WechatJsSign() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		WechatJsSign wechatJsSign = new WechatJsSign();
		wechatJsSign.setUrl("http://example.com");
		wechatJsSign.setAppId("wxb011e7747898ad8c");
		wechatJsSign.sign();
		for (Map.Entry entry : wechatJsSign.getRet().entrySet()) {
			LOG.debug(entry.getKey() + ", " + entry.getValue());
		}
	};

	public Map<String, String> sign() throws Exception {
		jsapiTicket = WechatBaseService.getInstance().getJsapiTicket(appId);
		ret = new HashMap<String, String>();
		// String nonce_str = create_nonce_str();
		// String timestamp = create_timestamp();
		String nonce_str = "fffcee7b-4e31-4560-a511-181eab3c236b";
		String timestamp = "1477653916";
		jsapiTicket = "jsapi_ticket";

		String string1;
		String signature = "";

		string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
		LOG.debug(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapiTicket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		LOG.debug(ret);

		return ret;
	}

	private String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	private String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getRet() {
		return ret;
	}

	public void setRet(Map<String, String> ret) {
		this.ret = ret;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getJsapiTicket() {
		return jsapiTicket;
	}

}

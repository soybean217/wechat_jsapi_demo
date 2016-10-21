package org.demo.info;

import org.apache.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RspToken {

	private static final Logger LOG = Logger.getLogger(RspToken.class);

	@Expose
	@SerializedName("access_token")
	private String accessToken;
	@Expose
	@SerializedName("expires_in")
	private long expiresIn;
	private String appId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	@Override
	public String toString() {
		return "RspToken [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", appId=" + appId + "]";
	}

	public boolean verify() {
		if (accessToken != null && accessToken.length() > 0 && expiresIn > 0) {
			return true;
		} else {
			LOG.error("verify fail:" + this);
			return false;
		}
	}

}

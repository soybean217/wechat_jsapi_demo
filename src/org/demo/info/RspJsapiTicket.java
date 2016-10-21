package org.demo.info;

import org.apache.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RspJsapiTicket {

	private static final Logger LOG = Logger.getLogger(RspJsapiTicket.class);

	private String ticket;
	@Expose
	@SerializedName("expires_in")
	private long expiresIn;
	private String appId;
	private int errcode;
	private String errmsg;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public boolean verify() {
		if (ticket != null && ticket.length() > 0 && expiresIn > 0) {
			return true;
		} else {
			LOG.error("verify fail:" + this);
			return false;
		}
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public String toString() {
		return "RspJsapiTicket [ticket=" + ticket + ", expiresIn=" + expiresIn + ", appId=" + appId + ", errcode=" + errcode
				+ ", errmsg=" + errmsg + "]";
	}

}

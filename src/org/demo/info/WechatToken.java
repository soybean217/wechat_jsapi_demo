package org.demo.info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.common.util.ConnectionService;

public class WechatToken {

	private final static Logger LOG = Logger.getLogger(WechatToken.class);

	private String appId;
	private String token;
	private Long nextTime;
	private Long lastModTime;
	private Long validTime;
	private String secret;
	private String jsapiTicket;
	private Long jsapiTicketValidTime;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public boolean isInValidTime() {
		boolean result = true;
		if (this.validTime <= System.currentTimeMillis() + 1000000) {
			result = false;
		}
		return result;
	}

	public boolean isJsapiTicketInValidTime() {
		boolean result = true;
		if (this.jsapiTicketValidTime <= System.currentTimeMillis() + 1000000) {
			result = false;
		}
		return result;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getNextTime() {
		return nextTime;
	}

	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}

	public Long getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(Long lastModTime) {
		this.lastModTime = lastModTime;
	}

	public Long getValidTime() {
		return validTime;
	}

	public void setValidTime(Long validTime) {
		this.validTime = validTime;
	}

	public String getJsapiTicket() {
		return jsapiTicket;
	}

	public void setJsapiTicket(String jsapiTicket) {
		this.jsapiTicket = jsapiTicket;
	}

	public Long getJsapiTicketValidTime() {
		return jsapiTicketValidTime;
	}

	public void setJsapiTicketValidTime(Long jsapiTicketValidTime) {
		this.jsapiTicketValidTime = jsapiTicketValidTime;
	}

	public WechatToken updateDb() {
		LOG.info(this);
		// String sql = "update `tbl_wechat_tokens` set
		// token=?,nextTime=?,lastModTime=?,validTime=? where appId=?";
		String sql = "update `tbl_wechat_tokens` set token=?,lastModTime=?,validTime=?,jsapiTicket=?,jsapiTicketValidTime=? where appId=?";
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		try {
			con = ConnectionService.getInstance().getConnectionForLocal();
			ps = con.prepareStatement(sql);
			int m = 1;
			Long now = System.currentTimeMillis();
			ps.setString(m++, this.getToken());
			// ps.setLong(m++,
			// Long.parseLong(ConfigManager.getConfigData("nextSuccessInterval")) +
			// now);
			ps.setLong(m++, now);
			ps.setLong(m++, this.getValidTime());
			ps.setString(m++, this.getJsapiTicket());
			ps.setLong(m++, this.getJsapiTicketValidTime());
			ps.setString(m++, appId);
			if (ps.executeUpdate() == 0) {
				LOG.error("update failure:" + this);
			} else {
				LOG.info("updated:" + appId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this;
	}

}

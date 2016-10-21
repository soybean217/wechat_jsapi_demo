package org.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.common.util.ConnectionService;
import org.demo.info.RspJsapiTicket;
import org.demo.info.RspToken;
import org.demo.info.WechatToken;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WechatBaseService {

	private static final Logger LOG = Logger.getLogger(WechatBaseService.class);
	private static WechatBaseService instance = new WechatBaseService();

	private WechatBaseService() {
	}

	public static WechatBaseService getInstance() {
		return instance;
	}

	private static LoadingCache<String, WechatToken> cache;
	static {
		cache = CacheBuilder.newBuilder().maximumSize(20)// 设置大小，条目数
				.expireAfterWrite(10, TimeUnit.SECONDS)// 设置失效时间，创建时间
				.expireAfterAccess(20, TimeUnit.HOURS) // 设置时效时间，最后一次被访问
				.removalListener(new RemovalListener<String, WechatToken>() { // 移除缓存的监听器
					public void onRemoval(RemovalNotification<String, WechatToken> notification) {
					}
				}).build(new CacheLoader<String, WechatToken>() { // 通过回调加载缓存
					@Override
					public WechatToken load(String name) throws Exception {
						return getTokenFromDb(name);
					}
				});
	}

	public String sendWechatInterface(String url, HttpEntity entity) throws ClientProtocolException, IOException {
		String result = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(url);

			httppost.setEntity(entity);

			LOG.debug("Executing request: " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				LOG.debug("----------------------------------------");
				LOG.debug(response.getStatusLine());
				result = EntityUtils.toString(response.getEntity());
				LOG.debug(result);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return result;
	}

	public WechatToken getAccessToken(String appId) throws Exception {
		WechatToken result = cache.get(appId);
		if (!result.isInValidTime()) {
			processToken(result);
			if (!result.isInValidTime()) {
				throw new IllegalStateException("Wechat token expired .appID:" + appId);
			}
		}
		return result;
	}

	public String getJsapiTicket(String appId) throws Exception {
		WechatToken wechatToken = getAccessToken(appId);
		if (!wechatToken.isJsapiTicketInValidTime()) {
			processJsapiTicket(wechatToken);
			if (!wechatToken.isJsapiTicketInValidTime()) {
				throw new IllegalStateException("Wechat JsapiTicket expired .appID:" + appId);
			}
		}
		return wechatToken.getJsapiTicket();
	}

	private static WechatToken getTokenFromDb(String appId)
			throws SQLException, IllegalArgumentException, IllegalStateException {
		LOG.debug("get record from db:" + appId);
		WechatToken result = new WechatToken();
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		try {
			con = ConnectionService.getInstance().getConnectionForLocal();
			String sql = "SELECT * FROM tbl_wechat_tokens where appId=?;";
			ps = con.prepareStatement(sql);
			int m = 1;
			ps.setString(m++, appId);
			rs = ps.executeQuery();
			if (rs.next()) {
				result.setAppId(rs.getString("appId"));
				result.setToken(rs.getString("token"));
				result.setNextTime(rs.getLong("nextTime"));
				result.setLastModTime(rs.getLong("lastModTime"));
				result.setValidTime(rs.getLong("validTime"));
				result.setSecret(rs.getString("secret"));
				result.setJsapiTicket(rs.getString("jsapiTicket"));
				result.setJsapiTicketValidTime(rs.getLong("jsapiTicketValidTime"));
			} else {
				throw new IllegalArgumentException("Can not find token of  appId:" + appId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private String getAccessTokenFromWechat(WechatToken wechatToken) throws ClientProtocolException, IOException {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + wechatToken.getAppId()
				+ "&secret=" + wechatToken.getSecret();
		return sendWechatInterface(url, null);
	}

	private String getJsapiTicketFromWechat(WechatToken wechatToken) throws ClientProtocolException, IOException {
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + wechatToken.getToken()
				+ "&type=jsapi";
		return sendWechatInterface(url, null);
	}

	private void processToken(WechatToken wechatToken) throws SQLException, Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		String rsp = getAccessTokenFromWechat(wechatToken);
		LOG.info("receive from tencent:" + rsp);
		RspToken rspToken = gson.fromJson(rsp, RspToken.class);
		if (rspToken.verify()) {
			wechatToken.setValidTime(rspToken.getExpiresIn() * 1000 + System.currentTimeMillis());
			wechatToken.setToken(rspToken.getAccessToken());
			wechatToken.updateDb();
		}
	}

	private void processJsapiTicket(WechatToken wechatToken) throws SQLException, Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		String rsp = getJsapiTicketFromWechat(wechatToken);
		LOG.info("receive processJsapiTicket from tencent:" + rsp);
		RspJsapiTicket rspJsapiTicket = gson.fromJson(rsp, RspJsapiTicket.class);
		if (rspJsapiTicket.verify()) {
			wechatToken.setJsapiTicketValidTime(rspJsapiTicket.getExpiresIn() * 1000 + System.currentTimeMillis());
			wechatToken.setJsapiTicket(rspJsapiTicket.getTicket());
			wechatToken.updateDb();
		}
	}

	public static void main(String[] args) throws Exception {
		// LOG.debug(WechatBaseService.getInstance().getAccessToken("wxb011e7747898ad8c"));
		LOG.debug(WechatBaseService.getInstance().getJsapiTicket("wxb011e7747898ad8c"));
		LOG.debug(WechatBaseService.getInstance().getJsapiTicket("wxb011e7747898ad8c"));
	}
}

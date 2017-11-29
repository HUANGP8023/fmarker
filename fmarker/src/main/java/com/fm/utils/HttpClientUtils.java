package com.fm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * @author HUANGP
 * @date 2017-7-26 下午2:48:27
 * @des HTTP 请求工具包
 */
@SuppressWarnings({ "deprecation", "unused" })
public class HttpClientUtils {

	private static int CONNECT_TIME_OUT = 1000 * 15;// 设置连接超时时间，单位毫秒。
	private static int REQUEST_TIME_OUT = 1000 * 5;// 设置从connect
													// Manager获取Connection超时时间，单位毫秒
	private static int SOCKET_TIME_OUT = 1000 * 15;// 请求获取数据的超时时间，单位毫秒。(
													// 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。）

	/**
	 * @des HTTP POST 请求
	 * @author HUANGP
	 * @date 2017年7月26日
	 * @param reqUrl   请求地址
	 * @param params   请求参数（支持Map,非Map参数按String类型请求）
	 * @param keyStorePath  证书路径
	 * @param keyStorePass  证书秘钥
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String httpPost(String reqUrl, Object params,String keyStorePath,String keyStorePass) throws Exception {
		CloseableHttpClient httpclient = null;
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		String resStr = "";
		try {
			httpclient = createHttpClient(reqUrl, keyStorePath, keyStorePass);
			httpPost = new HttpPost(reqUrl);
			httpPost.setHeader("Content-type","application/x-www-form-urlencoded");
			httpPost.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			httpPost.setConfig(initRequestConfig());

			if (params instanceof Map) {
				Map paramsMap = (Map) params;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				if (paramsMap != null && paramsMap.size() > 0) {
					Set<Entry<String, Object>> set = paramsMap.entrySet();
					Iterator<Entry<String, Object>> iterator = set.iterator();
					while (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						String key = entry.getKey();
						String value = String.valueOf(entry.getValue());
						nameValuePairs.add(new BasicNameValuePair(key, value));
					}
				}
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "utf-8");
				httpPost.setEntity(urlEncodedFormEntity);
			} else {
				StringEntity param = new StringEntity(String.valueOf(params),"utf-8");
				httpPost.setEntity(param);
			}
			response = httpclient.execute(httpPost);
			resStr = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null)response.close();
				if (httpPost != null)httpPost.releaseConnection();
				if (httpclient != null)httpclient.close();
			} catch (Exception ex) {
				throw ex;
			}
		}
		return resStr;
	}

	/**
	 * @des HTTP GET 请求
	 * @author HUANGP
	 * @date 2017年7月26日
	 * @param reqUrl         请求地址
	 * @param keyStorePath   证书路径
	 * @param keyStorePass   证书秘钥
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String reqUrl, String keyStorePath,String keyStorePass) throws Exception {
		CloseableHttpClient httpclient = null;
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		String resStr = "";
		try {
			httpclient = createHttpClient(reqUrl, keyStorePath, keyStorePass);
			httpGet = new HttpGet(reqUrl);
			httpGet.setHeader("Content-type","application/x-www-form-urlencoded");
			httpGet.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			httpGet.setConfig(initRequestConfig());
			response = httpclient.execute(httpGet);
			resStr = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null)response.close();
				if (httpGet != null)httpGet.releaseConnection();
				if (httpclient != null)httpclient.close();
			} catch (Exception ex) {
				throw ex;
			}
		}
		return resStr;
	}
	
	
	public static String parseParamsFromHttp(InputStream inputStream) throws Exception {
		StringBuffer requestBuffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String readLine;
		while ((readLine = reader.readLine()) != null) {
			requestBuffer.append(readLine);
		}
		reader.close();
		String params = requestBuffer.toString();
		return params;
	}

	/**
	 * @author HUANGP
	 * @date 2017年7月26日
	 * @des 请求参数配置
	 */
	private static RequestConfig initRequestConfig() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT).setConnectionRequestTimeout(REQUEST_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT).build();
		return requestConfig;
	}

	private static CloseableHttpClient createHttpClient(String host,String keyStorePath, String keyStorePass) throws Exception {

		SSLContext sslcontext = null;
		if (StringUtils.isBlank(keyStorePath)|| StringUtils.isBlank(keyStorePass)) {
			sslcontext = createIgnoreVerifySSL();
		} else {
			sslcontext = custom(keyStorePath, keyStorePass);
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext))
				.build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		// 将最大连接数增加到200
		cm.setMaxTotal(200);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(20);

		CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(new HttpRequestRetryHandler() {

					/* 重新请求自定义规则 */
					public boolean retryRequest(IOException exception,int executionCount, HttpContext context) {
						if (executionCount < 1)
							return true;
						else
							return false;
					}
					

				}).setConnectionManager(cm).build();

		return httpClient;
	}

	/**
	 * 信任证书
	 * @param keyStorePath 密钥库路径
	 * @param keyStorepass 密钥库密码
	 * @return
	 * @throws Exception
	 */
	private static SSLContext custom(String keyStorePath, String keyStorepass)
			throws Exception {
		SSLContext sc = null;
		FileInputStream instream = null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File(keyStorePath));
			trustStore.load(instream,keyStorepass.toCharArray());
			sc = SSLContexts.custom().loadTrustMaterial(trustStore,new TrustSelfSignedStrategy()).build();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				instream.close();
			} catch (IOException ex) {
				throw ex;
			}
		}
		return sc;
	}

	/**
	 * @des 绕过证书
	 * @author HUANGP
	 * @date 2017年7月26日
	 * @return
	 * @throws Exception
	 */
	private static SSLContext createIgnoreVerifySSL() throws Exception {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] xcs, String str) {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String str) {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
		} catch (Exception ex) {
			throw ex;
		}
		return ctx;
	}

	

	public static void main(String[] args) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aaaaaaa","33333");
		params.put("bbbbbbb","11111");
		params.put("ccccccc","22222");
		System.out.println(httpPost("http://127.0.0.1:8080/fmarker/v1.2.2/hello",params, "", ""));
	}

}
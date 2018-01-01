package cn.edu.pku.sei.tsr.dragon.utils;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public static String get(URI uri) {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpclient.execute(new HttpGet(uri));
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(String uri) {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			CloseableHttpResponse response = httpclient.execute(new HttpGet(uri));
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

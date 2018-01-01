package cn.edu.pku.sei.tsr.dragon.stackoverflow.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.sei.tsr.dragon.utils.HttpUtils;

public class StackExchange {
	public static List<SearchResult> search(String index, String tag) {
		List<SearchResult> results = new ArrayList<>();
		int page = 0;
		while (results.size() < 10) {
			URI uri = getSearchURI(++page, index, tag);
			JSONObject json = new JSONObject(HttpUtils.get(uri));
			JSONArray items = json.getJSONArray("items");
			boolean hasMore = json.getBoolean("has_more");
			for (Object obj : items) {
				JSONObject item = (JSONObject) obj;
				if (!item.has("accepted_answer_id")) continue;
				String title = item.getString("title");
				String link = item.getString("link");
				int questionID = item.getInt("question_id");
				results.add(new SearchResult(title, link, questionID));
			}
			if (!hasMore) break;
		}

		return results.subList(0, Math.min(10, results.size()));
	}

	public static <T> List<T> search(String index, String tag, int limit, Function<SearchResult, T> callback) {
		List<T> results = new ArrayList<>();
		int page = 0;
		while (results.size() < limit) {
			URI uri = getSearchURI(++page, index, tag);
			JSONObject json = new JSONObject(HttpUtils.get(uri));
			JSONArray items = json.getJSONArray("items");
			boolean hasMore = json.getBoolean("has_more");
			for (Object obj : items) {
				JSONObject item = (JSONObject) obj;
				if (!item.has("accepted_answer_id")) continue;
				String title = item.getString("title");
				String link = item.getString("link");
				int questionID = item.getInt("question_id");
				T result = callback.apply(new SearchResult(title, link, questionID));
				if (result != null) results.add(result);
				if (results.size() == limit) break;
			}
			if (!hasMore) break;
		}

		return results;
	}

	private static URI getSearchURI(int page, String index, String tag) {
		try {
			return new URIBuilder()
				.setScheme("http")
				.setHost("api.stackexchange.com")
				.setPath("/2.2/search")
				.setParameter("page", "" + page)
				.setParameter("pagesize", "20")
				.setParameter("order", "desc")
				.setParameter("sort", "relevance")
				.setParameter("tagged", tag)
				.setParameter("intitle", index)
				.setParameter("site", "stackoverflow")
				.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}

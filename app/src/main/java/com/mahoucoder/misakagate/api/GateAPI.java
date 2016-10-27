package com.mahoucoder.misakagate.api;

import com.mahoucoder.misakagate.activities.OKHTTPClientFactory;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jamesji on 28/10/2016.
 */

public class GateAPI {
    private static final String ANIME_BASE_URL = "http://anime.2d-gate.org";
    public static final String ANIME_CACHE_URL = ANIME_BASE_URL + "/__cache.html";

    public static Response getAnimeListDOM() throws IOException {
        OkHttpClient client = OKHTTPClientFactory.getClient();
        Request request = new Request.Builder().url(ANIME_CACHE_URL)
                .header("User-Agent", "Android Application by MahouCoder")
                .build();
        return client.newCall(request).execute();
    }

//    private List<Anime> getAnimeList() throws IOException {
//        Response response = getAnimeListDOM();
//        Document parsedDocument = Jsoup.parse(response.body().byteStream(), "utf-8", GateAPI.ANIME_CACHE_URL);
//        Element body = parsedDocument.body();
//        Element animeList = body.getElementById("animeList");
//        Elements thead = animeList.getElementsByTag("thead");
//        Elements tbody = animeList.getElementsByTag("tbody");
//        Elements tr = tbody.select("tr");
//        Log.d("CachePageParsing", tr.size() + " items found in table. ");
//        Iterator<Element> trIterator = tr.iterator();
//        ArrayList<Anime> animeArrayList = new ArrayList<>();
//        while (trIterator.hasNext()) {
//            Iterator<Element> tdIterator = trIterator.next().select("td").iterator();
//            animeArrayList.add(new Anime(tdIterator.next().))
//        }
//    }
}

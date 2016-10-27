package com.mahoucoder.misakagate.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.models.Anime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetDOMTask().execute();
    }

    class GetDOMTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response response = GateAPI.getAnimeListDOM();
                Document parsedDocument = Jsoup.parse(response.body().byteStream(), "utf-8", GateAPI.ANIME_CACHE_URL);
                Element body = parsedDocument.body();
                Element animeList = body.getElementById("animeList");
                Elements thead = animeList.getElementsByTag("thead");
                Elements tbody = animeList.getElementsByTag("tbody");
                Elements tr = tbody.select("tr");
                Log.d("CachePageParsing", tr.size() + " items found in table. ");
                Iterator<Element> trIterator = tr.iterator();
                ArrayList<Anime> animeArrayList = new ArrayList<>();
                ArrayList<String> animeReprStringArrayList = new ArrayList<>();
                while (trIterator.hasNext()) {
                    Iterator<Element> tdIterator = trIterator.next().select("td").iterator();
                    StringBuilder sb = new StringBuilder();
                    while (tdIterator.hasNext()) {
                        sb.append(tdIterator.next().html());
                    }
                    animeReprStringArrayList.add(sb.toString());
                }
                System.out.println(animeReprStringArrayList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
package com.example.kakehi.itunesmusicsearch;

import com.example.kakehi.itunesmusicsearch.QiitaClient.QiitaService;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QiitaListActivity extends AppCompatActivity {

    private QiitaListAdapter qiitaListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qiita_activity_list);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://qiita.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        qiitaListAdapter = new QiitaListAdapter(getApplicationContext(), R.layout.list_item);
        // ListViewに表示
        ListView myListView = (ListView) findViewById(R.id.qiita_list_view);
        myListView.setAdapter(qiitaListAdapter);

        QiitaService service = retrofit.create(QiitaService.class);
        Call<List<Repo>> call = service.listRepos("java");
        call.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {

                if (call.isExecuted()) {
                    List<Repo> result = response.body();
                    List<Repo> repos = new ArrayList<>();

                    // 拡張for文
                    for (Repo r : result) {
                        Repo repo = new Repo();
                        User user = new User();
                        user.setProfile_image_url(r.getUser().profile_image_url);
                        repo.setUser(user);
                        repo.setId(r.id);
                        repo.setTitle(r.title);
                        repo.setUrl(r.url);
                        repos.add(repo);
                        qiitaListAdapter.add(repos);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {}
        });
    }

    public class QiitaListAdapter extends ArrayAdapter<List<Repo>> {

        private LayoutInflater layoutInflater;

        public QiitaListAdapter(Context c, int id) {
            super(c, id);
            this.layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView titleView = (TextView) convertView.findViewById(R.id.track_text_view);
            TextView urlView = (TextView) convertView.findViewById(R.id.artist_text_view);
            List<Repo> repo = (List<Repo>) getItem(i);

//            //todo iconのURLにしないと
//            imageView.setTag(repo.get(i).getProfile_image_url());
            imageView.setTag(repo.get(i).getUser().getProfile_image_url());
            titleView.setText(repo.get(i).getTitle());
            urlView.setText(repo.get(i).getUrl());


            Log.v("profile image", repo.get(i).getUser().getProfile_image_url());
            Log.v("debug", repo.get(i).title);

            return convertView;
        }
    }
}

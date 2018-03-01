package com.example.kakehi.itunesmusicsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ListActivity extends Activity {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruImageCache());

        mAdapter = new ListAdapter(this, R.layout.list_item);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener());

        final EditText editText = (EditText) findViewById(R.id.edit_text);
        editText.setOnKeyListener(new OnKeyListener());
    }

    private class ListAdapter extends ArrayAdapter<JSONObject> {

        public ListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // 再利用可能なViewがない場合は作る
                //親のViewGroupを指定せずにlist_itemViewを作っているだけ。
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView trackTextView = (TextView) convertView.findViewById(R.id.track_text_view);
            TextView artistTextView = (TextView) convertView.findViewById(R.id.artist_text_view);

            ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) imageView.getTag();
            if (imageContainer != null) {
                imageContainer.cancelRequest(); // 画像取得中のリクエストをキャンセルする（再利用された時）
            }

            imageView.setImageBitmap(null); // 残ってる画像を消す（再利用された時）

            // 表示する行番号のデータを取り出す
            JSONObject result = getItem(position);

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
            imageView.setTag(mImageLoader.get(result.optString("artworkUrl100"), listener));

            trackTextView.setText(result.optString("trackName"));
            artistTextView.setText(result.optString("artistName"));

            return convertView;
        }
    }

    private class OnKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                EditText editText = (EditText) view;

                // キーボードを閉じる
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                // urlエンコード
                String text = editText.getText().toString();
                try {
                    // url encode　例. スピッツ > %83X%83s%83b%83c
                    text = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("", e.toString(), e);
                    return true;
                }

                if (!TextUtils.isEmpty(text)) {
                    String url = "https://itunes.apple.com/search?term=" + text + "&country=JP&media=music&lang=ja_jp";
                    mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, url, listener, errorListener));
                }
                return true;
            }
            return false;
        }

        private Response.Listener listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("", response.toString());
                // 前の検索結果があれば上書きしたいので消去する。
                mAdapter.clear();

                JSONArray results = response.optJSONArray("results");
                if (results != null) {
                    for (int i = 0; i < results.length(); i++) {
                        mAdapter.add(results.optJSONObject(i));
                    }
                }

            }
        };

        private Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "http request errored");
                Log.d("Error Message", error.getMessage());
            }

        };
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(ListActivity.this, DetailActivity.class);

            // タップされた行番号のデータを取り出す
            JSONObject result = mAdapter.getItem(position);
            intent.putExtra("track_name", result.optString("trackName"));
            intent.putExtra("preview_url", result.optString("previewUrl"));

            startActivity(intent);
        }
    }
}

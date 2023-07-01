package com.example.camera2apisample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostAsyncHttpRequest extends AsyncTask<PostAsyncHttpRequest.Param, Void, String> {
    private Activity mActivity;

    public PostAsyncHttpRequest(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(Param... params) {
        Param param = params[0];
        Log.e("doInbackground", param.uri);
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        try {
            // 画像をjpeg形式でstreamに保存
            ByteArrayOutputStream jpg = new ByteArrayOutputStream();
            param.bmp.compress(Bitmap.CompressFormat.JPEG, 100, jpg);

            URL url = new URL(param.uri);
            CookieManager cm = new CookieManager();
            CookieHandler.setDefault(cm);

            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");

            // ヘッダーを設定
            connection.setDoInput(true);    // リクエストのボディ送信を許可
            connection.setUseCaches(false); // キャッシュを使用しない

            connection.setRequestProperty("User-Agent", "Android");
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            // 接続開始
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.e("GET responseCode", "" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // CookieからCSRF Token取得
                List<HttpCookie> cookieList = cm.getCookieStore().getCookies();
                for (HttpCookie cookie : cookieList) {
                    Log.e("cookie", cookie.toString());
                }
                String csrf = cookieList.get(0).toString();

                // POST通信開始
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                connection2.setRequestMethod("POST");
                connection2.setDoInput(true);    // リクエストのボディ送信を許可
                connection2.setDoOutput(true);   // レスポンスのボディ受信を許可、GETの場合falseに設定
                connection2.setRequestProperty("User-Agent", "Android");
                connection2.setRequestProperty("Content-Type", "application/octet-stream");

                // Tokenのみを取得
                String token = csrf.substring(10);
                Log.e("Token", token);
                // ヘッダーにCSRF Tokenをセット
                connection2.setRequestProperty("X-CSRFToken", token);
                // 画像データを投げる
                OutputStream out = new BufferedOutputStream(connection2.getOutputStream());
                out.write(jpg.toByteArray());
                out.flush();
                // 接続
                connection2.connect();

                // レスポンスコード取得
                responseCode = connection2.getResponseCode();
                Log.e("POST responseCode:", "" + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // サーバーからのレスポンスデータを受け取る
                    InputStream is = connection2.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("response data=", sb.toString());
                    is.close();
                }
                connection2.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection!=null)
                connection.disconnect();
        }
        return sb.toString();
    }

    public void onPostExecute(String string) {
        // 終了時に呼び出される
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    static class Param {
        public String uri;
        public Bitmap bmp;
        public Param(String uri, Bitmap bmp) {
            this.uri = uri;
            this.bmp = bmp;
        }
    }
}

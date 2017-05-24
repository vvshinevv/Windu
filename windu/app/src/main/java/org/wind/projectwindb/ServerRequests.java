package org.wind.projectwindb;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerRequests {

    private String TAGLOG = "로그: ServerRequests: ";
    public static final int CONNECTION_TIMEOUT = 1000 * 5;


    ProgressDialog progressDialog;
    Context context;
    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("잠시만 기다려주세요...");
        this.context = context;
    }

    public void GetUserDataFromServer(String urlMessage, GetDataCallback dataCallback) {
        new GetUserDataAsyncTask(dataCallback).execute(urlMessage);
    }

    public void GetUserDataFromServerUsingProgressDialog(String urlMessage, GetDataCallback dataCallback) {
        new GetUserDataUsingProgressDialogAsyncTask(dataCallback).execute(urlMessage);
    }

    public class GetUserDataAsyncTask extends AsyncTask<String, String, String> {

        GetDataCallback dataCallback;

        public GetUserDataAsyncTask(GetDataCallback dataCallback) {
            this.dataCallback = dataCallback;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();

            HttpClient httpClient = new DefaultHttpClient();

            HttpParams httpRequestParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpPost httpPost = new HttpPost(params[0]);

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();

                if(statusLine.getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String lineContents;

                    while((lineContents = bufferedReader.readLine()) != null) {
                        Log.e("Line", lineContents);
                        stringBuilder.append(lineContents);
                    }
                }
            } catch (Exception e) {
                Log.i(TAGLOG, "HttpClient.execute err: " + e.getStackTrace());
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dataCallback.done(s);
        }
    }

    public class GetUserDataUsingProgressDialogAsyncTask extends AsyncTask<String, String, String> {

        GetDataCallback dataCallback;


        public GetUserDataUsingProgressDialogAsyncTask(GetDataCallback dataCallback) {
            progressDialog.show();
            this.dataCallback = dataCallback;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();

            HttpClient httpClient = new DefaultHttpClient();

            HttpParams httpRequestParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpPost httpPost = new HttpPost(params[0]);

            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                StatusLine statusLine = httpResponse.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String lineContents;

                    while ((lineContents = bufferedReader.readLine()) != null) {
                        Log.e("Line", lineContents);
                        stringBuilder.append(lineContents);
                    }
                }
            } catch (Exception e) {
                Log.i(TAGLOG, "HttpClient.execute err: " + e.getStackTrace());
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            dataCallback.done(s);
        }
    }
}

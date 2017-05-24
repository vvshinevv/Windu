package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SettingCategoryActivity
* 설명 :
*   카테고리를 입력하는 UI가 존재하는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SettingCategoryActivity extends FragmentActivity {
    // LOG TAG
    private String TAGLOG = "로그 : SettingCategoryActivity : ";

    EditText categoryEDT;

    String ReturnMSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settingcategory);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        TextView backbtn = (TextView)findViewById(R.id.Button_settingcategory_backbutton);
        TextView helpmebtn = (TextView)findViewById(R.id.Button_settingcategory_button);

        categoryEDT = (EditText)findViewById(R.id.EditText_settingcategory_category);



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        helpmebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();

                String CheckEception = categoryEDT.getText().toString();
                if(CheckEception.contains(" ") || CheckEception.contains(".") || CheckEception.contains("#") || CheckEception.contains("\n") || CheckEception.contains(","))
                {
                    Toast.makeText(getApplicationContext(),"띄어쓰기 및 특수문자는 사용하실 수 없습니다.",Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 1; i <= 5; i++) {
                        String temp = pref.getString("CATEGORYUSE" + String.valueOf(i), "-1");
                        if (temp.equals("-1")) {
                            if (!categoryEDT.getText().toString().equals("")) {
                                edit.putString("CATEGORY_TEXT" + String.valueOf(i), categoryEDT.getText().toString());
                                edit.putString("CATEGORYUSE" + String.valueOf(i), "1");
                                edit.commit();

                                String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");
                                String tempCategory[] = new String[6];
                                int tempCategoryCnt = 0;
                                for (int j = 1; j <= 5; j++) {
                                    String temp2 = pref.getString("CATEGORYUSE" + String.valueOf(j), "-1");
                                    if (temp2.equals("1")) {
                                        tempCategory[j] = pref.getString("CATEGORY_TEXT" + String.valueOf(j), "-1");
                                        tempCategoryCnt++;
                                    } else {
                                        tempCategory[j] = "";
                                    }
                                }

                                String message = "http://222.116.135.136/ctsetting.php?usercode=" + tempCode + "&ctc=" + String.valueOf(tempCategoryCnt) +
                                        "&ct1=" + tempCategory[1] + "&ct2=" + tempCategory[2] + "&ct3=" + tempCategory[3] + "&ct4=" + tempCategory[4] + "&ct5=" + tempCategory[5];
                                Log.i(TAGLOG, "카테고리 등록 로깅 : " + message);

                                settingCategoryFromServer(message);
                                //GetDataFromSever getdatafromserver = new GetDataFromSever();
                                //getdatafromserver.execute(message);
                                break;
                            }
                        }
                    }
                    finish();
                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void settingCategoryFromServer(String urlMessage) {
        Log.e(TAGLOG, "settingCategoryFromServer: " + "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String returnedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        returnedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "settingCategoryFromServer by hong: " + returnedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "settingCategoryFromServer Error by hong: " + returnedCode);

                }
            }
        });
    }

    //JSON 통신 //////////////////////////////////////////////////////////////////////////
    private class GetDataFromSever extends AsyncTask<String, String, String> {
        JSONArray countriesArray;

        @Override
        protected String doInBackground(String... urls){
            ArrayList<NameValuePair> post = new ArrayList<>();

            //post.add(new BasicNameValuePair("firstdata","1"));

            StringBuilder builder = new StringBuilder();

            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httppost = new HttpPost(urls[0]);

            try{
                UrlEncodedFormEntity urlencodedfromentity = new UrlEncodedFormEntity(post,"UTF-8");
                httppost.setEntity(urlencodedfromentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == 200){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while((line = reader.readLine())!=null) {
                        Log.i(TAGLOG, "line : " + line);
                        builder.append(line);
                    }

                    //응답받은 데이터 파싱
                    try{

                        JSONArray root = new JSONArray(builder.toString());
                        for (int i=0; i < root.length();i++){
                            JSONObject jobject = root.getJSONObject(i);

                        }
                        ReturnMSG="1";
                    } catch (JSONException e){
                        Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                        ReturnMSG="0";
                        e.printStackTrace();
                    }


                }

            } catch (Exception e) {
                Log.i(TAGLOG,"Exception try1 : " + e.getStackTrace());
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result){
            if(ReturnMSG.equals("1")) {

            }
        }
    }

}
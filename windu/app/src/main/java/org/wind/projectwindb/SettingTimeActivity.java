package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SettingTimeActivity
* 설명 :
*   도움요청 알림 시간을 설정하는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
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

public class SettingTimeActivity extends FragmentActivity {
    // LOG TAG
    private String TAGLOG = "로그 : SettingTimeActivity : ";

    TextView time1;
    TextView time2;

    int flag1,flag2;
    int time1our,time1min;
    int time2our,time2min;

    String ReturnMSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settingtime);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        TextView backbtn = (TextView)findViewById(R.id.Button_settingtime_backbutton);
        TextView helpmebtn = (TextView)findViewById(R.id.Button_settingtime_button);

        time1 = (TextView)findViewById(R.id.TextView_settingtime_time1);
        time2 = (TextView)findViewById(R.id.TextView_settingtime_time2);

        flag1=0;
        flag2=0;

        time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(SettingTimeActivity.this , mTimeSetListener, 00, 00, false);
                dialog.show();
            }
        });

        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(SettingTimeActivity.this , mTimeSetListener2, 00, 00, false);
                dialog.show();
            }
        });


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
                if(flag1==1 && flag2==1) {
                    for (int i = 1; i <= 3; i++) {
                        String temp = pref.getString("TIMEUSE" + String.valueOf(i), "-1");
                        if (temp.equals("-1")) {

                            edit.putString("TIME_TEXT" + String.valueOf(i), time1.getText().toString() + "~" + time2.getText().toString());
                            edit.putString("TIMEUSE" + String.valueOf(i), "1");
                            edit.commit();

                            String tempCode = pref.getString("USER_MANAGEMENT_CODE","-1");

                            String message="http://222.116.135.136/setalarmtime.php?usercode="+tempCode+"&ain="+String.valueOf(i)+
                                    "&stime="+String.format("%02d",time1our)+":"+String.format("%02d",time1min)+":00&etime="+String.format("%02d",time2our)+":"+String.format("%02d",time2min)+":00";
                            Log.i(TAGLOG, "시간정보 등록 로깅 : " + message);

                            settingAlarmTimeFromServer(message);
                            //GetDataFromSever getdatafromserver = new GetDataFromSever();
                            //getdatafromserver.execute(message);

                            break;
                        }
                    }
                    finish();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void settingAlarmTimeFromServer(String urlMessage) {
        Log.e(TAGLOG, "settingAlarmTimeFromServer: " + "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String retrunedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        retrunedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "settingAlarmTimeFromServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "settingAlarmTimeFromServer Error by hong: " + retrunedCode);

                }
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String temp;
                    time1our=hourOfDay;
                    time1min=minute;
                    if(hourOfDay>12) {
                        temp=String.format("%s %02d:%02d","PM",hourOfDay-12,minute);
                    }else{
                        temp=String.format("%s %02d:%02d","AM",hourOfDay,minute);
                    }
                    time1.setText(temp);
                    time1.setBackgroundColor(Color.argb(100, 0, 150, 136));
                    flag1=1;
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener(){
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time2our=hourOfDay;
                    time2min=minute;
                    String temp;
                    if(hourOfDay>12) {
                        temp=String.format("%s %02d:%02d","PM",hourOfDay-12,minute);
                    }else{
                        temp=String.format("%s %02d:%02d","AM",hourOfDay,minute);
                    }

                    int time1value=(time1our*60)+time1min;
                    int time2value=(time2our*60)+time2min;

                    if(time1value<time2value) {
                        time2.setText(temp);
                        time2.setBackgroundColor(Color.argb(100, 0, 150, 136));
                        flag2=1;
                    }else{
                        Toast.makeText(getApplicationContext(),"알람시작 시각보다 작거나 같은시각은 설정하실 수 없습니다.",Toast.LENGTH_LONG).show();
                    }
                }
            };

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
                        Log.i(TAGLOG,"line : "+line);
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
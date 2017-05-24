/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : MainActivi
* 설명 : 스플래쉬 화면, 로그인 화면
* 내부 DB 목록 :
*  ID
*  PW
*  USER_MANAGEMENT_CODE FIRST_LOGIN
*  TOKEN
*  FIRST_LOGIN
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

package org.wind.projectwindb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



public class MainActivi extends Activity {

    // LOG TAG
    private String TAGLOG = "로그 : MainActivi : ";
    private String resultdata;
    private String user_management_code_data;
    private Boolean eceptioncode;

    // Resgistration Id from GCM
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "127059344199";
    private static final String WEB_SERVER_URL = "http://222.116.135.136/";

    GoogleCloudMessaging gcm;
    TextView regIdView;

    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    private String gcmRegId;

    String tempToken;

    public static Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //스플래쉬 코드//////////////////////////////////////////////////////////////////
        startActivity(new Intent(this, firstSplashActivity.class));
        //스플래쉬 코드//////////////////////////////////////////////////////////////////

        mContext=this;
    }

    @Override
    public void onResume(){
        super.onResume();

        setContentView(R.layout.activity_main);

        regIdView = (TextView) findViewById(R.id.regID);

        if (isGoogelPlayInstalled()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

            // Read saved registration id from shared preferences.
            gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");

            if (TextUtils.isEmpty(gcmRegId)) {
                new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WITH_GCM);
            }else{
                //regIdView.setText(gcmRegId);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("TOKEN", gcmRegId);
                editor.commit();
                //Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_SHORT).show();
            }
        }

        //서비스 사용자 수 체크 ////////////////////////////////////////////////////////////////////
        getUserCount("http://222.116.135.136/usercnt.php");

        //최초로그인 확인 코드//////////////////////////////////////////////////////////////////
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String loginstring = pref.getString("FIRST_LOGIN", "0");

        String tempID;
        String tempPW;
        String temptokentest;
        if(loginstring.equals("1")){

            Log.i(TAGLOG,"로그 :  여기의 내용을 실행");

            tempID = pref.getString("ID","-1");
            tempPW = pref.getString("PW","-1");
            temptokentest = pref.getString("TOKEN","-1");
            if(!tempID.equals("-1") &&!tempPW.equals("-1") && !temptokentest.equals("-1")) {
                String message = "http://222.116.135.136/login1.php?id=" + tempID + "&pw=" + tempPW + "&token=" + temptokentest;
                Log.i(TAGLOG,"자동로그인 메세지 로깅 (Login Message) : "+message);

                getDataFromServerWhenLogin(message);
            }

        }
        //최초로그인 확인 코드//////////////////////////////////////////////////////////////////

        final TextView emailtb = (TextView)findViewById(R.id.emailtb);
        final TextView pwtb = (TextView)findViewById(R.id.passwordtb);
        final TextView signuptb = (TextView)findViewById(R.id.signuptb);
        emailtb.setText(Html.fromHtml("<u>이메일</u>"));
        pwtb.setText(Html.fromHtml("<u>비밀번호</u>"));
        signuptb.setText(Html.fromHtml("<u>가입하기</u>"));

        //회원가입 이벤트 처리///////////////////////////////////////////////////////////////////
        signuptb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), signupActivity.class);
                startActivity(new Intent(intent_01));
            }
        });
        //회원가입 이벤트 처리///////////////////////////////////////////////////////////////////

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    //로그인 버튼 눌렸을 시 JSON POST
    public  void LoginButtonClicked(View view){
        final EditText IDet = (EditText)findViewById(R.id.ID_edittext);
        final EditText PWet = (EditText)findViewById(R.id.PW_edittext);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        tempToken = pref.getString("TOKEN","-1");
        String message="http://222.116.135.136/login1.php?id="+IDet.getText().toString()+"&pw="+PWet.getText().toString()+"&token="+tempToken;
        Log.i(TAGLOG,"로그인 메세지 로깅 (Login Message) : "+message);

        getDataFromServerWhenLogin(message);
        //GetDataFromSever getdatafromserver = new GetDataFromSever();
        //getdatafromserver.execute(message);
    }

    /**By honghee ToDo:확인요망 */
    private void getUserCount(String urlMessage) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String userTotalNumber = "";
                try {
                    JSONObject jsonObject = new JSONObject(dataRequiredToParsing);
                    for( int i = 0 ; i < jsonObject.length() ; i++) {
                        userTotalNumber = jsonObject.getString("result");
                        Log.e("honghong", userTotalNumber);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView Usercnt = (TextView) findViewById(R.id.TextView_MainActivity_Usercnt);
                Usercnt.setText(userTotalNumber);
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void getDataFromServerWhenLogin(String urlMessage) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServerUsingProgressDialog(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String resultCode = "";
                String userManagementCodeData = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        resultCode = jsonObject.getString("result");
                        Log.e("자동.. ", resultCode);
                        userManagementCodeData = jsonObject.getString("user_management_code");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(resultCode.equals("1")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = pref.edit();
                    final EditText IDet = (EditText) findViewById(R.id.ID_edittext);
                    final EditText PWet = (EditText) findViewById(R.id.PW_edittext);

                    if (pref.getString("FIRST_LOGIN", "-1").equals("-1")) {
                        edit.putString("ID", IDet.getText().toString());
                        edit.putString("PW", PWet.getText().toString());
                        edit.putString("TOKEN", tempToken);
                        edit.putString("FIRST_LOGIN", "1");
                        edit.putString("USER_MANAGEMENT_CODE", userManagementCodeData);
                        edit.commit();
                    }

                    Toast.makeText(getApplicationContext(), "로그인 되었습니다. GPS 정보를 확인합니다.", Toast.LENGTH_LONG).show();

                    Intent intent_02 = new Intent(getApplicationContext(), HelpMainActivity.class);
                    startActivity(new Intent(intent_02));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드가 틀립니다.", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    //이하 구글메세지 서비스 이용 코드/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Google Play Service is not installed",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }

    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    "AndroidSRCDemo", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public void saveInSharedPref(String result) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_GCM_REG_ID, result);
        editor.commit();
    }

    static class MyHandler extends Handler {
        private final WeakReference<MainActivi> mainActiviWeakReference;

        MyHandler(MainActivi activity) {
            mainActiviWeakReference = new WeakReference<MainActivi>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivi activity = mainActiviWeakReference.get();
            if(activity != null ) {
                activity.handleMessage(msg);
            }
        }
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case MSG_REGISTER_WITH_GCM:
                new GCMRegistrationTask().execute();
                break;
            case MSG_REGISTER_WEB_SERVER:
                new WebServerRegistrationTask().execute();
                break;
            case MSG_REGISTER_WEB_SERVER_SUCCESS:
                Toast.makeText(getApplicationContext(),
                        "registered with web server", Toast.LENGTH_LONG).show();
                break;
            case MSG_REGISTER_WEB_SERVER_FAILURE:
                Toast.makeText(getApplicationContext(),
                        "registration with web server failed",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (gcm == null && isGoogelPlayInstalled()) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            try {
                gcmRegId = gcm.register(GCM_SENDER_ID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getApplicationContext(), "registered with GCM",
                        Toast.LENGTH_LONG).show();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("TOKEN", result);
                editor.commit();
                //regIdView.setText(result);
                saveInSharedPref(result);
                new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WEB_SERVER);
            }
        }

    }

    private class WebServerRegistrationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(WEB_SERVER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("regId", gcmRegId);

            StringBuilder postBody = new StringBuilder();
            Iterator iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry param = (Entry) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();

                int status = conn.getResponseCode();
                if (status == 200) {
                    // Request success
                    new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WEB_SERVER_SUCCESS);
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                new MyHandler(MainActivi.this).sendEmptyMessage(MSG_REGISTER_WEB_SERVER_FAILURE);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
    }
}
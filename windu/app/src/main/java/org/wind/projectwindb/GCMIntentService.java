package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : GCMIntentService
* 설명 : GCM 서비스 노티피케이션 설정 클래스
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GCMIntentService extends IntentService {

    // LOG TAG
    private String TAGLOG = "로그 : GCMIntentService : ";

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    String Chatnum;
    String asking_code;
    String checkyouandme;


    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {

            // read extras as sent from server
            String message = extras.getString("message");
            String serverTime = extras.getString("timestamp");

            Log.i(TAGLOG, "궁금합니다. 어떤데이터가 들어오는지 보여주세요 : " + message);




            if(message!=null){
                String[] result=message.split("/");

                if(result[0].contains("도움이 완료되었습니다.") || result[0].contains("help")) {
                    HelpAndHelpCompletesendNotification(message);
                } else {

                    ChatboxActivity chatboxactivity = new ChatboxActivity();
                    if ((ChatboxActivity) chatboxactivity.mContext != null) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor edit = pref.edit();

                        String tempAhli = pref.getString("NOWCHATAHLI", "-1");
                        Log.i(TAGLOG, "A와 B의 값 생각대로팅 - " + tempAhli +"/"+ result[0] + "/"+tempAhli.equals(result[0]));
                        if (tempAhli.equals(result[0])) {
                            ((ChatboxActivity) chatboxactivity.mContext).refresh();
                            Log.i(TAGLOG, "같아서 리프레쉬 생각대로팅 - " + tempAhli);
                        } else {
                            Log.i(TAGLOG, "달라서 불러왕 생각대로팅 - " + tempAhli);
                            Intent i = new Intent(this, popup.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("NAME", result[2]);
                            i.putExtra("TEXT", result[3]);

                            Log.i(TAGLOG, "이거 왜 안해줘 - " + result[2] + result[3]);

                            PendingIntent p = PendingIntent.getActivity(this, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
                            try {
                                p.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                            sendNotification(message);
                        }
                    } else {
                        sendNotification(message);
                    }
                }
            }

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void HelpAndHelpCompletesendNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        ///윈드유 인텐트 리스트///////////////////////////////////////////////////////////
        Intent windyouintentlist[]=new Intent[3];
        windyouintentlist[0]=new Intent(this, MainActivi.class);
        windyouintentlist[1]=new Intent(this, HelpMainActivity.class);
        windyouintentlist[2]=new Intent(this, WindYouActivity.class);
        ///윈드유 인텐트 리스트///////////////////////////////////////////////////////////

        String[] result = msg.split("/");

        if(result[0].contains("도움이 완료되었습니다.")){

            PendingIntent contentIntent = PendingIntent.getActivities(this, NOTIFICATION_ID, windyouintentlist
                    , PendingIntent.FLAG_UPDATE_CURRENT);
            long[] vibrate = {0, 100, 200, 300};// 첫번째는 진동 시작전 기다리는 시간, 진동시간, 대기시간, 진동시간, 대기시간

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.icon_wind8686)
                    .setContentTitle("도움요청 완료")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(result[0]))
                    .setContentText(result[0])
                    .setNumber(100)
                    .setAutoCancel(true)
                    .setVibrate(vibrate);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        }else if(result[0].equals("help")){

            PendingIntent contentIntent = PendingIntent.getActivities(this, NOTIFICATION_ID, windyouintentlist
                    , PendingIntent.FLAG_UPDATE_CURRENT);

            long[] vibrate = {0, 100, 200, 300};// 첫번째는 진동 시작전 기다리는 시간, 진동시간, 대기시간, 진동시간, 대기시간

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.icon_wind8686)
                    .setContentTitle(result[1]+"님의 도움요청")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(result[2]))
                    .setContentText(result[2])
                    .setNumber(100)
                    .setAutoCancel(true)
                    .setVibrate(vibrate);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String[] result = msg.split("/");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        String dataset = pref.getString(result[0], "-1");


        ///윈드유 인텐트 리스트///////////////////////////////////////////////////////////
        Intent windyouintentlist[]=new Intent[4];
        windyouintentlist[0]=new Intent(this, MainActivi.class);
        windyouintentlist[1]=new Intent(this, HelpMainActivity.class);
        windyouintentlist[2]=new Intent(this, WindYouActivity.class);
        windyouintentlist[3]=new Intent(this, ChatboxActivity.class);
        windyouintentlist[3].putExtra("AHLI", result[0]);
        windyouintentlist[3].putExtra("FINISH_CHECK", "0");
        ///윈드유 인텐트 리스트///////////////////////////////////////////////////////////




        PendingIntent contentIntent = PendingIntent.getActivities(this, NOTIFICATION_ID, windyouintentlist
                , PendingIntent.FLAG_UPDATE_CURRENT);

        if (dataset.equals("-1")) {  //데이터셋 정보가 없을 경우 채팅방을 띄우지 않고 데이터셋을 확보한다. 윈드유 데이터셋이 없는경우는 윈드유방으로 이동
            Chatnum = result[0];
            asking_code = result[1];
            if (result[1].equals("windyou")) {
                checkyouandme = "WINDYOU";
            } else {
                checkyouandme = "WINDME";
                datasetting(result[1]);

            }
        }

        if(!result[1].equals("windyou"))//윈드미의 경우 윈드미프로세스로
        {
            ///윈드미 인텐트 리스트///////////////////////////////////////////////////////////
            Intent windmeintentlist[]=new Intent[5];
            windmeintentlist[0]=new Intent(this, MainActivi.class);
            windmeintentlist[1]=new Intent(this, HelpMainActivity.class);
            windmeintentlist[2]=new Intent(this, WindMeActivity.class);
            windmeintentlist[3]=new Intent(this, SelectedWindmeGroup.class);
            String temp[]=pref.getString("SELETEDGROUP"+result[1],"-1").split("/");
            Log.i(TAGLOG,"궁금합니다."+pref.getString("SELETEDGROUP"+result[1],"-1"));
            windmeintentlist[3].putExtra("ASKINGCODE", temp[0]);
            windmeintentlist[3].putExtra("ASKING_TEXT", temp[1]);
            windmeintentlist[3].putExtra("NUMBER_OF_ASK_USER", temp[2]);
            windmeintentlist[3].putExtra("FINISH_CHECK", temp[3]);
            windmeintentlist[4] = new Intent(this, ChatboxActivity.class);
            windmeintentlist[4].putExtra("AHLI", result[0]);
            windmeintentlist[4].putExtra("FINISH_CHECK", "0");
            ///윈드미 인텐트 리스트///////////////////////////////////////////////////////////

            contentIntent = PendingIntent.getActivities(this, NOTIFICATION_ID, windmeintentlist
                , PendingIntent.FLAG_UPDATE_CURRENT);
        }

        editor.putString("lastChat" + result[0], result[3]);
        editor.commit();

        long[] vibrate = {0, 100, 200, 300};// 첫번째는 진동 시작전 기다리는 시간, 진동시간, 대기시간, 진동시간, 대기시간

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon_wind8686)
                .setContentTitle(result[2])
                .setStyle(new NotificationCompat.BigTextStyle().bigText(result[3]))
                .setContentText(result[3])
                .setNumber(100)
                .setAutoCancel(true)
                .setVibrate(vibrate);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public void datasetting(String groupnum){
        String message="http://222.116.135.136/grouplist.php?asking_code="+groupnum;
        Log.i(TAGLOG, "메세지 확인 : " + message);
        GetDataFromSever getdatafromserver = new GetDataFromSever(this);
        getdatafromserver.execute(message);

    }





    //JSON 통신 //////////////////////////////////////////////////////////////////////////
    private class GetDataFromSever extends AsyncTask<String, String, String> {
        JSONArray root;
        Context context;

        private String[] yourcode;
        private String[] list_index;
        private String[] nickname;
        private String[] profile_image;
        private String[] grade;
        private String[] response_value;
        private String[] category1;
        private String[] category2;
        private String[] category3;
        private String[] category4;
        private String[] category5;

        public GetDataFromSever(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... urls) {
            ArrayList<NameValuePair> post = new ArrayList<>();

            post.add(new BasicNameValuePair("firstdata", "1"));

            StringBuilder builder = new StringBuilder();

            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httppost = new HttpPost(urls[0]);

            try {
                UrlEncodedFormEntity urlencodedfromentity = new UrlEncodedFormEntity(post, "UTF-8");
                httppost.setEntity(urlencodedfromentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line="";
                    while ((line = reader.readLine()) != null) {
                        Log.i(TAGLOG, "Test line : " + line);
                        builder.append(line);
                    }
                }
            } catch (Exception e) {
                Log.i(TAGLOG, "Exception try1 : " + e.getStackTrace());
                e.printStackTrace();
            }

            //응답받은 데이터 파싱
            yourcode= new String[100];
            list_index = new String[100];
            nickname= new String[100];
            profile_image= new String[100];
            grade= new String[100];
            response_value= new String[100];
            category1= new String[100];
            category2= new String[100];
            category3= new String[100];
            category4= new String[100];
            category5= new String[100];

            try {
                root = new JSONArray(builder.toString());
                for (int i = 0; i < root.length(); i++) {
                    JSONObject jobject = root.getJSONObject(i);

                    yourcode[i] = jobject.getString("user_code");
                    list_index[i]=jobject.getString("list_index");
                    nickname[i] = jobject.getString("nickname");
                    profile_image[i] = jobject.getString("image");
                    grade[i]=jobject.getString("grade");
                    response_value[i] = jobject.getString("response_value");
                    category1[i] = jobject.getString("category1");
                    category2[i] = jobject.getString("category2");
                    category3[i] = jobject.getString("category3");
                    category4[i] = jobject.getString("category4");
                    category5[i] = jobject.getString("category5");
                }

            } catch (JSONException e) {//요청된 도움이 없을 경우
                Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            for (int i = 0; i < root.length(); i++) {
                if(list_index[i].equals(Chatnum)){
                    String ahlitemp = list_index[i];

                    String dataset = yourcode[i] + "&"
                            + profile_image[i] + "&"
                            + nickname[i] + "&"
                            + asking_code + "&" + checkyouandme + "&" + grade[i];
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(ahlitemp, dataset);
                    edit.commit();
                }
            }
        }
    }




}

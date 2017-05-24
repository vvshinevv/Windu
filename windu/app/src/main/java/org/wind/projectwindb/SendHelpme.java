package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SendHelpme
* 설명 :
*   도움요청을 전송하는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class SendHelpme extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 : SelectPeopleWindme : ";

    RelativeLayout relativeLayout;
    TextView[] DymnicTv;


    private ChoosePeopleListAdapter adapter;
    private ChoosePeopleItem choosePeopleItem;
    private ArrayList<ChoosePeopleItem> arDessert=new ArrayList<ChoosePeopleItem>();

    private String[] UserInfo;
    String tempString;
    Integer Datacnt;
    String tempMsg;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_help_me);
        context = this;

        //////선택된 사람들 호출 ///////////////////////////////////////////////////////////////////////////////////
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String Data = pref.getString("ChooseDATA", "-1");
        Datacnt = Integer.parseInt(pref.getString("ChooseCnt", "-1"));

        UserInfo = new String[Datacnt*3];
        UserInfo = Data.split("/");

        relativeLayout = (RelativeLayout)findViewById(R.id.LinearLayout_SendHelpMe_PeopleNikBoard1);

        tempString="";
        int prvid=0;

        for(int i =0; i < Datacnt;i++)
        {

            final TextView textview = new TextView(this);
            textview.setText(" " + UserInfo[i * 3]);
            textview.setBackgroundColor(Color.argb(255, 255, 255, 255));
            textview.setTextSize(15);
            textview.setTextColor(Color.argb(250, 68, 68, 68));

            int curid = prvid+1;
            textview.setId(curid);
            final RelativeLayout.LayoutParams params2 =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            params2.addRule(RelativeLayout.BELOW, prvid);
            textview.setLayoutParams(params2);

            prvid=curid;
            relativeLayout.addView(textview, params2);

            tempString+=UserInfo[i*3+1]+",";
            tempString += UserInfo[i * 3 + 2];
            if(i!=Datacnt-1) {tempString += "@";}
        }

        TextView cnttv = (TextView)findViewById(R.id.TextView_SendHelpMe_peopleCnt);
        cnttv.setText(Integer.toString(Datacnt));

        final EditText msg = (EditText)findViewById(R.id.EditText_SendHelpMe_MSG);



        LinearLayout sendbtn = (LinearLayout)findViewById(R.id.Button_SendHelpMe_Send);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString().length()<10)
                {
                    Toast.makeText(getApplicationContext(),"10글자 이상 입력하세요.",Toast.LENGTH_LONG).show();
                } else {
                    try {
                        tempMsg = URLEncoder.encode(msg.getText().toString(), "UTF-8");
                        String message = "http://222.116.135.136/sendmessage.php?usercode=" + pref.getString("USER_MANAGEMENT_CODE", "-1")
                                + "&candidatescnt=" + Integer.toString(Datacnt) + "&candidates_list=" + tempString + "&message=" + tempMsg + "&latitude="
                                + pref.getString("LATITUDE", "-1") + "&longitude=" + pref.getString("LONGITUDE", "-1");

                        Log.i(TAGLOG, "메세지 확인 : " + message);

                        sendHelpMessageToServer(message);
                        //GetDataFromSever getdatafromserver = new GetDataFromSever(SendHelpme.this);
                        //getdatafromserver.execute(message);

                        finish();

                    } catch (Exception e) {
                        Log.i(TAGLOG, "전송실패 : ");
                        Toast.makeText(getApplicationContext(), "전송에 실패하였습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        TextView backbtn = (TextView)findViewById(R.id.Button_SendHelpMe_backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void sendHelpMessageToServer(String urlMessage) {
        Log.e(TAGLOG, "sendHelpMessageToServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String returnedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < jsonArray.length() ; i++ ) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        returnedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "sendHelpMessageToServer by hong: " + returnedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "sendHelpMessageToServer Error by hong: " + returnedCode);
                }
            }
        });
    }
}


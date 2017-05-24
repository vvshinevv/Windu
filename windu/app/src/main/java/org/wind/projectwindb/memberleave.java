package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : memberleave
* 설명 : 회원탈퇴 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class memberleave extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 :  memberleave : ";

    String profile;
    String UserCode;
    String grade;
    String nickname;

    String NULL_RETURN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.memberleave);


        RelativeLayout checkbtn = (RelativeLayout)findViewById(R.id.Layout_MemberLeave_memberleavebtn);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.Checkbox_MemberLeave_memberleave);
        final TextView memberleavebtn = (TextView)findViewById(R.id.TextView_MemberLeave_memberleavebtn);
        TextView backbtn = (TextView)findViewById(R.id.TextView_MemberLeave_backButton);

        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked() == true) {
                    checkBox.setChecked(false);
                    memberleavebtn.setBackgroundColor(Color.argb(50, 54, 93, 98));
                } else {
                    checkBox.setChecked(true);
                    memberleavebtn.setBackgroundColor(Color.argb(255, 54, 93, 98));
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked() == true) {
                    memberleavebtn.setBackgroundColor(Color.argb(255, 54, 93, 98));
                } else {
                    memberleavebtn.setBackgroundColor(Color.argb(50, 54, 93, 98));
                }
            }
        });

        memberleavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    String message = "http://222.116.135.136/userexpier.php?usercod=" + UserCode;
                    Log.i(TAGLOG, "계정 탈퇴 전송 : " + message);
                    leaveMemberFromServer(message);
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void leaveMemberFromServer(String urlMessage) {
        Log.e(TAGLOG, "leaveMemberFromServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String returnedCoed = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);

                    for( int i = 0 ; i < jsonArray.length() ; i++ ) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        returnedCoed = jsonObject.getString("result");

                        Log.e(TAGLOG,"leaveMemberFromServer by hong: " + returnedCoed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "leaveMemberFromServer Erorr by hong: " + returnedCoed);

                }
            }
        });
    }
}




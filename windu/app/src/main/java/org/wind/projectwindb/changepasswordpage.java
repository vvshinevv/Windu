package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : changepasswordpage
* 설명 : 패스워드 변경화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class changepasswordpage extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 :  changepasswordpage : ";

    String UserCode;
    String grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.changepasswordpage);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        UserCode = pref.getString("USER_MANAGEMENT_CODE","-1");

        TextView backbtn = (TextView)findViewById(R.id.TextView_changepasswordpage_backButton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView submitbtn = (TextView)findViewById(R.id.TextView_changepasswordpage_submitButton);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempcurrentpassworad = pref.getString("PW","-1");
                EditText cpw = (EditText)findViewById(R.id.EditText_changepasswordpage_currentpassword);
                EditText npw1= (EditText)findViewById(R.id.EditText_changepasswordpage_newPassword1);
                EditText npw2= (EditText)findViewById(R.id.EditText_changepasswordpage_newPassword2);

                if(tempcurrentpassworad.equals(cpw.getText().toString()))
                {
                    if(npw1.getText().toString().equals(npw2.getText().toString())){
                        if(!npw1.getText().toString().equals(tempcurrentpassworad)) {
                            String message = "http://222.116.135.136/changepassword.php?usercode=" + UserCode + "&pw=" + npw1.getText().toString();
                            Log.i(TAGLOG, "비밀번호 메세지 전송 : " + message);
                            updatePasswordToServer(message);
                            //GetDataFromSever getdatafromserver = new GetDataFromSever(getApplicationContext());
                            //getdatafromserver.execute(message);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"새로운 비밀번호는 현재 비밀번호와 다르게 입력해야 합니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"새로운 비밀번호는 동일하게 입력해야 합니다.",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"현재 비밀번호가 다릅니다.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void updatePasswordToServer(String urlMessage) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String returnData = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for ( int i = 0 ; i < jsonArray.length() ; i++ ) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        returnData = jsonObject.getString("result");

                        Log.e(TAGLOG, "비밀번호 업뎃 by hong: " + returnData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "비밀번호 업뎃 에러 by hong: " + returnData);

                }
            }
        });
    }
}




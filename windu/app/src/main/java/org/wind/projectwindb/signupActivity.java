package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : signupActivity
* 설명 :
*   회원가입 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class signupActivity extends Activity {

    private String TAGLOG="로그 : signupActivity : ";
    private String return_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        final EditText IDet = (EditText)findViewById(R.id.ID_edittext);
        final EditText PWet = (EditText)findViewById(R.id.PW_edittext);
        final EditText ConfirmPWet = (EditText)findViewById(R.id.ConfrimPW_edittext);

        Button signinbtn = (Button)findViewById(R.id.signinbutton);


        signinbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Boolean idform = android.util.Patterns.EMAIL_ADDRESS.matcher(IDet.getText().toString()).matches();
                if (idform == false) {
                    Toast.makeText(getApplicationContext(), "ID는 E-MAIL 형태로 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    if (IDet.getText().toString() == null || IDet.getText().toString().equals("") || PWet.getText().toString() == null || PWet.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "ID나 PW를 입력하세요.", Toast.LENGTH_LONG).show();
                    } else {
                        if (!PWet.getText().toString().equals(ConfirmPWet.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "입력된 패스워드가 서로 다릅니다.", Toast.LENGTH_LONG).show();
                        } else {
                            String message = "http://222.116.135.136/joinus.php?email=" + IDet.getText().toString() + "&pw=" + PWet.getText().toString();
                            registerUser(message);


                            //GetDataFromSever getdatafromserver = new GetDataFromSever();
                            // getdatafromserver.execute(message);

                            finish();
                        }
                    }
                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void registerUser(String urlMessage) {
        Log.e(TAGLOG, "registerUser: " + "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject jobject = root.getJSONObject(i);
                        return_msg = jobject.getString("result");

                        //Log.i("확인 1", resultdata +"/" +user_management_code_data);
                    }
                } catch (JSONException e) {
                    Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                    e.printStackTrace();
                }

                if (return_msg.equals("1")) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : WindMeActivity
* 설명 :
*   리뷰를 작성하는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class WriteReview extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 : SelectPeopleWindme : ";

    private ChoosePeopleListAdapter adapter;
    private ChoosePeopleItem choosePeopleItem;
    private ArrayList<ChoosePeopleItem> arDessert=new ArrayList<ChoosePeopleItem>();

    private String[] UserInfo;
    String tempString;
    Integer Datacnt;
    String tempMsg;

    String profileimage;
    String nikname;
    String gradetxt;
    String asking_code;
    String yourcode;
    String myUserCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.writereview);

        final Intent intent = new Intent(this.getIntent());
        profileimage = intent.getStringExtra("PROFILEIMAGE");
        nikname = intent.getStringExtra("NIKNAME");
        asking_code = intent.getStringExtra("AHLI");
        yourcode= intent.getStringExtra("YOURMANAGEMENTCODE");
        gradetxt = intent.getStringExtra("YOURGRADE");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        myUserCode = pref.getString("USER_MANAGEMENT_CODE", "-1");



        final RatingBar rb = (RatingBar)findViewById(R.id.Ratingbar_writereview);
        final TextView tv = (TextView)findViewById(R.id.TextView_WriteReview_value);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv.setText(String.valueOf(rating));
            }
        });


        TextView niknametv = (TextView)findViewById(R.id.TextView_writeReview_Nickname);
        niknametv.setText(nikname);

        // URL 이미지 생성 //////////////////////////////////////////////////////////
        String temp = profileimage;
        String bguri = temp.replace("//", "/");
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.ImageView_selectpeoplelist_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));

        // URL 이미지 생성 //////////////////////////////////////////////////////////
        ImageView grade = (ImageView)findViewById(R.id.ImageView_selectpeoplelist_grade);
        GradeMachingTask gt = new GradeMachingTask();
        gt.Setgradeimage(grade, gradetxt);


        LinearLayout tvsendButton = (LinearLayout)findViewById(R.id.TextView_writeReview_SendButton);
        tvsendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText msg = (EditText) findViewById(R.id.EditText_WriteReview_MSG);

                    if (msg.length() < 5) {
                        Toast.makeText(getApplicationContext(),"리뷰를 다섯글자 이상 작성해주세요.",Toast.LENGTH_LONG).show();
                    } else {
                        String tempS = URLEncoder.encode(msg.getText().toString(), "UTF-8");

                        String tempValue = tv.getText().toString();
                        float tempfloat = Float.parseFloat(tempValue);
                        String inputReviewValue = String.format("%.0f",tempfloat);

                        String message = "http://222.116.135.136/writereview.php?ahli=" + asking_code + "&usercode=" + myUserCode
                                + "&youcode=" + yourcode + "&score=" + inputReviewValue + "&message=" + tempS;
                        Log.i(TAGLOG, "리뷰보내는메시지 로그 : " + message);

                        //TODO:모듈분리
                        writeReviewToServer(message);

                        Intent intent_01 = new Intent(getApplicationContext(), WindMeActivity.class);
                        intent_01.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(new Intent(intent_01));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAGLOG, "메세지 생성 에러");
                }
            }
        });

        TextView backbtn = (TextView)findViewById(R.id.TextView_writeReview_backButton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**By honghee ToDo:확인요망 */
    private void writeReviewToServer(String urlMessage) {
        Log.e(TAGLOG, "writeReviewToServer: " + "진입");
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
                        Log.e(TAGLOG, "writeReviewToServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "writeReviewToServer Error by hong: " + retrunedCode);

                }
            }
        });
    }
}


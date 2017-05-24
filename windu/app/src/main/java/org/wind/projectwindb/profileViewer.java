package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : profileViewer
* 설명 :
*   사진 선택시, 상세 프로필을 볼 수 있는 화면
*
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class profileViewer extends Activity {

    private String TAGLOG="로그 : profileViewer : ";
    private String return_msg;

    String userCode;
    String bguri, tempdd;

    private String[] temp;

    String ReturnMSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_viewer);

        final Intent intent = new Intent(this.getIntent());
        userCode = intent.getStringExtra("USERCODE");

        String message = "http://222.116.135.136/mypage.php?usercode=" + userCode;
        Log.i(TAGLOG, "메세지 확인 : " + message);

        getUserDataFromServer(message);
        //GetDataFromSever getdatafromserver = new GetDataFromSever();
        //getdatafromserver.execute(message);

        ImageView xbutton = (ImageView)findViewById(R.id.ImageView_profileViewer_xbutton);
        xbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void getUserDataFromServer(String urlMessage) {
        Log.e(TAGLOG, "getUserDataFromServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                temp = new String[12];

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobject = jsonArray.getJSONObject(i);
                        temp[0] = jobject.getString("profile_image");
                        temp[1] = jobject.getString("nickname");
                        temp[2] = jobject.getString("grade");
                        temp[3] = jobject.getString("avg_reviewscore");
                        temp[4] = jobject.getString("category_subject1");
                        temp[5] = jobject.getString("category_subject2");
                        temp[6] = jobject.getString("category_subject3");
                        temp[7] = jobject.getString("category_subject4");
                        temp[8] = jobject.getString("category_subject5");
                        temp[9] = jobject.getString("reviewcnt");
                        temp[10] = jobject.getString("limit_count_to_send");
                        temp[11] = jobject.getString("limit_distance_to_send");
                    }
                    ReturnMSG="1";
                } catch (JSONException e) {
                    e.printStackTrace();
                    ReturnMSG="0";
                    Toast.makeText(getApplicationContext(),"데이터가 잘못들어왔습니다.",Toast.LENGTH_LONG).show();
                }

                if(ReturnMSG.equals("1"))
                {
                    String proUri = temp[0];
                    bguri = proUri.replace("//", "/");
                    SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.ImageView_profileViewer_profile);
                    simpleDraweeView.setImageURI(Uri.parse(bguri));

                    TextView nikTv = (TextView)findViewById(R.id.TextView_profileViewer_nikname);
                    nikTv.setText(temp[1]);

                    TextView category = (TextView)findViewById(R.id.TextView_profileViewer_category);
                    tempdd="";
                    if(!temp[4].equals("#")){tempdd+=temp[4]+" ";}
                    if(!temp[5].equals("#")){tempdd+=temp[5]+" ";}
                    if(!temp[6].equals("#")){tempdd+=temp[6]+" ";}
                    if(!temp[7].equals("#")){tempdd+=temp[7]+" ";}
                    if(!temp[8].equals("#")){tempdd+=temp[8]+" ";}
                    category.setText(tempdd);

                    GradeMachingTask gt = new GradeMachingTask();
                    ImageView gradelv = (ImageView)findViewById(R.id.ImageView_profileViewer_grade);
                    gt.Setgradeimage(gradelv, temp[2]);

                    TextView reviewCnt = (TextView)findViewById(R.id.TextView_profileViewer_reviewcnt);
                    reviewCnt.setText(temp[9]);

                    TextView ReviewScore = (TextView)findViewById(R.id.TextView_profileViewer_reliabilityLeft);
                    float tempfloat = Float.parseFloat(temp[3]);
                    String convertfloatdata= String.format("%.0f",tempfloat);
                    ReviewScore.setText(convertfloatdata);

                    ImageView reviewbtn = (ImageView)findViewById(R.id.ImageView_profileViewer_review);
                    reviewbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent_01 = new Intent(getApplicationContext() , ReadReview.class);
                            intent_01.putExtra("YOURUSERCODE", userCode);
                            intent_01.putExtra("PROFILE", bguri);
                            intent_01.putExtra("CATEGORY", tempdd);
                            intent_01.putExtra("GRADE",temp[2]);
                            intent_01.putExtra("REVIEW_AVR",temp[3]);
                            v.getContext().startActivity(intent_01);

                        }
                    });
                }else{
                    finish();
                    Toast.makeText(getApplicationContext(),"잘못된 접근 입니다.",Toast.LENGTH_LONG).show();;
                }
            }
        });

    }
}
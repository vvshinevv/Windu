package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : ReadReview
* 설명 :
*   현재까지 발생한 리뷰 내용을 확인할 수 있는 화면
*
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ReadReview extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 :  ReadReaview : ";

    private ReadReviewAdapter adapter;
    private ReviewItem reviewItem;
    private ArrayList<ReviewItem> arDessert=new ArrayList<ReviewItem>();
    private String[] nickname;
    private String[] review_text;
    private String[] datetime;
    private String[] get_score;

    String myUserCode;

    String gradetxt;
    String yourUserCode;
    String profile;
    String category;
    String review_avr;

    String NULL_RETURN;

    //JSONArray root;
    //Context context;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.readreview);

        final Intent intent = new Intent(this.getIntent());
        yourUserCode = intent.getStringExtra("YOURUSERCODE");

        profile = intent.getStringExtra("PROFILE");
        category = intent.getStringExtra("CATEGORY");
        gradetxt =  intent.getStringExtra("GRADE");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        TextView categorytv = (TextView)findViewById(R.id.TextView_readReview_Category);
        categorytv.setText(category);
        // 유저 카테고리 표시 ////////////////////////////////////////////

        // URL 이미지 생성 //////////////////////////////////////////////////////////
        String temp = profile;
        String bguri = temp.replace("//", "/");
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.ImageView_selectpeoplelist_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));

        // 등급 이미지 생성 //////////////////////////////////////////////////////////
        ImageView grade = (ImageView)findViewById(R.id.ImageView_selectpeoplelist_grade);
        GradeMachingTask gt = new GradeMachingTask();
        gt.Setgradeimage(grade, gradetxt);

        // 메세지 전송 //////////////////////////////////////////////////////////
        String message="http://222.116.135.136/checkreview.php?usercode="+yourUserCode;
        Log.i(TAGLOG,"리뷰 보여줘 메세지 전송 : "+message);

        GetDataFromSever getdatafromserver = new GetDataFromSever(this);
        getdatafromserver.execute(message);


        TextView backbtn = (TextView)findViewById(R.id.TextView_readReview_backButton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //TODO: 통일된 통신 모듈 사용 요망
    //JSON 통신 //////////////////////////////////////////////////////////////////////////
    private class GetDataFromSever extends AsyncTask<String, String, String> {
        JSONArray root;
        Context context;

        public GetDataFromSever(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... urls) {
            ArrayList<NameValuePair> post = new ArrayList<>();

            StringBuilder builder = new StringBuilder();

            //post.add(new BasicNameValuePair("firstdata", "1"));

            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httppost = new HttpPost(urls[0]);

            try {
                UrlEncodedFormEntity urlencodedfromentity = new UrlEncodedFormEntity(post, HTTP.UTF_8);
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
            arDessert = new ArrayList<ReviewItem>();
            nickname= new String[100];
            review_text= new String[100];
            datetime= new String[100];
            get_score= new String[100];

            try {
                root = new JSONArray(builder.toString());
                for (int i = 0; i < root.length(); i++) {
                    JSONObject jobject = root.getJSONObject(i);
                    nickname[i] = jobject.getString("nickname");
                    review_text[i] = jobject.getString("review_text");
                    datetime[i] = jobject.getString("datetime");
                    get_score[i] = jobject.getString("get_score");
                }
                NULL_RETURN="0";
            } catch (JSONException e) {//요청된 도움이 없을 경우
                Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                Log.i(TAGLOG, "메세지 파싱 오류");
                NULL_RETURN="1";
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            int cntScore=0;
            int i=0;
            if (result != null) {
                final ReadReviewAdapter adapter = new ReadReviewAdapter(context, R.layout.readreview_list_form, arDessert);
                if(NULL_RETURN.equals("0")) {

                    for (i = 0; i < root.length(); i++) {
                        reviewItem = new ReviewItem(Integer.toString(i+1),
                                nickname[i],review_text[i], datetime[i], get_score[i]);
                        cntScore += Integer.valueOf(get_score[i]);
                        adapter.arD.add(reviewItem);
                    }
                    list = (ListView) findViewById(R.id.Layout_readReview_list);
                    list.setAdapter(adapter);

                    TextView reviewcnt = (TextView)findViewById(R.id.TextView_readReview_NumOfreview);
                    reviewcnt.setText(Integer.toString(i));

                    RatingBar ratingBar = (RatingBar)findViewById(R.id.Ratingbar_readReview);
                    ratingBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });


                    TextView scorevalue = (TextView)findViewById(R.id.TextView_readReview_value);

                    float ratring = cntScore/i;
                    ratingBar.setRating(ratring);

                    String inputReviewValue = String.format("%.0f",ratring);
                    scorevalue.setText(inputReviewValue);

                }
                else  {
                    TextView reviewcnt = (TextView)findViewById(R.id.TextView_readReview_NumOfreview);
                    reviewcnt.setText("0");
                }
            }

        }
    }
}

class ReviewItem{
    public String ListIndex;
    public String Nickname;
    public String Review_text;
    public String Datetime;
    public String Get_score;

    public ReviewItem(String listindex,String nickname,String review_text,String datetime, String get_score){
        ListIndex = listindex;
        Nickname = nickname;
        Review_text = review_text;
        Datetime = datetime;
        Get_score = get_score;
    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class ReadReviewAdapter extends BaseAdapter {

    Context con;
    LayoutInflater inflater;
    ArrayList<ReviewItem> arD;
    int layout;

    public ReadReviewAdapter(Context context, int alayout, ArrayList<ReviewItem> aarD)
    {
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arD=aarD;
        layout=alayout;
    }

    @Override
    public int getCount(){
        return arD.size();
    }

    @Override
    public Object getItem(int position){
        return arD.get(position).Nickname;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        if(convertView==null){
            convertView = inflater.inflate(layout,parent,false);
        }

        TextView indexcntTv = (TextView)convertView.findViewById(R.id.TextView_readReview_cnt);
        indexcntTv.setText(arD.get(position).ListIndex + ".");

        RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.Ratingbar_readReviewlist_listrating);
        ratingBar.setRating(Float.valueOf(arD.get(position).Get_score));
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



        TextView nicknmaeTv = (TextView)convertView.findViewById(R.id.TextView_readReview_nikname);
        nicknmaeTv.setText(arD.get(position).Nickname);

        TextView datetime = (TextView)convertView.findViewById(R.id.TextView_readReview_date);
        datetime.setText(arD.get(position).Datetime);

        TextView reviewContentsTv = (TextView)convertView.findViewById(R.id.TextView_readReview_contents);
        reviewContentsTv.setText(arD.get(position).Review_text);

        return convertView;
    }
}
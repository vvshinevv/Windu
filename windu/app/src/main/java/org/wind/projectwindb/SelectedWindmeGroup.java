package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SelectedWindmeGroup
* 설명 :
*   windme 에서 요청한 도움요청 목록중 하나를 선택했을 때, 나타나는 화면
* 내부 DB 목록 :
*   edit.putString(ahlitemp, dataset);
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

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


public class SelectedWindmeGroup extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 : SelectedWindmeGroup : ";

    private SelectedGroupAdapter adapter;
    private SelectedGroupItem selectedGroupItem;
    private ArrayList<SelectedGroupItem> arDessert=new ArrayList<SelectedGroupItem>();
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

    private String NULL_RETURN="0";

    ListView list;

    String asking_code;
    String finishCheck;

    String Chatnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Fresco.initialize(this);


        setContentView(R.layout.selected_windme_group);

        Chatnum = "0";

        Intent intent = new Intent(this.getIntent());
        asking_code= intent.getStringExtra("ASKINGCODE");
        String asking_text= intent.getStringExtra("ASKING_TEXT");
        String num_ask_user = intent.getStringExtra("NUMBER_OF_ASK_USER");
        finishCheck =intent.getStringExtra("FINISH_CHECK");

        TextView groupNum = (TextView)findViewById(R.id.TextView_grouplist_member_count);
        groupNum.setText(num_ask_user);

        String message="http://222.116.135.136/grouplist.php?asking_code="+asking_code;
        Log.i(TAGLOG, "메세지 확인 : " + message);
        GetDataFromSever getdatafromserver = new GetDataFromSever(this);
        getdatafromserver.execute(message);

        TextView temptv = (TextView)findViewById(R.id.TextView_SelectedWindmeGroup_ThisHelpMSG);
        temptv.setText(asking_text);

        TextView backbtn = (TextView)findViewById(R.id.TextView_SelectedWindmeGroup_backButton);
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
            arDessert = new ArrayList<SelectedGroupItem>();

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
                NULL_RETURN="0";
            } catch (JSONException e) {//요청된 도움이 없을 경우
                Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                NULL_RETURN="1";
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                final SelectedGroupAdapter adapter = new SelectedGroupAdapter(context, R.layout.selected_windme_group_listform, arDessert);
                if(NULL_RETURN.equals("0")) {

                    for (int i = 0; i < root.length(); i++) {
                        selectedGroupItem = new SelectedGroupItem(yourcode[i],list_index[i], nickname[i], profile_image[i], grade[i], response_value[i],
                                    category1[i], category2[i], category3[i], category4[i], category5[i]);

                            adapter.arD.add(selectedGroupItem);
                    }
                    /*{"list_index":"204","nickname":"Alien","image":"http:////222.116.135.136//image//default_profile_image.png","grade":"1",
    "response_value":"0","category1":"#Art","category2":"#","category3":"#","category4":"#","category5":"#"}]*/

                    Log.i(TAGLOG,"궁금해서 확인하는 로그 : "+Chatnum);

                    if(!Chatnum.equals("0")){
                        for (int i = 0; i < root.length(); i++) {
                            if(list_index[i].equals(Chatnum)){
                                String ahlitemp = list_index[i];
                                String dataset=yourcode[i]+"&"
                                        +profile_image[i]+"&"
                                        + nickname[i]+"&"
                                        +asking_code+"&"+"WINDME"+"&"+grade[i];
                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString(ahlitemp, dataset);
                                edit.commit();
                            }
                        }
                    } else {


                        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_selectedwindmeGroup = (LinearLayout) findViewById(R.id.Layout_SelectedWindmeGroup_list);
                        inflater.inflate(R.layout.selected_windme_group_list, layout_selectedwindmeGroup, true);

                        list = (ListView) findViewById(R.id.ListView_SelectedWindmeGroup_List);
                        list.setAdapter(adapter);

                        //Wind me 종권을 선택 했을때..
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.i(TAGLOG, "TEST 사용자코드 :" + arDessert.get(position).Yourcode);

                                if (arDessert.get(position).Response_value.equals("1")) {
                                    Intent intent_01 = new Intent(getApplicationContext(), ChatboxActivity.class);
                                    //String youManagementCode= intent_01.getStringExtra(arD.get(position).);

                                    String ahlitemp = arDessert.get(position).List_index;

                                    String dataset = arDessert.get(position).Yourcode + "&"
                                            + arDessert.get(position).Profile_image + "&"
                                            + arDessert.get(position).Nickname + "&"
                                            + asking_code + "&" + "WINDME" + "&" + arDessert.get(position).Grade;
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor edit = pref.edit();
                                    edit.putString(ahlitemp, dataset);
                                    edit.commit();

                                    intent_01.putExtra("AHLI", arDessert.get(position).List_index);
                                    intent_01.putExtra("FINISH_CHECK", finishCheck);
                                    startActivity(new Intent(intent_01));
                                } else {
                                    Toast.makeText(getApplicationContext(), "도움 요청이 아직 수락되지 않았습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }


                }
                else  {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layout_selectpeoplelist = (LinearLayout)findViewById(R.id.Layout_SelectedWindmeGroup_list);
                    inflater.inflate(R.layout.selected_windme_group_list, layout_selectpeoplelist, true);
                }
            }
        }
    }
}


//////////////////////////////////////////////테이블뷰 관련 데이터형태 및 어댑터 오버라이딩

class SelectedGroupItem{
    public String Yourcode;
    public String List_index;
    public String Nickname;
    public String Profile_image;
    public String Grade;
    public String Response_value;
    public String Category1;
    public String Category2;
    public String Category3;
    public String Category4;
    public String Category5;


    public SelectedGroupItem(String yourcode,String list_index,String nickname, String profile_image,String grade,String response_value,String category1,
                             String category2, String category3, String category4, String category5){
        Yourcode = yourcode;
        List_index = list_index;
        Nickname = nickname;
        Profile_image = profile_image;
        Grade = grade;
        Response_value = response_value;
        Category1 = category1;
        Category2 = category2;
        Category3 = category3;
        Category4 = category4;
        Category5 = category5;

    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class SelectedGroupAdapter extends BaseAdapter {

    Context con;
    LayoutInflater inflater;
    ArrayList<SelectedGroupItem> arD;
    int layout;

    public SelectedGroupAdapter(Context context, int alayout, ArrayList<SelectedGroupItem> aarD)
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
        return arD.get(position).Yourcode;
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
        // URL 이미지 생성 //////////////////////////////////////////////////////////
        String temp = arD.get(position).Profile_image;
        String bguri = temp.replace("//", "/");

        Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(bguri));
        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new CacheKey(bguri));
        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new CacheKey(bguri));

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)convertView.findViewById(R.id.ImageView_SelectedWindmeGroup_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));


        // URL 이미지 생성 //////////////////////////////////////////////////////////
        ImageView grade = (ImageView)convertView.findViewById(R.id.ImageView_SelectedWindmeGroup_grade);
        GradeMachingTask gt = new GradeMachingTask();
        gt.Setgradeimage(grade, arD.get(position).Grade);

       TextView nikname = (TextView)convertView.findViewById(R.id.TextView_SelectedWindmeGroup_Nickname);
        nikname.setText(arD.get(position).Nickname);


        TextView category = (TextView)convertView.findViewById(R.id.TextView_SelectedWindmeGroup_Category);
        String tempdd="";
//        if(!arD.get(position).Category1.equals("#")){tempdd+=arD.get(position).Category1+" ";}
//        if(!arD.get(position).Category2.equals("#")){tempdd+=arD.get(position).Category2+" ";}
//        if(!arD.get(position).Category3.equals("#")){tempdd+=arD.get(position).Category3+" ";}
//        if(!arD.get(position).Category4.equals("#")){tempdd+=arD.get(position).Category4+" ";}
//        if(!arD.get(position).Category5.equals("#")){tempdd+=arD.get(position).Category5+" ";}

        ImageView blind = (ImageView)convertView.findViewById(R.id.ImageView_SelectedWindmeGroup_blindviewer);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(con.getApplicationContext());
        if(arD.get(position).Response_value.equals("1")){
            blind.setBackgroundColor(Color.parseColor("#00ebebeb"));
            tempdd=pref.getString("lastChat"+arD.get(position).List_index,"-1");
            if(tempdd.equals("-1")){
                tempdd="도움요청이 수락 되었습니다.";
            }
        }else{
            tempdd="도움 요청 중 입니다.";
            blind.setBackgroundColor(Color.parseColor("#bbebebeb"));
        }
        category.setText(tempdd);


        return convertView;
    }
}




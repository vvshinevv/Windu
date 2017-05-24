package org.wind.projectwindb;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SelectPeopleWindme
* 설명 :
*   도움요청시 반경선택 후, 서버로부터 수신된 도움요청가능한 사용자 리스트를 표시해주는 화면
* 내부 DB 목록 :
*   edit.putString("ChooseDATA",tempString);
*   edit.putString("ChooseCnt",Integer.toString(cnt));
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SelectPeopleWindme extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 : SelectPeopleWindme : ";

    private ChoosePeopleListAdapter adapter;
    private ChoosePeopleItem choosePeopleItem;
    private ArrayList<ChoosePeopleItem> arDessert=new ArrayList<ChoosePeopleItem>();
    private String[] usercode;
    private String[] nickname;
    private String[] profile_image;
    private String[] distance;
    private String[] grade;
    private String[] category_subject1;
    private String[] category_subject2;
    private String[] category_subject3;
    private String[] category_subject4;
    private String[] category_subject5;
    private String[] reviewcnt;

    private String NULL_RETURN="0";

    Context context;
    ListView list;
    TextView locationtextView;

    Double latitude;
    Double longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Fresco.initialize(this);
        setContentView(R.layout.select_people_windme);
        locationtextView = (TextView)findViewById(R.id.TextView_settinglocation_locationtext);


        //////리스트뷰 호출 ///////////////////////////////////////////////////////////////////////////////////
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //실제 사용코드
        String user_management_code = pref.getString("USER_MANAGEMENT_CODE","-1");
        String tempLATI = pref.getString("LATITUDE","-1");
        String tempLONG = pref.getString("LONGITUDE","-1");
        String set_RADIUS = pref.getString("SET_RADIUS","-1");

        String message="http://222.116.135.136/candidatechoose2.php?usercode="+user_management_code+"&latitude="+tempLATI+"&longitude="+tempLONG;
        Log.i(TAGLOG, "메세지 확인 : " + message);
        chooseCadidateFromServer1(message);

        latitude = Double.parseDouble(tempLATI);
        longtitude = Double.parseDouble(tempLONG);

        getLocation(latitude, longtitude);


        ChoosePeopleListAdapter.InitExam(this);
        //swichbutton = (ToggleButton)findViewById(R.id.ToggleButton_selectpeoplewindme_alignbutton);
        TextView backbtn = (TextView)findViewById(R.id.Button_selectPeoplewindme_backbutton);
        Button bSelectOtherLocation = (Button) findViewById(R.id.bSelectOtherLocation);
        /**swichbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String user_management_code = pref.getString("USER_MANAGEMENT_CODE", "-1");
            String tempLATI = pref.getString("LATITUDE", "-1");
            String tempLONG = pref.getString("LONGITUDE", "-1");
            String set_RADIUS = pref.getString("SET_RADIUS", "-1");

            String message = "http://222.116.135.136/candidatechoose.php?usercode=" + user_management_code + "&latitude=" + tempLATI + "&longitude=" + tempLONG + "&limit_distance=" + set_RADIUS;
            Log.i(TAGLOG, "메세지 확인 : " + message);
            chooseCadidateFromServer(message);
            }
        });*/

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bSelectOtherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectRadiusWindMeActivity.class));
            }
        });

       // DoitSomething("2");

    }



    //위도 경도에 따른 주소값 가져오기//////////////////////////////////////////////////////////
    public void getLocation(double lat, double lng){
        String str = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "주소를 찾지 못하였습니다.");
            e.printStackTrace();
        }
        locationtextView.setText(str);
    }
    //위도 경도에 따른 주소값 가져오기//////////////////////////////////////////////////////////

    /**By honghee ToDo:확인요망 */
    /**private void chooseCadidateFromServer(String urlMessage) {
        Log.e(TAGLOG, "chooseCadidateFromServer: "+"진입");

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                int jsonArrayLength = 0;
                arDessert = new ArrayList<ChoosePeopleItem>();

                usercode= new String[100];
                nickname= new String[100];
                profile_image= new String[100];
                distance= new String[100];
                grade= new String[100];
                category_subject1= new String[100];
                category_subject2= new String[100];
                category_subject3= new String[100];
                category_subject4= new String[100];
                category_subject5= new String[100];
                reviewcnt= new String[100];

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobject = jsonArray.getJSONObject(i);

                        usercode[i] = jobject.getString("usercode");
                        nickname[i] = jobject.getString("nickname");
                        profile_image[i] = jobject.getString("profile_image");
                        distance[i] = jobject.getString("distance");
                        grade[i]=jobject.getString("grade");
                        category_subject1[i] = jobject.getString("category_subject1");
                        category_subject2[i] = jobject.getString("category_subject2");
                        category_subject3[i] = jobject.getString("category_subject3");
                        category_subject4[i] = jobject.getString("category_subject4");
                        category_subject5[i] = jobject.getString("category_subject5");
                        reviewcnt[i] = jobject.getString("reviewcnt");

                        jsonArrayLength++;
                    }
                    NULL_RETURN="0";
                } catch (JSONException e) {//요청된 도움이 없을 경우
                    Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                    NULL_RETURN="1";
                    e.printStackTrace();
                }

                if (dataRequiredToParsing != null) {
                    int i=0;
                    int j=0;
                    final ChoosePeopleListAdapter adapter = new ChoosePeopleListAdapter(context, R.layout.select_people_listform, arDessert);
                    if(NULL_RETURN.equals("0")) {
                        Integer[] sequence;
                        sequence=new Integer[100];

                        if(!swichbutton.isChecked()) {
                            for (i = 0; i < jsonArrayLength; i++) {
                                sequence[i] = 0;
                                for (j = 0; j < jsonArrayLength; j++) {
                                    if (Integer.parseInt(distance[i]) < Integer.parseInt(distance[j]) && i != j) {
                                        sequence[i] += 1;
                                    }
                                }
                            }

                            for (i = jsonArrayLength-1; i >=0 ; i--) {
                                for (j = jsonArrayLength-1; j >=0 ; j--) {
                                    if (sequence[j] == i) {
                                        choosePeopleItem = new ChoosePeopleItem(usercode[j], nickname[j], profile_image[j], distance[j], grade[j], category_subject1[j],
                                                category_subject2[j], category_subject3[j], category_subject4[j], category_subject5[j], reviewcnt[j],false);
                                        adapter.arD.add(choosePeopleItem);
                                    }
                                }
                            }
                        } else if(swichbutton.isChecked()) {  /////////////체크 레벨
                            for (i = 0; i < jsonArrayLength; i++) {
                                sequence[i] = 0;
                                for (j = 0; j < jsonArrayLength; j++) {
                                    if (Integer.parseInt(grade[i]) < Integer.parseInt(grade[j]) && i != j) {
                                        sequence[i] += 1;
                                    }
                                }
                            }


                            for (i = 0; i < jsonArrayLength; i++) {
                                for (j = 0; j < jsonArrayLength; j++) {
                                    if (sequence[j] == i) {
                                        choosePeopleItem = new ChoosePeopleItem(usercode[j], nickname[j], profile_image[j], distance[j], grade[j], category_subject1[j],
                                                category_subject2[j], category_subject3[j], category_subject4[j], category_subject5[j], reviewcnt[j],false);
                                        adapter.arD.add(choosePeopleItem);
                                    }
                                }
                            }
                        }

                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_selectpeoplelist = (LinearLayout) findViewById(R.id.Layout_SelectPeopleWindme_List);
                        inflater.inflate(R.layout.select_people_list, layout_selectpeoplelist, true);


                        list = (ListView) findViewById(R.id.ListView_SelectPeopleWindme_List);
                        list.setAdapter(adapter);

                        TextView sendbtn = (TextView)findViewById(R.id.Textview_SelectPeopleWindme_Send);
                        sendbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //GPS 업데이트 종료///////////////////////////////////////////////////////////////////
                                GPSProvider gpsProvider = new GPSProvider(SelectPeopleWindme.this);
                                gpsProvider.stopUsingGPS();
                                //GPS 업데이트 종료///////////////////////////////////////////////////////////////////
                                int cnt = 0;
                                String tempString = "";
                                for (int i = 0; i < arDessert.size(); i++) {
                                    if (arDessert.get(i).IsChecked == true) {
                                        tempString += arDessert.get(i).Nickname + "/" +
                                                arDessert.get(i).Usercode + "/" +
                                                arDessert.get(i).Grade + "/";
                                        cnt++;
                                    }
                                }
                                Log.i(TAGLOG, tempString);
                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("ChooseDATA", tempString);
                                edit.putString("ChooseCnt", Integer.toString(cnt));
                                edit.commit();

                                if(cnt==0){
                                    Toast.makeText(getApplicationContext(),"최소 1명은 선택해야 합니다.",Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent_01 = new Intent(getApplicationContext(), SendHelpme.class);
                                    startActivity(new Intent(intent_01));
                                    finish();
                                }
                            }
                        });
                    }
                    else  {
                        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_selectpeoplelist = (LinearLayout)findViewById(R.id.Layout_SelectPeopleWindme_List);
                        inflater.inflate(R.layout.select_people_list_empty, layout_selectpeoplelist, true);
                    }
                }
            }
        });
    }*/

    private void chooseCadidateFromServer1(String urlMessage) {
        Log.e(TAGLOG, "chooseCadidateFromServer: "+"진입");

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                int jsonArrayLength = 0;
                arDessert = new ArrayList<ChoosePeopleItem>();

                usercode= new String[100];
                nickname= new String[100];
                profile_image= new String[100];
                distance= new String[100];
                grade= new String[100];
                category_subject1= new String[100];
                category_subject2= new String[100];
                category_subject3= new String[100];
                category_subject4= new String[100];
                category_subject5= new String[100];
                reviewcnt= new String[100];

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobject = jsonArray.getJSONObject(i);

                        usercode[i] = jobject.getString("usercode");
                        nickname[i] = jobject.getString("nickname");
                        profile_image[i] = jobject.getString("profile_image");
                        distance[i] = jobject.getString("distance");
                        grade[i]=jobject.getString("grade");
                        category_subject1[i] = jobject.getString("category_subject1");
                        category_subject2[i] = jobject.getString("category_subject2");
                        category_subject3[i] = jobject.getString("category_subject3");
                        category_subject4[i] = jobject.getString("category_subject4");
                        category_subject5[i] = jobject.getString("category_subject5");
                        reviewcnt[i] = jobject.getString("reviewcnt");

                        jsonArrayLength++;
                    }
                    NULL_RETURN="0";
                } catch (JSONException e) {//요청된 도움이 없을 경우
                    Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                    NULL_RETURN="1";
                    e.printStackTrace();
                }

                if (dataRequiredToParsing != null) {
                    int i=0;
                    int j=0;
                    final ChoosePeopleListAdapter adapter = new ChoosePeopleListAdapter(context, R.layout.select_people_listform, arDessert);
                    if(NULL_RETURN.equals("0")) {
                        Integer[] sequence;
                        sequence=new Integer[100];


                        for (i = 0; i < jsonArrayLength; i++) {
                            sequence[i] = 0;
                            for (j = 0; j < jsonArrayLength; j++) {
                                if (Integer.parseInt(distance[i]) < Integer.parseInt(distance[j]) && i != j) {
                                    sequence[i] += 1;
                                }
                            }
                        }

                        for (i = jsonArrayLength-1; i >=0 ; i--) {
                            for (j = jsonArrayLength-1; j >=0 ; j--) {
                                if (sequence[j] == i) {
                                    choosePeopleItem = new ChoosePeopleItem(usercode[j], nickname[j], profile_image[j], distance[j], grade[j], category_subject1[j],
                                            category_subject2[j], category_subject3[j], category_subject4[j], category_subject5[j], reviewcnt[j],false);
                                    adapter.arD.add(choosePeopleItem);
                                }
                            }
                        }


                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_selectpeoplelist = (LinearLayout) findViewById(R.id.Layout_SelectPeopleWindme_List);
                        inflater.inflate(R.layout.select_people_list, layout_selectpeoplelist, true);


                        list = (ListView) findViewById(R.id.ListView_SelectPeopleWindme_List);
                        list.setAdapter(adapter);

                        TextView sendbtn = (TextView)findViewById(R.id.Textview_SelectPeopleWindme_Send);
                        sendbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //GPS 업데이트 종료///////////////////////////////////////////////////////////////////
                                GPSProvider gpsProvider = new GPSProvider(SelectPeopleWindme.this);
                                gpsProvider.stopUsingGPS();
                                //GPS 업데이트 종료///////////////////////////////////////////////////////////////////
                                int cnt = 0;
                                String tempString = "";
                                for (int i = 0; i < arDessert.size(); i++) {
                                    if (arDessert.get(i).IsChecked == true) {
                                        tempString += arDessert.get(i).Nickname + "/" +
                                                arDessert.get(i).Usercode + "/" +
                                                arDessert.get(i).Grade + "/";
                                        cnt++;

                                    }
                                }
                                Log.i(TAGLOG, tempString);
                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("ChooseDATA", tempString);
                                edit.putString("ChooseCnt", Integer.toString(cnt));
                                edit.commit();

                                if(cnt==0){
                                    Toast.makeText(getApplicationContext(),"최소 1명은 선택해야 합니다.",Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent_01 = new Intent(getApplicationContext(), SendHelpme.class);
                                    startActivity(new Intent(intent_01));
                                    finish();
                                }
                            }
                        });
                    }
                    else  {
                        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_selectpeoplelist = (LinearLayout)findViewById(R.id.Layout_SelectPeopleWindme_List);
                        inflater.inflate(R.layout.select_people_list_empty, layout_selectpeoplelist, true);
                    }
                }
            }
        });
    }
}

//////////////////////////////////////////////테이블뷰 관련 데이터형태 및 어댑터 오버라이딩
class ChoosePeopleItem{
    public String Usercode;
    public String Nickname;
    public String Profile_image;
    public String Distance;
    public String Grade;
    public String Category_subject1;
    public String Category_subject2;
    public String Category_subject3;
    public String Category_subject4;
    public String Category_subject5;
    public String Reviewcnt;
    public Boolean IsChecked;

        public ChoosePeopleItem(String usercode,String nickname, String profile_image, String distance,String grade ,String category_subject1, String category_subject2, String category_subject3, String category_subject4, String category_subject5,String reviewcnt,Boolean isChecked){
            Usercode = usercode;
        Nickname = nickname;
        Profile_image = profile_image;
        Distance = distance;
        Grade = grade;
        Category_subject1 = category_subject1;
        Category_subject2 = category_subject2;
        Category_subject3 = category_subject3;
        Category_subject4 = category_subject4;
        Category_subject5 = category_subject5;
        Reviewcnt = reviewcnt;
        IsChecked = isChecked;
    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class ChoosePeopleListAdapter extends BaseAdapter {

    static Context con;
    static Context con1;
    static int checkedPerson = 0;
    LayoutInflater inflater;

    ArrayList<ChoosePeopleItem> arD;
    int layout;

    TextView tvChoosingPeople;
    TextView tvMayChoosePeople;

    public ChoosePeopleListAdapter(Context context, int alayout, ArrayList<ChoosePeopleItem> aarD)
    {
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arD=aarD;
        layout=alayout;

    }

    public static void InitExam(Context main) {
        con1 = main;
    }
    @Override
    public int getCount(){
        tvMayChoosePeople = (TextView)((Activity)con1).findViewById(R.id.tvMayChoosePeople);
        tvMayChoosePeople.setText(Integer.toString(arD.size()));
        return arD.size();
    }

    @Override
    public Object getItem(int position){
        return arD.get(position).Usercode;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){

        if(convertView==null){
            convertView = inflater.inflate(layout,parent,false);
        }
        // URL 프로필 이미지 생성 //////////////////////////////////////////////////////////
        String temp = arD.get(position).Profile_image;
        String bguri = temp.replace("//","/");

        Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(bguri));
        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new CacheKey(bguri));
        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new CacheKey(bguri));

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)convertView.findViewById(R.id.ImageView_selectpeoplelist_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(con, profileViewer.class);
                intent_01.putExtra("USERCODE", arD.get(position).Usercode);
                con.startActivity(new Intent(intent_01));
            }
        });

        // URL 이미지 생성 //////////////////////////////////////////////////////////
        ImageView grade = (ImageView)convertView.findViewById(R.id.ImageView_selectpeoplelist_grade);
        GradeMachingTask gt = new GradeMachingTask();
        gt.Setgradeimage(grade, arD.get(position).Grade);

        //Review 버튼 터치시 발생 이벤트 ///////////////////////////////////////////
        ImageView reviewBTN = (ImageView)convertView.findViewById(R.id.ImageView_selectpeoplewindme_readReviewButton);

        reviewBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempdd="";
                if(!arD.get(position).Category_subject1.equals("#")){tempdd+=arD.get(position).Category_subject1+" ";}
                if(!arD.get(position).Category_subject2.equals("#")){tempdd+=arD.get(position).Category_subject2+" ";}
                if(!arD.get(position).Category_subject3.equals("#")){tempdd+=arD.get(position).Category_subject3+" ";}
                if(!arD.get(position).Category_subject4.equals("#")){tempdd+=arD.get(position).Category_subject4+" ";}
                if(!arD.get(position).Category_subject5.equals("#")){tempdd+=arD.get(position).Category_subject5+" ";}

                Intent intent_01 = new Intent(con , ReadReview.class);
                intent_01.putExtra("YOURUSERCODE", arD.get(position).Usercode);
                intent_01.putExtra("PROFILE", arD.get(position).Profile_image);
                intent_01.putExtra("GRADE", arD.get(position).Grade);
                intent_01.putExtra("CATEGORY", tempdd);
                v.getContext().startActivity(intent_01);
            }
        });


        TextView NiknameTv = (TextView)convertView.findViewById(R.id.TextView_selectpeoplewindme_Nikname);
        NiknameTv.setText(arD.get(position).Nickname);

        TextView category = (TextView)convertView.findViewById(R.id.TextView_selectpeoplewindme_Category);
        String tempdd="";
        if(!arD.get(position).Category_subject1.equals("#")){tempdd+=arD.get(position).Category_subject1+" ";}
        if(!arD.get(position).Category_subject2.equals("#")){tempdd+=arD.get(position).Category_subject2+" ";}
        if(!arD.get(position).Category_subject3.equals("#")){tempdd+=arD.get(position).Category_subject3+" ";}
        if(!arD.get(position).Category_subject4.equals("#")){tempdd+=arD.get(position).Category_subject4+" ";}
        if(!arD.get(position).Category_subject5.equals("#")){tempdd+=arD.get(position).Category_subject5+" ";}
        category.setText(tempdd);

        TextView reviewcnt = (TextView)convertView.findViewById(R.id.TextView_selectpeoplewindme_review_count);
        reviewcnt.setText(arD.get(position).Reviewcnt);

        TextView kmcnt = (TextView)convertView.findViewById(R.id.TextView_selectpeoplewindme_Distance);
        TextView kmMSG = (TextView)convertView.findViewById(R.id.TextView_selectpeoplewindme_Distance_infoMessage);

        float km = Float.parseFloat(arD.get(position).Distance)/1000;
        if(km<1) {
            kmcnt.setText("1");
            kmMSG.setText("Km 이내에 있습니다.");
        } else {
            kmcnt.setText(String.format("%.2f",km));
            kmMSG.setText("Km 떨어져 있습니다.");
        }


        //final String text = arD.get(position);
        tvChoosingPeople = (TextView)((Activity)con1).findViewById(R.id.tvChoosingPeople);

        ToggleButton CheckPeople = (ToggleButton)convertView.findViewById(R.id.Togglebutton_checkedPeopleButton);
        CheckPeople.setFocusable(false);
        CheckPeople.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SelectPeopleWindme selectPeopleWindme = new SelectPeopleWindme();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                //Intent intent = new Intent(con)
                if (isChecked == true) {
                    arD.get(position).IsChecked = true;
                    checkedPerson++;
                    tvChoosingPeople.setText(Integer.toString(checkedPerson));
                     //selectPeopleWindme.DoitSomething(Integer.toString(checkedPerson));
                } else {
                    arD.get(position).IsChecked = false;
                    checkedPerson--;
//                    tvChoosingPeople.setText(Integer.toString(checkedPerson));
                    tvChoosingPeople.setText(Integer.toString(checkedPerson));

                    // selectPeopleWindme.DoitSomething(Integer.toString(checkedPerson));
                }
            }
        });
        return convertView;
    }
}


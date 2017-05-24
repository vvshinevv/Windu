package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : setting
* 설명 :
*   개인설정을 하는 화면 ( 알림시간, 위치설정, 카테고리 설정)
* 내부 DB 목록 :
*   edit.putString("CATEGORYUSE" + String.valueOf(i), "-1");
*   edit.putString("TIMEUSE" + String.valueOf(i+1), "-1");
*   edit.putString("LOCATIONUSE" + String.valueOf(i+1), "-1");
*
*   edit.putString("TIME_TEXT" + tempinfo[6+(i*3)], timeSum1 + "~" + timeSum2 );
*   edit.putString("TIMEUSE" + tempinfo[6 + (i * 3)], "1");
*
*   edit.putString("LOCATIONSETTING_LATITUDE" + tempinfo[15+(i*3)], tempinfo[16+(i*3)]);
*   edit.putString("LOCATIONSETTING_LONGITUDE" + tempinfo[15+(i*3)], tempinfo[17 + (i * 3)]);
*
*   edit.putString("LOCATIONSETTING_LOCATIONTEXT" + tempinfo[15+(i*3)], addressval);
*   edit.putString("LOCATIONUSE" + tempinfo[15 + (i * 3)], "1");
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class setting extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 :  setting : ";

    String NULL_RETURN;

    private SettingAdapter adapter;
    private Settingitem settingitem;
    private ArrayList<Settingitem> arDessert=new ArrayList<Settingitem>();
    private ArrayList<Settingitem> arDessert2=new ArrayList<Settingitem>();
    private ArrayList<Settingitem> arDessert3=new ArrayList<Settingitem>();

    public static Context mContext;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.setting);

        mContext=this;

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edit = pref.edit();

        for(int i=1;i<=5;i++)
        {
                edit.putString("CATEGORYUSE" + String.valueOf(i), "-1");
        }
        for(int i=0;i<=2;i++)
        {
                edit.putString("TIMEUSE" + String.valueOf(i+1), "-1");
        }
        for(int i=0;i<=2;i++) {
                edit.putString("LOCATIONUSE" + String.valueOf(i+1), "-1");
        }
        edit.commit();


        String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");
        String message="http://222.116.135.136/usersetting.php?usercode="+tempCode;
        Log.i(TAGLOG, "셋팅정보확인로깅 : " + message);

        //TODO: 통신모듈 분리
        userSettingFromServer(message);
        //GetDataFromSever getdatafromserver = new GetDataFromSever(this);
        //getdatafromserver.execute(message);


        TextView submit = (TextView)findViewById(R.id.TextView_setting_submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        RelativeLayout setlocation = (RelativeLayout)findViewById(R.id.Layout_setting_Location);
        setlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt=0;
                for (int i = 1; i <= 3; i++) {
                    String temp = pref.getString("LOCATIONUSE" + String.valueOf(i), "-1");
                    if (temp.equals("1")) {
                        cnt++;
                    }
                }

                if(cnt>2) {
                    Toast.makeText(getApplicationContext(),"알림위치는 최대 세곳까지만 등록하실 수 있습니다.",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent_01 = new Intent(getApplicationContext(), SettingLocationActivity.class);
                    startActivity(new Intent(intent_01));
                }
            }
        });

        RelativeLayout settime = (RelativeLayout)findViewById(R.id.Layout_setting_time);
        settime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt=0;
                for (int i = 1; i <= 3; i++) {
                    String temp = pref.getString("TIMEUSE" + String.valueOf(i), "-1");
                    if (temp.equals("1")) {
                        cnt++;
                    }
                }

                if(cnt>2) {
                    Toast.makeText(getApplicationContext(),"알림구간 설정은 최대 세개까지만 등록하실 수 있습니다.",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent_01 = new Intent(getApplicationContext(), SettingTimeActivity.class);
                    startActivity(new Intent(intent_01));
                }
            }
        });

        RelativeLayout setcategory = (RelativeLayout)findViewById(R.id.Layout_setting_category);
        setcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt=0;
                for (int i = 1; i <= 5; i++) {
                    String temp = pref.getString("CATEGORYUSE" + String.valueOf(i), "-1");
                    if (temp.equals("1")) {
                        cnt++;
                    }
                }

                if(cnt>4) {
                    Toast.makeText(getApplicationContext(),"카테고리는 최대 다섯개까지만 등록하실 수 있습니다.",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent_01 = new Intent(getApplicationContext(), SettingCategoryActivity.class);
                    startActivity(new Intent(intent_01));
                }
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edit = pref.edit();
//        edit.putString("LOCATIONTEXT"+"1", "마로니에타운");
//        edit.putString("LOCATIONCNT", "1");
//        edit.commit();

        arDessert = new ArrayList<Settingitem>();

        SettingAdapter adapter = new SettingAdapter(getApplicationContext(), R.layout.setting_listform, arDessert);

        int check=0;

        for (int i = 1; i <= 3; i++) {
            String temp = pref.getString("LOCATIONUSE"+String.valueOf(i),"-1");
            if(temp.equals("1")) {
                settingitem = new Settingitem(String.valueOf(i), "LOCATION", pref.getString("LOCATIONSETTING_LOCATIONTEXT" + String.valueOf(i), "-1"), "CNT");
                adapter.arD.add(settingitem);
                check++;
            }
        }
        if(check==0) {
            //이곳에다가는 설정된 알림 위치가 없습니다.를 써야함
            settingitem = new Settingitem("1", "", "설정된 알림 위치가 없습니다.", "NOCNT");
            adapter.arD.add(settingitem);
        }
        ListView Locationlist = (ListView) findViewById(R.id.ListView_setting_locationTable);
        Locationlist.setAdapter(adapter);

        Locationlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arDessert.get(position).Type.equals("NOCNT")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(setting.mContext);

                    builder.setTitle("알림위치 제거");
                    builder.setMessage("선택한 알림 위치를 제거하시겠습니까?");

                    final String delid = arDessert.get(position).TableIndex;
                    final String tempstr = arDessert.get(position).Icontype;

                    // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (tempstr.equals("LOCATION")) {
                                edit.putString("LOCATIONUSE" + delid, "-1");
                                edit.commit();

                                String tempCode = pref.getString("USER_MANAGEMENT_CODE","-1");
                                String message="http://222.116.135.136/deletelocation.php?usercode="+tempCode+"&lindex="+delid;
                                Log.i(TAGLOG, "위치정보 삭제 로깅 : " + message);
                                //TODO: 통신모듈 분리
                                deleteLocationFromServer(message);
                                //GetDataFromSever getdatafromserver = new GetDataFromSever(setting.this);
                                //getdatafromserver.execute(message);

                                Intent intent = new Intent(setting.this, setting.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        arDessert2 = new ArrayList<Settingitem>();

        SettingAdapter adapter2 = new SettingAdapter(getApplicationContext(), R.layout.setting_listform, arDessert2);

        int check2=0;

        for (int i = 1; i <= 3; i++) {
            String temp = pref.getString("TIMEUSE"+String.valueOf(i),"-1");
            if(temp.equals("1")) {
                settingitem = new Settingitem(String.valueOf(i), "TIME", pref.getString("TIME_TEXT" + String.valueOf(i), "-1"), "CNT");
                adapter2.arD.add(settingitem);
                check2++;
            }
        }
        if(check2==0) {
            //이곳에다가는 설정된 알림 위치가 없습니다.를 써야함
            settingitem = new Settingitem("1", "", "설정된 알림 시간이 없습니다.", "NOCNT");
            adapter2.arD.add(settingitem);
        }
        ListView Timelist = (ListView) findViewById(R.id.ListView_setting_TimeTable);
        Timelist.setAdapter(adapter2);

        Timelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arDessert2.get(position).Type.equals("NOCNT")) {

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(setting.this);

                    builder2.setTitle("알림시간 제거");
                    builder2.setMessage("선택한 알림 시간을 제거하시겠습니까?");

                    final String delid = arDessert2.get(position).TableIndex;
                    final String tempstr = arDessert2.get(position).Icontype;

                    // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
                    builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (tempstr.equals("TIME")) {
                                edit.putString("TIMEUSE" + delid, "-1");
                                edit.commit();

                                String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");
                                String message = "http://222.116.135.136/deletealarm.php?ain=" + delid + "&usercode=" + tempCode;
                                Log.i(TAGLOG, "시간정보 삭제 로깅 : " + message);

                                //TODO: 통신모듈 분리
                                deleteAlarmFromServer(message);
                                //GetDataFromSever getdatafromserver = new GetDataFromSever(setting.this);
                                //getdatafromserver.execute(message);

                                Intent intent = new Intent(setting.this, setting.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }
                    });

                    builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    AlertDialog dialog = builder2.create();
                    dialog.show();
                }
            }
        });

        arDessert3 = new ArrayList<Settingitem>();

        SettingAdapter adapter3 = new SettingAdapter(getApplicationContext(), R.layout.setting_listform, arDessert3);

        int check3=0;

        for (int i = 1; i <= 5; i++) {
            String temp = pref.getString("CATEGORYUSE"+String.valueOf(i),"-1");
            if(temp.equals("1")) {
                settingitem = new Settingitem(String.valueOf(i), "CATEGORY", pref.getString("CATEGORY_TEXT" + String.valueOf(i), "-1"), "CNT");
                adapter3.arD.add(settingitem);
                check3++;
            }
        }
        if(check3==0) {
            //이곳에다가는 설정된 알림 위치가 없습니다.를 써야함
            settingitem = new Settingitem("1", "", "설정된 카테고리가 없습니다.", "NOCNT");
            adapter3.arD.add(settingitem);
        }
        ListView categorylist = (ListView) findViewById(R.id.ListView_setting_CategoryTable);
        categorylist.setAdapter(adapter3);

        categorylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arDessert3.get(position).Type.equals("NOCNT")) {

                    AlertDialog.Builder builder3 = new AlertDialog.Builder(setting.this);

                    builder3.setTitle("카테고리 제거");
                    builder3.setMessage("선택한 카테고리를 제거하시겠습니까?");

                    final String delid = arDessert3.get(position).TableIndex;
                    final String tempstr = arDessert3.get(position).Icontype;

                    // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
                    builder3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (tempstr.equals("CATEGORY")) {
                                edit.putString("CATEGORYUSE" + delid, "-1");
                                edit.commit();

                                String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");
                                String tempCategory[] = new String[6];
                                int tempCategoryCnt = 0;
                                for (int j = 1; j <= 5; j++) {
                                    String temp2 = pref.getString("CATEGORYUSE" + String.valueOf(j), "-1");
                                    if (temp2.equals("1")) {
                                        tempCategory[j] = pref.getString("CATEGORY_TEXT" + String.valueOf(j), "-1").replace("#", "");
                                        tempCategoryCnt++;
                                    } else {
                                        tempCategory[j] = "";
                                    }
                                }

                                String message = "http://222.116.135.136/ctsetting.php?usercode=" + tempCode + "&ctc=" + String.valueOf(tempCategoryCnt) +
                                        "&ct1=" + tempCategory[1] + "&ct2=" + tempCategory[2] + "&ct3=" + tempCategory[3] + "&ct4=" + tempCategory[4] + "&ct5=" + tempCategory[5];
                                Log.i(TAGLOG, "카테고리 삭제 로깅 : " + message);

                                //TODO: 통신모듈 분리
                                settingCategoryFromServer(message);
                                //GetDataFromSever getdatafromserver = new GetDataFromSever(setting.this);
                                //getdatafromserver.execute(message);

                                Intent intent = new Intent(setting.this, setting.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }
                    });

                    builder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    AlertDialog dialog = builder3.create();
                    dialog.show();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void userSettingFromServer(String urlMessage) {
        Log.e(TAGLOG, "userSettingFromServer: "+"진입");

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String temp[] = dataRequiredToParsing.split("\"");

                if(temp.length > 20) {
                    String tempInfo[] = new String[25];
                    int cnt = 0;

                    for( int i = 0 ; i < temp.length ; i++ ) {
                        if ((i + 1) % 4 == 0) {
                            tempInfo[cnt] = temp[i];
                            Log.i(TAGLOG, "테스트 로그(tempinfo) :" + tempInfo[cnt]);
                            cnt++;
                        }
                    }

                    for(int i=1;i<=5;i++) {
                        if(!tempInfo[i].equals("#"))
                        {
                            edit.putString("CATEGORY_TEXT" + String.valueOf(i), tempInfo[i].replace("#",""));
                            edit.putString("CATEGORYUSE" + String.valueOf(i), "1");
                        } else {
                            edit.putString("CATEGORYUSE" + String.valueOf(i), "-1");
                        }
                    }

                    for(int i=0;i<=2;i++) {
                        if(!tempInfo[6+(i*3)].equals("0")) {
                            String timeSum1="";
                            String temptime1[]= tempInfo[7+(i*3)].split(":");

                            String timeSum2="";
                            String temptime2[]= tempInfo[8+(i*3)].split(":");

                            if(Integer.valueOf(temptime1[0])>12){timeSum1="PM "+ String.format("%02d",Integer.valueOf(temptime1[0])-12) +":"+temptime1[1];}
                            else {timeSum1="AM "+temptime1[0]+":"+temptime1[1];}

                            if(Integer.valueOf(temptime2[0])>12){timeSum2="PM "+String.format("%02d", Integer.valueOf(temptime2[0]) - 12)+":"+temptime2[1];}
                            else {timeSum2="AM "+temptime2[0]+":"+temptime2[1];}

                            edit.putString("TIME_TEXT" + tempInfo[6+(i*3)], timeSum1 + "~" + timeSum2 );
                            edit.putString("TIMEUSE" + tempInfo[6 + (i * 3)], "1");
                        }
                    }
                    for(int i=0;i<=2;i++) {
                        if(!tempInfo[15+(i*3)].equals("0"))
                        {
                            edit.putString("LOCATIONSETTING_LATITUDE" + tempInfo[15+(i*3)], tempInfo[16+(i*3)]);
                            edit.putString("LOCATIONSETTING_LONGITUDE" + tempInfo[15+(i*3)], tempInfo[17 + (i * 3)]);

                            String addressval = getLocation(Double.valueOf(tempInfo[16+(i*3)]),Double.valueOf(tempInfo[17+(i*3)]));

                            edit.putString("LOCATIONSETTING_LOCATIONTEXT" + tempInfo[15+(i*3)], addressval);
                            edit.putString("LOCATIONUSE" + tempInfo[15 + (i * 3)], "1");
                        }
                    }
                    edit.commit();
                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void deleteLocationFromServer(String urlMessage) {
        Log.e(TAGLOG, "deleteLocationFromServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String retrunedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < jsonArray.length() ; i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        retrunedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "userSettingFromServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "userSettingFromServer Error by hong: " + retrunedCode);

                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void deleteAlarmFromServer(String urlMessage) {
        Log.e(TAGLOG, "deleteAlarmFromServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String retrunedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < jsonArray.length() ; i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        retrunedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "deleteAlarmFromServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "deleteAlarmFromServer Error by hong: " + retrunedCode);

                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void settingCategoryFromServer(String urlMessage) {
        Log.e(TAGLOG, "settingCategoryFromServer: "+"진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String returnedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < jsonArray.length() ; i++ ){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        returnedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "settingCategoryFromServer by hong: " + returnedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "settingCategoryFromServer Error by hong: " + returnedCode);
                }
            }
        });
    }

    //위도 경도에 따른 주소값 가져오기//////////////////////////////////////////////////////////
    public String getLocation(double lat, double lng){
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

        return str;

    }
    //위도 경도에 따른 주소값 가져오기//////////////////////////////////////////////////////////
}

//////////////////////////////////////////////테이블뷰 관련 데이터형태 및 어댑터 오버라이딩
class Settingitem{
    public String TableIndex;
    public String Icontype;
    public String Text;
    public String Type;


    public Settingitem(String tableIndex,
                       String icontype,String text,String type){

        TableIndex = tableIndex;
        Icontype = icontype;
        Text = text;
        Type = type;
    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class SettingAdapter extends BaseAdapter {

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    Context con;
    LayoutInflater inflater;
    ArrayList<Settingitem> arD;
    int layout;

    public SettingAdapter(Context context, int alayout, ArrayList<Settingitem> aarD)
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
        return arD.get(position).TableIndex;
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

        pref = PreferenceManager.getDefaultSharedPreferences(con);
        edit = pref.edit();

        if(arD.get(position).Type.equals("NOCNT")){
            ImageView imageView = (ImageView)convertView.findViewById(R.id.ImageView_setting_listview_icon);
            imageView.setImageResource(R.drawable.tempimage);

            TextView textView = (TextView)convertView.findViewById(R.id.TextView_setting_listview_text);
            textView.setText(arD.get(position).Text);
        } else {
            ImageView imageView = (ImageView)convertView.findViewById(R.id.ImageView_setting_listview_icon);
            if(arD.get(position).Icontype.equals("LOCATION")) {
                imageView.setImageResource(R.drawable.icon_setting_gps_logo_white);
            } else if(arD.get(position).Icontype.equals("TIME")){
                imageView.setImageResource(R.drawable.icon_setting_watch_logo_white);
            } else if(arD.get(position).Icontype.equals("CATEGORY")){
                imageView.setImageResource(R.drawable.icon_setting_category_logo_white);
            }

            TextView textView = (TextView)convertView.findViewById(R.id.TextView_setting_listview_text);
            textView.setText(arD.get(position).Text);
        }
        return convertView;
    }
}


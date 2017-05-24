package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : WindMeActivity
* 설명 :
*   도움요청을 수락하고 도움을 줄 수 있는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class WindYouActivity extends AppCompatActivity {

    // LOG TAG
    private String TAGLOG = "로그 : WindYOUActivity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.windyou);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout1);
        tabLayout.addTab(tabLayout.newTab().setText("진행중인 목록"));
        tabLayout.addTab(tabLayout.newTab().setText("완료된 목록"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager1);
        final WindyouPageAdapter adapter = new WindyouPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        TextView backbtn = (TextView)findViewById(R.id.TextView_windyou_backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void plusButtonClicked(View v) {
        Intent intent_01 = new Intent(getApplicationContext(),SelectPeopleWindme.class);
        startActivity(new Intent(intent_01));
    }
}

//////////////////////////////////////////////테이블뷰 관련 데이터형태 및 어댑터 오버라이딩
class WindyouItem{
    public String TableIndex;
    public String Asking_help_list_index;
    public String Asking_code;
    public String User_management_code;
    public String Asking_user_management_code;
    public String Nickname;
    public String Image;
    public String Asking_message;
    public String Datetime;
    public String Number_of_asked_user;
    public String Asking_finish_check_value;
    public String Number_of_response;
    public String Response_value;


    public WindyouItem(String tableIndex,
                        String asking_help_list_index,String asking_code, String user_management_code, String asking_user_management_code, String nickname,
                       String image,String asking_message,String datetime,String number_of_asked_user,String asking_finish_check_value,
                       String number_of_response,String response_value){

        TableIndex = tableIndex;
        Asking_help_list_index = asking_help_list_index;
        Asking_code = asking_code;
        User_management_code=user_management_code;
        Asking_user_management_code = asking_user_management_code;
        Nickname = nickname;
        Image = image;
        Asking_message = asking_message;
        Datetime = datetime;
        Number_of_asked_user = number_of_asked_user;
        Asking_finish_check_value = asking_finish_check_value;
        Number_of_response = number_of_response;
        Response_value = response_value;

    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class windYouListAdapter extends BaseAdapter {

    Context con;
    LayoutInflater inflater;
    ArrayList<WindyouItem> arD;
    int layout;

    public windYouListAdapter(Context context, int alayout, ArrayList<WindyouItem> aarD)
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

        ImageView blind = (ImageView)convertView.findViewById(R.id.ImageView_windyou_blindviewer);
        if(arD.get(position).Asking_finish_check_value.equals("0")){
            blind.setBackgroundColor(Color.parseColor("#00ebebeb"));
        } else if(arD.get(position).Asking_finish_check_value.equals("1")){
            blind.setBackgroundColor(Color.parseColor("#bbebebeb"));
        }


        String temp = arD.get(position).Image;
        String bguri = temp.replace("//", "/");

        Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(bguri));
        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new CacheKey(bguri));
        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new CacheKey(bguri));

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)convertView.findViewById(R.id.ImageView_windyou_list_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));


        TextView list_nickName = (TextView) convertView.findViewById(R.id.TextView_windyou_list_nikname);
        list_nickName.setText(arD.get(position).Nickname);

        TextView list_helpWirte = (TextView) convertView.findViewById(R.id.TextView_windyou_list_helpwrite);
        list_helpWirte.setText(arD.get(position).Asking_message);

        TextView list_dateTime = (TextView) convertView.findViewById(R.id.TextView_windyou_list_date);
        list_dateTime.setText(arD.get(position).Datetime);

        return convertView;
    }
}



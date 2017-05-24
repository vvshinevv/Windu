package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : WindMeActivity
* 설명 :
*   도움요청 목록을 확인할 수 있고, 도움요청을 할 수 있는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;


public class WindMeActivity extends AppCompatActivity {

    // LOG TAG
    private String TAGLOG = "로그 : WindMeActivity : ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.windme);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("진행중인 목록"));
        tabLayout.addTab(tabLayout.newTab().setText("완료된 목록"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final WindMePagerAdapter adapter = new WindMePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

        TextView backbtn = (TextView)findViewById(R.id.backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView helpmebtn = (TextView)findViewById(R.id.helpmebutton);

        helpmebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), SelectRadiusWindMeActivity.class);
                startActivity(new Intent(intent_01));
            }
        });

    }

    public void plusButtonClicked(View v) {
        Intent intent_01 = new Intent(getApplicationContext(),SelectRadiusWindMeActivity.class);
        startActivity(new Intent(intent_01));
    }
}


//////////////////////////////////////////////테이블뷰 관련 데이터형태 및 어댑터 오버라이딩
class GroupItem{
    public String TableIndex;
    public String Asking_code;
    public String Profile;
    public String Distance;
    public String Number_of_asked_user;
    public String Asking_message;
    public String Send_asking_datetime;
    public String Asking_finish_check_value;


    public GroupItem(String tableIndex,
            String asking_code,String profile, String number_of_asked_user, String asking_message, String send_asking_datetime,String asking_finish_check_value){

        TableIndex = tableIndex;
        Asking_code = asking_code;
        Profile = profile;
        Number_of_asked_user=number_of_asked_user;
        Asking_message = asking_message;
        Send_asking_datetime = send_asking_datetime;
        Asking_finish_check_value = asking_finish_check_value;
    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기

class windmeListAdapter extends BaseAdapter {

    Context con;
    LayoutInflater inflater;
    ArrayList<GroupItem> arD;
    int layout;

    public windmeListAdapter(Context context, int alayout, ArrayList<GroupItem> aarD)
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

        ImageView blind = (ImageView)convertView.findViewById(R.id.ImageView_windme_blindviewer);
        if(arD.get(position).Asking_finish_check_value.equals("0")){
            blind.setBackgroundColor(Color.parseColor("#00ebebeb"));
        } else if(arD.get(position).Asking_finish_check_value.equals("1")){
            blind.setBackgroundColor(Color.parseColor("#bbebebeb"));
        }

        TextView number_of_user = (TextView) convertView.findViewById(R.id.TextView_member_count);
        number_of_user.setText(arD.get(position).Number_of_asked_user);
        TextView help_write = (TextView) convertView.findViewById(R.id.TextView_helpwrite);
        help_write.setText(arD.get(position).Asking_message);
        TextView date = (TextView) convertView.findViewById(R.id.TextView_date);
        date.setText(arD.get(position).Send_asking_datetime);

        return convertView;
    }
}



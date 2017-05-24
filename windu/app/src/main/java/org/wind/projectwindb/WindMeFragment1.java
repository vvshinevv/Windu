package org.wind.projectwindb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WindMeFragment1 extends Fragment {
    private String TAGLOG = "로그 : WindMeFragment1 : ";

    private GroupItem groupItem;
    private ArrayList<GroupItem> arDessert=new ArrayList<GroupItem>();
    private String[] asking_code;
    private String[] number_of_asked_user;
    private String[] asking_message;
    private String[] send_asking_datetime;
    private String[] asking_finish_check_value;
    private String NULL_RETURN="0";
    ListView list;
    Context context;
    String user_management_code;

    View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.before_wind_me, container, false);
        context = view.getContext();

        user_management_code = "89";
        String message="http://222.116.135.136/windmelist.php?usercode="+user_management_code;

        windMeListFromServer(message);
        return view;

    }
    private void windMeListFromServer(String urlMessage) {
        Log.e(TAGLOG, "windMeListFromServer: " + "진입");

        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                int jsonArrayCount = 0;

                arDessert = new ArrayList<GroupItem>();

                asking_code= new String[100];
                number_of_asked_user= new String[100];
                asking_message= new String[100];
                send_asking_datetime= new String[100];
                asking_finish_check_value= new String[100];

                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < root.length() ; i++ ){
                        JSONObject jsonObject = root.getJSONObject(i);

                        asking_code[i] = jsonObject.getString("asking_code");
                        number_of_asked_user[i] = jsonObject.getString("number_of_asked_user");
                        asking_message[i] = jsonObject.getString("asking_message");
                        send_asking_datetime[i] = jsonObject.getString("send_asking_datetime");
                        asking_finish_check_value[i] = jsonObject.getString("asking_finish_check_value");

                        jsonArrayCount++;
                    }
                    NULL_RETURN="0";
                } catch (JSONException e) {
                    e.printStackTrace();
                    NULL_RETURN="1";
                }

                if( dataRequiredToParsing != null ) {
                    int cnt = 0;
                    int index = 0;

                    windmeListAdapter adapter = new windmeListAdapter(context, R.layout.group_list_form, arDessert);

                    if(NULL_RETURN.equals("0")) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                        SharedPreferences.Editor edit = pref.edit();
                        for( int i = 0 ; i < jsonArrayCount ; i++ ) {
                            if(asking_finish_check_value[i].equals("0")) {
                                groupItem = new GroupItem(Integer.toString(index), asking_code[i],
                                        "@drawable/icon_group", number_of_asked_user[i], asking_message[i], send_asking_datetime[i],
                                        asking_finish_check_value[i]);
                                adapter.arD.add(groupItem);
                                cnt++;
                                index++;

                                String dataset = asking_code[i]+"/"+asking_message[i]+"/"+number_of_asked_user[i]+"/"+asking_finish_check_value[i];
                                edit.putString("SELETEDGROUP" + asking_code[i], dataset);
                            }
                        }
                        edit.commit();

                        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_helpmelist = (LinearLayout) view.findViewById(R.id.Layout_helpmeListView);
                        inflater.inflate(R.layout.windme_list, layout_helpmelist, true);

                        list = (ListView) view.findViewById(R.id.ListView_windme);

                        //list.addHeaderView(header);
                        list.setAdapter(adapter);
                        /*
                        String temp = pref.getString("PROFILE_IMAGE", "-1");
                        String bgurl = temp.replace("//","/");
                        Uri uri = Uri.parse(bgurl);
                        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
                        draweeView.setImageURI(uri);

                        // 등급별 등급 아이콘 거리 인원 제한 생성 ///////////////////////////////////////////////////////////////////////
                        GradeMachingTask gt = new GradeMachingTask();
                        ImageView gradelv = (ImageView)findViewById(R.id.ImageView_windme_grade);
                        TextView gradeKm = (TextView)findViewById(R.id.TextView_windme_LimitDist);
                        TextView gradePeople = (TextView)findViewById(R.id.TextView_windme_Limitpeople);
                        gt.Setgradeimage(gradelv, pref.getString("GRADE", "-1"));

                        gt.SetgradeDistanceText(gradeKm, pref.getString("GRADE", "-1"));
                        gt.SetgradePeopleText(gradePeople, pref.getString("GRADE", "-1"));
                        // 등급별 등급 아이콘 거리 인원 제한 생성 ///////////////////////////////////////////////////////////////////////
                        */

                        //리스트 터치시 반응//////////////////////////////////////////////////////////////////////////////
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Log.i(TAGLOG, "TEST 질문 코드" + arDessert.get(position).Asking_code);
                                Intent intent_01 = new Intent(context.getApplicationContext(), SelectedWindmeGroup.class);
                                intent_01.putExtra("ASKINGCODE", arDessert.get(position).Asking_code);
                                intent_01.putExtra("ASKING_TEXT", arDessert.get(position).Asking_message);
                                intent_01.putExtra("NUMBER_OF_ASK_USER", arDessert.get(position).Number_of_asked_user);
                                intent_01.putExtra("FINISH_CHECK", arDessert.get(position).Asking_finish_check_value);
                                startActivity(new Intent(intent_01));
                            }
                        });
                    }

                } else {
                    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layout_helpmelist = (LinearLayout)view.findViewById(R.id.Layout_helpmeListView);
                    inflater.inflate(R.layout.windme_emptyhelpme, layout_helpmelist, true);
                }
            }
        });
    }
}

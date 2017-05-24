package org.wind.projectwindb;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WindYouFragment2 extends Fragment {
    private String TAGLOG = "로그 : WindYouFragment2 : ";

    private windYouListAdapter adapter;
    private WindyouItem windyouItem;
    private ArrayList<WindyouItem> arDessert=new ArrayList<WindyouItem>();
    private String[] asking_help_list_index;
    private String[] asking_code;
    private String[] user_management_code;
    private String[] asking_user_management_code;
    private String[] nickname;
    private String[] image;
    private String[] asking_message;
    private String[] datetime;
    private String[] number_of_asked_user;
    private String[] asking_finish_check_value;
    private String[] number_of_response;
    private String[] response_value;

    String ahlitemp;
    String yourcodetemp;
    String profiletemp;
    String niknametemp;
    String Askingcodetemp;
    String Askingfinishvalue;


    private String NULL_RETURN="0";
    ListView list;
    Context context;

    View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fresco.initialize(getContext());
        view = inflater.inflate(R.layout.before_wind_you, container, false);
        context = view.getContext();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String user_management_code;

        user_management_code = pref.getString("USER_MANAGEMENT_CODE","-1");
        String message="http://222.116.135.136/windyoulist.php?usercode="+user_management_code;
        Log.i(TAGLOG, "윈드유 화면 메시지 로그" + message);
        windYouListFromServer(message);

        return view;
    }

    private void acceptHelpFromServer(String urlMessage) {
        Log.e(TAGLOG, "acceptHelpFromServer: " + "진입");
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String retrunedCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        retrunedCode = jsonObject.getString("result");
                        Log.e(TAGLOG, "acceptHelpFromServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "acceptHelpFromServer Error by hong: " + retrunedCode);

                }
            }
        });
    }

    private void windYouListFromServer(String urlMessage) {
        Log.e(TAGLOG, "windYouListFromServer: "+"진입");

        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                int jsonArrayCount = 0;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

                arDessert = new ArrayList<WindyouItem>();

                asking_help_list_index= new String[100];
                asking_code= new String[100];
                user_management_code= new String[100];
                asking_user_management_code= new String[100];
                nickname= new String[100];
                image= new String[100];
                asking_message= new String[100];
                datetime= new String[100];
                number_of_asked_user= new String[100];
                asking_finish_check_value= new String[100];
                number_of_response= new String[100];
                response_value= new String[100];

                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject jobject = root.getJSONObject(i);

                        asking_help_list_index[i] = jobject.getString("asking_help_list_index");
                        asking_code[i] = jobject.getString("asking_code");
                        user_management_code[i] = jobject.getString("user_management_code");
                        asking_user_management_code[i] = jobject.getString("asking_user_management_code");
                        nickname[i] = jobject.getString("nickname");
                        image[i] = jobject.getString("image");
                        asking_message[i] = jobject.getString("asking_message");
                        datetime[i] = jobject.getString("datetime");
                        number_of_asked_user[i] = jobject.getString("number_of_asked_user");
                        asking_finish_check_value[i] = jobject.getString("asking_finish_check_value");
                        number_of_response[i] = jobject.getString("number_of_response");
                        response_value[i] = jobject.getString("response_value");
                        jsonArrayCount++;
                    }
                    NULL_RETURN="0";
                } catch (JSONException e) {//요청된 도움이 없을 경우
                    Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                    NULL_RETURN="1";
                    e.printStackTrace();
                }

                if(dataRequiredToParsing != null) {
                    int cnt = 0;

                    Log.i(TAGLOG, "dataRequiredToParsing : " + dataRequiredToParsing);
                    windYouListAdapter adapter = new windYouListAdapter(context, R.layout.windyou_listform, arDessert);

                    if (NULL_RETURN.equals("0")) {
                        for (int i = 0; i < jsonArrayCount; i++) {
                            windyouItem = new WindyouItem(Integer.toString(i), asking_help_list_index[i], asking_code[i], user_management_code[i], asking_user_management_code[i],
                                    nickname[i], image[i], asking_message[i], datetime[i], number_of_asked_user[i], asking_finish_check_value[i]
                                    , number_of_response[i], response_value[i]);
                            if (asking_finish_check_value[i].equals("1")) {
                                adapter.arD.add(windyouItem);
                                cnt++;
                            }
                        }


                        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout_helpmelist = (LinearLayout) view.findViewById(R.id.Layout_helpyouListView);
                        inflater.inflate(R.layout.windyou_list, layout_helpmelist, true);

                        list = (ListView) view.findViewById(R.id.ListView_windyou);
                        list.setAdapter(adapter);

                    /*
                    String temp = pref.getString("PROFILE_IMAGE", "-1");
                    String bgurl = temp.replace("//", "/");
                    Uri uri = Uri.parse(bgurl);
                    SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.ImageView_windyou_profile);
                    draweeView.setImageURI(uri);*/

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ahlitemp = arDessert.get(position).Asking_help_list_index;
                                yourcodetemp = arDessert.get(position).Asking_user_management_code;
                                profiletemp = arDessert.get(position).Image;
                                niknametemp = arDessert.get(position).Nickname;
                                Askingcodetemp = arDessert.get(position).Asking_code;
                                Askingfinishvalue = arDessert.get(position).Asking_finish_check_value;

                                if (arDessert.get(position).Response_value.equals("0")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                    builder.setTitle("도움요청 수락");
                                    builder.setMessage("도움요청을 수락하시겠습니까?");
                                    builder.setIcon(R.drawable.icon_wind8686);

                                    builder.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String message = "http://222.116.135.136/response.php?ahli=" + ahlitemp;
                                            Log.i(TAGLOG, "도움수락 테스트로그=" + ahlitemp);
                                            //ToDo : 모듈 분리
                                            acceptHelpFromServer(message);

                                            //GetDataFromSever getdatafromserver = new GetDataFromSever(WindYouActivity.this);
                                            //getdatafromserver.execute(message);

                                            String dataset = yourcodetemp + "&" + profiletemp + "&" + niknametemp + "&" + Askingcodetemp + "&" + "WINDYOU" + "&" + "NOGRADE";
                                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                                            SharedPreferences.Editor edit = pref.edit();
                                            edit.putString(ahlitemp, dataset);
                                            edit.commit();

                                            Intent intent_01 = new Intent(context.getApplicationContext(), ChatboxActivity.class);
                                            intent_01.putExtra("AHLI", ahlitemp);
                                            intent_01.putExtra("FINISH_CHECK", Askingfinishvalue);
                                            startActivity(new Intent(intent_01));
                                        }
                                    });
                                    builder.setNegativeButton("거부", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                } else {
                                    Log.i(TAGLOG, "수락된 도움 요청선택 채팅방 번호 : " + Askingcodetemp);

                                    String dataset = yourcodetemp + "&" + profiletemp + "&" + niknametemp + "&" + Askingcodetemp + "&" + "WINDYOU" + "&" + "NOGRADE";

                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                                    SharedPreferences.Editor edit = pref.edit();
                                    edit.putString(ahlitemp, dataset);
                                    edit.commit();

                                    Intent intent_01 = new Intent(context.getApplicationContext(), ChatboxActivity.class);
                                    intent_01.putExtra("AHLI", ahlitemp);
                                    intent_01.putExtra("FINISH_CHECK", arDessert.get(position).Asking_finish_check_value);
                                    startActivity(new Intent(intent_01));
                                }
                            }
                        });
                    } else {
                        Log.i(TAGLOG, "윈드유 결과값 NULL");
                        list = (ListView) view.findViewById(R.id.ListView_windyou);
                        list.setAdapter(adapter);
                    }
                }
            }
        });
    }
}

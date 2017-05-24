package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : ChatboxActivity.java
* 설명 : 채팅방 관련화면
* * 내부 DB 목록 :
* 채팅방셋팅
* edit.putString("NOWCHATAHLI", ahli);
* edit.putString("CHAT_YOUPROFILE", profileimage);
* edit.putString("CHAT_YOUNIKNAME", nikname);
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


public class ChatboxActivity extends Activity {

    // LOG TAG
    private String TAGLOG = "로그 : ChatboxActivity : ";

    private ChatboxAdapter adapter;
    private ChatboxItem groupItem;
    private ArrayList<ChatboxItem> arDessert=new ArrayList<ChatboxItem>();
    private String[] user_code;
    private String[] message;
    private String[] datetime;
    private String[] check_value;
    private String[] youNme;
    /*{"user_code":"39","message":"안녕하세","datetime":"2015-09-10 02:03:51","check_value":"0"}*/

    private String NULL_RETURN="0";
    ListView list;

    String profileimage;
    String nikname ;
    String grade;
    String ahli;
    String yourcode;
    String asking_code;
    String yourgade;

    String chatposition;

    String finich_check;

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.chatboxactivity);

        mContext=this;

        arDessert = new ArrayList<ChatboxItem>();
        adapter = new ChatboxAdapter(this, R.layout.chatbox_listform, arDessert);

        final Intent intent = new Intent(this.getIntent());
        ahli = intent.getStringExtra("AHLI");
        finich_check = intent.getStringExtra("FINISH_CHECK");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = pref.edit();
        String dataset = pref.getString(ahli,"-1");
        String[] result = dataset.split("&");
        Log.i(TAGLOG,"궁금하다 궁금해 데이터셋 내용 보여줘:"+dataset);
        //String dataset=yourcodetemp+"/"+profiletemp+"/"+niknametemp+"/"+Askingcodetemp+"/"+"WINDYOU"+"/"+"NOGRADE";
        yourcode=result[0];
        profileimage = result[1];
        nikname = result[2];
        asking_code = result[3];
        chatposition = result[4];
        yourgade = result[5];

        //현재 열려있는 채팅방 번호 부여 ////////////////////////////////////////////////////////////////////
        edit.putString("NOWCHATAHLI", ahli);
        edit.commit();
        //현재 열려있는 채팅방 번호 부여 ////////////////////////////////////////////////////////////////////

        //채팅방 사진 닉네임 셋팅///////////////////////////////////////////////////////////////////////
        edit.putString("CHAT_YOUPROFILE", profileimage);
        edit.putString("CHAT_YOUNIKNAME", nikname);
        edit.commit();
        //채팅방 사진 닉네임 셋팅///////////////////////////////////////////////////////////////////////

        //TODO: 이곳에 축복을
        String message = "http://222.116.135.136/chatread.php?ahli=" + ahli;
        Log.i(TAGLOG, "메세지 확인 : " + message);
        chatReadFromServer(message);


        TextView backbtn = (TextView) findViewById(R.id.TextView_Chatbox_backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재열려있는 채팅방 번호 리셋//////////////////////////////////////////////////////////////////
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("NOWCHATAHLI", "-1");
                edit.commit();
                //현재열려있는 채팅방 번호 리셋//////////////////////////////////////////////////////////////////

                finish();
            }
        });

        TextView completeHelp = (TextView) findViewById(R.id.TextView_Chatbox_CompleteHelpButton);
        if (chatposition.equals("WINDME")) {
            completeHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //http://222.116.135.136/finish.php?asking_code=&youcode=
                    //도움 완료 메세지 전송 ////////////////////////////////////////////////////////
                    String HelpCompleteMessage = "http://222.116.135.136/finish.php?asking_code=" + asking_code + "&youcode=" + yourcode;
                    Log.i(TAGLOG, "도움 완료 메세지 확인 : " + HelpCompleteMessage);
                    //TODO: 이곳에 축복을
                    finishHelpingFromServer(HelpCompleteMessage);
                    //GetDataFromSever getdatafromserver = new GetDataFromSever(ChatboxActivity.this);
                    //getdatafromserver.execute(HelpCompleteMessage);
                    //도움 완료 메세지 전송 ////////////////////////////////////////////////////////

                    Intent intent_01 = new Intent(getApplicationContext(), WriteReview.class);
                    intent_01.putExtra("PROFILEIMAGE", profileimage);
                    intent_01.putExtra("NIKNAME", nikname);
                    intent_01.putExtra("AHLI", ahli);
                    intent_01.putExtra("YOURMANAGEMENTCODE", yourcode);
                    intent_01.putExtra("YOURGRADE", yourgade);
                    intent_01.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(new Intent(intent_01));
                }
            });
        }
        else {
            completeHelp.setText("");
        }

        if(finich_check.equals("1"))
        {
            Button messagebutton = (Button)findViewById(R.id.Button_Chatbox_sendButton);
            messagebutton.setVisibility(View.INVISIBLE);
            EditText editText = (EditText)findViewById(R.id.EditText_Chatbox_MSG);
            editText.setVisibility(View.INVISIBLE);
            completeHelp.setText("");
        }
        else {
            Button messagebutton = (Button)findViewById(R.id.Button_Chatbox_sendButton);
            messagebutton.setVisibility(View.VISIBLE);
            EditText editText = (EditText)findViewById(R.id.EditText_Chatbox_MSG);
            editText.setVisibility(View.VISIBLE);
            if(chatposition.equals("WINDME")){completeHelp.setText("도움완료");}
        }
    }

    /**By honghee ToDo:확인요망 */
    private void chatReadFromServer(String urlMessage) {
        Log.e("chatReadFromServer", "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String my_management_code = "";
                String tempyoucode = "";
                String tempmsg;

                user_code = new String[100];
                message = new String[1000];
                datetime= new String[1000];
                check_value= new String[1000];
                youNme = new String[1000];

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                my_management_code = pref.getString("USER_MANAGEMENT_CODE", "-1");

                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < root.length() ; i++ ) {
                        JSONObject jsonObject = root.getJSONObject(i);

                        user_code[i] = jsonObject.getString("user_code");
                        tempmsg = jsonObject.getString("message");
                        message[i] = tempmsg.replace("__", "\n");
                        datetime[i] = jsonObject.getString("datetime");
                        check_value[i] = jsonObject.getString("check_value");

                        if(user_code[i].equals(my_management_code)){youNme[i]="1";}//나면 1
                        else{youNme[i]="0";tempyoucode=user_code[i];}//내가 아니면 0
                    }
                    editor.putString("YOU_MANAGEMENT_CODE", tempyoucode);
                    Log.e(TAGLOG, "여기가 맞나...." + tempyoucode);

                    for( int i = 0 ; i < root.length() ; i++ ) {
                        groupItem = new ChatboxItem(user_code[i], message[i], datetime[i], check_value[i],youNme[i]);
                        adapter.arD.add(groupItem);

                        if( i == root.length() - 1 ) {
                            editor.putString("lastChat" + ahli, message[i]);
                            editor.commit();
                        }
                    }
                    final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layout_chatboxlist = (LinearLayout) findViewById(R.id.Layout_Chatbox_list);
                    inflater.inflate(R.layout.chatbox_list, layout_chatboxlist, true);

                    list = (ListView) findViewById(R.id.ListView_Chatbox_List);
                    list.setAdapter(adapter);
                    list.setDividerHeight(0);

                    if(arDessert.isEmpty() == false) {
                        list.setSelection(arDessert.size());
                    }

                    list.deferNotifyDataSetChanged();
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            /*데이블이 눌렀을 경우 처리*/
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void chatWriteFromServer(String urlMessage) {
        Log.e("chatWriteFromServer", "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String resultCode = "";
                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        resultCode = jsonObject.getString("result");

                        Log.e(TAGLOG, "chatWriteFromServer by hong" + resultCode);
                    }
                } catch (JSONException e) {
                    Log.e(TAGLOG, "chatWriteFromServer Error by hong" + resultCode);

                    e.printStackTrace();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void finishHelpingFromServer(String urlMessage) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String resultCode = "";

                try {
                    JSONArray jsonArray = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        resultCode = jsonObject.getString("result");

                        Log.e(TAGLOG, "finishHelpingFromServer by hong" + resultCode);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "finishHelpingFromServer Error by hong" + resultCode);

                }
            }
        });

    }

    @Override
    public void onStop(){
        super.onStop();

        finish();
    }

    public void refresh(){
        String message = "http://222.116.135.136/chatread.php?ahli=" + ahli;
        Log.i(TAGLOG, "메세지 확인 : " + message);
        //TODO: 이곳에 축복을
        chatReadFromServer(message);
    }

    public void MessegeSendButtonClicked(View view){


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String myUserCode = pref.getString("USER_MANAGEMENT_CODE", "-1");

        EditText msgET = (EditText)findViewById(R.id.EditText_Chatbox_MSG);
       if(!msgET.getText().toString().equals("")) {
           try {
               String tempmsg = msgET.getText().toString().replace("\n","__");
               String msg = URLEncoder.encode(tempmsg, "UTF-8");
               String message = "http://222.116.135.136/chatwrite.php?ahli=" + ahli
                       + "&usercode=" + myUserCode + "&youcode=" + yourcode + "&message=" + msg;
               Log.i(TAGLOG, "메세지 확인 : " + message);
               //TODO: 이곳에 축복을
               chatWriteFromServer(message);
               //TODO: 이곳에 축복을
               //채팅창 갱신
               String message2 = "http://222.116.135.136/chatread.php?ahli=" + ahli;
               chatReadFromServer(message2);

               //텍스트뷰 초기화
               msgET.setText("");

           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }
}


///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기
class ChatboxItem{
    public String User_code;
    public String Message;
    public String Datetime;
    public String Check_value;
    public String YouNme;

    public ChatboxItem(String user_code,String message, String datetime,String check_value,
                       String youNme){
        User_code = user_code;
        Message = message;
        Datetime = datetime;
        Check_value = check_value;
        YouNme = youNme;
    }
}
///////////////////////////////////////////// 커스텀 리스트뷰 어탭터 설정하기
class ChatboxAdapter extends BaseAdapter {

    Context con;
    LayoutInflater inflater;
    ArrayList<ChatboxItem> arD;
    int layout;

    public ChatboxAdapter(Context context, int alayout, ArrayList<ChatboxItem> aarD)
    {
        con = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arD=aarD;
        layout=alayout;
    }
    /*{"user_code":"39","message":"안녕하세","datetime":"2015-09-10 02:03:51","check_value":"0"}*/
    @Override
    public int getCount(){
        return arD.size();
    }

    @Override
    public Object getItem(int position){
        return arD.get(position).User_code;
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

        if(arD.get(position).YouNme.equals("1")) {  //나일떄

            ImageView profile = (ImageView)convertView.findViewById(R.id.ImageView_Chatbox_profile_you);
            profile.setImageBitmap(null);

            TextView nikname = (TextView)convertView.findViewById(R.id.TextView_Chatbox_Nickname);
            nikname.setText("");

            TextView msg = (TextView) convertView.findViewById(R.id.TextView_Chatbox_MSG);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)msg.getLayoutParams();
            msg.setText(arD.get(position).Message);
            msg.setBackgroundResource(R.drawable.image_me_chatbox);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            msg.setGravity(Gravity.RIGHT);
            params.addRule(RelativeLayout.RIGHT_OF, 0);
            params.setMargins(0, 0, 30, 0);
            msg.setLayoutParams(params);

            TextView date = (TextView)convertView.findViewById(R.id.TextView_Chatbox_DateTime);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)date.getLayoutParams();
            date.setText(arD.get(position).Datetime);
            params2.addRule(RelativeLayout.LEFT_OF, R.id.TextView_Chatbox_MSG);
            params2.addRule(RelativeLayout.RIGHT_OF,0);

        } else { //상대편일때

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(con.getApplicationContext());
            String youprofile = pref.getString("CHAT_YOUPROFILE", "-1");
            String younikname = pref.getString("CHAT_YOUNIKNAME", "-1");

            String temp = youprofile;
            String bguri = temp.replace("//", "/");
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView)convertView.findViewById(R.id.ImageView_Chatbox_profile_you);
            simpleDraweeView.setImageURI(Uri.parse(bguri));

            TextView nikname = (TextView)convertView.findViewById(R.id.TextView_Chatbox_Nickname);
            nikname.setText(younikname);

            TextView msg = (TextView) convertView.findViewById(R.id.TextView_Chatbox_MSG);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)msg.getLayoutParams();
            msg.setText(arD.get(position).Message);
            msg.setBackgroundResource(R.drawable.image_you_chatbox);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            msg.setGravity(Gravity.LEFT);
            params.addRule(RelativeLayout.RIGHT_OF, R.id.ImageView_Chatbox_profile_you);
            params.setMargins(0, 0, 0, 0);
            msg.setLayoutParams(params);

            TextView date = (TextView)convertView.findViewById(R.id.TextView_Chatbox_DateTime);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)date.getLayoutParams();
            date.setText(arD.get(position).Datetime);
            params2.addRule(RelativeLayout.LEFT_OF, 0);
            params2.addRule(RelativeLayout.RIGHT_OF, R.id.TextView_Chatbox_MSG);

        }

        return convertView;
    }
}


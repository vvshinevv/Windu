package org.wind.projectwindb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AskToAdministrator extends Activity {

    TextView cancel;
    EditText content;
    LinearLayout trans;
    String con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_to_administrator);

        cancel = (TextView)findViewById(R.id.cancel_help_me);
        content = (EditText)findViewById(R.id.content_help_me);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AskToAdministrator.this, HelpMainActivity.class));
            }
        });


        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");
        //System.out.println("usercode : "+tempCode);


        trans = (LinearLayout)findViewById(R.id.trans_help_me);
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                con = content.getText().toString();
                String conn = con.replaceAll("\\p{Space}", "v");
                System.out.println("test : "+conn);

                if(con.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "의견을 작성해 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                }else{
                    trans(conn);
                }
            }
        });

    }

    public void trans(String content)
    {
        class Connect extends AsyncTask<String, Void, String>
        {
            HttpClient httpClient;
            HttpResponse httpResponse;
            HttpPost httpPost;
            String temp;

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");


            @Override
            protected String doInBackground(String... strings) {
                httpClient = new DefaultHttpClient();
                httpPost = new HttpPost("http://222.116.135.136/suggest.php?usercode="+tempCode+"&message="+strings[0]);
                //?usercode=&message=

                System.out.println("usercode : "+tempCode + "message : "+strings[0]);
                //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("usercode", tempCode));
                //nameValuePairs.add(new BasicNameValuePair("message", strings[0]));


                try {
                    //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httpResponse = httpClient.execute(httpPost);
                    temp = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    System.out.println(temp);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return temp;
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String temp = jsonObject.getString("result");
                    System.out.println("Json Parsing : "+temp);

                    int x = Integer.parseInt(temp);
                    if(x==1)
                    {
                        Toast.makeText(getApplicationContext(), "소중한 의견 보내주셔서 감사합니다.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AskToAdministrator.this, userinfopage.class));
                        finish();
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "전송에 실패 하였습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onPostExecute(s);
            }
        }
        Connect ct = new Connect();
        ct.execute(content);
    }
}

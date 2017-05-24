package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SettingLocationActivity
* 설명 :
*   추가적인 도움요청 수신 위치를 설정하는 화면
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingLocationActivity extends FragmentActivity {
    // LOG TAG
    private String TAGLOG = "로그 : SettingLocationActivity : ";

    private GoogleMap map;

    private GPSProvider gps;
    double latitudetemp=100;
    double longitudetemp=190;

    private Integer MaximumRadius;
    int tempPickerValue_radius;

    Double tempLATI;
    Double tempLong;

    TextView locationtextView;

    int flag1;

    String ReturnMSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settinglocation);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        locationtextView = (TextView)findViewById(R.id.TextView_settinglocation_locationtext);

        TextView backbtn = (TextView)findViewById(R.id.Button_settinglocation_backbutton);
        TextView helpmebtn = (TextView)findViewById(R.id.Button_settinglocation_button);

        flag1=0;

        //맵 연결////////////////////////////////////////////////////////////////////////////////
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        tempLATI = Double.valueOf(pref.getString("LATITUDE", "-1")).doubleValue();
        tempLong = Double.valueOf(pref.getString("LONGITUDE", "-1")).doubleValue();
        showCurrentLocation(tempLATI, tempLong, tempPickerValue_radius);
        //맵 연결////////////////////////////////////////////////////////////////////////////////

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                addCircleToMap(tempPickerValue_radius,latLng.latitude,latLng.longitude);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("LATITUDE",Double.toString(latLng.latitude));
                edit.putString("LONGITUDE", Double.toString(latLng.longitude));
                edit.commit();
                tempLATI=latLng.latitude;
                tempLong=latLng.longitude;

                getLocation(tempLATI,tempLong);

                flag1=1;
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        helpmebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //마지막 설정값 저장
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();

                if(flag1==1) {
                    for (int i = 1; i <= 3; i++) {
                        String temp = pref.getString("LOCATIONUSE" + String.valueOf(i), "-1");
                        if (temp.equals("-1")) {
                            edit.putString("LOCATIONSETTING_LATITUDE" + String.valueOf(i), Double.toString(tempLATI));
                            edit.putString("LOCATIONSETTING_LONGITUDE" + String.valueOf(i), Double.toString(tempLong));
                            edit.putString("LOCATIONSETTING_LOCATIONTEXT" + String.valueOf(i), locationtextView.getText().toString());
                            edit.putString("LOCATIONUSE" + String.valueOf(i), "1");
                            edit.commit();

                            String tempCode = pref.getString("USER_MANAGEMENT_CODE","-1");

                            String message="http://222.116.135.136/setlocationinfo.php?lindex="+String.valueOf(i)+"&latitude="+Double.toString(tempLATI)+
                                    "&longitude="+Double.toString(tempLong)+"&usercode="+tempCode;
                            Log.i(TAGLOG, "위치정보 등록 로깅 : " + message);

                            settingLocationFromServer(message);
                            //GetDataFromSever getdatafromserver = new GetDataFromSever();
                            //getdatafromserver.execute(message);
                            break;
                        }
                    }
                    finish();
                }
            }
        });
    }
    /**By honghee ToDo:확인요망 */
    private void settingLocationFromServer(String urlMessage) {
        Log.e(TAGLOG, "settingLocationFromServer: "+"진입");
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
                        Log.e(TAGLOG, "settingLocationFromServer by hong: " + retrunedCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAGLOG, "settingLocationFromServer Error by hong: " + retrunedCode);

                }
            }
        });
    }

    private void addCircleToMap(int radius,Double tempLATI, Double tempLong) { //radius 입력은 키로미터 단위로 입력됨

        double R = 6371d; // earth's mean radius in km
        double d = (radius/R)/2; //radius given in km
        double lat1 = Math.toRadians(tempLATI);
        double lon1 = Math.toRadians(tempLong);
        PolylineOptions options = new PolylineOptions();
        for (int x = 0; x <= 360; x++)
        {
            double brng = Math.toRadians(x);
            double latitudeRad = Math.asin(Math.sin(lat1)*Math.cos(d) + Math.cos(lat1)*Math.sin(d)*Math.cos(brng));
            double longitudeRad = (lon1 + Math.atan2(Math.sin(brng)*Math.sin(d)*Math.cos(lat1), Math.cos(d)-Math.sin(lat1)*Math.sin(latitudeRad)));
            options.add(new LatLng(Math.toDegrees(latitudeRad), Math.toDegrees(longitudeRad)));
        }
        map.addPolyline(options.color(Color.argb(250,110,227,247)).width(3));

        // circle settings
        int radiusM = (radius*1000)/2;// your radius in meters
        double latitude = tempLATI;// your center latitude
        double longitude =tempLong; // your center longitude
        LatLng latLng = new LatLng(latitude,longitude);

        // draw circle
        int d2 = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d2, d2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(Color.argb(40,110,227,247));
        c.drawCircle(d2 / 2, d2 / 2, d2 / 2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        // mapView is the GoogleMap
        map.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng, radiusM * 2, radiusM * 2).
                transparency(0.5f));


        MarkerOptions maker = new MarkerOptions();
        maker.position(new LatLng(tempLATI,tempLong));
        map.addMarker(maker);


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



    //위치에 따른 맵 위치 조정 및 줌수치 수정////////////////////////////////////////////////////////
    public void showCurrentLocation(Double latitude, Double longitude, int tempPickerValue){
        LatLng curPoint =new LatLng(latitude, longitude);
        int MapFocusValue=15;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,MapFocusValue));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    //JSON 통신 //////////////////////////////////////////////////////////////////////////
    private class GetDataFromSever extends AsyncTask<String, String, String> {
        JSONArray countriesArray;

        @Override
        protected String doInBackground(String... urls){
            ArrayList<NameValuePair> post = new ArrayList<>();

            //post.add(new BasicNameValuePair("firstdata","1"));

            StringBuilder builder = new StringBuilder();

            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            HttpPost httppost = new HttpPost(urls[0]);

            try{
                UrlEncodedFormEntity urlencodedfromentity = new UrlEncodedFormEntity(post,"UTF-8");
                httppost.setEntity(urlencodedfromentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == 200){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while((line = reader.readLine())!=null) {
                        Log.i(TAGLOG,"line : "+line);
                        builder.append(line);
                    }

                    //응답받은 데이터 파싱
                    try{

                        JSONArray root = new JSONArray(builder.toString());
                        for (int i=0; i < root.length();i++){
                            JSONObject jobject = root.getJSONObject(i);

                        }
                        ReturnMSG="1";
                    } catch (JSONException e){
                        Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                        ReturnMSG="0";
                        e.printStackTrace();
                    }


                }

            } catch (Exception e) {
                Log.i(TAGLOG,"Exception try1 : " + e.getStackTrace());
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result){
            if(ReturnMSG.equals("1")) {

            }
        }
    }


}
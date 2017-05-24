package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : SelectRadiusWindMeActivity
* 설명 :
*   도움요청시 반경과, 도움요청 위치를 선택 할 수 있는 UI가 존재하는 화면
* 내부 DB 목록 :
*   edit.putString("LATITUDE", Double.toString(tempLATI));
*   edit.putString("LONGITUDE",Double.toString(tempLong));
*   edit.putString("SET_RADIUS",Integer.toString(numberPicker.getValue()));
////////////////////////////////////////////////////////////////////////////////////////////////////
*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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


public class SelectRadiusWindMeActivity extends FragmentActivity {
    // LOG TAG
    private String TAGLOG = "로그 : SelectRadiusWindMeActivity : ";

    private GoogleMap map;

    private GPSProvider gps;
    double latitudetemp=100;
    double longitudetemp=190;

    private Integer MaximumRadius;
    int tempPickerValue_radius;

    Double tempLATI;
    Double tempLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_radius_windme);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        TextView backbtn = (TextView)findViewById(R.id.Button_selectrius_backbutton);
        TextView helpmebtn = (TextView)findViewById(R.id.helpmebutton);

        //최대반경 설정 /////////////////////////////////////////////////////////////////////////
        //GradeMachingTask gmt = new GradeMachingTask();
        //TextView radiustv = (TextView)findViewById(R.id.TextView_selectradiuswindme_radius);
        //MaximumRadius = gmt.GradeInDistanceOut(pref.getString("GRADE","-1"));
        //gmt.SetgradeDistanceText(radiustv,pref.getString("GRADE", "-1"));
        //최대반경 설정 /////////////////////////////////////////////////////////////////////////

        //넘버피커 셋팅 /////////////////////////////////////////////////////////////////////////
        //final NumberPicker numberPicker = (NumberPicker)findViewById(R.id.NumberPicker_seletradiuswindme_radius);
        //numberPicker.setMinValue(1);
        //numberPicker.setMaxValue(MaximumRadius);
        //numberPicker.setValue(1);
        tempPickerValue_radius=15;//반경범위를 기준으로 맵의 포커스 축척값을 계산하기위한 값

        //넘버피커 셋팅 /////////////////////////////////////////////////////////////////////////

        //맵 연결////////////////////////////////////////////////////////////////////////////////
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        tempLATI = Double.valueOf(pref.getString("LATITUDE", "-1")).doubleValue();
        tempLong = Double.valueOf(pref.getString("LONGITUDE", "-1")).doubleValue();
        showCurrentLocation(tempLATI, tempLong, tempPickerValue_radius);
        addCircleToMap(tempPickerValue_radius, tempLATI, tempLong);
        //맵 연결////////////////////////////////////////////////////////////////////////////////

        /**numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                map.clear();
                addCircleToMap(newVal, tempLATI, tempLong);
                tempPickerValue_radius=newVal;
                showCurrentLocation(tempLATI, tempLong, tempPickerValue_radius);
            }
        });*/

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
                edit.putString("LATITUDE", Double.toString(tempLATI));
                edit.putString("LONGITUDE",Double.toString(tempLong));
                edit.putString("SET_RADIUS",Integer.toString(tempPickerValue_radius));
                edit.commit();

                Intent intent_01 = new Intent(getApplicationContext(), SelectPeopleWindme.class);
                startActivity(new Intent(intent_01));
                finish();
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

    //위치에 따른 맵 위치 조정 및 줌수치 수정////////////////////////////////////////////////////////
    public void showCurrentLocation(Double latitude, Double longitude, int tempPickerValue){
        LatLng curPoint =new LatLng(latitude, longitude);
        int MapFocusValue=0;
        if(tempPickerValue>0 && tempPickerValue<=2){MapFocusValue=15;}
        else if(tempPickerValue>2 && tempPickerValue<=5){MapFocusValue=14;}
        else if(tempPickerValue>5 && tempPickerValue<=7){MapFocusValue=13;}
        else if(tempPickerValue>7 && tempPickerValue<=12){MapFocusValue=12;}
        else if(tempPickerValue>12 && tempPickerValue<=25){MapFocusValue=11;}
        else {MapFocusValue=10;}

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,MapFocusValue));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }






}
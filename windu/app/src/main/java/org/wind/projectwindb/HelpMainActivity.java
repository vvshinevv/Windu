package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : HelpMainActivity
* 설명 : 도움받기 및 도움주기, 개인정보 변경을 선택할 수 있는 화면
* 내부 DB 목록 :
* GPS 셋팅
*        edit.putString("LATITUDE",Double.toString(latitude));
*        edit.putString("LONGITUDE",Double.toString(longitude));
* 개인정보 셋팅
*        저장되어있는 텍스트리스트
*        edit.putString("PROFILE_IMAGE",temp[0]);
*        edit.putString("NICKNAME",temp[1]);
*        edit.putString("GRADE", temp[2]);
*        edit.putString("AVG_REVIEWSCORE", temp[3]);
*        edit.putString("CATEGORY_SUBJECT1", temp[4]);
*        edit.putString("CATEGORY_SUBJECT2", temp[5]);
*        edit.putString("CATEGORY_SUBJECT3", temp[6]);
*        edit.putString("CATEGORY_SUBJECT4", temp[7]);
*        edit.putString("CATEGORY_SUBJECT5", temp[8]);
*        edit.putString("REVIEWCNT", temp[9]);
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HelpMainActivity extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 : HelpMainActivity : ";

    private GPSProvider gps;

    boolean isPageOpen = false;

    Animation translateLeftAnim;
    Animation translateRightAnim;
    ProgressBar progressBar;

    LinearLayout SlidingbuttonPage01;
    LinearLayout slidingPage01;
    Button slidingbutton;

    TextView tvUsername;
    TextView tvTotalStateOfExp;
    TextView tvLevel;

    private String[] temp;

    double latitudetemp=100;
    double longitudetemp=190;

    String ReturnMSG;

    //이미지 가져오기 관련 변수 선언 /////////////////
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    //이미지 가져오기 관련 변수 선언 /////////////////

    public static Context mContext;
    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_helpmain);


        mContext=this;
        backPressCloseHandler = new BackPressCloseHandler(this);

        gpsDataupdate();

        slidingbutton = (Button) findViewById(R.id.signinbutton);
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage1);
        SlidingbuttonPage01 = (LinearLayout) findViewById(R.id.slidbuttonpage);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        progressBar = (ProgressBar) findViewById(R.id.pbProgressbar);
        tvTotalStateOfExp = (TextView) findViewById(R.id.tvTotalStateOfExp);
        tvLevel = (TextView) findViewById(R.id.tvLevel);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);


        tvLevel.setText(getGrade());
    }
    private String getGrade() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String grade = pref.getString("GRADE", "");
        return grade;
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();

        //유저 정보 가져오기 /////////////////////////////////////////////////////////////////////////
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tempCode = pref.getString("USER_MANAGEMENT_CODE","-1");
        String username = pref.getString("NICKNAME","-1");

        String message="http://222.116.135.136/mypage2.php?usercode="+tempCode;
        Log.i(TAGLOG,"유저정보 보내줘--"+message);
        tvUsername.setText(username);
        getUserDataFromServer(message);

        //GetDataFromSever getdatafromserver = new GetDataFromSever();
        //getdatafromserver.execute(message);
        //유저 정보 가져오기 /////////////////////////////////////////////////////////////////////////

        if(isPageOpen ==true) {
            slidingPage01.startAnimation(translateRightAnim);
            SlidingbuttonPage01.startAnimation(translateRightAnim);
        }
        gpsDataupdate();

    }

    @Override
    public void onPause(){
        super.onPause();
        gps.stopUsingGPS();
    }

    /**By honghee ToDo:확인요망 */
    private void updateLatitudeLogitudeToServer(String urlMessage) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String resultData = "";

                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for( int i = 0 ; i < root.length() ; i++ ) {
                        JSONObject jsonObject = root.getJSONObject(i);

                        resultData = jsonObject.getString("result");
                        Log.e(TAGLOG, "위도 경도 갱신 by hong : " + resultData);
                    }
                } catch (JSONException e) {
                    Log.e(TAGLOG, "위도 경도 갱신 에러 by hong : " + resultData);
                    e.printStackTrace();
                }
            }
        });
    }

    /**By honghee ToDo:확인요망 */
    private void getUserDataFromServer(String urlMessage) {
        Log.e(TAGLOG, "getUserDataFromServer: " + "진입");
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.GetUserDataFromServer(urlMessage, new GetDataCallback() {
            @Override
            public void done(String dataRequiredToParsing) {
                String[] temp = new String[13];
                String returnMSG = "";
                try {
                    JSONArray root = new JSONArray(dataRequiredToParsing);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject jsonObject = root.getJSONObject(i);

                        temp[0] = jsonObject.getString("profile_image");
                        temp[1] = jsonObject.getString("nickname");
                        temp[2] = jsonObject.getString("grade");
                        temp[3] = jsonObject.getString("avg_reviewscore");
                        temp[4] = jsonObject.getString("category_subject1");
                        temp[5] = jsonObject.getString("category_subject2");
                        temp[6] = jsonObject.getString("category_subject3");
                        temp[7] = jsonObject.getString("category_subject4");
                        temp[8] = jsonObject.getString("category_subject5");
                        temp[9] = jsonObject.getString("reviewcnt");
                        temp[10] = jsonObject.getString("exp");
                        //temp[10] = jsonObject.getString("limit_count_to_send");
                        //temp[11] = jsonObject.getString("limit_distance_to_send");
                        temp[12] = jsonObject.getString("set_location_cnt");
                    }
                    returnMSG = "1";
                } catch (JSONException e) {
                    Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                    ReturnMSG = "0";
                    e.printStackTrace();
                }

                if (returnMSG.equals("1")) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = pref.edit();

                    edit.putString("PROFILE_IMAGE", temp[0]);
                    edit.putString("NICKNAME", temp[1]);
                    edit.putString("GRADE", temp[2]);
                    edit.putString("AVG_REVIEWSCORE", temp[3]);
                    edit.putString("CATEGORY_SUBJECT1", temp[4]);
                    edit.putString("CATEGORY_SUBJECT2", temp[5]);
                    edit.putString("CATEGORY_SUBJECT3", temp[6]);
                    edit.putString("CATEGORY_SUBJECT4", temp[7]);
                    edit.putString("CATEGORY_SUBJECT5", temp[8]);
                    edit.putString("REVIEWCNT", temp[9]);
                    edit.putString("EXP", temp[10]);
                    edit.putString("SET_LOCATION_CNT", temp[12]);
                    edit.commit();

                    String tempImage = pref.getString("PROFILE_IMAGE", "-1");
                    String bguri = tempImage.replace("//", "/");
                    SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.ImageView_slidingpage_profile);
                    simpleDraweeView.setImageURI(Uri.parse(bguri));

                    String temp1 = pref.getString("EXP", "-1");
                    tvTotalStateOfExp.setText(temp1);
                    String[] temp2 = temp1.split("/");
                    Log.e("String", temp2[0] + temp2[1]);

                    String firString = temp2[0].substring(1); Log.e("String", firString);
                    String secString = temp2[1].substring(0, temp2[1].length() - 1); Log.e("Stringg", secString);

                    progressBar.setMax(Integer.parseInt(secString));
                    progressBar.setProgress(Integer.parseInt(firString));
                }
            }
        });
    }

    // 리뷰보기 버튼 이벤트 처리 코드 /////////////////////////////////////////////////////////////
    public void reviewbuttonclicked(View v){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tempCode = pref.getString("USER_MANAGEMENT_CODE","-1");

        pref.getString("CATEGORY_SUBJECT1", "-1");
        String tempdd="";
        if(!pref.getString("CATEGORY_SUBJECT1","-1").equals("#")){tempdd+=pref.getString("CATEGORY_SUBJECT1","-1")+" ";}
        if(!pref.getString("CATEGORY_SUBJECT2","-1").equals("#")){tempdd+=pref.getString("CATEGORY_SUBJECT2","-1")+" ";}
        if(!pref.getString("CATEGORY_SUBJECT3","-1").equals("#")){tempdd+=pref.getString("CATEGORY_SUBJECT3","-1")+" ";}
        if(!pref.getString("CATEGORY_SUBJECT4","-1").equals("#")){tempdd+=pref.getString("CATEGORY_SUBJECT4","-1")+" ";}
        if(!pref.getString("CATEGORY_SUBJECT5","-1").equals("#")){tempdd+=pref.getString("CATEGORY_SUBJECT5","-1")+" ";}

        String profile = pref.getString("PROFILE_IMAGE","-1");

        String review_avr = pref.getString("AVG_REVIEWSCORE","-1");

        String gradetxt = pref.getString("GRADE", "-1");

        Intent intent_01 = new Intent(getApplicationContext() , ReadReview.class);
        intent_01.putExtra("YOURUSERCODE", tempCode);
        intent_01.putExtra("PROFILE",profile);
        intent_01.putExtra("CATEGORY", tempdd);
        intent_01.putExtra("REVIEW_AVR", review_avr);
        intent_01.putExtra("GRADE", gradetxt);

        startActivity(intent_01);
    }
    // 리뷰보기 버튼 이벤트 처리 코드 /////////////////////////////////////////////////////////////

    public void gpsDataupdate(){
        //GPS 설정 ok//////////////////////////////////////////////////////////////////////////////////////////////////
        gps = new GPSProvider(HelpMainActivity.this);

        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Location distance = gps.getLocation();
//            double speed = gps.onLocationChanged();
            Toast.makeText(getApplicationContext(),"GPS 좌표 : "+String.valueOf(latitude)+"/"+String.valueOf(longitude),Toast.LENGTH_LONG).show();

            //위도 및 경도 정보 파일시스템 저장 ////////////////////////////////////////////////////////////////////
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("LATITUDE",Double.toString(latitude));
            edit.putString("LONGITUDE",Double.toString(longitude));
            edit.commit();
            latitudetemp=latitude;
            longitudetemp=longitude;
            //위도 및 경도 정보 파일시스템 저장 ////////////////////////////////////////////////////////////////////
        } else {
            gps.showSettingAlert();
        }
        //GPS 설정 ok//////////////////////////////////////////////////////////////////////////////////////////////////

        //GPS 위경도 WIND서버 갱신//////////////////////////////////////////////////////////////////////////////////////////////////
        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tempCode2 = pref2.getString("USER_MANAGEMENT_CODE", "-1");
        String message2="http://222.116.135.136/localinfoupdate.php?usercode="+tempCode2+"&latitude="
                +Double.toString(latitudetemp)+"&longitude="+Double.toString(longitudetemp);
        Log.i(TAGLOG, "위도경도--" + message2);

        updateLatitudeLogitudeToServer(message2);
        //GetDataFromSever getdatafromserver2 = new GetDataFromSever();
        //getdatafromserver2.execute(message2);
        //GPS 위경도 WIND서버 갱신//////////////////////////////////////////////////////////////////////////////////////////////////
    }

    //개인정보란 새로고침 이벤트 처리//////////////////////////////////////////////////////////////
    public void refresh() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String temp = pref.getString("PROFILE_IMAGE", "-1");
        String bguri = temp.replace("//", "/");

        Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(bguri));
        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new CacheKey(bguri));
        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new CacheKey(bguri));

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.ImageView_slidingpage_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));
    }
    //개인정보란 새로고침 이벤트 처리//////////////////////////////////////////////////////////////

    public void userinfobuttonclicked(View v){
        Intent intent_01 = new Intent(getApplicationContext() , userinfopage.class);
        startActivity(intent_01);
    }

    public void settingOnclicked(View v){
        Intent intent_01 = new Intent(getApplicationContext() , setting.class);
        startActivity(intent_01);
    }

    public void onSlidingbtnClicked(View v){
        if (isPageOpen) {
            slidingPage01.startAnimation(translateRightAnim);
            SlidingbuttonPage01.startAnimation(translateRightAnim);

        } else {
            slidingPage01.setVisibility(View.VISIBLE);
            slidingPage01.startAnimation(translateLeftAnim);
            SlidingbuttonPage01.startAnimation(translateLeftAnim);
        }
    }





    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()     {
    /*
     * 참고 해볼곳
     * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
     * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
     * http://www.damonkohler.com/2009/02/android-recipes.html
     * http://www.firstclown.us/tag/android/
     */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()     {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)     {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.

                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                }

                File f = new File(mImageCaptureUri.getPath());


                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");

                AndroidUploader androidUploader = new AndroidUploader();
                String uploadFilePath = mImageCaptureUri.getPath().toString();
                androidUploader.intuploadFile(uploadFilePath,tempCode);

                // 임시 파일 삭제
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

                mImageCaptureUri = data.getData();

                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());


                copyFile(original_file, cpoy_file);
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");

                intent.setDataAndType(mImageCaptureUri, "image/*");
                //intent.putExtra("outputX", 500);
                //intent.putExtra("outputY", 500);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);

                intent.putExtra("output", mImageCaptureUri);
                //intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }
        }
    }
    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     * @return Uri
     */
    private Uri createSaveCropFile(){
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        return uri;
    }


    /**
     * 선택된 uri의 사진 Path를 가져온다.
     * uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     * @param uri
     * @return
     */
    private File getImageFile(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if(mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor !=null ) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }
    /**
     * 파일 복사
     * @param srcFile : 복사할 File
     * @param destFile : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void profileOnclicked(View v){

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }


    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {

                isPageOpen = true;
            }
        }
        public void onAnimationRepeat(Animation animation) {

        }
        public void onAnimationStart(Animation animation) {

        }
    }

    public void helpOthers(View v) {
        Intent intent_01 = new Intent(getApplicationContext(),WindYouActivity.class);
        Log.v("hello","world");
        startActivity(new Intent(intent_01));
    }

    //도움요청 클릭 시
    public void helpRequestToOthers(View v){
        startActivity(new Intent(getApplicationContext(), SelectPeopleWindme.class));
    }



    public void helpList(View v) {
        Intent intent_01 = new Intent(getApplicationContext(),WindMeActivity.class);
        startActivity(new Intent(intent_01));
    }

    public void AskToAdministrator(View v) {
        Intent administrator = new Intent(getApplicationContext(), AskToAdministrator.class);
        startActivity(new Intent(administrator));
    }

}
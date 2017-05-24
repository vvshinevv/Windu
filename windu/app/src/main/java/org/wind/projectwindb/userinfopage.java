package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : userinfopage
* 설명 :
*   사용자 정보를 수정하는 화면 (프로필사진, 패스워드, 닉네임, 로그아웃)
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;


public class userinfopage extends Activity {
    // LOG TAG
    private String TAGLOG = "로그 :  userinfopage : ";

    String profile;
    String UserCode;
    String grade;
    String nickname;

    String NULL_RETURN;

    //이미지 가져오기 관련 변수 선언 /////////////////
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    //이미지 가져오기 관련 변수 선언 /////////////////

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.userinfopage);

        mContext=this;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        UserCode = pref.getString("USER_MANAGEMENT_CODE","-1");
        profile = pref.getString("PROFILE_IMAGE","-1");
        grade = pref.getString("GRADE","-1");
        nickname = pref.getString("NICKNAME","-1");

        // URL 이미지 생성 //////////////////////////////////////////////////////////
        String temp = profile;
        String bguri = temp.replace("//", "/");
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.ImageView_userinfopage_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));

        // 등급 이미지 생성 //////////////////////////////////////////////////////////
        ImageView gradeimage = (ImageView)findViewById(R.id.ImageView_userinfopage_grade);
        GradeMachingTask gt = new GradeMachingTask();
        gt.Setgradeimage(gradeimage, grade);

        // 닉네임 //////////////////////////////////////////////////////////
        final EditText nicknameTv =(EditText)findViewById(R.id.EditText_userInfo_nikname);
        nicknameTv.setText(nickname);
//        EditText emailTv =(EditText)findViewById(R.id.EditText_userInfo_email);
//        emailTv.setText("의미없는거같은데삭제요망");

        RelativeLayout passbtn = (RelativeLayout)findViewById(R.id.Layout_userinfopage_passwordchange);
        passbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), changepasswordpage.class);
                startActivity(new Intent(intent_01));
            }
        });

        //관리자에게 문의하기 버튼 클릭 시
//        LinearLayout administrator = (LinearLayout)findViewById(R.id.Administrator);
//        administrator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), AskToAdministrator.class));
//            }
//        });

        RelativeLayout logoutbtn = (RelativeLayout)findViewById(R.id.Layout_userinfopage_logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(userinfopage.this);

                builder.setTitle("로그아웃");
                builder.setMessage("정말로 로그아웃 하시겠습니까?");
                builder.setIcon(R.drawable.icon_wind8686);

                // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String message="http://222.116.135.136/logout.php?usercode="+UserCode;
                        Log.i(TAGLOG,"로그아웃 메세지 전송 : "+message);
                        GetDataFromSever getdatafromserver = new GetDataFromSever(getApplicationContext());
                        getdatafromserver.execute(message);
                        Toast.makeText(getApplicationContext(),"로그아웃 하셨습니다.",Toast.LENGTH_LONG).show();
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("FIRST_LOGIN","-1");
                        edit.commit();
                        Intent intent_01 = new Intent(getApplicationContext(), MainActivi.class);
                        intent_01.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(new Intent(intent_01));
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        RelativeLayout memberleavebtn = (RelativeLayout)findViewById(R.id.Layout_userinfopage_memberleave);
        memberleavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAGLOG,"아임 터치확인");
                Intent intent_01 = new Intent(getApplicationContext(), memberleave.class);
                startActivity(new Intent(intent_01));
            }
        });

        TextView submit = (TextView)findViewById(R.id.TextView_userInfo_submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changenikname = nicknameTv.getText().toString();
                if(changenikname.contains(" ")){
                    Toast.makeText(getApplicationContext(),"닉네임에 공백 문자열이 포함되어 있습니다.",Toast.LENGTH_LONG).show();
                } else {

                    String message = "http://222.116.135.136/changemyinfo.php?usercode=" + UserCode + "&nickname=" + changenikname;
                    Log.i(TAGLOG, "회원정보 변경 전송 : " + message);
                    GetDataFromSever getdatafromserver = new GetDataFromSever(getApplicationContext());
                    getdatafromserver.execute(message);

                    Intent intent_01 = new Intent(getApplicationContext(), HelpMainActivity.class);

                    Toast.makeText(getApplicationContext(),"회원정보가 수정되었습니다..",Toast.LENGTH_LONG).show();

                    intent_01.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(new Intent(intent_01));
                }
            }
        });
    }



    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction() {
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
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                intent.putExtra("outputX", 1000);
                intent.putExtra("outputY", 1000);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                //intent.putExtra("return-data", true);
                intent.putExtra("output", mImageCaptureUri);

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


    public void userubfo_profileOnclicked(View v){


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

    public void refresh() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tempCode = pref.getString("USER_MANAGEMENT_CODE", "-1");

        String temp = pref.getString("PROFILE_IMAGE", "-1");
        String bguri = temp.replace("//", "/");

        Fresco.getImagePipeline().evictFromMemoryCache(Uri.parse(bguri));

        Fresco.getImagePipelineFactory().getMainDiskStorageCache().remove(new CacheKey(bguri));
        Fresco.getImagePipelineFactory().getSmallImageDiskStorageCache().remove(new CacheKey(bguri));

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.ImageView_userinfopage_profile);
        simpleDraweeView.setImageURI(Uri.parse(bguri));
    }


    //JSON 통신 //////////////////////////////////////////////////////////////////////////
    private class GetDataFromSever extends AsyncTask<String, String, String> {
        JSONArray root;
        Context context;

        public GetDataFromSever(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... urls) {
            ArrayList<NameValuePair> post = new ArrayList<>();

            StringBuilder builder = new StringBuilder();

            post.add(new BasicNameValuePair("firstdata", "1"));

            HttpClient httpclient = new DefaultHttpClient();
            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            Log.i(TAGLOG,"테스트111"+urls[0]);
            HttpPost httppost = new HttpPost(urls[0]);

            try {
                UrlEncodedFormEntity urlencodedfromentity = new UrlEncodedFormEntity(post, HTTP.UTF_8);
                httppost.setEntity(urlencodedfromentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line="";
                    while ((line = reader.readLine()) != null) {
                        Log.i(TAGLOG, "Test line : " + line);
                        builder.append(line);
                     }
                }
            } catch (Exception e) {
                Log.i(TAGLOG, "Exception try1 : " + e.getStackTrace());
                e.printStackTrace();
            }

            //응답받은 데이터 파싱
            try {
                root = new JSONArray(builder.toString());
                for (int i = 0; i < root.length(); i++) {
                    JSONObject jobject = root.getJSONObject(i);

                }
                NULL_RETURN="0";
            } catch (JSONException e) {//요청된 도움이 없을 경우
                Log.i(TAGLOG, "Exception : parsing" + e.getStackTrace());
                Log.i(TAGLOG, "메세지 파싱 오류");
                NULL_RETURN="1";
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            int cntScore=0;
            int i=0;
            if (result != null) {

                }
                else  {

                }
        }
    }
}




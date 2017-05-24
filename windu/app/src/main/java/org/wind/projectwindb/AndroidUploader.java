package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : AndroidUploader
* 설명 : 이미지 업로드 클래스
////////////////////////////////////////////////////////////////////////////////////////////////////
*/



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AndroidUploader extends Activity{

    TextView messageText;
    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;

    /**********  File Path *************/

//    final String uploadFilePath = "storage/emulated/0/DCIM/Screenshots/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
//    final String uploadFileName = "Screenshot01.png"; //전송하고자하는 파일 이름

    public int intuploadFile(String sourceFileUri,String Usercode) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //dialog = ProgressDialog.show(HelpMainActivity.this, "", "Uploading file...", true);

        upLoadServerUri = "http://222.116.135.136/aaa.php";

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            //dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"
                    + sourceFileUri);

            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("usercode", Usercode);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"usercode\""+ lineEnd+ lineEnd
                        + Usercode);
//                dos.writeBytes("Content-Disposition: form-data; name=\"table_name\"\r\n\r\n" +tableName);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+fileName+"\""+lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)

                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {

                            //Toast.makeText(getApplicationContext(), "File Upload Complete.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }



                //close the streams //

                fileInputStream.close();
                dos.flush();

                dos.close();

                HelpMainActivity helpMainActivity= new HelpMainActivity();
                if((HelpMainActivity)helpMainActivity.mContext!=null) {
                    ((HelpMainActivity) helpMainActivity.mContext).refresh();
                }

                userinfopage userinfopage_activity= new userinfopage();
                if((userinfopage)userinfopage_activity.mContext!=null) {
                    ((userinfopage)userinfopage_activity.mContext).refresh();
                }

            } catch (MalformedURLException ex) {

                ex.printStackTrace();
                runOnUiThread(new Runnable() {

                    public void run() {

                        //Toast.makeText(getApplicationContext(), "MalformedURLException",Toast.LENGTH_SHORT).show();

                    }

                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

            } catch (Exception e) {

                e.printStackTrace();

                //Toast.makeText(getApplicationContext(), "Got Exception : see logcat ",Toast.LENGTH_SHORT).show();
                Log.i("Upload file to server Exception", "Exception : " + e.getMessage(),e);

            }
            Log.e("upload 뿌잉뿌잉: ", Integer.toString(serverResponseCode));

            //dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }

}
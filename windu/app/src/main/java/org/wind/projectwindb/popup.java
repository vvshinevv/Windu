package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : popup
* 설명 :
*   채팅창에서 작업을 진행중인 경우,
*   메세지 수신시 카카오톡처럼 팝업 발생
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class popup extends Activity implements
        OnClickListener {

    private Button mConfirm, mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup);

        Intent intent = new Intent(this.getIntent());
        String name= intent.getStringExtra("NAME");
        String text= intent.getStringExtra("TEXT");

        TextView nametv = (TextView)findViewById(R.id.TextView_popup_name);
        TextView texttv = (TextView)findViewById(R.id.TextView_popup_txt);

        nametv.setText(name);
        texttv.setText(text);

        Loading();
    }

    private void Loading() {
        // TODO Auto-generated method stub
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                finish();   // activity 종료
            }
        };
        handler.sendEmptyMessageDelayed(0, 1500);    // ms, 3초후 종료시킴
    }

    private void setContent() {
//        mConfirm = (Button) findViewById(R.id.btnConfirm);
//        mCancel = (Button) findViewById(R.id.btnCancel);
//
//        mConfirm.setOnClickListener(this);
//        mCancel.setOnClickListener(this);
    }

    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btnConfirm:
//                this.finish();
//                break;
//            case R.id.btnCancel:
//                this.finish();
//                break;
//            default:
//                break;
//        }
    }
}
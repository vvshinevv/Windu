package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : firstSplashActivity
* 설명 : 스플래쉬 화면 ( 로딩화면 )

////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


public class firstSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstsplashactivity);

        new SplashHandler(firstSplashActivity.this).sendEmptyMessageDelayed(3, 3000);
    }
    static class SplashHandler extends Handler {
        private final WeakReference<firstSplashActivity> mActivity;

        SplashHandler(firstSplashActivity activity) {
            mActivity = new WeakReference<firstSplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            firstSplashActivity activity = mActivity.get();

            if(activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
    public void handleMessage(Message msg) {
        finish();
    }
}
package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : ScreenViewFlipper
* 설명 :
*   HelpMainActivity의 슬라이드 화면 클래스
////////////////////////////////////////////////////////////////////////////////////////////////////
*/

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


/**
 * ScreenView Flipper
 *
 * @author Mike
 */

public class ScreenViewFlipper extends LinearLayout implements OnTouchListener {

	/**
	 * Count of index buttons. Default is 3
	 */
	public static int countIndexes = 3;


	/**
	 * Button Layout
	 */
	LinearLayout buttonLayout;

	/**
	 * Index button images
	 */
	ImageView[] indexButtons;

	/**
	 * Views for the Flipper
	 */
	View[] views;

	/**
	 * Flipper instance
	 */
    ViewFlipper flipper;

    /**
     * X coordinate for touch down
     */
    float downX;

    /**
     * X coordinate for touch up
     */
    float upX;

    /**
     * Current index
     */
    int currentIndex = 0;


	public ScreenViewFlipper(Context context) {
		super(context);
		
		init(context);
	}

	public ScreenViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}

    /**
     * Initialize
     *
     * @param context
     */
	public void init(Context context) {
		setBackgroundColor(0xffbfbfbf);

		// Layout inflation
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.screenview, this, true);

		//buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.setOnTouchListener(this);


		LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.leftMargin = 0;


		indexButtons = new ImageView[countIndexes];
		views = new TextView[countIndexes];
		for(int i = 0; i < countIndexes; i++) {
            if(i== 0) {flipper.setBackgroundResource(R.drawable.helpmain_frontslide);}

//            indexButtons[i] = new ImageView(context);
//                if (i == currentIndex) {
//                    indexButtons[i].setImageResource(R.drawable.redbar);
//                } else {
//                    indexButtons[i].setImageResource(R.drawable.bluebar);
//                }
//
//                indexButtons[i].setPadding(0, 10, 0, 0);
//                buttonLayout.addView(indexButtons[i], params);

		}


	}
    
	/**
	 * Update the display of index buttons
	 */
	private void updateIndexes() {
		for(int i = 0; i < countIndexes; i++) {
//			if (i == currentIndex) {
//				indexButtons[i].setImageResource(R.drawable.redbar);
//			} else {
//				indexButtons[i].setImageResource(R.drawable.bluebar);
//			}

        if(currentIndex == 0)
            {
                flipper.setBackgroundResource(R.drawable.helpmain_frontslide);
            } else if(currentIndex==1){
                flipper.setBackgroundResource(R.drawable.frontslide2);
            } else if(currentIndex==2){
                flipper.setBackgroundResource(R.drawable.frontslide3);
            }
		}
	}




	/**
	 * onTouch event handling
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if(v != flipper) return false;

		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			downX = event.getX();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			upX = event.getX();

			if( upX < downX ) {  // in case of right direction
 
				flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
		        		R.anim.wallpaper_open_enter));

		        flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
		        		R.anim.wallpaper_open_exit));

		        if (currentIndex < (countIndexes-1)) {
		        	flipper.showNext();

		        	// update index buttons
		        	currentIndex++;
		        	updateIndexes();
		        }
			} else if (upX > downX){ // in case of left direction

				flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
		        		R.anim.push_right_in));
		        flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
		        		R.anim.push_right_out));

		        if (currentIndex > 0) {
		        	flipper.showPrevious();

		        	// update index buttons
		        	currentIndex--;
		        	updateIndexes();
		        }
			}
		}

		return true;
	}
 
}

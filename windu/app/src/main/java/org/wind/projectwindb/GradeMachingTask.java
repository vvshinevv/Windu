package org.wind.projectwindb;

/*
////////////////////////////////////////////////////////////////////////////////////////////////////
* 작성자 : 최홍희
* 소스코드 버전 : 1.0
* 파일명 : GradeMachingTask
* 설명 :
*   등급별 아이콘 셋팅
*   등급별 요청 제한 인원 셋팅
*   등급별 요청 제한 거리 셋팅
////////////////////////////////////////////////////////////////////////////////////////////////////
*/


import android.widget.ImageView;
import android.widget.TextView;


public class GradeMachingTask {
    ImageView imageView = null;
    TextView textView=null;

    public void Setgradeimage(ImageView imageViews, String grade)
    {
        this.imageView = imageViews;

        if(grade.equals("1")) {
            imageView.setImageResource(R.drawable.grade1_icon);
        }
        else if(grade.equals("2")){
            imageView.setImageResource(R.drawable.grade2_icon);
        }
        else if(grade.equals("3")){
            imageView.setImageResource(R.drawable.grade3_icon);
        }
        else if(grade.equals("4")){
            imageView.setImageResource(R.drawable.grade4_icon);
        }
        else if(grade.equals("5")){
            imageView.setImageResource(R.drawable.grade5_icon);
        }
        else if(grade.equals("6")){
            imageView.setImageResource(R.drawable.grade6_icon);
        }
        else if(grade.equals("7")){
            imageView.setImageResource(R.drawable.grade7_icon);
        }
    }

    public void SetgradePeopleText(TextView textViews, String grade)
    {
        this.textView = textViews;

        if(grade.equals("1")) {
            textView.setText("5");
        }
        else if(grade.equals("2")){
            textView.setText("7");
        }
        else if(grade.equals("3")){
            textView.setText("9");
        }
        else if(grade.equals("4")){
            textView.setText("11");
        }
        else if(grade.equals("5")){
            textView.setText("14");
        }
        else if(grade.equals("6")){
            textView.setText("17");
        }
        else if(grade.equals("7")){
            textView.setText("20");
        }
    }

    public void SetgradeDistanceText(TextView textViews, String grade)
    {
        this.textView = textViews;

        if(grade.equals("1")) {
            textView.setText("5");
        }
        else if(grade.equals("2")){
            textView.setText("10");
        }
        else if(grade.equals("3")){
            textView.setText("15");
        }
        else if(grade.equals("4")){
            textView.setText("20");
        }
        else if(grade.equals("5")){
            textView.setText("25");
        }
        else if(grade.equals("6")){
            textView.setText("30");
        }
        else if(grade.equals("7")){
            textView.setText("35");
        }
    }

    public int GradeInDistanceOut(String grade){
        int result=0;
        if(grade.equals("1")) {
            result=5;
        }
        else if(grade.equals("2")){
            result=10;
        }
        else if(grade.equals("3")){
            result=15;
        }
        else if(grade.equals("4")){
            result=20;
        }
        else if(grade.equals("5")){
            result=25;
        }
        else if(grade.equals("6")){
            result=30;
        }
        else if(grade.equals("7")){
            result=35;
        }
        return result;
    }

}
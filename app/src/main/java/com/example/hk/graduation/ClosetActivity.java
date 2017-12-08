package com.example.hk.graduation;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClosetActivity extends FragmentActivity {
    //ImageButton pic_search;
    private int textCnt;
    private TextView mainText;
    private LinearLayout mainPic;
    private boolean leftCnt = false;
    private boolean rightCnt = false;
    private boolean drawerCnt = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
        textCnt = 0;
        startActivity(new Intent(this, MainActivity.class));
        cntInit();
        mainPic = (LinearLayout)findViewById(R.id.main);
        mainText = (TextView)findViewById(R.id.closetText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_closet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mainPic.setBackgroundResource(R.drawable.closetmain);
        cntInit();
        super.onResume();
        textCnt++;
        if(textCnt>6) textCnt=3;
        if(textCnt == 3) {
            mainText.setText("오늘은 무슨 옷을 입을까?");
        } else if(textCnt == 4) {
            mainText.setText("매일매일 뭘 입을지 고르는건 스트레스야");
        } else if(textCnt == 5) {
            mainText.setText("뭘 입어도 똑같네.역시 패완얼인건가(ToT)");
        } else {
            mainText.setText("오늘은 왠지 잘생겨보이는걸 훗!");
        }

    }
    public void picSearch(View v) {
        //SoundPool sound = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        //int soundId = sound.load(this,R.raw.sound01,1);

        //sound.play(soundId,1f,1f,0,0,1f);
        if(leftCnt) {
            Intent intent_01 = new Intent(getApplicationContext(), FindActivity.class);
            startActivity(intent_01);
            //mainPic.setBackgroundResource(R.drawable.closetmain);
        } else {
            mainText.setText("왼쪽 옷장에서는 사진촬영을 통해서 옷을 찾았던것 같아!");
            cntInit();
            mainPic.setBackgroundResource(R.drawable.closetleft);
            leftCnt = true;
        }
    }

    public void upSearch(View v) {
        if(rightCnt) {
            Intent intent_02 = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent_02);
        } else {
            mainText.setText("오른쪽 옷장에서는 저장된 사진을 통해서 옷을 찾을수 있구나!");
            //Toast.makeText(this, "한번 더 누르면 옷장이 열립니다.", Toast.LENGTH_SHORT).show();
            cntInit();
            mainPic.setBackgroundResource(R.drawable.closetright);
            rightCnt = true;
        }
    }

    public void  appExit(View v) {
        if(drawerCnt) {
            finish();
        } else {
            mainText.setText("서랍장에서 양말만 꺼내 신으면 외출준비 끝!");
            Toast.makeText(this, "한번 더 누르면 어플이 종료됩니다.", Toast.LENGTH_SHORT).show();
            cntInit();
            mainPic.setBackgroundResource(R.drawable.closetdraw);
            drawerCnt = true;
        }
    }

    public void cntInit() {
        leftCnt = false;
        rightCnt = false;
        drawerCnt = false;
    }

}
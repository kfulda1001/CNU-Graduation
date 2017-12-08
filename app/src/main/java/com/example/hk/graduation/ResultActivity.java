package com.example.hk.graduation;

import java.io.DataInputStream;
import java.io.File;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;


public class ResultActivity extends FragmentActivity implements OnTouchListener {
    int state;
    private ViewFlipper m_viewFlipper;
    private int m_nPreTouchPosX = 0;
    private static final String serverIP = "119.81.252.88"; // 서버 아이피
    private static final int serverPort = 5555; // 접속 포트
    ImageView progress;
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        state = 1;
        m_viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        m_viewFlipper.setOnTouchListener(this);

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");
        String name2 = intent.getExtras().getString("name2");
        String name3 = intent.getExtras().getString("name3");

        name = name.substring(0,name.length()-4);
        name2 = name2.substring(0,name2.length()-4);
        name3 = name3.substring(0,name3.length()-4);

        File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FindClothes");
        if (!mediaFile.exists()) {
            mediaFile.mkdirs();
        }

        LinearLayout sub1 = (LinearLayout) View.inflate(this, R.layout.result1, null);
        LinearLayout sub2 = (LinearLayout) View.inflate(this, R.layout.result2, null);
        LinearLayout sub3 = (LinearLayout) View.inflate(this, R.layout.result3, null);

        m_viewFlipper.addView(sub1);
        m_viewFlipper.addView(sub2);
        m_viewFlipper.addView(sub3);

        progress = (ImageView) findViewById(R.id.progress);

        StringTokenizer st1 = new StringTokenizer(name, "_");
        StringTokenizer st2 = new StringTokenizer(name2, "_");
        StringTokenizer st3 = new StringTokenizer(name3, "_");
        st1.nextToken();
        st2.nextToken();
        st3.nextToken();
        Bitmap myBitmap1 = BitmapFactory.decodeFile(mediaFile.getPath() + File.separator + "result1.jpg");
        ImageView myImage1 = (ImageView) findViewById(R.id.result1Img);
        myImage1.setImageBitmap(myBitmap1);
        ((TextView) findViewById(R.id.result1Brand)).setText(st1.nextToken());
        ((TextView) findViewById(R.id.result1Num)).setText(st1.nextToken());
        ((TextView) findViewById(R.id.result1Name)).setText(st1.nextToken());

        Bitmap myBitmap2 = BitmapFactory.decodeFile(mediaFile.getPath() + File.separator + "result2.jpg");
        ImageView myImage2 = (ImageView) findViewById(R.id.result2Img);
        myImage2.setImageBitmap(myBitmap2);
        ((TextView) findViewById(R.id.result2Brand)).setText(st2.nextToken());
        ((TextView) findViewById(R.id.result2Num)).setText(st2.nextToken());
        ((TextView) findViewById(R.id.result2Name)).setText(st2.nextToken());


        Bitmap myBitmap3 = BitmapFactory.decodeFile(mediaFile.getPath() + File.separator + "result3.jpg");
        ImageView myImage3 = (ImageView) findViewById(R.id.result3Img);
        myImage3.setImageBitmap(myBitmap3);
        ((TextView) findViewById(R.id.result3Brand)).setText(st3.nextToken());
        ((TextView) findViewById(R.id.result3Num)).setText(st3.nextToken());
        ((TextView) findViewById(R.id.result3Name)).setText(st3.nextToken());

        File[] files = mediaFile.listFiles();

        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void MoveNextView() {
        m_viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.appear_from_right));
        m_viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.disappear_to_left));
        m_viewFlipper.showNext();
        if(state == 1) {
            progress.setImageResource(R.drawable.progress2);
            state = 2;
        } else if(state == 2) {
            progress.setImageResource(R.drawable.progress3);
            state = 3;
        } else {
            progress.setImageResource(R.drawable.progress1);
            state = 1;
        }
    }
    public boolean  onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);

            alertDlg.setMessage("옷장을 닫으실건가요?");
            alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() { //확인 버튼
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            }) ;
            alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() { // 취소 버튼
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDlg.create();
            //alert.setTitle("");
            alert.show();
        }
        return false;
    }

    private void MovewPreviousView() {
        m_viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.appear_from_left));
        m_viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.disappear_to_right));
        m_viewFlipper.showPrevious();
        if(state == 1) {
            progress.setImageResource(R.drawable.progress3);
            state = 3;
        } else if(state == 2) {
            progress.setImageResource(R.drawable.progress1);
            state = 1;
        } else {
            progress.setImageResource(R.drawable.progress2);
            state = 2;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            m_nPreTouchPosX = (int) event.getX();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int nTouchPosX = (int) event.getX();

            if (nTouchPosX < m_nPreTouchPosX) {
                MoveNextView();
            } else if (nTouchPosX > m_nPreTouchPosX) {
                MovewPreviousView();
            }

            m_nPreTouchPosX = nTouchPosX;
        }

        return true;
    }

}
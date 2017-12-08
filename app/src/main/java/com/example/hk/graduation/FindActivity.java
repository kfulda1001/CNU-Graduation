package com.example.hk.graduation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.net.Socket;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Handler;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;
import android.view.View;
import android.widget.Toast;
import android.app.ProgressDialog;

public class FindActivity extends FragmentActivity  {
    private Preview mPreview ;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private boolean state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        mPreview = (Preview) findViewById(R.id.camera_preview);
        Toast toast_01 = Toast.makeText(this, "검색할 의류를 촬영해주세요", Toast.LENGTH_SHORT);
        toast_01.setGravity(Gravity.CENTER,0,0);
        toast_01.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find, menu);
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
    protected void onDestroy() {
        if (mPreview != null) {

            if (mPreview.mCamera != null) {
                mPreview.mCamera.stopPreview();
                mPreview.mCamera.release();
                mPreview.mCamera = null;
            }
        }
        super.onDestroy();
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            Calendar calendar = Calendar.getInstance();
            String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d",
                    calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

            File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FindClothes");
            if(!mediaFile.exists()){
                mediaFile.mkdirs();
            }

            File file = new File(mediaFile.getPath() +File.separator+ FileName+".jpg");

            try {

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
                fos.close();
                state = true;
                TCPclient tcpThread = new TCPclient(mediaFile.getPath() +File.separator+ FileName+".jpg");
                Thread thread = new Thread(tcpThread);
                thread.start();
                mHandler = new Handler();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mProgressDialog = ProgressDialog.show(FindActivity.this,"", "옷장을 뒤지는중입니다.",true);
                        mHandler.postDelayed( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    if (mProgressDialog!=null && !state){
                                        mProgressDialog.dismiss();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    }
                } );

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void takePictures(View v) {
        mPreview.mCamera.takePicture(null ,null, mPicture);
    }


    private class TCPclient implements Runnable {

        private static final String serverIP = "52.193.28.32"; // 서버 아이피
        private static final int serverPort = 55555; // 접속 포트

        private Socket socket = null;
        private String msg;
        // private String return_msg;
        public TCPclient(String _msg) {
            this.msg = _msg;
        }

        public void run() {
            String resultName = null;
            String resultName2 = null;
            String resultName3 = null;
            try {

                socket = new Socket(serverIP,serverPort );
                try {
                    DataInputStream dis = new DataInputStream(new FileInputStream(msg));
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    int len;

                    byte[] buf = new byte[2048];
                    while((len=dis.read(buf)) > 0) {
                        dos.write(buf,0,len);
                        dos.flush();
                    }
                    dis.close();
                    dos.close();

                    socket = new Socket(serverIP,serverPort);

                    File mediaFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FindClothes");
                    if(!mediaFile.exists()){
                        mediaFile.mkdirs();
                    }
                    File file = new File(mediaFile.getPath() +File.separator+"result1.jpg");
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buf2 = new byte[2048];
                    int read_length = 0;

                    DataInputStream diss = new DataInputStream(socket.getInputStream());
                    resultName = diss.readUTF();

                    while((read_length = diss.read(buf2)) > 0) {
                        output.write(buf2,0,read_length);
                        output.flush();
                    }

                    socket = new Socket(serverIP,serverPort);

                    file = new File(mediaFile.getPath() +File.separator+"result2.jpg");
                    output = new FileOutputStream(file);

                    DataInputStream disss = new DataInputStream(socket.getInputStream());
                    resultName2 = disss.readUTF();

                    while((read_length = disss.read(buf2)) > 0) {
                        output.write(buf2,0,read_length);
                        output.flush();
                    }

                    socket = new Socket(serverIP,serverPort);
                    file = new File(mediaFile.getPath() +File.separator+"result3.jpg");
                    output = new FileOutputStream(file);

                    DataInputStream dissss = new DataInputStream(socket.getInputStream());
                    resultName3 = dissss.readUTF();

                    while((read_length = dissss.read(buf2)) > 0) {
                        output.write(buf2,0,read_length);
                        output.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                    state = false;
                    Intent intent_result = new Intent(getApplicationContext(), ResultActivity.class);
                    intent_result.putExtra("name", resultName);
                    intent_result.putExtra("name2", resultName2);
                    intent_result.putExtra("name3", resultName3);
                    startActivity(intent_result);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }// run

    }// TCPclient


}
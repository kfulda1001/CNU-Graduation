package com.example.hk.graduation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryActivity extends FragmentActivity {

    private Context mContext;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    private boolean state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mContext = this;

        GridView gv = (GridView)findViewById(R.id.ImgGridView);
        final ImageAdapter ia = new ImageAdapter(this);
        gv.setAdapter(ia);
        gv.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                ia.callImageViewer(position);

            }
        });
        Toast toast_01 = Toast.makeText(this, "검색할 의류의 이미지를 선택해주세요", Toast.LENGTH_SHORT);
        toast_01.setGravity(Gravity.CENTER,0,0);
        toast_01.show();
    }

    /**==========================================
     * 		        Adapter class
     * ==========================================*/
    public class ImageAdapter extends BaseAdapter {
        private String imgData;
        private String geoData;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;

        ImageAdapter(Context c){
            mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
            getThumbInfo(thumbsIDList, thumbsDataList);
        }

        public final void callImageViewer(int selectedIndex){
            //Intent i = new Intent(mContext, GallerypopActivity.class);
            String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
            //i.putExtra("filename", imgPath);
            //startActivityForResult(i, 1);
            state = true;
            TCPclient tcpThread = new TCPclient(imgPath);
            Thread thread = new Thread(tcpThread);
            thread.start();
            mHandler = new Handler();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mProgressDialog = ProgressDialog.show(GalleryActivity.this,"", "옷장을 뒤지는중입니다.",true);
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

        }

        public boolean deleteSelected(int sIndex){
            return true;
        }

        public int getCount() {
            return thumbsIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(190, 190));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            }else{
                imageView = (ImageView) convertView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();

            bo.inSampleSize = 6;

            ContentResolver cr = getContentResolver();
            int id = Integer.parseInt(thumbsIDList.get(position));
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, bo);

            imageView.setImageBitmap(bitmap);
            return imageView;
        }

        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;
                String data;
                String imgSize;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                }while (imageCursor.moveToNext());
            }
            imageCursor.close();
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID){
            String imageDataPath = null;
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='"+ thumbID +"'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()){
                if (imageCursor.getCount() > 0){
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                }
            }

            imageCursor.close();
            return imageDataPath;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
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
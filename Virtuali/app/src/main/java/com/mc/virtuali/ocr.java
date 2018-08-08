package com.mc.virtuali;

import android.*;
import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ocr extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final Pattern sPattern = Pattern.compile("/(?:\\w+)|(?:[0-9]_)/");
    public int PERMISSION_CODE_CAMERA = 1;
    public int PERMISSION_CODE_READ_EXT_ST = 2;
    public int PERMISSION_CODE_WRITE_EXT_ST = 3;
    public int PERMISSION_CODE_REC_AUDIO = 4;
    public int PERMISSION_CODE_VIBRATE = 5;


    SurfaceView camera;
    TextView info;
    CameraSource camsrc;
    int count = 0;
    int flag = 0;
    int flag1 = 0;
    private static Toast toast;
    private TessBaseAPI mTess;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> ocr_results;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        String ar[] = result.split(" ");

        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toast != null)
            toast.cancel();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (toast != null)
            toast.cancel();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //PERMISSION TAKING CODE
        super.onCreate(savedInstanceState);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO}

                        , 10);
                Log.v("abc", "i am idiot");
            } else {
                return;
            }
        }
        setContentView(R.layout.activity_ocr);

//        if (ContextCompat.checkSelfPermission(this, new String[]{android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
//            Toast.makeText(this, "Thanks for giving permission for Camera", Toast.LENGTH_SHORT).show();
//        } else {
//            RequestPermission("Camera", PERMISSION_CODE_CAMERA);
//
//        }
//
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Thanks for giving permission of Reading External Storage", Toast.LENGTH_SHORT).show();
//        } else {
//            RequestPermission("Read_EXT_ST", PERMISSION_CODE_READ_EXT_ST);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Thanks for giving permission of Writing External Storage", Toast.LENGTH_SHORT).show();
//        } else {
//            RequestPermission("Write_EXT_ST", PERMISSION_CODE_WRITE_EXT_ST);
//        }
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Thanks for giving permission for Audio Recording", Toast.LENGTH_SHORT).show();
//        } else {
//            RequestPermission("Audio", PERMISSION_CODE_REC_AUDIO);
//        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "ALL permissions Done!!", Toast.LENGTH_SHORT).show();
//        } else {
//            RequestPermission("Vibrate", PERMISSION_CODE_VIBRATE);
//        }
        //END PERMISSION CODE


        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ocr_results = new ArrayList<String>();

        camera = findViewById(R.id.camera);
        info = findViewById(R.id.info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TextRecognizer text = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!text.isOperational()) {
            toast.makeText(this, "Not available!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("kk", "hhhhhhhh");

            camsrc = new CameraSource.Builder(getApplicationContext(), text)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setAutoFocusEnabled(true)
                    .build();

            camera.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        camsrc.start(camera.getHolder());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    camsrc.stop();
                }
            });
        }

        text.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }


            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                final SparseArray<TextBlock> item = detections.getDetectedItems();

                if(item.size() != 0){
                    info.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder sb = new StringBuilder();
                            Log.d("CRE", "kkkkkk" + count);

                            if (count<1) {
                                for (int i = 0; i < item.size(); i++) {
                                    TextBlock tb = item.valueAt(i);

                                    sb.append(tb.getValue());
                                    Log.d("yass",tb.getValue().toString());
                                    ocr_results.add(sb.toString());
                                    if(sb.length()==0)
                                    {
                                        Log.d("yass","KUCH DIKHA NHI");
                                        count=0;
                                    }
                                    else
                                    {   Log.d("yass","NOOOOOOO");
                                        count=1;
                                    }
                                    sb.append("\n");

                                    flag1=0;
                                }

                            }
                            Log.d("CRE", "xxxx" + ocr_results.size());

                            if(count==1) {
                                if (flag1 == 0) {

                                    //toast.makeText(getApplicationContext(), "Detection done", Toast.LENGTH_SHORT).show();
                                    flag1 = 1;
                                    String key = databaseReference.getKey();
                                    databaseReference.child("medicine").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int counter = 0;
                                            Boolean x = dataSnapshot.hasChildren();
                                            String x12 = x.toString();
                                            for (String searchedString : ocr_results) {

                                                String[] s2_l = searchedString.toLowerCase().split("_");
                                                StringBuilder sb2 = new StringBuilder();
                                                for (String s21 : s2_l)
                                                    sb2.append(s21 + " ");
                                                String s2 = sb2.toString().trim();

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Log.d("CRE", "kkkkkk" + snapshot.getKey());
                                                    String uid = snapshot.getKey();
                                                    String dosage = snapshot.child("Dosage").getValue(String.class);
                                                    String prescription = snapshot.child("Prescribed").getValue(String.class);
                                                    String intake = snapshot.child("How It Should be Taken").getValue(String.class);

                                                    //uid.toLowerCase().equals(searchedString.toLowerCase())
                                                    String[] s1_l = uid.toLowerCase().split("_");
                                                    StringBuilder sb1 = new StringBuilder();
                                                    for (String s11 : s1_l)
                                                        sb1.append(s11 + " ");
                                                    String s1 = sb1.toString().trim();


                                                    if (edit_distance(s1.toLowerCase(),s2.toLowerCase())<=2) {
                                                        medicine m = new medicine();
                                                        m.name = uid;
                                                        m.dosage = dosage;
                                                        m.prescription = prescription;
                                                        m.intake = intake;
                                                        flag = 1;
                                                        Intent intent1 = new Intent(getApplicationContext(), SearchResultsActivity.class);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                        intent1.putExtra("dosage", m.dosage);
                                                        intent1.putExtra("prescription", m.prescription);
                                                        intent1.putExtra("intake", m.intake);
                                                        intent1.putExtra("medicine_name", m.name);

                                                        getApplicationContext().startActivity(intent1);
                                                        counter++;

                                                    }
                                                }


                                            }

                                            Log.d("CRE", "kkkkklllllll" + flag);


                                            if (flag == 0) {
                                                //toast.makeText(getApplicationContext(), "Rotate left", Toast.LENGTH_SHORT).show();

                                               /* Thread timer = new Thread() {

                                                    @Override
                                                    public void run() {

                                                        try {
                                                            sleep(3000);

                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }


                                                    }
                                                };

                                                timer.start();
                                                */

                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //Do something after 100ms
                                                        count = 0;
                                                    }
                                                }, 3000);


                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                            info.setText(sb.toString());
                        }
                    });
                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == 0x7f080017/*R.id.action_settings*/) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent1 = new Intent(this, ocr.class);
//            intent1.putExtra("lang",lang_var);
            this.startActivity(intent1);
        } else if (id == R.id.nav_search) {
            Intent intent1 = new Intent(this, SearchBarActivity.class);
//            intent1.putExtra("lang",lang_var);
            this.startActivity(intent1);

        } else if (id == R.id.nav_notes) {
            Intent intent1 = new Intent(this, NotesActivity.class);
            this.startActivity(intent1);

        } else if (id == R.id.nav_reminders) {
            Intent intent1 = new Intent(this,com.mc.virtuali.ui.MainActivity.class);
            this.startActivity(intent1);

        }
        else if (id == R.id.nav_exit) {
            ActivityCompat.finishAffinity(ocr.this);

            finish();
        }

        else if (id == R.id.nav_camera_prescription) {
            Bundle bundle = new Bundle();
            bundle.putString("option", "camera");

            Intent i = new Intent(getApplicationContext(), Screen1.class);
            i.putExtras(bundle);
            this.startActivity(i);

        }

        else if (id == R.id.nav_gallery_prescriptions) {
            Bundle bundle = new Bundle();
            bundle.putString("option", "gallery");

            Intent i = new Intent(getApplicationContext(), Screen1.class);
            i.putExtras(bundle);
            this.startActivity(i);


        }

        else if (id == R.id.nav_prescription_list) {
            Intent intent1 = new Intent(this,PrescriptionList.class);
            this.startActivity(intent1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        ActivityCompat.finishAffinity(ocr.this);

        finish();
        System.exit(0);
    }

    int min(int a,int b,int c)
    {
        if(a<=b&&a<=c)
            return a;
        else if(b<=c&&b<=a)
            return b;
        else
            return c;
    }
    int edit_distance(String s1, String s2 )
    {

        int m=s1.length();
        int n=s2.length();
        int dp[][] = new int[m+1][n+1];
        for (int i=0;i<=m;i++)
        {
            for (int j=0;j<=n;j++)
            {
                if(j==0)
                    dp[i][j]=i;

                else if(i==0)
                    dp[i][j]=j;

                else if(s1.charAt(i-1)==s2.charAt(j-1))
                    dp[i][j]=dp[i-1][j-1];

                else
                    dp[i][j]=1+min(dp[i-1][j-1],dp[i-1][j],dp[i][j-1]);

            }
        }

        return dp[m][n];
    }
    public void RequestPermission(String s, int code){
        switch(s){
            case "Camera":
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){

                }
                else{
                    ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA}, code);}

            }
            case "Read_EXT_ST":{
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

                    }
                    else{
                        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, code);}
                }

            }
            case "Write_EXT_ST":{
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    }
                    else{
                        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);}

                }
            }
            case "Audio":{
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)){

                    }
                    else{
                        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.RECORD_AUDIO}, code);}

                }
            }
            case "Vibrate": {
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.VIBRATE)){

                    }
                    else{
                        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.VIBRATE}, code);}
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE_CAMERA){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode== PERMISSION_CODE_READ_EXT_ST){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PERMISSION_CODE_WRITE_EXT_ST){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PERMISSION_CODE_REC_AUDIO){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PERMISSION_CODE_VIBRATE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
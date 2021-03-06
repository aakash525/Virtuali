package com.mc.virtuali;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PrescriptionList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView mTextMessage;

    private RecyclerView recyclerViewPrescription;
    private ArrayList<Prescription> prescriptionArraylist;
    private PrescriptionAdapter prescriptionAdapter;
    private TextView textViewNoPrescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViews();
        fetchPrescriptions();
    }

    private void initViews() {
        prescriptionArraylist = new ArrayList<Prescription>();

        /** enabling back button ***/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** setting up recyclerView **/
        recyclerViewPrescription = (RecyclerView) findViewById(R.id.recyclerViewPrescriptions);
        recyclerViewPrescription.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerViewPrescription.setHasFixedSize(true);

        textViewNoPrescriptions = (TextView) findViewById(R.id.textViewNoPrescriptions);

    }

    private void fetchPrescriptions() {

        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/Virtuali/Prescriptions";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        try {
            Log.d("Files", "Size: " + files.length);
        }
        catch (Exception e){
            Log.d("Files","No files exist");
        }
        if( files!=null ){

            for (int i = 0; i < files.length; i++) {

                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/Virtuali/Prescriptions/" + fileName;

                Prescription prescription = new Prescription(recordingUri, fileName);
                prescriptionArraylist.add(prescription);
            }

            textViewNoPrescriptions.setVisibility(View.GONE);
            recyclerViewPrescription.setVisibility(View.VISIBLE);
            setAdaptertoRecyclerView();

        }else{
            textViewNoPrescriptions.setVisibility(View.VISIBLE);
            recyclerViewPrescription.setVisibility(View.GONE);
        }

    }

//    public void onClick(final View view) {
//        int itemPosition = recyclerViewPrescription.getChildLayoutPosition(view);
//        Prescription item = prescriptionArraylist.get(itemPosition);
//        Toast.makeText(getApplicationContext(), item.filename, Toast.LENGTH_LONG).show();
//    }

    private void setAdaptertoRecyclerView() {

        Log.d("Count", "This is count: "+prescriptionArraylist.size());
        prescriptionAdapter = new PrescriptionAdapter(this,prescriptionArraylist);
        recyclerViewPrescription.setAdapter(prescriptionAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prescription_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == 0x7f080017) {
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
            Bundle bundle = new Bundle();
            bundle.putString("option","camera");

            Intent i = new Intent(getApplicationContext(), ocr.class);
            i.putExtras(bundle);
            this.startActivity(i);

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
        else if (id == R.id.nav_prescription_list) {
            Intent intent1 = new Intent(this,PrescriptionList.class);
            this.startActivity(intent1);
        }
        else if (id == R.id.nav_exit) {
            ActivityCompat.finishAffinity(PrescriptionList.this);

            finish();
        }
        else if (id == R.id.nav_gallery_prescriptions) {
            Bundle bundle = new Bundle();
            bundle.putString("option", "gallery");

            Intent i = new Intent(getApplicationContext(), Screen1.class);
            i.putExtras(bundle);
            this.startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

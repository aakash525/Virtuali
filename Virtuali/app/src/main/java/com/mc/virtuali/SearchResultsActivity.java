package com.mc.virtuali;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SearchResultsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView dosage;
    TextView prescription;
    TextView intake;
    CardView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cv = (CardView)findViewById(R.id.card_view);
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        dosage=(TextView)findViewById(R.id.dosage_description);
        prescription=(TextView)findViewById(R.id.prescription_description);
        intake=(TextView)findViewById(R.id.intake_description);
        Intent intent = getIntent();
        //HashMap<String,medicine> med=(HashMap<String, medicine>)intent.getSerializableExtra("medicine");
        String medname=intent.getExtras().getString("medicine_name");
        String dosage_i=intent.getExtras().getString("dosage");
        String prescription_i=intent.getExtras().getString("prescription");
        String intake_i=intent.getExtras().getString("intake");

        Context context=getBaseContext();
        Resources resources = context.getResources();
        getSupportActionBar().setTitle(resources.getString(R.string.searchresults));
        ctl.setTitle(medname);
        cv.setCardBackgroundColor(getResources().getColor(R.color.lightCyan));

        Log.d("lollll","kkkkkk"+medname);

        dosage.setText(dosage_i);
        prescription.setText(prescription_i);
        intake.setText(intake_i);

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
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            Intent intent = new Intent(this,SearchBarActivity.class);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
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
        else if (id == R.id.nav_exit) {
            ActivityCompat.finishAffinity(SearchResultsActivity.this);

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
}

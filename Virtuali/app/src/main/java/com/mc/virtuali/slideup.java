package com.mc.virtuali;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class slideup extends AppCompatActivity {

    private SlidingUpPanelLayout mLayout;
    private static final String TAG = "Slideup";
    TextView dosage;
    TextView prescription;
    TextView intake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideup);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });

        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        Log.d("lollll","kkkkkk"+medname);

        dosage.setText(dosage_i);
        prescription.setText(prescription_i);
        intake.setText(intake_i);

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
            Intent intent = new Intent(this,Home.class);
            this.startActivity(intent);
        }
    }
}

package com.mc.virtuali;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class Home extends AppCompatActivity {

    Button camera,gallery,exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        camera = findViewById(R.id.bcamera);
        gallery = findViewById(R.id.bgallery);
        exit = findViewById(R.id.bexit);

        // Button action listener
        if(OpenCVLoader.initDebug())
        {
            System.out.println("loooooooooooooooooooollllllllllll");
        }
        else
            System.out.println("kkkkkkkkkkkkkkk");

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("option","camera");

                Intent i = new Intent(getApplicationContext(), Screen1.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("option","gallery");

                Intent i = new Intent(getApplicationContext(), Screen1.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });

    }
}

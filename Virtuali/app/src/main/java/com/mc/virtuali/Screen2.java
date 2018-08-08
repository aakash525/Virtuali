package com.mc.virtuali;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;

/**
 * Created by adc on 10/4/18.
 */

public class Screen2 extends AppCompatActivity {

//    Button back;
    TextView text;
    ImageButton save,search;
    String fileName;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        back = findViewById(R.id.back);
        text = findViewById(R.id.ocrtext);
        save = findViewById(R.id.save);
        search = findViewById(R.id.search);

        Bundle bundle = getIntent().getExtras();
        final String str = bundle.getString("text");
        final String ar[]=str.split(" ");


        text.setText(str);
        Log.i("screen2",str);
        String key = databaseReference.getKey();
        databaseReference.child("medicine").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                Boolean x = dataSnapshot.hasChildren();
                String x12 = x.toString();
                for (String searchedString : ar) {

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


                        if ((s1.toLowerCase().contains(s2.toLowerCase()))) {
                            medicine m = new medicine();
                            m.name = uid;
                            m.dosage = dosage;
                            m.prescription = prescription;
                            m.intake = intake;

                            final Intent intent1 = new Intent(getApplicationContext(), SearchResultsActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            intent1.putExtra("dosage", m.dosage);
                            intent1.putExtra("prescription", m.prescription);
                            intent1.putExtra("intake", m.intake);
                            intent1.putExtra("medicine_name", m.name);

                            search.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getApplicationContext().startActivity(intent1);
                                }
                            });
                            counter++;

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                File root = android.os.Environment.getExternalStorageDirectory();
                String name = String.valueOf(System.currentTimeMillis() + ".txt");


                File prescription_dir = new File(root.getAbsolutePath() + "/Virtuali/Prescriptions/");
                if (!prescription_dir.exists()){
                    prescription_dir.mkdirs();
                }


                fileName += root.getAbsolutePath() + "/Virtuali/Prescriptions/" + name;

                File file = new File(root.getAbsolutePath() + "/Virtuali/Prescriptions/"+name);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), "utf-8"))) {
                    writer.write(str);
                    Toast.makeText(Screen2.this, "prescription created", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d("kb","Ëxception");
                    e.printStackTrace();
                }
            }
        });

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

    public int min(int a, int b, int c){
        if(a > b && a > c)
            return a;
        else
        if(b > c)
            return b;
        else
            return c;
    }
}




package com.mc.virtuali;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class SearchBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText search_edit_text;
    RecyclerView recyclerView1;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    HashMap<String,medicine> med;
    ArrayList<String> medname;
    boolean calledAlready=false;
    private Button button;
    SearchAdapter searchAdapter;
    int flag=1;
    String medicine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (!calledAlready) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                calledAlready = true;
            }

        }
        catch(Exception e){}

        setContentView(R.layout.activity_search_bar);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView12);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        /*
        * Create a array list for each node you want to use
        * */
        med = new HashMap<>();
        medname = new ArrayList<>();
        button = (Button) this.findViewById(R.id.voice_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                intent.putExtra("medicine_name", medicine);
                try{
                    startActivityForResult(intent,200);
                }catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("CRE","kkkkkk");

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    button.setVisibility(View.INVISIBLE);
                    medicine = s.toString();
                    setAdapter(s.toString());
                    Log.d("CRE","CHANGE");
                } else {
                    /*
                    * Clear the list when editText is empty
                    * */
                    button.setVisibility(View.VISIBLE);
                    med.clear();
                    medname.clear();
                    recyclerView1.removeAllViews();
                }
            }
        });
    }



    private void setAdapter(final String searchedString1) {
        Log.d("CRE","INSIDE ADAP");
        String key=databaseReference.getKey();
        databaseReference.child("medicine").addListenerForSingleValueEvent(new ValueEventListener() {



            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("CRE","HEY FROM SNAP");

                med.clear();
                medname.clear();
                recyclerView1.removeAllViews();

                int counter = 0;
                Boolean x=dataSnapshot.hasChildren();
                String x12=x.toString();
                Log.d("CRE", x12);

                /*
                * Search all users for matching searched string
                * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("CRE","kkkkkk"+snapshot.getKey());
                    String uid = snapshot.getKey();
                    Log.d("CRE","HI"+uid);
                    String dosage = snapshot.child("Dosage").getValue(String.class);
                    String prescription = snapshot.child("Prescribed").getValue(String.class);
                    String intake = snapshot.child("How It Should be Taken").getValue(String.class);
                    String nyay[]=uid.split("_");
                    String ans="";
                    String searchedString=searchedString1;
                    for(int i=0;i<nyay.length;i++)
                    {
                        ans+=nyay[i]+" ";
                    }

                    if(flag==1)
                    {
                        if(edit_distance(ans,searchedString)<2)
                        {   Log.d("HEI","HEYYY");
                            Log.d("HE1",ans+" "+searchedString);
                            searchedString=ans;
                        }
                    }
                    if (uid.toLowerCase().contains(searchedString.toLowerCase())) {
                        Log.d("HEI","HEY2");
                        medicine m=new medicine();
                        m.name=uid;
                        m.dosage=dosage;
                        m.prescription=prescription;
                        m.intake=intake;
                        med.put(uid,m);
                        medname.add(uid.replace("_"," "));
                        counter++;
                    } /*else if (uid.toLowerCase().contains(searchedString.toLowerCase())) {
                        fullNameList.add(uid);
                        userNameList.add(uid);
                        counter++;
                    }*/

                    /*
                    * Get maximum of 15 searched results only
                    * */
                    if (counter == 2)
                        break;
                }

                searchAdapter = new SearchAdapter(SearchBarActivity.this,med,medname);
                recyclerView1.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_bar, menu);
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

            Intent i = new Intent(getApplicationContext(), Screen1.class);
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
            ActivityCompat.finishAffinity(SearchBarActivity.this);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                search_edit_text.setText(result.get(0));
                flag=1;
            }
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            Intent intent = new Intent(this,ocr.class);
            this.startActivity(intent);
        }
    }
}


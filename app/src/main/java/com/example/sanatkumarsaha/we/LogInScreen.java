package com.example.sanatkumarsaha.we;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class LogInScreen extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (webPage.equals("true")){
                SharedPreferences share = getSharedPreferences("history",MODE_PRIVATE);
                SharedPreferences.Editor editor = share.edit();
                if (share.getString("LogInScreen_username","").equals(""))
                    editor.putString("LogInScreen_username",Username);
                else if (!Arrays.asList(array).contains(Username)) editor.putString("LogInScreen_username",share.getString("LogInScreen_username","")+","+Username);
                editor.apply();
                Toast.makeText(LogInScreen.this,"Logged In Successfully.",Toast.LENGTH_LONG).show();
                username.setText("");
                password.setText("");
                startActivity(i);
                return;
            }else{
                Toast.makeText(LogInScreen.this,"Wrong Username or Password!",Toast.LENGTH_LONG).show();
                webPage = "";
                return;
            }
        }
    };

    EditText password;
    AutoCompleteTextView username;
    TextView showpass;
    int a;
    String webPage = "";
    String data = "";
    String Username;
    String Password;
    Intent i;
    ProgressDialog progressDialog;
    String history;
    String[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        a=0;
        username.setOnClickListener(new AutoCompleteTextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() <1){
                    username.showDropDown();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("history",MODE_PRIVATE);
        history = sharedPreferences.getString("LogInScreen_username","");
        if (!history.equals("")){
            array = history.split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
            username.setAdapter(adapter);
        }
    }

    public void logInClicked(View v){

        if (!isConnected()){
            Toast.makeText(LogInScreen.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        Username = username.getText().toString();
        Password = password.getText().toString();
        if (Username.equals("") || Password.equals("")){
            Toast.makeText(LogInScreen.this,"Invalid Input!",Toast.LENGTH_LONG).show();
            return;
        }
        i = new Intent(this,AccountData.class);
        i.putExtra("username",Username);
        i.putExtra("password",Password);
        progressDialog = ProgressDialog.show(this,"","LoggingIn...");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://sanat.byethost12.com/LogIn.php?username="+Username+"&key="+"12051996"+"&password="+Password);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                    while ((data = buffer.readLine())!=null){
                        webPage += data;
                    }
                } catch (IOException e) {
                    return;
                }handler.sendEmptyMessage(0);
            }
        };

        Thread thread = new Thread(r);
        thread.start();
        }


    public void createAccountClicked(View v){
        if (!isConnected()){
            Toast.makeText(LogInScreen.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        username.setText("");
        password.setText("");
        Intent i = new Intent(this,CreateAccount.class);
        startActivity(i);
    }

    public void show(View v){

        showpass = (TextView) findViewById(R.id.show);
        if (showpass.getText().toString().equals("Show Password")){
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            password.setSelection(password.getText().toString().length());
            showpass.setText("Hide Password");
        }
        else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setSelection(password.getText().toString().length());
            showpass.setText("Show Password");
        }

    }

    @Override
    public void onBackPressed() {
        if (a == 0){
            Toast.makeText(LogInScreen.this,"Press Again To Close The App",Toast.LENGTH_LONG).show();
            a++;
        }
        else{
        super.onBackPressed();}
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }
}

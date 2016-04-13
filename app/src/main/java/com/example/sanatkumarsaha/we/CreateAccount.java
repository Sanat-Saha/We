package com.example.sanatkumarsaha.we;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.Message;
import android.os.Handler;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


public class CreateAccount extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (webPage.equals("true")){
                progressDialog.dismiss();
                Toast.makeText(CreateAccount.this,"Sorry, The Username is already in use!",Toast.LENGTH_LONG).show();
                webPage = "";
                return;
            }else{
                Runnable k = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/CreateAccount.php?username="+Username+"&key=12051996&password="+Password+"&name="+ URLEncoder.encode(Name,"utf-8"));
                            httpClient.execute(httpPost);
                        }catch (IOException e){
                            Toast.makeText(CreateAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                            return;
                        }handler2.sendEmptyMessage(0);
                    }
                };

                Thread thread1 = new Thread(k);
                thread1.start();
            }
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Toast.makeText(CreateAccount.this,"Account Created Successfully.",Toast.LENGTH_LONG).show();
            username.setText("");
            password.setText("");
            confirmpassword.setText("");
            name.setText("");
            startActivity(i);
        }
    };

    EditText username,password,confirmpassword,name;
    TextView showpass;
    String webPage = "";
    String data = "";
    String Username;
    String Password;
    String Name;
    Intent i;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        name = (EditText) findViewById(R.id.name);
    }

    public void createAccountClicked(View v){

        if (!isConnected()){
            Toast.makeText(CreateAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        Username = username.getText().toString();
        Password = password.getText().toString();
        final String confirmPassword = confirmpassword.getText().toString();
        Name = name.getText().toString();

        if (Username.equals("") || Password.equals("") || Name.equals("") || confirmPassword.equals("")){
            Toast.makeText(CreateAccount.this,"Invalid Input",Toast.LENGTH_LONG).show();
            return;
        }
        if (!Password.equals(confirmPassword)){
            Toast.makeText(CreateAccount.this,"Please Confirm The Password Correctly",Toast.LENGTH_LONG).show();
            return;
        }

        i = new Intent(this,LogInScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        progressDialog = ProgressDialog.show(this, "", "Creating Account....");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://sanat.byethost12.com/CreateAccountUsernameCheck.php?username="+Username+"&key="+"12051996");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                    while ((data = buffer.readLine())!=null){
                        webPage += data;
                    }
                } catch (IOException e) {
                    Toast.makeText(CreateAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                    return;
                }handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    public void show(View v){

        showpass = (TextView) findViewById(R.id.show);
        if (showpass.getText().toString().equals("Show Password")){
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            password.setSelection(password.getText().toString().length());
            confirmpassword.setSelection(confirmpassword.getText().toString().length());
            showpass.setText("Hide Password");
        }
        else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setSelection(password.getText().toString().length());
            confirmpassword.setSelection(confirmpassword.getText().toString().length());
            showpass.setText("Show Password");
        }

    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }


}

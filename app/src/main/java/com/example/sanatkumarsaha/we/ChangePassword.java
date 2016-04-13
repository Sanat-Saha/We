package com.example.sanatkumarsaha.we;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ChangePassword extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Toast.makeText(ChangePassword.this, "Password Changed Successfully. Please Log In Again!", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    };

    EditText password,confirmpassword;
    TextView showpass;
    String username,password_o,password_n;
    String webPage = "";
    String data = "";
    Intent intent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        if (!isConnected()){
            Toast.makeText(ChangePassword.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        password = (EditText)findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
    }

    public void changePasswordClicked(View v){
        if (!isConnected()){
            Toast.makeText(ChangePassword.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        password_n = password.getText().toString();
        String Confirmpassword = confirmpassword.getText().toString();
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password_o = bundle.getString("password");

        if (password_n.equals("") || Confirmpassword.equals("")){
            Toast.makeText(ChangePassword.this,"Invalid Input!",Toast.LENGTH_LONG).show();
            return;
        }

        if (password_n.equals(password_o)){
            Toast.makeText(ChangePassword.this, "New Password Can Not be Same As the Old One", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password_n.equals(Confirmpassword)){
            Toast.makeText(ChangePassword.this, "Please Confirm The Password Correctly", Toast.LENGTH_LONG).show();
            return;
        }

        intent = new Intent(this,LogInScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        progressDialog = ProgressDialog.show(this,"","Changing Password...");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/ChangeAccountPassword.php?username="+username+"&key="+"12051996"+"&password_o="+password_o+"&password_n="+password_n);
                    httpClient.execute(httpPost);
                } catch (IOException e) {
                    Toast.makeText(ChangePassword.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                    return;
                }handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
        password.setText("");
        confirmpassword.setText("");
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

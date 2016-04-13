package com.example.sanatkumarsaha.we;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class AccountData extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            a++;
            if (a == 2){
                progressDialog1.dismiss();
            }
            if(!webPage.equals(""))
            userData.setText(webPage);
            else userData.setText("(Empty)");
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            a++;
            if (a == 2){
                progressDialog1.dismiss();
            }
            name = webPage2;
            webPage2 = "";
            title.setText("Welcome! "+name+", Here is your Private Data-");
            j.putExtra("name",name);
            startService(j);
        }
    };


    TextView title,userData;
    String username,password,name;
    String webPage = "";
    String data = "";
    String webPage2 = "";
    String data2 = "",data1 = "";
    Intent j;
    int a,b,c;
    ProgressDialog progressDialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_data);
        if (!isConnected()){
            Toast.makeText(AccountData.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        title = (TextView) findViewById(R.id.title);
        userData = (TextView) findViewById(R.id.userData);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        j = new Intent(this,Background.class);
        j.putExtra("username",username);
        j.putExtra("password",password);
        progressDialog1 = ProgressDialog.show(this,"","Loading...");
        a = 0;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://sanat.byethost12.com/AccountData.php?username="+username+"&key="+"12051996"+"&password="+password);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                    b=0;
                    while ((data = buffer.readLine())!=null){
                        if(b == 0) {webPage += data;}
                        else {
                            webPage += "\n";
                            webPage += data;
                        }
                        b++;
                    }
                } catch (IOException e) {
                    Toast.makeText(AccountData.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                    return;
                }handler.sendEmptyMessage(0);
            }
        };

        Thread thread = new Thread(r);
        thread.start();

        Runnable k = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://sanat.byethost12.com/getName.php?username="+username+"&key="+"12051996"+"&password="+password);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                    while ((data2 = buffer.readLine())!=null){
                        webPage2 += data2;
                    }
                } catch (IOException e) {
                    Toast.makeText(AccountData.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                    return;
                }handler2.sendEmptyMessage(0);
            }
        };

        Thread thread2 = new Thread(k);
        thread2.start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isConnected()){
            Toast.makeText(AccountData.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()){

            case R.id.edit_account_data:
                Intent intent = new Intent(this,EditAccountData.class);
                intent.putExtra("username",username);
                intent.putExtra("password",password);
                intent.putExtra("data",userData.getText().toString());
                startActivity(intent);
                return true;
            case R.id.change_password:
                Intent j = new Intent(this,ChangePassword.class);
                j.putExtra("username",username);
                j.putExtra("password",password);
                startActivity(j);
                return true;
            case R.id.delete_account:
                Intent i = new Intent(this,DeleteAccount.class);
                i.putExtra("username",username);
                i.putExtra("password",password);
                i.putExtra("name",name);
                startActivity(i);
                return true;
            case R.id.received_messages:
                Intent k = new Intent(this,ReceivedMessages.class);
                k.putExtra("username", username);
                k.putExtra("password",password);
                k.putExtra("name",name);
                startActivity(k);
                return true;
            case R.id.send_message:
                Intent m = new Intent(this,SendMessage.class);
                m.putExtra("username", username);
                m.putExtra("password",password);
                m.putExtra("name",name);
                startActivity(m);
                return true;
            case R.id.sent_messages:
                Intent n = new Intent(this,SentMessages.class);
                n.putExtra("username", username);
                n.putExtra("password",password);
                n.putExtra("name",name);
                startActivity(n);
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        final Intent s = new Intent(this,LogInScreen.class);
        s.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Log Out");
        alertDialog.setMessage("Are You Sure You Want To Log Out?");
        alertDialog.setCancelable(true);
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService(j);

                startActivity(s);
                Toast.makeText(AccountData.this, "Logged Out Successfully", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.setButton3("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();

    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }
}

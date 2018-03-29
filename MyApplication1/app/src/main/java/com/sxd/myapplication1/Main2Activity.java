package com.sxd.myapplication1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.sxd.myapplication1.SpSaveRead.saveCurrentUserName;
import static com.sxd.myapplication1.SpSaveRead.saveUserPassword;

public class Main2Activity extends AppCompatActivity {

    EditText editTextUserName;
    EditText editTextPassword;
    TextView currentUserName;
    Button buttonLogin;
    Button buttonSign;
    String userName;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        buttonLogin = (Button)findViewById(R.id.button_login);
        buttonSign = (Button)findViewById(R.id.button_sign);

        currentUserName = (TextView)findViewById(R.id.currentUserName);
        currentUserName.setText(MyConst.UserName);

        editTextUserName = (EditText)findViewById(R.id.userName);
        editTextPassword = (EditText)findViewById(R.id.userPassword);
        currentUserName = (TextView)findViewById(R.id.currentUserName);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextUserName.getText().toString().isEmpty()){
                    test("Input your user name please");
                }
                else if(editTextPassword.getText().toString().isEmpty()){
                    test("Input your password");
                }
                else{
                    userName = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    VerifyLogin(userName,password);
                }
            }
        });

        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextUserName.getText().toString().isEmpty()) {
                    test("Input your user name please");
                } else if (editTextPassword.getText().toString().isEmpty()) {
                    test("Input your password");
                } else {
                    userName = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    Sign(userName,password);
                }
            }
        });
    }

    public void VerifyLogin(String title,String content){
        buttonLogin.setEnabled(false);
        buttonSign.setEnabled(false);
        VerifyLoginTask verifyLoginTask = new VerifyLoginTask();
        verifyLoginTask.execute(title,content);
    }

    private class VerifyLoginTask extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            ReadAndSave server = new ReadAndSave();
            return server.read(strings[0]).equals(strings[1]);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(!s) {
                test("Wrong user name or password");
                buttonLogin.setEnabled(true);
                buttonSign.setEnabled(true);
            }
            else{
                MyConst.current_page = MyConst.HOMEPAGE;
                MyConst.UserName = userName;
                currentUserName.setText(userName);
                Login();
            }
        }
    }

    public void Sign(String title,String content){
        buttonLogin.setEnabled(false);
        buttonSign.setEnabled(false);
        SignTask signTask = new SignTask();
        signTask.execute(title,content);
    }

    private class SignTask extends AsyncTask<String ,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean exist;
            ReadAndSave server = new ReadAndSave();
            exist = !server.read(strings[0]).isEmpty();
            if(!exist)
                server.save(strings[0],strings[1]);
            return exist;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                test("user name existed");
                buttonLogin.setEnabled(true);
                buttonSign.setEnabled(true);
            }
            else {
                MyConst.UserName = userName;
                MyConst.current_page = MyConst.HOMEPAGE;
                currentUserName.setText(userName);
                Login();
            }
        }
    }

    public void Login(){
        LoginTask loginTask = new LoginTask();
        loginTask.execute();
    }

    private class LoginTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean saved;
            ReadAndSave server = new ReadAndSave();
            saved = server.save("CurrentUserName",userName);
            return saved;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            buttonLogin.setEnabled(true);
            buttonSign.setEnabled(true);
            if(aBoolean){
                MyConst.login = true;
                MyConst.current_page = MyConst.HOMEPAGE;
                test("login successful");
                saveCurrentUserName(userName);
                saveUserPassword(userName,password);
                Intent intent = new Intent();
                intent.setClass(Main2Activity.this, MainActivity.class);
                Main2Activity.this.startActivity(intent);
            }
            else
                test("login failed: network fail");
        }
    }

    public void test(String testInfo){
        Toast.makeText(Main2Activity.this, testInfo,
                Toast.LENGTH_SHORT).show();
    }
}



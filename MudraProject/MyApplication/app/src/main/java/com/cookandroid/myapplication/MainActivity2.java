package com.cookandroid.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button movieBtn = (Button) findViewById(R.id.btn1);
        Button personBtn = (Button) findViewById(R.id.btn2);

        movieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        Chatting.class);
                startActivity(intent);      // 다음 화면으로 넘어감
            }
        });

        personBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        Chatting.class);
                startActivity(intent);      // 다음 화면으로 넘어감
            }
        });
    }
}

// [인물], [영화] 카테고리 선택 화면은 추가하지 않는 것으로 결정
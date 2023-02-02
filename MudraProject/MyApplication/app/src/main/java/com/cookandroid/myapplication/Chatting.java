package com.cookandroid.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;




public class Chatting extends AppCompatActivity {

    String answer = "umm";              // 답변의 초기값은 "모르겠습니다" 로 설정
    EditText et;
    ListView listView;
    int count = 0;          // clickSend 속 변수
    private NetworkService networkService;

    ArrayList<MessageItem> messageItems = new ArrayList<>();
    ChatAdapter adapter;

    //Firebase Database 관리 객체참조변수
    FirebaseDatabase firebaseDatabase;

    // chat 노드, 질문 노드의 참조객체 참조변수 (in firebase)
    DatabaseReference chatRef;
    DatabaseReference questionRef;

    final String Qgenre = "본 영화의 장르가 무엇인가요?";
    final String Qgrade = "본 영화의 관람가 등급은 무엇인가요?";
    final String Qactor = "본 영화에 주연으로 누가 출연하나요?";
    final String Qdirector = "본 영화의 감독은 누구인가요?";
    final String Qdate = "본 영화는 몇년도에 개봉했나요?";

    List<String> Qlist = Arrays.asList(Qgenre, Qgrade, Qactor, Qdirector, Qdate);
    String[] answers = new String[5];
    Boolean Iscorrect = false;          // 최종답변 도출완료? Yes or No
    JSONObject jobject = new JSONObject();          // answer 담긴 json 파일 만들기  // 최종 답변 json
    JSONArray jArray = new JSONArray();
    JSONObject answerjson = new JSONObject();           //  answer 정보가 들어갈 jsonobject 선언

    private static final String urls = "http://10.0.2.2:5000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Button yesBtn = (Button) findViewById(R.id.yesBtn);
        Button noBtn = (Button) findViewById(R.id.noBtn);
        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        et = findViewById(R.id.editText);
        listView = findViewById(R.id.listview);
        TextView tvName = (TextView) findViewById(R.id.tv_name);


        adapter = new ChatAdapter(messageItems, getLayoutInflater());
        listView.setAdapter(adapter);

        //Firebase DB관리 객체와 'chat'노드와 질문 노드의 참조객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatRef = firebaseDatabase.getReference("chat");
        questionRef = firebaseDatabase.getReference("질문");


        chatRef.removeValue();
        messageItems.clear();


        //firebaseDB에서 채팅 메세지들 실시간 읽어오기
        //'chat'노드에 저장되어 있는 데이터들을 읽어오기
        //chatRef에 데이터가 변경되는 것을 듣는 리스너 추가
        chatRef.addChildEventListener(new ChildEventListener() {
            //새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //G.nickName = "User";
                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                String answer = messageItem.getMessage();

                //새로운 메세지를 리스뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem);

                //리스트뷰를 갱신 (채팅화면에 나타남)
                adapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동

                makeJson(Qlist.get(count), answer);

                count = count + 1;

                if (count <= Qlist.size()-1) {          // size = 5
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String q = Qlist.get(count);
                            // 채팅방에 다음 질문 미리 건네주기
                            printMessage("Mudra", q);
                        }
                    }, 1000);      // 1초 뒤 실행
                }

                else {
                    printResult();

                    et.setClickable(false);         //  editText 비활성화
                    et.setEnabled(false);
                    et.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                                // 클릭했을 경우 발생할 이벤트
                                Toast.makeText(getApplicationContext(), "YES와 NO 버튼 중 하나를 클릭해주세요.", Toast.LENGTH_LONG).show();
                            }
                            return false;
                        }
                    });

                    yesBtn.setEnabled(true);            // 마지막 결과때 활성화해주기
                    noBtn.setEnabled(true);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Collections.shuffle(Qlist);             // 질문 리스트 랜덤으로 섞음

        String q = Qlist.get(0);            // 1번째 질문 던짐

        printMessage("Mudra", q);



        yesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jArray.add(answerjson);
                //System.out.println(jArray);             // 4/17 굳.
                jobject.put("답변", jArray);               //  답변 : { answerjson 내용 ... }

                Iscorrect = true;
                String message = "영화 탐색이 완료되었습니다! :)";
                printMessage("Mudra", message);     //  Mudra가 채팅 보내기
                sendServer();
            }
        });


        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Iscorrect = false;
                String message = "죄송합니다. 제가 알고있는 정보로는 잘 모르겠어요.";
                printMessage("Mudra", message);
            }
        });
    }

    public void printMessage(String name, String message) {
        G.nickName = "Mudra";
        Calendar calendar = Calendar.getInstance();     // 현재시간 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);      // 13:12
        String profileUrl = G.profileUrl;

        String nickName = G.nickName;
        String m = message;
        MessageItem messageItem = new MessageItem(nickName, m, time, profileUrl);

        messageItems.add(messageItem);

        adapter.notifyDataSetChanged();
        listView.setSelection(messageItems.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
        G.nickName = "User";
    }

    public void makeJson(String q, String answer) {
        switch (q){
            case Qgenre:
                answerjson.put("genre",answer);
                break;
            case Qactor:
                answerjson.put("actors",answer);
                break;
            case Qdate:
                answerjson.put("date",answer);
                break;
            case Qdirector:
                answerjson.put("director",answer);
                break;
            case Qgrade:
                answerjson.put("grade",answer);
                break;
        }
    }

    public void printResult() {
        // flask 에 총 [answers] 배열 보내기
        // 데이터 전처리된 결과 받아오기 , 아래 "최종영화" 안에 결과값 넣어주기
        String result = "최종 영화";
        G.nickName = "Mudra";
        String message = "";

        String m = "해당 조건에 맞는 영화를 탐색중입니다...";
        printMessage("Mudra", m);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String message = "찾으시는 영화 제목은 " + "\'"+result + "\'" + " 입니다.\n맞습니까?";
                printMessage("Mudra", message);
            }
        }, 3000);      // 3초 뒤 실행
    }

    public void sendServer(){
        class sendData extends AsyncTask<Void, Void, String> {
            protected void onPreExecute() {
                super.onPreExecute();
            }
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsonInput = new JSONObject();

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            answerjson.toString()
                    );

                    Request request = new Request.Builder()
                            .post(reqBody)
                            .url(urls+"/getAnswer")
                            .build();

                    Response responses = null;
                    responses = client.newCall(request).execute();
                    System.out.println(responses.body().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        sendData sendData = new sendData();
        sendData.execute();
    }


    public void onBackPressed(){            // 뒤로가기 버튼 누르면 firebase chat 노드 데이터 초기화 (채팅내역도 초기화)
        super.onBackPressed();
        chatRef.removeValue();
        messageItems.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void clickSend(View view) {
        // firebase DB에 저장할 닉네임, 메세지 값
        String nickName = G.nickName;
        String message = et.getText().toString();
        String profileUrl = G.profileUrl;

        // 메세지 작성 시간 문자열로
        Calendar calendar = Calendar.getInstance();     // 현재시간 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);      // 13:12


        // firebase DB에 저장할 값 (MessageItem 객체) 설정
        MessageItem messageItem = new MessageItem(nickName, message, time, profileUrl);
        //'chat'노드에 MessageItem객체를 통해
        chatRef.push().setValue(messageItem);

        // 답변 모음집에 추가
        if (count < 5) {
            answers[count] = message;
        }

        //EditText에 있는 글씨 지우기
        et.setText("");


        //소프트키패드를 안보이도록 
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        //처음 시작할때 EditText가 다른 뷰들보다 우선시 되어 포커스를 받아 버림.
        //즉, 시작부터 소프트 키패드가 올라와 있음.

    }
}
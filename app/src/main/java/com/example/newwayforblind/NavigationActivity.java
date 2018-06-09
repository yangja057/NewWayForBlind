package com.example.newwayforblind;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class NavigationActivity extends AppCompatActivity {

    Animation alpha, blink;
    LinearLayout above, below;
    FrameLayout doubleTap;
    TextView blinkText;
    ImageView blinkArrow;

    static final int STEP_CHANGED = 100;
    static final int STEP_COMPLETE = 300;
    static final int SOON = 6;
    static final int STRAIGHT = 5;
    private String[] route;
    private int routeLength;
    private Handler handler;
    private StepCheck stepCheck;
    private Orientation orientation;
    private OrientationCheck orientationCheck;
    private boolean ttsReady = false;
    private TextToSpeech tts;
    private int routeIndex;
    private int goalStep;
    private double stride;
    private boolean endPoint = false;
    private boolean isNav = false;
    private boolean isAlert = false;
    private boolean isStraight = false;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsReady = true;
                tts.setLanguage(Locale.KOREA);
            }
        });

        doubleTap = (FrameLayout) findViewById(R.id.doubleTap);
        doubleTap.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void doubleTap() {
                doubleTap.setVisibility(View.GONE);
                initAnimation();
                tts.speak("안내를 시작합니다.", TextToSpeech.QUEUE_ADD, null, null);

                if(!isNav){
                    isNav = true;
                    startNav();
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == STEP_CHANGED) {
                    String dir = orientationCheck.checkDirection(orientation.getOrientation());
                    if(dir.equals("")){

                    }else if(dir.equals("왼쪽")){

                    }else if(dir.equals("오른쪽")){

                    }else{

                    }
                    Log.v("step", stepCheck.getStep() + "");
                    if (goalStep - stepCheck.getStep() <= SOON && !endPoint && !isAlert) {
                        tts.speak("잠시 후 " + route[routeIndex] + "방향입니다.", TextToSpeech.QUEUE_ADD, null, null);
                        isAlert = true;
                        rotateArrow(route[routeIndex]);
                    }
                    if (stepCheck.getStep() >= STRAIGHT && !isStraight) {
                        isStraight = true;
                        rotateArrow("직진");
                    }
                } else if (msg.what == STEP_COMPLETE) {
                    if (!endPoint)
                        arrivePoint();
                    else
                        endNav();
                }
            }
        };

        orientation = new Orientation(getApplicationContext());

        init();
    }

    public void rotateArrow(String direction) {
        if(direction.equals("오른쪽")) {
            blinkArrow.setRotation(90);
        }

        else if(direction.equals("왼쪽")) {
            blinkArrow.setRotation(-90);
        }
        else if(direction.equals("직진")) {
            blinkArrow.setRotation(0);
        }
    }

    public void initAnimation() {
        alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        blink = AnimationUtils.loadAnimation(this, R.anim.blink);

        above = (LinearLayout)findViewById(R.id.above);
        below = (LinearLayout)findViewById(R.id.below);
        blinkText = (TextView)findViewById(R.id.blinkText);
        blinkArrow = (ImageView)findViewById(R.id.blinkArrow);

        above.startAnimation(alpha);
        below.startAnimation(alpha);
        blinkText.startAnimation(blink);
        blinkArrow.startAnimation(blink);
    }

    public void init(){
        Intent intent = getIntent();
        String result = intent.getStringExtra("route");
        route = result.split("/");
        routeLength = route.length;
        //보폭 받아오기
        stride = 1;
    }

    public void startNav(){
        stepCheck = new StepCheck(getApplicationContext(), handler);
        orientationCheck = new OrientationCheck();
        routeIndex = 0;
        stepCheck.startSensor();
        orientation.startSensor();
        goalStep = (int)(Integer.parseInt(route[1]) / stride + 0.5);
        if(route[routeIndex].equals("직진")){
            isStraight = true;
        }
        while(true) {
            if (ttsReady) {
                String text = route[routeIndex];
                text += "으로 " + goalStep + "걸음 가세요.";
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                stepCheck.setStepCount(goalStep);
                routeIndex = 2;
                break;
            }
        }
    }

    public void endNav(){
        stepCheck.endSensor();
        orientation.endSensor();
        tts.speak("목적지에 도달하였습니다.", TextToSpeech.QUEUE_ADD, null, null);
    }

    public void arrivePoint(){
        goalStep = Integer.parseInt(route[routeIndex + 1]);
        stepCheck.resetStep();
        while(true) {
            if (ttsReady) {
                String text = route[routeIndex];
                text += "으로 " + goalStep + "걸음 가세요.";
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                stepCheck.setStepCount(goalStep);
                routeIndex += 2;
                isAlert = false;
                isStraight = false;
                break;
            }
        }
        if(routeIndex == routeLength)
            endPoint = true;
    }
}

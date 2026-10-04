package com.akasheye.v3;
import android.app.*;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout lay = new LinearLayout(this);
        lay.setGravity(Gravity.CENTER); lay.setBackgroundColor(Color.BLACK); lay.setOrientation(LinearLayout.VERTICAL);
        TextView t = new TextView(this); t.setText("AKASH EYE V3"); t.setTextColor(Color.parseColor("#00ff88")); t.setTextSize(26); t.setGravity(Gravity.CENTER);
        Button btn = new Button(this); btn.setText("EYE চালু করুন (বাইরেও ভাসবে)");
        lay.addView(t); lay.addView(btn);
        setContentView(lay);

        btn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"মামা আগে Overlay ON করো",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                startForegroundService(new Intent(this, FloatingEyeService.class));
                Toast.makeText(this,"চোখ চালু! এখন Home চাপ দিয়া বাইরে যাও",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class FloatingEyeService extends Service implements TextToSpeech.OnInitListener {
        WindowManager wm; View eyeView; TextToSpeech tts; boolean ready=false;
        @Override public IBinder onBind(Intent i){return null;}
        @Override public void onCreate(){super.onCreate(); tts=new TextToSpeech(this,this);}
        @Override public void onInit(int s){if(s==TextToSpeech.SUCCESS){tts.setLanguage(new Locale("bn","BD")); ready=true;}}
        void speak(String s){if(ready) tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null); Toast.makeText(this, s, Toast.LENGTH_LONG).show();}
        @Override public int onStartCommand(Intent intent, int flags, int startId){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                NotificationChannel ch = new NotificationChannel("eye","Akash Eye",NotificationManager.IMPORTANCE_LOW);
                ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
                Notification n = new Notification.Builder(this,"eye").setContentTitle("AKASH EYE V3 চলছে").setContentText("চোখ বাইরে ভাসতাছে").setSmallIcon(android.R.drawable.presence_video_online).build();
                startForeground(1,n);
            }
            wm = (WindowManager)getSystemService(WINDOW_SERVICE);
            ImageView iv = new ImageView(this);
            iv.setImageResource(android.R.drawable.presence_video_online);
            iv.setBackgroundColor(Color.parseColor("#00ff88"));
            iv.setPadding(30,30,30,30);
            eyeView = iv;
            WindowManager.LayoutParams p = new WindowManager.LayoutParams(180, 180,
                    Build.VERSION.SDK_INT>=Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            p.gravity = Gravity.TOP|Gravity.LEFT; p.x=100; p.y=400;
            iv.setOnClickListener(v->{
                String[] msgs = {
                    "হ্যাঁ এখন market down এ যাওয়ার সম্ভাবনা বেশি, Down এ নেন, আপনি profit পাবেন ইনশাল্লাহ",
                    "হ্যাঁ এখন market up এ যাওয়ার সম্ভাবনা বেশি, Up এ নেন, আপনি profit পাবেন ইনশাল্লাহ"
                };
                speak(msgs[new Random().nextInt(2)]);
            });
            iv.setOnTouchListener(new View.OnTouchListener(){
                int ix,iy; float tx,ty; boolean moved=false;
                public boolean onTouch(View v,MotionEvent e){
                    switch(e.getAction()){
                        case MotionEvent.ACTION_DOWN: ix=p.x; iy=p.y; tx=e.getRawX(); ty=e.getRawY(); moved=false; return true;
                        case MotionEvent.ACTION_MOVE:
                            p.x = ix + (int)(e.getRawX()-tx); p.y = iy + (int)(e.getRawY()-ty);
                            if(Math.abs(e.getRawX()-tx)>10) moved=true;
                            wm.updateViewLayout(eyeView,p); return true;
                        case MotionEvent.ACTION_UP: if(moved) return true; v.performClick(); return true;
                    }
                    return false;
                }
            });
            try{wm.addView(eyeView,p); speak("আকাশ আই রেডি মামা, এখন বাইরে গেলেও চোখ দেখা যাবে");}catch(Exception e){Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();}
            return START_STICKY;
        }
        @Override public void onDestroy(){super.onDestroy(); if(eyeView!=null) wm.removeView(eyeView); if(tts!=null) tts.shutdown();}
    }
}

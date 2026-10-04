package com.akasheye.v3;
import android.app.*;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Random;
public class FloatingEyeService extends Service implements TextToSpeech.OnInitListener {
  WindowManager wm; View eye; TextToSpeech tts; boolean ready=false;
  @Override public IBinder onBind(Intent i){return null;}
  @Override public void onCreate(){super.onCreate(); tts=new TextToSpeech(this,this);}
  @Override public void onInit(int s){if(s==TextToSpeech.SUCCESS){tts.setLanguage(new Locale("bn","BD")); ready=true;}}
  void speak(String s){if(ready){tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,null); Toast.makeText(this,s,Toast.LENGTH_LONG).show();}}
  @Override public int onStartCommand(Intent intent,int f,int id){
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
      NotificationChannel ch=new NotificationChannel("eye","eye",NotificationManager.IMPORTANCE_LOW);
      ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
      startForeground(1,new Notification.Builder(this,"eye").setContentTitle("AKASH EYE চলছে").setSmallIcon(android.R.drawable.presence_video_online).build());
    }
    wm=(WindowManager)getSystemService(WINDOW_SERVICE);
    ImageView iv=new ImageView(this); iv.setImageResource(android.R.drawable.presence_away);
    iv.setBackgroundColor(0xFF00FF88); iv.setPadding(20,20,20,20); eye=iv;
    WindowManager.LayoutParams p=new WindowManager.LayoutParams(150,150,
      Build.VERSION.SDK_INT>=Build.VERSION_CODES.O?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
    p.gravity=Gravity.TOP|Gravity.LEFT; p.x=100; p.y=300;
    iv.setOnClickListener(v->{
      String[] msgs={
        "হ্যাঁ এখন market down এ যাওয়ার সম্ভাবনা বেশি, Down এ নেন, আপনি profit পাবেন ইনশাল্লাহ",
        "হ্যাঁ এখন market up এ যাওয়ার সম্ভাবনা বেশি, Up এ নেন, আপনি profit পাবেন ইনশাল্লাহ",
        "মামা আমি কোনো market দেখতে পারতাছি না, একটা market open করুন"
      };
      speak(msgs[new Random().nextInt(msgs.length)]);
    });
    iv.setOnTouchListener(new View.OnTouchListener(){
      int ix,iy; float tx,ty; boolean move=false;
      public boolean onTouch(View v,MotionEvent e){
        switch(e.getAction()){
          case MotionEvent.ACTION_DOWN: ix=p.x; iy=p.y; tx=e.getRawX(); ty=e.getRawY(); move=false; return true;
          case MotionEvent.ACTION_MOVE:
            int dx=(int)(e.getRawX()-tx), dy=(int)(e.getRawY()-ty);
            if(Math.abs(dx)>10||Math.abs(dy)>10) move=true;
            p.x=ix+dx; p.y=iy+dy; wm.updateViewLayout(eye,p); return true;
          case MotionEvent.ACTION_UP: if(move) return true; else {v.performClick(); return false;}
        }
        return false;
      }
    });
    wm.addView(eye,p);
    speak("আকাশ আই রেডি মামা, চোখ সব জায়গায় ভাসবে");
    return START_STICKY;
  }
  @Override public void onDestroy(){super.onDestroy(); if(eye!=null) wm.removeView(eye); if(tts!=null) tts.shutdown();}
}

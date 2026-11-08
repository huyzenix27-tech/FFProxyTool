package com.huyzenix.ffproxytool;

import android.app.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class OverlayService extends Service {
    private WindowManager wm; private TextView bubble; private View panel; private boolean expanded=false;
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density);}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    @Override public void onCreate(){
        super.onCreate();
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch=new NotificationChannel("turbo_panel","Game Turbo panel",NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
            Notification n=new Notification.Builder(this,"turbo_panel").setContentTitle("Game Turbo đang chạy").setContentText("Thanh công cụ nổi đã sẵn sàng").setSmallIcon(android.R.drawable.ic_menu_view).build();
            if(Build.VERSION.SDK_INT>=34) startForeground(22,n,android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE); else startForeground(22,n);
        }
        wm=(WindowManager)getSystemService(WINDOW_SERVICE); showBubble();
    }
    private WindowManager.LayoutParams params(int w,int h){int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;return new WindowManager.LayoutParams(w,h,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);}
    private TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setPadding(dp(10),dp(8),dp(10),dp(8));return t;}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setTextSize(12);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setBackground(shape(0xFF343449,14));return b;}
    private void showBubble(){
        bubble=label("⚡",22);bubble.setGravity(Gravity.CENTER);bubble.setBackground(shape(0xFFFF3978,50));
        WindowManager.LayoutParams p=params(dp(54),dp(54));p.gravity=Gravity.TOP|Gravity.START;p.x=dp(10);p.y=dp(190);
        bubble.setOnTouchListener(new View.OnTouchListener(){float sx,sy;int ox,oy;boolean moved;
            public boolean onTouch(View v,MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=e.getRawX();sy=e.getRawY();ox=p.x;oy=p.y;moved=false;return true;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){int dx=(int)(e.getRawX()-sx),dy=(int)(e.getRawY()-sy);if(Math.abs(dx)+Math.abs(dy)>dp(5))moved=true;p.x=ox+dx;p.y=oy+dy;wm.updateViewLayout(bubble,p);return true;}
                if(e.getAction()==MotionEvent.ACTION_UP){if(!moved)toggle();return true;}return true;
            }
        });wm.addView(bubble,p);
    }
    private void toggle(){if(expanded){if(panel!=null)wm.removeView(panel);panel=null;expanded=false;}else{showPanel();expanded=true;}}
    private void launch(String pkg){Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i!=null){i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}else Toast.makeText(this,"Ứng dụng chưa được cài đặt",Toast.LENGTH_SHORT).show();}
    private void showPanel(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(12),dp(10),dp(12),dp(10));box.setBackground(shape(0xF21A1A28,20));
        box.addView(label("⚡ GAME TURBO",17));box.addView(label("CÔNG CỤ NHANH",10));
        Button tiktok=button("TikTok");box.addView(tiktok);tiktok.setOnClickListener(v->launch("com.zhiliaoapp.musically"));
        Button discord=button("Discord");box.addView(discord);discord.setOnClickListener(v->launch("com.discord"));
        Button youtube=button("YouTube");box.addView(youtube);youtube.setOnClickListener(v->launch("com.google.android.youtube"));
        Button ff=button("Free Fire");box.addView(ff);ff.setOnClickListener(v->launch("com.dts.freefireth"));
        Button ffm=button("Free Fire MAX");box.addView(ffm);ffm.setOnClickListener(v->launch("com.dts.freefiremax"));
        Button close=button("ĐÓNG BẢNG");box.addView(close);close.setOnClickListener(v->toggle());
        WindowManager.LayoutParams p=params(dp(210),WindowManager.LayoutParams.WRAP_CONTENT);p.gravity=Gravity.TOP|Gravity.START;p.x=dp(72);p.y=dp(190);panel=box;wm.addView(panel,p);
    }
    @Override public int onStartCommand(Intent i,int flags,int id){return START_STICKY;}
    @Override public void onDestroy(){try{if(panel!=null)wm.removeView(panel);if(bubble!=null)wm.removeView(bubble);}catch(Exception ignored){}super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}
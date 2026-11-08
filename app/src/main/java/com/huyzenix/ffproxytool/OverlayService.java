package com.huyzenix.ffproxytool;

import android.app.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OverlayService extends Service {
    private WindowManager wm; private View bubble, panel; private boolean expanded = false;
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density); }
    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("ff_panel", "Floating panel", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
            Notification n = new Notification.Builder(this, "ff_panel").setContentTitle("FF Proxy Tool đang chạy").setContentText("Menu nổi Touch đang hoạt động").setSmallIcon(android.R.drawable.ic_menu_info_details).build();
            if (Build.VERSION.SDK_INT >= 34) startForeground(7, n, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE); else startForeground(7, n);
        }
        wm = (WindowManager)getSystemService(WINDOW_SERVICE); showBubble();
    }
    private WindowManager.LayoutParams params(int w, int h) {
        int type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        return new WindowManager.LayoutParams(w, h, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
    }
    private TextView label(String s, int size) { TextView t = new TextView(this); t.setText(s); t.setTextColor(0xFFFFFFFF); t.setTextSize(size); t.setPadding(dp(10),dp(8),dp(10),dp(8)); return t; }
    private Button btn(String s) { Button b = new Button(this); b.setText(s); b.setTextSize(12); b.setAllCaps(false); return b; }
    private void showBubble() {
        bubble = label("FF", 16); bubble.setGravity(Gravity.CENTER); bubble.setBackgroundColor(0xFFB71C5C);
        WindowManager.LayoutParams p = params(dp(54), dp(54)); p.gravity = Gravity.TOP|Gravity.START; p.x = dp(12); p.y = dp(180);
        bubble.setOnTouchListener(new View.OnTouchListener() { float sx, sy; int ox, oy; boolean moved;
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction()==MotionEvent.ACTION_DOWN) { sx=e.getRawX(); sy=e.getRawY(); ox=p.x; oy=p.y; moved=false; return true; }
                if (e.getAction()==MotionEvent.ACTION_MOVE) { int dx=(int)(e.getRawX()-sx), dy=(int)(e.getRawY()-sy); if (Math.abs(dx)+Math.abs(dy)>dp(5)) moved=true; p.x=ox+dx; p.y=oy+dy; wm.updateViewLayout(bubble,p); return true; }
                if (e.getAction()==MotionEvent.ACTION_UP) { if (!moved) togglePanel(); return true; } return true;
            }
        }); wm.addView(bubble,p);
    }
    private void togglePanel() { if (expanded) { if(panel!=null) wm.removeView(panel); panel=null; expanded=false; } else { showPanel(); expanded=true; } }
    private void showPanel() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(10),dp(8),dp(10),dp(8)); box.setBackgroundColor(0xEE171B28);
        box.addView(label("FF PROXY TOOL  •  TOUCH MENU", 14));
        TextView status=label("Trạng thái: menu đang bật",12); box.addView(status);
        Button check=btn("KIỂM TRA KẾT NỐI"); box.addView(check); check.setOnClickListener(v -> { status.setText("Đang kiểm tra HTTPS..."); new Thread(() -> { long start=System.currentTimeMillis(); String result; HttpURLConnection c=null; try { c=(HttpURLConnection)new URL("https://www.cloudflare.com/cdn-cgi/trace").openConnection(); c.setConnectTimeout(5000); c.setReadTimeout(5000); c.getInputStream().close(); result="Kết nối HTTPS: OK • "+(System.currentTimeMillis()-start)+" ms"; } catch(Exception ex) { result="Kết nối thất bại: "+ex.getClass().getSimpleName(); } finally { if(c!=null)c.disconnect(); } final String out=result; new Handler(Looper.getMainLooper()).post(() -> status.setText(out)); }).start(); });
        Button close=btn("ĐÓNG BẢNG"); box.addView(close); close.setOnClickListener(v -> togglePanel());
        WindowManager.LayoutParams p=params(dp(250),WindowManager.LayoutParams.WRAP_CONTENT); p.gravity=Gravity.TOP|Gravity.START; p.x=dp(75); p.y=dp(180); panel=box; wm.addView(panel,p);
    }
    @Override public int onStartCommand(Intent i,int flags,int id) { return START_STICKY; }
    @Override public void onDestroy() { try { if(panel!=null)wm.removeView(panel); if(bubble!=null)wm.removeView(bubble); } catch(Exception ignored){} super.onDestroy(); }
    @Override public IBinder onBind(Intent i) { return null; }
}
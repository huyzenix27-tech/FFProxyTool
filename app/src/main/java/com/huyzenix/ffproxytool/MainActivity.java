package com.huyzenix.ffproxytool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density); }
    private TextView text(String value, int size) {
        TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(0xFFFFFFFF); v.setPadding(dp(8), dp(10), dp(8), dp(10)); return v;
    }
    private Button button(String value) {
        Button b = new Button(this); b.setText(value); b.setAllCaps(false); return b;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20), dp(24), dp(20), dp(20)); root.setBackgroundColor(0xFF10131B);
        TextView title = text("FF PROXY TOOL", 24); title.setGravity(Gravity.CENTER); root.addView(title);
        TextView desc = text("Bảng điều khiển nổi và kiểm tra kết nối mạng. Công cụ không sửa đổi dữ liệu trò chơi.", 14); root.addView(desc);
        Button overlay = button("BẬT MENU TOUCH NỔI"); root.addView(overlay);
        overlay.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())); startActivity(i); return;
            }
            Intent service = new Intent(this, OverlayService.class);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(service); else startService(service);
        });
        Button regular = button("MỞ FREE FIRE THƯỜNG"); root.addView(regular); regular.setOnClickListener(v -> launch("com.dts.freefireth"));
        Button max = button("MỞ FREE FIRE MAX"); root.addView(max); max.setOnClickListener(v -> launch("com.dts.freefiremax"));
        Button stop = button("TẮT MENU NỔI"); root.addView(stop); stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class)));
        TextView note = text("Nếu nút mở game báo chưa cài đặt, phiên bản đó chưa có trên điện thoại.", 12); root.addView(note);
        setContentView(root);
    }
    private void launch(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) startActivity(i); else android.widget.Toast.makeText(this, "Không tìm thấy game này trên máy.", android.widget.Toast.LENGTH_SHORT).show();
    }
}
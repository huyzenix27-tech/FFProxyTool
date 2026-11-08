package com.huyzenix.ffproxytool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private final int BG=0xFF101018, CARD=0xFF1C1C29, PINK=0xFFFF3978, MUTED=0xFFB8B8C8;
    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density); }
    private TextView txt(String s,int size,int color){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setPadding(dp(2),dp(5),dp(2),dp(5)); return t; }
    private LinearLayout column(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private GradientDrawable bg(int color,int radius){ GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private Button action(String title){ Button b=new Button(this); b.setText(title); b.setTextColor(Color.WHITE); b.setTextSize(13); b.setAllCaps(false); b.setBackground(bg(PINK,16)); return b; }
    private void card(LinearLayout root, View child){ LinearLayout wrap=column(); wrap.setPadding(dp(14),dp(10),dp(14),dp(10)); wrap.setBackground(bg(CARD,18)); wrap.addView(child); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.bottomMargin=dp(12); root.addView(wrap,p); }
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(BG);
        LinearLayout root=column(); root.setPadding(dp(18),dp(22),dp(18),dp(24)); scroll.addView(root);
        LinearLayout header=row();
        TextView logo=txt("⚡",30,Color.WHITE); logo.setGravity(Gravity.CENTER); logo.setBackground(bg(PINK,18)); header.addView(logo,new LinearLayout.LayoutParams(dp(54),dp(54)));
        LinearLayout names=column(); names.setPadding(dp(12),0,0,0); names.addView(txt("GAME TURBO",22,Color.WHITE)); names.addView(txt("Gaming control center • v2.0",12,MUTED)); header.addView(names);
        root.addView(header);
        TextView sub=txt("Tăng tiện ích khi chơi game trên HyperOS",14,MUTED); sub.setPadding(0,dp(16),0,dp(14)); root.addView(sub);
        LinearLayout game=column(); game.addView(txt("TRUNG TÂM TRÒ CHƠI",12,PINK)); game.addView(txt("Mở nhanh trò chơi",18,Color.WHITE));
        LinearLayout games=row(); Button ff=action("FREE FIRE"); Button ffm=action("FF MAX"); LinearLayout.LayoutParams half=new LinearLayout.LayoutParams(0,dp(50),1); half.rightMargin=dp(6); games.addView(ff,half); LinearLayout.LayoutParams half2=new LinearLayout.LayoutParams(0,dp(50),1); half2.leftMargin=dp(6); games.addView(ffm,half2); game.addView(games); card(root,game);
        ff.setOnClickListener(v->launch("com.dts.freefireth")); ffm.setOnClickListener(v->launch("com.dts.freefiremax"));
        LinearLayout tools=column(); tools.addView(txt("THANH CÔNG CỤ NỔI",12,PINK)); tools.addView(txt("Dùng ứng dụng khác khi chơi",18,Color.WHITE)); tools.addView(txt("Bật quyền hiển thị trên ứng dụng khác để dùng nút nổi.",12,MUTED));
        Button enable=action("BẬT MENU NỔI"); tools.addView(enable); card(root,tools);
        enable.setOnClickListener(v->{ if(!Settings.canDrawOverlays(this)){ startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))); return; } startPanel(); });
        LinearLayout apps=column(); apps.addView(txt("LỐI TẮT ỨNG DỤNG",12,PINK)); apps.addView(txt("Mở nhanh trong lúc chơi",18,Color.WHITE));
        LinearLayout appRow=row();
        String[][] items={{"TikTok","com.zhiliaoapp.musically"},{"Discord","com.discord"},{"YouTube","com.google.android.youtube"}};
        for(String[] item:items){ Button b=action(item[0]); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(48),1); p.setMargins(dp(3),dp(5),dp(3),dp(5)); appRow.addView(b,p); b.setOnClickListener(v->launch(item[1])); }
        apps.addView(appRow); apps.addView(txt("Muốn cửa sổ TikTok/Discord nổi: mở ứng dụng, vào màn hình đa nhiệm và chọn Cửa sổ nổi nếu HyperOS hỗ trợ.",11,MUTED)); card(root,apps);
        LinearLayout setting=column(); setting.addView(txt("TIỆN ÍCH",12,PINK)); setting.addView(txt("• Bảng nổi có thể kéo thả\n• Phím tắt Free Fire / FF MAX\n• Mở TikTok, Discord, YouTube\n• Chế độ không làm phiền có thể bật từ Trung tâm điều khiển",13,Color.WHITE)); card(root,setting);
        root.addView(txt("Giao diện lấy cảm hứng từ Game Turbo Xiaomi; không phải thành phần hệ thống Xiaomi.",11,MUTED));
        setContentView(scroll);
    }
    private void startPanel(){ Intent i=new Intent(this,OverlayService.class); if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i); Toast.makeText(this,"Menu nổi đã được bật",Toast.LENGTH_SHORT).show(); }
    private void launch(String pkg){ Intent i=getPackageManager().getLaunchIntentForPackage(pkg); if(i!=null) startActivity(i); else Toast.makeText(this,"Ứng dụng chưa được cài đặt",Toast.LENGTH_SHORT).show(); }
}
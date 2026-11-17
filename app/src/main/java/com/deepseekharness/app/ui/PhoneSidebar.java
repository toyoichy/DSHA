package com.deepseekharness.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepseekharness.app.R;
import com.deepseekharness.app.util.UiText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Compact-phone navigation drawer. All navigation remains delegated to MainActivity's
 * existing BottomNavigationView listener so gates, terminal actions and page state stay intact. */
public final class PhoneSidebar {
  private PhoneSidebar() {}

  public static void show(Context context, BottomNavigationView navigation) {
    if (context == null || navigation == null) return;
    Dialog dialog = new Dialog(context);
    LinearLayout panel = new LinearLayout(context);
    panel.setOrientation(LinearLayout.VERTICAL);
    panel.setPadding(dp(context, 16), dp(context, 22), dp(context, 16), dp(context, 16));
    panel.setBackgroundColor(context.getColor(R.color.shell_bg));

    TextView brand = new TextView(context);
    brand.setText("DSH  /  LOCAL FIRST");
    brand.setTextColor(context.getColor(R.color.shell_text));
    brand.setTextSize(17);
    brand.setLetterSpacing(0.08f);
    brand.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
    brand.setGravity(Gravity.CENTER_VERTICAL);
    brand.setPadding(dp(context, 12), 0, 0, 0);
    panel.addView(brand, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, dp(context, 64)));

    View separator = new View(context);
    separator.setBackgroundColor(context.getColor(R.color.shell_line));
    LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, dp(context, 1));
    separatorParams.bottomMargin = dp(context, 18);
    panel.addView(separator, separatorParams);

    int[] ids = {R.id.nav_launch, R.id.nav_plugins, R.id.nav_settings, R.id.nav_terminal};
    int[] icons = {R.drawable.ic_launch, R.drawable.ic_plugins,
        R.drawable.ic_settings, R.drawable.ic_terminal};
    String[] labels = {UiText.choose("启动", "Launch"), UiText.choose("插件", "Plugins"),
        UiText.choose("设置", "Settings"), UiText.choose("终端", "Terminal")};

    for (int i = 0; i < ids.length; i++) {
      final int itemId = ids[i];
      boolean selected = navigation.getSelectedItemId() == itemId;
      LinearLayout item = new LinearLayout(context);
      item.setOrientation(LinearLayout.HORIZONTAL);
      item.setGravity(Gravity.CENTER_VERTICAL);
      item.setPadding(dp(context, 16), 0, dp(context, 12), 0);
      item.setMinimumHeight(dp(context, 56));
      item.setClickable(true);
      item.setFocusable(true);
      item.setBackgroundResource(R.drawable.bg_shell_rail_item);
      item.setSelected(selected);
      ImageView icon = new ImageView(context);
      icon.setImageResource(icons[i]);
      icon.setColorFilter(context.getColor(selected
          ? R.color.shell_accent : R.color.shell_text_secondary));
      item.addView(icon, new LinearLayout.LayoutParams(dp(context, 22), dp(context, 22)));
      TextView label = new TextView(context);
      label.setText(labels[i]);
      label.setTextSize(15);
      label.setTextColor(context.getColor(selected
          ? R.color.shell_text : R.color.shell_text_secondary));
      label.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
      LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
          0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
      labelParams.leftMargin = dp(context, 18);
      item.addView(label, labelParams);
      item.setContentDescription(labels[i]);
      item.setOnClickListener(v -> {
        dialog.dismiss();
        navigation.setSelectedItemId(itemId);
      });
      LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT, dp(context, 56));
      itemParams.bottomMargin = dp(context, 5);
      panel.addView(item, itemParams);
    }

    dialog.setContentView(panel);
    Window window = dialog.getWindow();
    if (window != null) {
      window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
      WindowManager.LayoutParams params = window.getAttributes();
      params.width = dp(context, 300);
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.gravity = Gravity.START;
      params.dimAmount = 0.42f;
      window.setAttributes(params);
    }
    dialog.show();
    if (window != null) {
      window.setLayout(dp(context, 300), WindowManager.LayoutParams.MATCH_PARENT);
      window.setGravity(Gravity.START);
    }
  }

  private static int dp(Context context, int value) {
    return Math.round(value * context.getResources().getDisplayMetrics().density);
  }
}

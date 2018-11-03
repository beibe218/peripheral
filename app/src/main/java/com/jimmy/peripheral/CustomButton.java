package com.jimmy.peripheral;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 类描述：
 * 创建人：jimmy.yang
 * 创建时间：2018-10-18 15:33
 * Email: jimmy.yang@keimai.cn
 * 修改备注：
 */

public class CustomButton extends LinearLayout {

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_button, this);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        ImageView icon_iv = view.findViewById(R.id.custom_btn_icon_iv);
        TextView text_tv = view.findViewById(R.id.custom_btn_text_tv);

        Drawable icon = a.getDrawable(R.styleable.CustomButton_btn_icon);
        icon_iv.setImageDrawable(icon);

        String text = a.getString(R.styleable.CustomButton_btn_text);
        text_tv.setText(text);

        int size = a.getDimensionPixelSize(R.styleable.CustomButton_btn_size, 50);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, size);
        view.setLayoutParams(params);
        a.recycle();
    }

}

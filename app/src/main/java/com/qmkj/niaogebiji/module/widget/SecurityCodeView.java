package com.qmkj.niaogebiji.module.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:验证码输入框
 */
public class SecurityCodeView extends RelativeLayout {
        private EditText editText;
        private TextView[] TextViews;
        private StringBuffer stringBuffer = new StringBuffer();
        private int count = 4;
        private String inputContent;
        private Typeface typeface;

        public SecurityCodeView(Context context) {
            this(context, null);
        }

        public SecurityCodeView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SecurityCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            TextViews = new TextView[4];
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/DIN-Medium.otf");
            View.inflate(context, R.layout.view_security_code, this);

            editText = findViewById(R.id.et);
            TextViews[0] = findViewById(R.id.item_code_iv1);
            TextViews[1] = findViewById(R.id.item_code_iv2);
            TextViews[2] = findViewById(R.id.item_code_iv3);
            TextViews[3] = findViewById(R.id.item_code_iv4);
            //将光标隐藏
            editText.setCursorVisible(false);
            setListener();
        }

        private void setListener() {
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //如果字符不为""时才进行操作
                    if (!editable.toString().equals("")) {
                        if (stringBuffer.length() > 3) {
                            //当文本长度大于3位时editText置空
                            editText.setText("");
                            return;
                        } else {
                            //将文字添加到StringBuffer中
                            stringBuffer.append(editable);
                            //添加后将EditText置空
                            editText.setText("");
                            count = stringBuffer.length();
                            inputContent = stringBuffer.toString();
                            if (stringBuffer.length() == 4) {
                                //文字长度位4  则调用完成输入的监听
                                if (inputCompleteListener != null) {
                                    inputCompleteListener.inputComplete();
                                }
                            }
                        }

                        for (int i = 0; i < stringBuffer.length(); i++) {
                            TextViews[i].setTypeface(typeface);
                            TextViews[i].setText(String.valueOf(inputContent.charAt(i)));
                            //TODO 2019.11.19 按照设计图，不要四周边框，只有底部边框，注释掉
//                            TextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_blue);
                        }

                    }
                }
            });

            //输入键盘中的删除键
            editText.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onKeyDelete()) {
                        return true;
                    }
                    return true;
                }
                return false;
            });
        }


        public boolean onKeyDelete() {
            if (count == 0) {
                count = 4;
                return true;
            }
            if (stringBuffer.length() > 0) {
                //删除相应位置的字符
                stringBuffer.delete((count - 1), count);
                count--;
                inputContent = stringBuffer.toString();
                TextViews[stringBuffer.length()].setText("");
//                TextViews[stringBuffer.length()].setBackgroundResource(R.drawable.bg_user_verify_code_grey);
                if (inputCompleteListener != null){
                     //有删除就通知manger
                    inputCompleteListener.deleteContent(true);
                }

            }
            return false;
        }

        /**
         * 清空输入内容
         */
        public void clearEditText() {
            stringBuffer.delete(0, stringBuffer.length());
            inputContent = stringBuffer.toString();
            for (int i = 0; i < TextViews.length; i++) {
                TextViews[i].setText("");
//                TextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_grey);
            }
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            return super.onKeyDown(keyCode, event);
        }

        private InputCompleteListener inputCompleteListener;

        public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
            this.inputCompleteListener = inputCompleteListener;
        }

        public interface InputCompleteListener {
            void inputComplete();

            void deleteContent(boolean isDelete);
        }

        /**
         * 获取输入文本
         *
         * @return
         */
        public String getEditContent() {
            return inputContent;
        }

    }


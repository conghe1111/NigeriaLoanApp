package com.chocolate.nigerialoanapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.chocolate.nigerialoanapp.R;


public class EmailAutoCompleteTextView extends AppCompatAutoCompleteTextView {

        private static final String TAG = "EmailAutoCompleteTextView";

        private String[] emailSufixs = new String[]{
                "@gmail.com",
                "@hotmail.com",
                "@yahoo.com",
                "@icloud.com",
                "@outlook.com",};


        public EmailAutoCompleteTextView(Context context) {
            super(context);
            init(context);
        }

        public EmailAutoCompleteTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public EmailAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        public void setAdapterString(String[] es) {
            if (es != null && es.length > 0)
                this.emailSufixs = es;
        }


        private void init(final Context context) {
            //adapter中使用默认的emailSufixs中的数据，可以通过setAdapterString来更改
            this.setAdapter(new EmailAutoCompleteAdapter(context, R.layout.register_auto_complete_item, emailSufixs));
            //使得在输入1个字符之后便开启自动完成
            this.setThreshold(1);
            this.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        String text = EmailAutoCompleteTextView.this.getText().toString();
                        //当该文本域重新获得焦点后，重启自动完成
                        if (!"".equals(text))
                            performFiltering(text, 0);
                    } else {
                        //当文本域丢失焦点后，检查输入email地址的格式
                        EmailAutoCompleteTextView ev = (EmailAutoCompleteTextView) v;
                        String text = ev.getText().toString();
                        //这里正则写的有点粗暴:)
                        if (text != null && text.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$")) {

                        } else {
                            Toast toast = Toast.makeText(context, "not a correct email.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });
        }

        @Override
        protected void replaceText(CharSequence text) {
            //当我们在下拉框中选择一项时，android会默认使用AutoCompleteTextView中Adapter里的文本来填充文本域
            //因为这里Adapter中只是存了常用email的后缀
            //因此要重新replace逻辑，将用户输入的部分与后缀合并
            Log.e("replaceText" , text.toString());
            String t = this.getText().toString();
            int index = t.indexOf("@");
            if (index != -1)
                t = t.substring(0, index);
            super.replaceText(t + text);
        }


        @Override
        protected void performFiltering(CharSequence text, int keyCode) {
            //该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，若它匹配
            //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
//        Log.i("performFiltering" + " performFiltering", text.toString() + "   " + keyCode);
            String t = text.toString();
            //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
            //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
            int index = t.indexOf("@");
            if (index == -1) {
                if (t.matches("^[a-zA-Z0-9_]+$")) {
                    super.performFiltering("@", keyCode);
                } else
                    this.dismissDropDown();//当用户中途输入非法字符时，关闭下拉提示框
            } else {
                super.performFiltering(t.substring(index), keyCode);
            }
        }

        private class EmailAutoCompleteAdapter extends ArrayAdapter<String> {
            public EmailAutoCompleteAdapter(Context context, int textViewResourceId, String[] email_s) {
                super(context, textViewResourceId, email_s);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.i("getView", "in GetView");
                View v = convertView;
                if (v == null)
                    v = LayoutInflater.from(getContext()).inflate(
                            R.layout.register_auto_complete_item, null);
                TextView tv = (TextView) v.findViewById(R.id.tv);
                String t = EmailAutoCompleteTextView.this.getText().toString();
                int index = t.indexOf("@");
                if (index != -1)
                    t = t.substring(0, index);
                //将用户输入的文本与adapter中的email后缀拼接后，在下拉框中显示
                tv.setText(t + getItem(position));
                Log.i("getView", tv.getText().toString());
                return v;
            }

        }
}

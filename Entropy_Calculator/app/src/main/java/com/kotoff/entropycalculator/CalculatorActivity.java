package com.kotoff.entropycalculator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.StreamHandler;

public class CalculatorActivity extends Activity {

    private List<View> elements;
    private Button adder_but;
    private LinearLayout calcute_h;
    private EditText new_elem;
    private EditText log_value;
    private TextView count_elem;
    private TextView sum_elem;
    private TextView answer;
    private TextView need_more;
    private int counter = 0;
    private double totalProbability = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        elements = new ArrayList<>();

        count_elem = (TextView) findViewById(R.id.countProbs);
        sum_elem   = (TextView) findViewById(R.id.sumProbs);
        need_more  = (TextView) findViewById(R.id.needMore);
        answer     = (TextView) findViewById(R.id.answer);

        new_elem  = (EditText) findViewById(R.id.prob);
        log_value = (EditText) findViewById(R.id.log_value);

        adder_but = (Button) findViewById(R.id.adder_button);
        calcute_h = (LinearLayout) findViewById(R.id.calc_block);
        final LinearLayout container = (LinearLayout) findViewById(R.id.expression);

        adder_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(adder_but.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                double probability = getProbability();
                if (probability > 0.000001) {
                    counter++;

                    final View view = getLayoutInflater().inflate(R.layout.edittext_layout, null);
                    final TextView text = (TextView) view.findViewById(R.id.logEdit);
                    TextView name = (TextView) view.findViewById(R.id.name_probability);
                    name.setText("p" + counter + " = ");
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setText("" + probability);
                    Button deleteField = (Button) view.findViewById(R.id.del_button);
                    deleteField.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                ((LinearLayout) view.getParent()).removeView(view);
                                elements.remove(view);
                                calcute_h.setVisibility(View.INVISIBLE);
                                answer.setText("");
                                totalProbability -= Double.parseDouble(text.getText().toString());
                                if (elements.size() == 0)
                                    totalProbability = 0.0;
                                sum_elem.setText("∑pi = " + totalProbability);
                                sum_elem.setTextColor(Color.parseColor("#E9967A"));
                                need_more.setText("еще " + (1.0 - totalProbability));

                                count_elem.setText("Кол-во pi: " + elements.size());
                            } catch (Exception ex) {
                            }
                        }
                    });

                    elements.add(view);
                    count_elem.setText("Кол-во pi: " + elements.size());
                    container.addView(view);
                    if (totalProbability > 0.999999) {
                        sum_elem.setTextColor(Color.parseColor("#98FB98"));
                        calcute_h.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    Double getProbability() {
        Double probability = 0.0;
        try {
            probability = Double.parseDouble(new_elem.getText().toString());
        } catch (Exception ex) {
            String[] nums = new_elem.getText().toString().split("/");
            try {
                probability = Double.parseDouble(nums[0]) / Double.parseDouble(nums[1]);
            } catch (Exception exec) {
                probability = 0.0;
            }
        }

        if (probability == 0) {
            Toast.makeText(this, "Потерян смысл - нулевая вероятность", Toast.LENGTH_SHORT).show();
        } else {
            if (probability > 1 || probability < 0) {
                Toast.makeText(this, "Недопустимое значение вероятности", Toast.LENGTH_SHORT).show();
                probability = 0.0;
            } else if (probability + totalProbability > 1.0000001) {
                Toast.makeText(this, "Невозможно добавить вероятность - общая сумма > 1", Toast.LENGTH_SHORT).show();
                probability = 0.0;
            } else {
                totalProbability += probability;
                sum_elem.setText("∑pi = " + totalProbability);
                need_more.setText("еще " + (1.0 - totalProbability));
            }
        }

        return probability;
    }

    public void calcuteEntropy(View v){
        double ans = getAnswer();
        answer.setText("H = " + ans);
    }

    private double getAnswer(){
        double log_base = Double.parseDouble(log_value.getText().toString());
        double answer = 0.0;
        for (View v: elements){
            double curr_val = Double.parseDouble(((TextView)v.findViewById(R.id.logEdit)).getText().toString());
            answer = answer - curr_val * log (log_base, curr_val);
        }
        return answer;
    }

    private double log(double base, double value){
        double answer = Math.log10(value)/Math.log10(base);
        return answer;
    }
}
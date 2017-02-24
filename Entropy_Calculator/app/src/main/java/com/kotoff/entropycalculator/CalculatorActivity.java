package com.kotoff.entropycalculator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
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

public class CalculatorActivity extends Activity {

    private List<View> elements;
    private Button adder_but;
    private EditText new_elem;
    private TextView count_elem;
    private int counter = 0;
    private double totalProbability = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        elements = new ArrayList<>();
        count_elem = (TextView) findViewById(R.id.numberAdd);
        new_elem = (EditText) findViewById(R.id.prob);
        adder_but = (Button) findViewById(R.id.adder_button);
        final LinearLayout container = (LinearLayout) findViewById(R.id.expression);

        adder_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(adder_but.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                double probability = getProbability();
                if (probability > 0) {
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
                                totalProbability -= Double.parseDouble(text.getText().toString());
                                count_elem.setText("" + elements.size());
                            } catch (Exception ex) {
                            }
                        }
                    });

                    elements.add(view);
                    count_elem.setText("" + elements.size());
                    container.addView(view);
                }
            }
        });
    }

    Double getProbability(){
        Double probability = 0.0;
        try {
            probability = Double.parseDouble(new_elem.getText().toString());
        } catch (Exception ex) {
            String[] nums = new_elem.getText().toString().split("/");
            try{
                probability = Double.parseDouble(nums[0]) / Double.parseDouble(nums[1]);
            }catch (Exception exec){
                probability = 0.0;
            }
        }

        if (probability == 0)
        {
            Toast.makeText(this, "Потерян смысл - нулевая вероятность", Toast.LENGTH_SHORT).show();
        } else {
            if (probability > 1){
                Toast.makeText(this, "Недопустимое значение вероятности", Toast.LENGTH_SHORT).show();
                probability = 0.0;
            }else
            if (probability + totalProbability > 1.0001){
                Toast.makeText(this, "Невозможно добавить вероятность - общая сумма > 1", Toast.LENGTH_SHORT).show();
                probability = 0.0;
            } else {
                totalProbability += probability;
            }
        }

        return probability;
    }
}
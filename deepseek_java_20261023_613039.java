package com.example.tripleapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView txtResult, txtHistory;
    private String expr = "";
    private double num1 = 0;
    private String op = "";
    private boolean newNum = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        txtResult = findViewById(R.id.txtResult);
        txtHistory = findViewById(R.id.txtHistory);

        int[] digits = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int id : digits) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> appendDigit(b.getText().toString()));
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (!expr.contains(".")) { expr += "."; txtResult.setText(expr); }
        });
        findViewById(R.id.btnAdd).setOnClickListener(v -> setOp("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> setOp("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> setOp("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> setOp("/"));
        findViewById(R.id.btnEq).setOnClickListener(v -> calc());
        findViewById(R.id.btnC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnBack).setOnClickListener(v -> backspace());
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            if (!expr.isEmpty()) {
                try { expr = String.valueOf(Double.parseDouble(expr) / 100);
                    txtResult.setText(expr); } catch (Exception ignored) {}
            }
        });
        findViewById(R.id.btnSign).setOnClickListener(v -> {
            if (!expr.isEmpty() && !expr.equals("0")) {
                if (expr.startsWith("-")) expr = expr.substring(1);
                else expr = "-" + expr;
                txtResult.setText(expr);
            }
        });
        findViewById(R.id.btnBackCalc).setOnClickListener(v -> finish());
    }

    private void appendDigit(String d) {
        if (newNum) { expr = d; newNum = false; }
        else if (expr.equals("0")) expr = d;
        else expr += d;
        txtResult.setText(expr);
    }

    private void setOp(String o) {
        if (!expr.isEmpty()) {
            num1 = Double.parseDouble(expr);
            op = o;
            newNum = true;
            txtHistory.setText(num1 + " " + op);
        }
    }

    private void calc() {
        if (op.isEmpty() || expr.isEmpty()) return;
        double n2 = Double.parseDouble(expr);
        double r = 0;
        switch (op) {
            case "+": r = num1 + n2; break;
            case "-": r = num1 - n2; break;
            case "*": r = num1 * n2; break;
            case "/":
                if (n2 == 0) { txtResult.setText("Error"); reset(); return; }
                r = num1 / n2; break;
        }
        txtHistory.setText(num1 + " " + op + " " + n2 + " =");
        expr = String.valueOf(r);
        if (expr.endsWith(".0")) expr = expr.replace(".0", "");
        txtResult.setText(expr);
        op = "";
        newNum = true;
    }

    private void backspace() {
        if (expr.length() > 0) {
            expr = expr.substring(0, expr.length() - 1);
            txtResult.setText(expr.isEmpty() ? "0" : expr);
        }
    }

    private void clearAll() {
        expr = ""; num1 = 0; op = ""; newNum = true;
        txtResult.setText("0");
        txtHistory.setText("");
    }

    private void reset() {
        expr = ""; num1 = 0; op = ""; newNum = true;
    }
}
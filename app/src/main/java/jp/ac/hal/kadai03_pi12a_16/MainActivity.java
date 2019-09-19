package jp.ac.hal.kadai03_pi12a_16;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    TextView tvProcess, tvResult;
    String process;
    
    Button[] num;

    Button btnPlus, btnMinus, btnMult, btnDiv, btnDecimal, btnBack, btnClear, btnBracket, btnEqual;

    Boolean isBracketOpen;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isBracketOpen = false;

        tvProcess = findViewById(R.id.processArea);
        tvResult = findViewById(R.id.outputArea);

        btnClear = findViewById(R.id.clear);
        btnBack = findViewById(R.id.backspace);
        btnBracket = findViewById(R.id.bracket);
        btnEqual = findViewById(R.id.equal);

        btnPlus = findViewById(R.id.plus);
        btnMinus = findViewById(R.id.minus);
        btnMult = findViewById(R.id.multiplied);
        btnDiv = findViewById(R.id.divided);
        btnDecimal = findViewById(R.id.dot);



        // 数字ボタン
        num = new Button[10];
        num[0] = findViewById(R.id.zero);
        num[1] = findViewById(R.id.one);
        num[2] = findViewById(R.id.two);
        num[3] = findViewById(R.id.three);
        num[4] = findViewById(R.id.four);
        num[5] = findViewById(R.id.five);
        num[6] = findViewById(R.id.six);
        num[7] = findViewById(R.id.seven);
        num[8] = findViewById(R.id.eight);
        num[9] = findViewById(R.id.nine);

        for(i=0; i< num.length; i++) {
            num[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    numClicked(view, tvProcess);
                    //process = tvProcess.getText().toString();
                    //tvProcess.setText(process + String.valueOf(i));
                }
            });
        }



        // クリアボタン押すと結果表示部分を空にする
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvProcess.setText("");
                tvResult.setText("");
            }
        });
        // バックスペースボタン
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                // プロセス文字の長さがゼロじゃない時だけバックスペースボタン適用
                if (process.length() > 0) {
                    tvProcess.setText(process.substring(0, process.length()-1));
                }
            }
        });
        // 括弧ボタン
        btnBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                if(isBracketOpen) {
                    tvProcess.setText(process + ")");
                    isBracketOpen = false;
                }
                else {
                    tvProcess.setText(process + "(");
                    isBracketOpen = true;
                }
            }
        });



        // 符号のボタン
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                tvProcess.setText(process + "+");
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                tvProcess.setText(process + "-");
            }
        });
        btnMult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                tvProcess.setText(process + "×");
            }
        });
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                tvProcess.setText(process + "÷");
            }
        });
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                tvProcess.setText(process + ".");
            }
        });




        // 「＝」ボタン
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process = tvProcess.getText().toString();
                process = process.replaceAll("×", "*");
                process = process.replaceAll("÷", "/");

                // rhinoのevaluateStringメソッドを利用して連続計算
                RhinoAndroidHelper rhinoHelper = new RhinoAndroidHelper();
                Context context = rhinoHelper.enterContext();
                context.setOptimizationLevel(-1);
                Scriptable scope = new ImporterTopLevel(context);
                String result = "";
                try {
                    result = context.evaluateString(scope, process, "Javascript", 1, null).toString();
                    // 四捨五入、「,」入れ、無駄な「.0」をなくす処理
                    BigDecimal bd = new BigDecimal(result);
                    BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    result = bd2.toString();
                    if (result.substring(result.length()-3).equals(".00")) {
                        result = result.substring(0, result.length()-3);
                        NumberFormat nf = NumberFormat.getNumberInstance();
                        result = nf.format(Integer.valueOf(result));
                        //result = String.format("%,d", bd.intValue());
                    }
                    else if (result.charAt(result.length()-1) == '0') {
                        result = result.substring(0, result.length()-1);
                    }
                } catch (Exception e) {
                    result = "エラー";
                } finally {
                    context.exit();
                    tvResult.setText(result);
                }
            }
        });


    }

    private void numClicked(View v, TextView tv) {
        Button btn = (Button) v;
        String str = btn.getText().toString();
        String str2 = tv.getText().toString();

        tv.setText(str2+str);
    }
}

package com.example.administrator.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FlowLayout flowLayout;
    private String[] name = {"刘翔","勒布朗詹姆斯","姚明","傅园慧","孙阳",
            "科比布莱恩特","费德勒","汉密尔顿","马云","朱婷"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowLayout = (FlowLayout) findViewById(R.id.flowlayout);

    }
}

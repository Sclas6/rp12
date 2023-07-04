package com.example.rp12java;

import static android.os.SystemClock.sleep;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScatterChart chart = findViewById(R.id.chart);
        NumberPicker sizePicker = findViewById(R.id.numPickerSize);
        NumberPicker rangePicker = findViewById(R.id.numPickerRange);
        TextView sizeText = findViewById(R.id.textSize);
        TextView rangeText = findViewById(R.id.textRange);
        Button resetButton = findViewById(R.id.buttonReset);
        Button genArrayButton = findViewById(R.id.buttonGenArray);
        Button bubbleSortButton = findViewById(R.id.buttonSortBubble);

        sizePicker.setMaxValue(1000);
        sizePicker.setMinValue(1);
        sizePicker.setValue(20);
        rangePicker.setMaxValue(1000);
        rangePicker.setMinValue(0);
        rangePicker.setValue(100);
        sizeText.setText("　要素数　");
        rangeText.setText("データ範囲");
        resetButton.setText("リセット");
        resetButton.setEnabled(false);
        genArrayButton.setText("配列生成");
        bubbleSortButton.setText("バブルソート開始");

        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<Integer> data = genRandomArray(20, 100);
        //ScatterDataSet dataSets = new ScatterDataSet(array2Data(data),"test");
        chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "test")));
        chart.invalidate();

        resetButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(true);
            genArrayButton.setEnabled(true);
            new Thread(() -> updateView(data, chart)).start();
            resetButton.setEnabled(false);
        });

        bubbleSortButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(false);
            resetButton.setEnabled(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> {
                bubbleSort(data, chart);
                handler.post(() -> resetButton.setEnabled(true));
            }).start();
        });

        genArrayButton.setOnClickListener(v -> new Thread(() -> {
            data.clear();
            data.addAll(genRandomArray(sizePicker.getValue(), rangePicker.getValue()));
            chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "")));
            chart.getData().notifyDataChanged();
            chart.invalidate();
            handler.post(() -> bubbleSortButton.setEnabled(true));
        }).start());
    }

    private ArrayList<Integer> genRandomArray(int len, int max){
        Random rand = new Random();
        ArrayList<Integer> list = new ArrayList<>(len);
        for(int i = 0; i < len; i++){
            list.add(i, rand.nextInt(max + 1));
        }
        return list;
    }

    private ArrayList<Entry> array2Data(ArrayList<Integer> list){
        ArrayList<Entry> entryList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            Entry entry = new Entry(i, list.get(i));
            entryList.add(entry);
        }
        return entryList;
    }

    private void updateView(ArrayList<Integer> list, ScatterChart chart){
        ScatterDataSet dataSets = new ScatterDataSet(array2Data(list),"test");
        chart.clear();
        chart.setData(new ScatterData(dataSets));
        chart.getData().notifyDataChanged();
        chart.invalidate();
    }

    private ArrayList<Integer> copyArray(ArrayList<Integer> list, int len){
        ArrayList<Integer> copied = new ArrayList<>(len);
        for(int i = 0; i < len; i++){
            copied.add(i, list.get(i));
        }
        return copied;
    }

    private void bubbleSort(ArrayList<Integer> rawList, ScatterChart chart){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        //Log.d("START", Arrays.toString(list));
        for(int i = 0; i < list.size() - 1; i++){
            for(int j = list.size() - 1; j > i; j--){
                if(list.get(j - 1) > list.get(j)){
                    int temp = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, temp);
                    updateView(list, chart);
                    sleep(50);
                    //Log.d("SORT", Arrays.toString(list));
                }
            }
        }
    }

}
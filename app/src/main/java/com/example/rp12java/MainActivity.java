package com.example.rp12java;

import static android.os.SystemClock.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
    private int compareCount = 0;
    private int swapCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScatterChart chart = findViewById(R.id.chart);
        NumberPicker sizePicker = findViewById(R.id.numPickerSize);
        NumberPicker rangePicker = findViewById(R.id.numPickerRange);
        TextView sizeText = findViewById(R.id.textSize);
        TextView rangeText = findViewById(R.id.textRange);
        TextView compareCount = findViewById(R.id.textCompare);
        TextView swapCount = findViewById(R.id.textSwap);
        Button resetButton = findViewById(R.id.buttonReset);
        Button genArrayButton = findViewById(R.id.buttonGenArray);
        Button bubbleSortButton = findViewById(R.id.buttonSortBubble);
        Button selectionSortButton = findViewById(R.id.buttonSortSelection);
        Button insertionSortButton = findViewById(R.id.buttonSortInsertion);
        Button quickSortButton = findViewById(R.id.buttonSortQuick);

        sizePicker.setMaxValue(500);
        sizePicker.setMinValue(1);
        sizePicker.setValue(20);
        rangePicker.setMaxValue(1000);
        rangePicker.setMinValue(0);
        rangePicker.setValue(100);
        sizeText.setText("　要素数　");
        rangeText.setText("データ範囲");
        compareCount.setText("比較: 0");
        swapCount.setText("交換: 0");
        resetButton.setText("リセット");
        resetButton.setEnabled(false);
        genArrayButton.setText("配列生成");
        bubbleSortButton.setText("バブルソート開始");
        selectionSortButton.setText("選択ソート開始");
        insertionSortButton.setText("挿入ソート開始");
        quickSortButton.setText("クイックソート開始");

        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<Integer> data = genRandomArray(20, 100);
        //ScatterDataSet dataSets = new ScatterDataSet(array2Data(data),"test");
        chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "test")));
        chart.invalidate();

        resetButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(true);
            selectionSortButton.setEnabled(true);
            insertionSortButton.setEnabled(true);
            quickSortButton.setEnabled(true);
            genArrayButton.setEnabled(true);
            compareCount.setText("比較: 0");
            swapCount.setText("交換: 0");
            resetCount();
            new Thread(() -> updateView(data, chart)).start();
            resetButton.setEnabled(false);
        });

        genArrayButton.setOnClickListener(v -> new Thread(() -> {
            data.clear();
            data.addAll(genRandomArray(sizePicker.getValue(), rangePicker.getValue()));
            chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "")));
            chart.getData().notifyDataChanged();
            chart.invalidate();
            handler.post(() -> {
                bubbleSortButton.setEnabled(true);
                selectionSortButton.setEnabled(true);
                insertionSortButton.setEnabled(true);
                quickSortButton.setEnabled(true);
            });
        }).start());

        bubbleSortButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(false);
            selectionSortButton.setEnabled(false);
            insertionSortButton.setEnabled(false);
            quickSortButton.setEnabled(false);
            resetButton.setEnabled(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> {
                bubbleSort(data, chart, compareCount, swapCount);
                handler.post(() -> resetButton.setEnabled(true));
            }).start();
        });

        selectionSortButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(false);
            selectionSortButton.setEnabled(false);
            insertionSortButton.setEnabled(false);
            quickSortButton.setEnabled(false);
            resetButton.setEnabled(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> {
                selectionSort(data, chart, compareCount, swapCount);
                handler.post(() -> resetButton.setEnabled(true));
            }).start();
        });

        insertionSortButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(false);
            selectionSortButton.setEnabled(false);
            insertionSortButton.setEnabled(false);
            quickSortButton.setEnabled(false);
            resetButton.setEnabled(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> {
                insertionSort(data, chart, compareCount, swapCount);
                handler.post(() -> resetButton.setEnabled(true));
            }).start();
        });

        quickSortButton.setOnClickListener(v -> {
            bubbleSortButton.setEnabled(false);
            selectionSortButton.setEnabled(false);
            insertionSortButton.setEnabled(false);
            quickSortButton.setEnabled(false);
            resetButton.setEnabled(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> {
                quickSort(data, 0, data.size() - 1, chart, compareCount, swapCount);
                handler.post(() -> resetButton.setEnabled(true));
            }).start();
        });
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

    @SuppressLint("DefaultLocale")
    private void bubbleSort(ArrayList<Integer> rawList, ScatterChart chart, TextView c, TextView s){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 0; i < list.size() - 1; i++){
            for(int j = list.size() - 1; j > i; j--){
                if(compare(list, j - 1, j, c) > 0){
                    swap(list, j - 1, j, s);
                    updateView(list, chart);
                    sleep(50);
                }
            }
        }
    }
    @SuppressLint("DefaultLocale")
    private void selectionSort(ArrayList<Integer> rawList, ScatterChart chart, TextView c, TextView s){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 0; i < list.size() - 1; i++){
            int min = i;
            for(int j = i + 1; j < list.size(); j++){
                if(compare(list, min, j, c) > 0) min = j;
            }
            swap(list, min, i, s);
            updateView(list, chart);
            sleep(50);
        }
    }
    @SuppressLint("DefaultLocale")
    private void insertionSort(ArrayList<Integer> rawList, ScatterChart chart, TextView c, TextView s){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 1; i < list.size(); i++){
            int j = i;
            while(j > 0 && compare(list, j - 1, j, c) > 0){
                swap(list, j - 1, j, s);
                updateView(list, chart);
                j--;
                sleep(50);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private ArrayList<Integer> quickSort(ArrayList<Integer> rawList, int start, int end, ScatterChart chart, TextView c, TextView s){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        Log.d("r", rawList.toString());
        Log.d("l", list.toString());
        int pivot, left, right;
        left = start;
        right = end;
        pivot = left;
        while(true) {
            while (compare(list, left, pivot, c) < 0) left++;
            while (compare(list, pivot, right, c) < 0) right--;
            if (left >= right) break;
            swap(list, left, right, s);
            updateView(list, chart);
            sleep(50);
            if (pivot == left || pivot == right) pivot = (pivot == left) ? right : left;
            left++;
            right--;
        }
        if(start < left - 1) list = quickSort(list, start, left - 1, chart, c, s);
        if(right + 1 < end) list = quickSort(list, right + 1, end, chart, c, s);
        return list;
    }
    @SuppressLint("DefaultLocale")
    int compare(ArrayList<Integer> list, int i, int j, TextView cCounter){
        Handler handler = new Handler(Looper.getMainLooper());
        compareCount++;
        handler.post(() -> cCounter.setText(String.format("比較: %d", compareCount)));
        return list.get(i) - list.get(j);
    }

    @SuppressLint("DefaultLocale")
    void swap(ArrayList<Integer> list, int i, int j, TextView sCounter){
        Handler handler = new Handler(Looper.getMainLooper());
        swapCount++;
        handler.post(() -> sCounter.setText(String.format("交換: %d", swapCount)));
        int temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    void resetCount(){
        compareCount = 0;
        swapCount = 0;
    }
}
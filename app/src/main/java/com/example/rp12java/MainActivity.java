package com.example.rp12java;

import static android.os.SystemClock.sleep;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private boolean isSorting = false;
    private int compareCount = 0;
    private int swapCount = 0;
    private Button genArrayButton = null;
    private Button bubbleSortButton = null;
    private Button selectionSortButton = null;
    private Button insertionSortButton = null;
    private Button quickSortButton = null;
    private TextView compare = null;
    private TextView swap = null;

    private ImageButton stalinSortButton = null;
    private ImageButton monkeySortButton = null;

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
        compare = findViewById(R.id.textCompare);
        swap = findViewById(R.id.textSwap);
        genArrayButton = findViewById(R.id.buttonGenArray);
        bubbleSortButton = findViewById(R.id.buttonSortBubble);
        selectionSortButton = findViewById(R.id.buttonSortSelection);
        insertionSortButton = findViewById(R.id.buttonSortInsertion);
        quickSortButton = findViewById(R.id.buttonSortQuick);
        stalinSortButton = findViewById(R.id.buttonSortStalin);
        monkeySortButton = findViewById(R.id.buttonSortMonkey);

        sizePicker.setMaxValue(500);
        sizePicker.setMinValue(1);
        sizePicker.setValue(20);
        rangePicker.setMaxValue(1000);
        rangePicker.setMinValue(0);
        rangePicker.setValue(100);
        sizeText.setText("　要素数　");
        rangeText.setText("データ範囲");
        compare.setText("比較: 0");
        swap.setText("交換: 0");
        resetButton.setText("リセット");
        genArrayButton.setText("配列生成");
        bubbleSortButton.setText("バブルソート開始");
        selectionSortButton.setText("選択ソート開始");
        insertionSortButton.setText("挿入ソート開始");
        quickSortButton.setText("クイックソート開始");

        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<Integer> data = genRandomArray(20, 100);
        chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "test")));
        chart.invalidate();

        resetButton.setOnClickListener(v -> {
            if(isSorting) isSorting = false;
            sleep(50);
            setSortButton(true);
            genArrayButton.setEnabled(true);
            compare.setText("比較: 0");
            swap.setText("交換: 0");
            resetCount();
            new Thread(() -> updateView(data, chart)).start();
        });

        genArrayButton.setOnClickListener(v -> new Thread(() -> {
            data.clear();
            data.addAll(genRandomArray(sizePicker.getValue(), rangePicker.getValue()));
            chart.setData(new ScatterData(new ScatterDataSet(array2Data(data), "")));
            chart.getData().notifyDataChanged();
            chart.invalidate();
            handler.post(() -> setSortButton(true));
        }).start());

        bubbleSortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> bubbleSort(data, chart)).start();
        });

        selectionSortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> selectionSort(data, chart)).start();
        });

        insertionSortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> insertionSort(data, chart)).start();
        });

        quickSortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            isSorting = true;
            new Thread(() -> quickSort(data, 0, data.size() - 1, chart)).start();
        });

        stalinSortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> stalinSort(data, chart)).start();
        });

        monkeySortButton.setOnClickListener(v -> {
            setSortButton(false);
            genArrayButton.setEnabled(false);
            new Thread(() -> monkeySort(data, chart)).start();
        });
    }

    private void setSortButton(boolean b){
        bubbleSortButton.setEnabled(b);
        selectionSortButton.setEnabled(b);
        insertionSortButton.setEnabled(b);
        quickSortButton.setEnabled(b);
        stalinSortButton.setEnabled(b);
        monkeySortButton.setEnabled(b);
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
        isSorting = true;
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 0; i < list.size() - 1; i++){
            for(int j = list.size() - 1; j > i; j--){
                if(!isSorting) return;
                if(compare(list, j - 1, j) > 0){
                    swap(list, j - 1, j);
                    updateView(list, chart);
                    sleep(50);
                }
            }
        }
    }
    private void selectionSort(ArrayList<Integer> rawList, ScatterChart chart){
        isSorting = true;
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 0; i < list.size() - 1; i++){
            int min = i;
            for(int j = i + 1; j < list.size(); j++){
                if(!isSorting) return;
                if(compare(list, min, j) > 0) min = j;
            }
            swap(list, min, i);
            updateView(list, chart);
            sleep(50);
        }
    }
    private void insertionSort(ArrayList<Integer> rawList, ScatterChart chart){
        isSorting = true;
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        for(int i = 1; i < list.size(); i++){
            int j = i;
            while(j > 0 && compare(list, j - 1, j) > 0){
                if(!isSorting) return;
                swap(list, j - 1, j);
                updateView(list, chart);
                j--;
                sleep(50);
            }
        }
    }

    private void monkeySort(ArrayList<Integer> rawList, ScatterChart chart){
        isSorting = true;
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        boolean sorted = false;
        while(!sorted){
            Collections.shuffle(list);
            updateView(list, chart);
            for(int i = 0; i < list.size() - 1;i++){
                if(!isSorting) return;
                if(compare(list, i, i + 1) > 0) break;
                if(i == list.size() - 2) sorted = true;
            }
            sleep(50);
        }
    }
    private int middle(ArrayList<Integer> list, int i, int j, int k){
        int a = list.get(i);
        int b = list.get(j);
        int c = list.get(k);
        if (a >= b){
            if (b >= c) return j;
            else if (a <= c) return i;
            else return k;
        }
        else if (a > c) return i;
        else if (b > c) return k;
        else return j;
    }

    private ArrayList<Integer> quickSort(ArrayList<Integer> rawList, int start, int end, ScatterChart chart){
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        int pivot, left, right;
        left = start;
        right = end;
        pivot = middle(list, left, right / 2, right);
        while(true) {
            if(!isSorting) return list;
            while (compare(list, left, pivot) < 0) left++;
            while (compare(list, pivot, right) < 0) right--;
            if (left >= right) break;
            swap(list, left, right);
            updateView(list, chart);
            sleep(50);
            if (pivot == left || pivot == right) pivot = (pivot == left) ? right : left;
            left++;
            right--;
        }
        if(start < left - 1) list = quickSort(list, start, left - 1, chart);
        if(right + 1 < end) list = quickSort(list, right + 1, end, chart);
        return list;
    }

    private void stalinSort(ArrayList<Integer> rawList, ScatterChart chart){
        isSorting = true;
        ArrayList<Integer> list = copyArray(rawList, rawList.size());
        int i = 1;
        while(i < list.size()){
            if(!isSorting) return;
            if(compare(list, i - 1, i) >= 0){
                list.remove(i);
                updateView(list, chart);
            }else i++;
            sleep(50);
        }
    }
    @SuppressLint("DefaultLocale")
    int compare(ArrayList<Integer> list, int i, int j){
        Handler handler = new Handler(Looper.getMainLooper());
        compareCount++;
        handler.post(() -> compare.setText(String.format("比較: %d", compareCount)));
        return list.get(i) - list.get(j);
    }

    @SuppressLint("DefaultLocale")
    void swap(ArrayList<Integer> list, int i, int j){
        Handler handler = new Handler(Looper.getMainLooper());
        swapCount++;
        handler.post(() -> swap.setText(String.format("交換: %d", swapCount)));
        int temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    void resetCount(){
        compareCount = 0;
        swapCount = 0;
    }
}
package com.mierzejewski.inzynierka;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.mierzejewski.inzynierka.common.CommonFragment;
import com.mierzejewski.inzynierka.model.Currency;
import com.mierzejewski.inzynierka.model.CurrencyExchangeRate;
import com.mierzejewski.inzynierka.model.CurrencyExchangeRateData;
import com.mierzejewski.inzynierka.model.MainData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.Highlight;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by dom on 07/12/14.
 */
public class CurrencyExchangeFragment extends CommonFragment implements
        OnChartValueSelectedListener
{

    private static int[] colorArray = {MainApp.getAppContext().getResources().getColor(R.color.color8)};

    private HashMap<Integer, CurrencyExchangeRate> rates;
    private ValueFormatter valueFormater = new ValueFormatter()
    {
        @Override
        public String getFormattedValue(float value)
        {
            return String.format("%.2f", value)+Currency.getDefault().symbol;
        }
    };

    TimePeriod period = TimePeriod.YEAR;

    CurrencyExchangeRateData dao = new MainData().getExchangeRateData();
    Currency currency = Currency.USD;

    protected LineChart mChart;


    View rootView;
    TextView ratesList;
    private Spinner currencySpinner;
    private Spinner periodSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.currency_exchange_fragment, container, false);

        ratesList = (TextView) rootView.findViewById(R.id.rates_list);
        currencySpinner = (Spinner) rootView.findViewById(R.id.currencySpinner);
        periodSpinner = (Spinner) rootView.findViewById(R.id.periodSpinner);

        ArrayAdapter periodArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.currency_range));
        periodSpinner.setAdapter(periodArrayAdapter);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                period = TimePeriod.values()[position];
                setData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Currency.values());
        currencySpinner.setAdapter(spinnerArrayAdapter);
        currencySpinner.setSelection(1);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                currency = Currency.values()[position];
                setData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        mChart = (LineChart) rootView.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

//        mChart.setDrawBarShadow(true);
//        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
//        mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(0);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);


        setData();


        return rootView;
    }


    private void setData() {

        if(period == TimePeriod.YEAR)
        {
            String[] months = getResources().getStringArray(R.array.months_abbr);
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < 12; i++) {

                xVals.add(months[i % 12]);
            }

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            int year = Calendar.getInstance().get(Calendar.YEAR);

            rates = dao.getRatingsFromYearMonthly(year, currency, Currency.getDefault());
            if(rates == null)
                return;

            for (int i = 0; i < 12; i++) {

                float val = 0f;
                if(rates.get(i)==null)
                    continue;
                else
                    val = (float) rates.get(i).getRate();

                yVals1.add(new Entry(val, rates.get(i).getExchangeDate().getMonth()));
            }

            LineDataSet set1 = new LineDataSet(yVals1, Currency.getDefault()+" -> "+currency.name());
            set1.setDrawFilled(true);
            set1.setDrawCubic(true);
            set1.setFillAlpha(150);
            set1.setValueFormatter(valueFormater);
            set1.setCircleColorHole(getResources().getColor(R.color.color8));
            set1.setFillColor(getResources().getColor(R.color.color1));
            set1.setColors(colorArray);
            set1.setCircleColors(colorArray);


            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);


            LineData data = new LineData(xVals, dataSets);
            data.setValueTextSize(10f);
            data.setValueFormatter(valueFormater);
            mChart.clear();
            mChart.setData(data);
        }
        else if(period == TimePeriod.MONTH)
        {
            ArrayList<String> days= new ArrayList<String>();
            Calendar c = Calendar.getInstance();
            int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            for(int i = 1;i<=monthMaxDays;i++)
            {
                days.add(""+i);
            }

            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < monthMaxDays ; i++) {

                xVals.add(days.get(i));
            }

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();

            rates = dao.getRatingsFromLastMonth(currency, Currency.getDefault());

            for (int i = 1; i <= monthMaxDays; i++)
            {
                float val = 0f;
                if(rates.get(i)==null)
                {
                    //yVals1.add(new BarEntry(val, i));
                }
                else
                {
                    val = (float) rates.get(i).getRate();
                    yVals1.add(new BarEntry(val, i-1));
                }
            }

            LineDataSet set1 = new LineDataSet(yVals1, Currency.getDefault()+" -> "+currency.name());
            set1.setDrawFilled(true);
            set1.setDrawCubic(true);
            set1.setFillAlpha(150);
            set1.setValueFormatter(valueFormater);
            set1.setCircleColorHole(getResources().getColor(R.color.color8));
            set1.setFillColor(getResources().getColor(R.color.color1));
            set1.setColors(colorArray);
            set1.setCircleColors(colorArray);
            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);

            LineData data = new LineData(xVals, dataSets);
            data.setValueTextSize(7f);

            data.setValueFormatter(valueFormater);
            mChart.clear();
            mChart.setData(data);
        }
        else if(period == TimePeriod.WEEK)
        {
            String[] weekDays = getResources().getStringArray(R.array.weekday_abbr);
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < 7; i++) {

                xVals.add(weekDays[i % 7]);
            }

            ArrayList<Entry> yVals1 = new ArrayList<Entry>();

            rates = dao.getRatingsFromLastWeek(currency, Currency.getDefault());


            for (int i = 1; i <= 7; i++)
            {
                float val = 0f;
                if(rates.get(i) == null)
                    continue;
                else
                    val = (float) rates.get(i).getRate();


                yVals1.add(new Entry(val, i));
            }

            LineDataSet set1 = new LineDataSet(yVals1, Currency.getDefault()+" -> "+currency.name());
            set1.setDrawFilled(true);
            set1.setDrawCubic(true);
            set1.setFillAlpha(150);
            set1.setValueFormatter(valueFormater);
            set1.setCircleColorHole(getResources().getColor(R.color.color8));
            set1.setFillColor(getResources().getColor(R.color.color1));
            set1.setColors(colorArray);
            set1.setCircleColors(colorArray);

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1);

            LineData data = new LineData(xVals, dataSets);
            data.setValueTextSize(10f);
            mChart.clear();
            data.setValueFormatter(valueFormater);
            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;

        //RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, AxisDependency.LEFT);

        //Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View toastLayout = inflater.inflate(R.layout.info_toast_layout, null);
        TextView rate = (TextView) toastLayout.findViewById(R.id.rate_text);
        TextView date = (TextView) toastLayout.findViewById(R.id.date_text);

        int ratesIndex = h.getXIndex();
        if(period == TimePeriod.MONTH)
            ratesIndex++;

        if(rates.get(ratesIndex) != null)
        {

            DateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateText = simpleFormat.format(rates.get(ratesIndex).getExchangeDate());
            rate.setText("1 " + rates.get(ratesIndex).getFrom() + " = " + rates.get(ratesIndex).getRate() + rates.get(ratesIndex).getTo());
            date.setText("" + dateText);

            Toast infoToast = new Toast(getActivity());
            infoToast.setDuration(Toast.LENGTH_LONG);
            infoToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            infoToast.setView(toastLayout);

            infoToast.show();
        }
    }

    public void onNothingSelected() {
    };
}

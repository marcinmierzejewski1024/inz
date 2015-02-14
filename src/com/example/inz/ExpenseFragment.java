package com.example.inz;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.example.inz.common.CommonActivity;
import com.example.inz.common.CommonFragment;
import com.example.inz.model.CashAmmount;
import com.example.inz.model.Category;
import com.example.inz.model.MainData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dom on 11/11/14.
 */
public class ExpenseFragment extends CommonFragment
{
    private static final int NAMED_CATEGORIES = 8;

    View rootView;
    List<Pair<Category,CashAmmount>> data = new ArrayList<Pair<Category, CashAmmount>>();

    private AdapterView.OnItemSelectedListener rangeListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Date now = new Date();
            String daysString = getResources().getStringArray(R.array.rangeDays)[position];
            long days = Integer.parseInt(daysString);
            long fromMilis = now.getTime();
            long timeDistance = (days * 24l * 60l * 60l * 1000l);
            fromMilis -= timeDistance;
            Date from = new Date(fromMilis);

            data = new MainData().getCategoryGroupedExpenses(from, NAMED_CATEGORIES);
            if(data != null)
            {
                drawChart();
                drawChartLegend();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.expense_fragment, container, false);

        Spinner range= (Spinner) rootView.findViewById(R.id.spinnerRange);
        range.setOnItemSelectedListener(rangeListener);

        return rootView;
    }

    private void drawChart()
    {
        PieGraph pg = (PieGraph)rootView.findViewById(R.id.graph);

        pg.removeSlices();
        for(Pair<Category,CashAmmount> singleItem:data)
        {
            PieSlice slice = new PieSlice();
            slice.setColor(Color.parseColor(singleItem.first.getHexColor()));
            slice.setGoalValue(singleItem.second.getPennies());
            slice.setValue(1);
            slice.setTitle(singleItem.first.getName());
            pg.addSlice(slice);
        }

        pg.setDuration(1000);//default if unspecified is 300 ms
        pg.setInterpolator(new AccelerateDecelerateInterpolator());
        pg.animateToGoalValues();
    }

    private void drawChartLegend()
    {
        try
        {
            ListView legend = (ListView) rootView.findViewById(R.id.legendListView);
            Pair<Category, CashAmmount>[] tmp=new Pair[1];
            LegendAdapter adapter = new LegendAdapter(getActivity(), (Pair<Category, CashAmmount>[]) data.toArray(tmp));
            legend.setAdapter(adapter);
            legend.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Long catId = (Long) view.getTag();
                ((CommonActivity)getActivity()).openCategory(catId);
            }
        });
        }
        catch (Exception e )
        {
            e.printStackTrace();
        }
    }
}

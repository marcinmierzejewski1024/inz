package com.mierzejewski.inzynierka;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mierzejewski.inzynierka.common.CommonActivity;
import com.mierzejewski.inzynierka.model.CashAmmount;
import com.mierzejewski.inzynierka.model.Category;

public class LegendAdapter extends ArrayAdapter<Pair<Category,CashAmmount>> {
    private final CommonActivity context;
    private Pair<Category,CashAmmount>[] values;

    public LegendAdapter(CommonActivity context, Pair<Category,CashAmmount>[] values)
    {
        super(context, R.layout.legend_item, values);
        this.context = context;
        this.values = values;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.legend_item, parent, false);

        rowView.setTag(values[position].first);

        TextView categoryName = (TextView) rowView.findViewById(R.id.categoryName);
        View colorView = rowView.findViewById(R.id.color);
        TextView categorySum = (TextView) rowView.findViewById(R.id.categorySum);

        ImageView categoryDelete = (ImageView) rowView.findViewById(R.id.categoryDelete);
        categoryDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((CommonActivity)context).deleteCategory(values[position].first.getCategoryId());
            }
        });

        if(Category.getRestCategoryExpense().equals(values[position].first)||Category.getRestCategoryIncome().equals(values[position].first))
        {
            categoryDelete.setVisibility(View.INVISIBLE);
        }
        else
        {
            categoryDelete.setVisibility(View.VISIBLE);
        }

        categoryName.setText(values[position].first.getName());
        categorySum.setText(values[position].second.toString());

        if(Build.VERSION.SDK_INT > 15)
        {
            colorView.setBackground(new ColorDrawable(Color.parseColor(values[position].first.getHexColor())));
        }
        else
        {
            colorView.setBackgroundDrawable(new ColorDrawable(Color.parseColor(values[position].first.getHexColor())));
        }

        return rowView;
    }
}
package com.essentialab.apps.mapadebolsillo.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.essentialab.apps.mapadebolsillo.R;
import com.essentialab.apps.mapadebolsillo.entities.DrawerItem;

public class ListItemInflationAction implements AdapterCommand{
	@Override
	public View execute(Object data, View v) {
		DrawerItem d = (DrawerItem) data;
		((TextView)v.findViewById(R.id.row_drawer_txt)).setText(d.title);
		((ImageView)v.findViewById(R.id.row_drawer_img)).setImageResource(d.iconId);
		return v;
	}
}

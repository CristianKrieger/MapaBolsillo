package com.essentialab.apps.mapadebolsillo.interfaces;

import android.view.View;
import android.widget.TextView;

import com.essentialab.apps.mapadebolsillo.R;

public class ListHeaderInflationAction implements AdapterCommand{
	@Override
	public View execute(Object data, View v) {
		((TextView)v.findViewById(R.id.header_list_txt)).setText((String)data);
		return v;
	}
}

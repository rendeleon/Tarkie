package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.BreaksObj;
import com.mobileoptima.tarkie.R;

import java.util.ArrayList;

public class BreaksAdapter extends ArrayAdapter<BreaksObj> {

	private ArrayList<BreaksObj> items;
	private LayoutInflater inflater;

	public BreaksAdapter(Context context, ArrayList<BreaksObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final BreaksObj breaks = items.get(position);
		if(breaks != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.breaks_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameBreaks = (CodePanLabel) view.findViewById(R.id.tvNameBreaks);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvNameBreaks.setText("");
			if(breaks.name != null) {
				holder.tvNameBreaks.setText(breaks.name);
			}
			if(!breaks.isDone) {
				holder.tvNameBreaks.setEnabled(true);
			}
			else {
				holder.tvNameBreaks.setEnabled(false);
			}
			if(items.indexOf(breaks) < items.size() - 1) {
				holder.tvNameBreaks.setBackgroundResource(R.drawable.state_rect_trans_dark);
			}
			else {
				holder.tvNameBreaks.setBackgroundResource(R.drawable.state_rect_trans_rad_bot_five);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameBreaks;
	}
}

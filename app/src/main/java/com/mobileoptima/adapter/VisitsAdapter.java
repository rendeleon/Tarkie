package com.mobileoptima.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.CheckInObj;
import com.mobileoptima.model.CheckOutObj;
import com.mobileoptima.model.VisitObj;
import com.mobileoptima.tarkie.R;

import java.util.ArrayList;

public class VisitsAdapter extends ArrayAdapter<VisitObj> {

	private int orange, green, red, gray;
	private ArrayList<VisitObj> items;
	private LayoutInflater inflater;

	public VisitsAdapter(Context context, ArrayList<VisitObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		Resources res = context.getResources();
		gray = res.getColor(R.color.gray_pri);
		red = res.getColor(R.color.red_pri);
		orange = res.getColor(R.color.orange_pri);
		green = res.getColor(R.color.theme_sec);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final VisitObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.visits_list_row, parent, false);
				holder = new ViewHolder();
				holder.tvNameVisits = (CodePanLabel) view.findViewById(R.id.tvNameVisits);
				holder.tvStatusVisits = (CodePanLabel) view.findViewById(R.id.tvStatusVisits);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvNameVisits.setText("");
			holder.tvStatusVisits.setText("");
			if(obj.name != null) {
				holder.tvNameVisits.setText(obj.name);
			}
			if(obj.isUpdate) {
				holder.tvNameVisits.setTextColor(gray);
			}
			else {
				holder.tvNameVisits.setTextColor(red);
			}
			if(obj.checkIn == null && obj.checkOut == null) {
				holder.tvStatusVisits.setText(R.string.active);
				holder.tvStatusVisits.setTextColor(orange);
			}
			else {
				String status = null;
				if(obj.checkIn != null && obj.isCheckIn) {
					CheckInObj checkIn = obj.checkIn;
					status = CodePanUtils.getNormalTime(checkIn.dTime, false) + " - ";
				}
				if(obj.checkOut != null && obj.isCheckOut) {
					CheckOutObj checkOut = obj.checkOut;
					status += CodePanUtils.getNormalTime(checkOut.dTime, false);
				}
				else {
					status += "NO OUT";
				}
				holder.tvStatusVisits.setTextColor(green);
				holder.tvStatusVisits.setText(status);
			}
		}
		return view;
	}

	private class ViewHolder {
		private CodePanLabel tvNameVisits;
		private CodePanLabel tvStatusVisits;
	}
}

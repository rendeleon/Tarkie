package com.mobileoptima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.StoreObj;
import com.mobileoptima.tarkie.R;

import java.util.ArrayList;

public class StoresAdapter extends ArrayAdapter<StoreObj> {

	private ArrayList<StoreObj> items;
	private LayoutInflater inflater;
	private Context context;

	public StoresAdapter(Context context, ArrayList<StoreObj> items) {
		super(context, 0, items);
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final StoreObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.stores_list_row, parent, false);
				holder = new ViewHolder();
				holder.rlHeader = (RelativeLayout) view.findViewById(R.id.rlHeader);
				holder.tvHeader = (CodePanLabel) view.findViewById(R.id.tvHeader);
				holder.tvStoreName = (CodePanLabel) view.findViewById(R.id.tvStoreName);
				holder.vDivider = view.findViewById(R.id.vDivider);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			int ten = context.getResources().getDimensionPixelOffset(R.dimen.ten);
			int five = context.getResources().getDimensionPixelOffset(R.dimen.five);
			if(position != 0) {
				holder.vDivider.setVisibility(View.VISIBLE);
				holder.rlHeader.setVisibility(View.GONE);
			}
			else {
				holder.rlHeader.setVisibility(View.VISIBLE);
			}
			if(obj.isHeader) {
				holder.rlHeader.setVisibility(View.VISIBLE);
				if(obj.header != null) {
					holder.tvHeader.setText(obj.header);
				}
				else {
					holder.tvHeader.setText("");
				}
			}
			else {
				holder.tvStoreName.setPadding(0, ten, 0, ten);
			}
			if(obj.isRemoveDivider) {
				if(position == 0) {
					holder.tvStoreName.setPadding(0, 0, 0, five);
				}
				else {
					holder.vDivider.setVisibility(View.GONE);
					holder.tvStoreName.setPadding(0, ten, 0, five);
				}
			}
			if(obj.name != null) {
				holder.tvStoreName.setText(obj.name);
			}
			else {
				holder.tvStoreName.setText("");
			}
		}
		return view;
	}

	private class ViewHolder {
		private RelativeLayout rlHeader;
		private CodePanLabel tvHeader, tvStoreName;
		private View vDivider;
	}
}

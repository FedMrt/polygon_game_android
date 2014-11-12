package it.mapeto2my.circles_square.adapters;

import it.mapeto2my.circles_square.exceptions.AdaptationNotSupportedException;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/* Classe per la generazione di istanze adapter custom; le istanze 
 * hanno comportamento molto simile al SimpleAdapter standard */
public class ListViewAdapter extends BaseAdapter {

	private Context context;
	private int layout;
	private List<Map<String,Object>> data;
	private String[] key;
	private int[] id;
	
	public ListViewAdapter(Context context, int layout, List<Map<String,Object>> data, String[] key ,int[] id){
		
		this.context = context;
		this.layout = layout;
		this.data = data;
		this.key = key;
		this.id = id;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint({ "ServiceCast", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout linearLayout = new LinearLayout(context);
		
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout,linearLayout,true);
		
		Map<String,Object> item = (Map<String,Object>)getItem(position);
		for(int i=0;i<id.length;i++){
			View view = linearLayout.findViewById(id[i]);
			Class viewClass = view.getClass(); 
			if(viewClass.equals(TextView.class))
				((TextView)view).setText(item.get(key[i]).toString());
			else if(viewClass.equals(ImageView.class) && item.get(key[i]).getClass().equals(Integer.class))
				((ImageView)view).setBackgroundColor((Integer)item.get(key[i]));
			else if(viewClass.equals(ImageView.class) && item.get(key[i]) instanceof Drawable)
				((ImageView)view).setBackground((Drawable)item.get(key[i]));
			else
				throw new AdaptationNotSupportedException();
		}

		return linearLayout;
	}
}
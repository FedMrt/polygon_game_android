package it.mapeto2my.circles_square;


import java.lang.reflect.Field;

import it.mapeto2my.circles_square.adapters.ListViewAdapter;
import it.mapeto2my.circles_square.exceptions.ResflexReadingException;
import it.mapeto2my.circles_square.models.Circle;
import it.mapeto2my.circles_square.models.Square;
import it.mapeto2my.circles_square.models.Triangle;
import it.mapeto2my.circles_square.utils.GenericConversion;
import it.mapeto2my.circles_square.views.CircleView;
import it.mapeto2my.circles_square.views.CircleView.OnTouchShapeListener;
import it.pepo.autowire.base.BaseActivity;
import it.pepo.autowire.base.annotations.Autowire;
import it.pepo.circles_square.R;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SquareActivity extends BaseActivity {

	
	private static int NUMERO_ISTANZE_ATTIVE; /* Campo di classe per tracing istanze */
	
	private Builder dialogBuilder; /* Istanza Builder dei dialogs (cambio forma e cambio colore ) */
	
	@Autowire
	private CircleView circle_view; /* Istanza circle view soggetta ad autowire (ha associati getter e setter) */
	
	/* Costruttore (con parametro booleano per il controllo della richiesta garbage collection )*/
	public SquareActivity(){
		super(false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(R.id.class, savedInstanceState, R.layout.activity_square);
		
		circle_view.setOnTouchShapeListener(new OnTouchShapeListener() {
			
			@Override
			public boolean onTouchShape(CircleView sender) {
				
				String colorName = "";
				Field[] colorFields =  R.color.class.getFields();
				Resources resources = getResources();
				
				try{
					for (Field field : colorFields) {
						if(resources.getColor((Integer)field.get(null)) == sender.getShapeColor())
							colorName = field.getName();
					}
				}catch(Exception ex){
					Log.e("SQUARE_ACTIVITY_ERR", ex.toString());
				}
				
				String toastText = sender.getShapeName().equals("cerchio") ? 
										getResources().getString(R.string.dialog_text,sender.getShapeName() + " " + colorName):
												getResources().getString(R.string.dialog_text_rotation, sender.getShapeName() + " " + colorName);
				
				Toast.makeText(SquareActivity.this, 
								toastText, 
									Toast.LENGTH_LONG).show();
				return true;
			}
		});
		
		dialogBuilder =  new Builder(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc(); /* richiesta garbage collection */
	}
	
	/* Metodo richiamato alla creazione del menu contestuale; in esso 
	 * effettuo l'inflating del layout di menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean retVal =  super.onCreateOptionsMenu(menu);
		
		getMenuInflater().inflate(R.menu.color_menu, menu);
		
		return retVal;
	}
	
	/* Metodo richiamato alla selezione di un item di menu; 
	 * in esso forziamo l'apertura del dialog di opzioni
	 * */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		boolean returnedVal =  super.onMenuItemSelected(featureId, item);

		openDialogAfterMenuItemClick(item.getItemId());
		
		return returnedVal;
	}	

	/* Metodo che determina aspetto e comportamento del dialog
	 * di opzioni dipendentemente dall'intero in input (id item di menu selezionato)*/
	private void openDialogAfterMenuItemClick(int itemId){
		
		ListViewAdapter listViewAdapter = null; 
		DialogInterface.OnClickListener listener = null;
		Resources resources = getResources();
		String dialogTitle = null;
		
		String[] keys = null;
		String[] firstArray = null;
		Object[] secondArray = null;
		
		
		switch (itemId) {
		
			case R.id.change_color_mi:
		
				dialogTitle = resources.getString(R.string.change_color_dgtitle_label);
				keys = new String[]{"nome_colore","valore_colore"};
				firstArray = resources.getStringArray(R.array.colors_array);
				secondArray = GenericConversion.getColorIntArray(resources, firstArray);
				listener = new ChangeColorDialogOnClickListener();
				
				break;
			case R.id.change_shape_mi:
				
				dialogTitle = resources.getString(R.string.change_shape_dgtitle_label);
				keys = new String[]{"nome_poligono","immagine_poligono"};
				firstArray = resources.getStringArray(R.array.shapes_array);
				secondArray = GenericConversion.getShapeDrawableArray(resources, firstArray);
				listener = new ChangeShapeDialogOnClickListener();
				break;
		}
		
		
		listViewAdapter = new ListViewAdapter(this, 
				R.layout.circles_list_item_1,
					GenericConversion.getMapsList(keys, firstArray,secondArray),
													keys,
														new int[]{R.id.textView1,R.id.imageView1});
		
		dialogBuilder.setTitle(dialogTitle);
		dialogBuilder.setAdapter(listViewAdapter, listener);
		dialogBuilder.create().show();
	
	}
	
	/* Classe che codifica un oggetto listener per il click su una generica voce di lista visualizzata 
	 * nel dialog nel caso in cui il dialog abbia comportamento ed aspetto previsti dal regime di 
	 * cambio colore
	 * */
	private class ChangeColorDialogOnClickListener implements DialogInterface.OnClickListener{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			Resources resources = getResources();
			
			String[] colors = resources.getStringArray(R.array.colors_array);
			int[] colorCodes = new int[colors.length];
			for(int i=0;i<colorCodes.length;i++){
				
				try{
					colorCodes[i] = resources.getColor(R.color.class.getField(colors[i]).getInt(null));
				}catch(Exception ex){
					Log.e("SQUARE_ACTIVITY_ERR", ex.toString());
				}	
			}
			circle_view.setShapeColor(colorCodes[which]); /* redraw */
		}
	}
	
	/* Classe che codifica un oggetto listener per il click su una generica voce di lista visualizzata 
	 * nel dialog nel caso in cui il dialog abbia comportamento ed aspetto previsti dal regime di 
	 * cambio forma
	 * */
	private class ChangeShapeDialogOnClickListener implements DialogInterface.OnClickListener{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			String[] shapesArray = getResources().getStringArray(R.array.shapes_array);
			
			circle_view.setPolygon(which == 0 ? new Circle() : which == 1 ? new Triangle() : new Square());
			circle_view.setShapeName(shapesArray[which]);
			circle_view.setShapeNumber(which); /* redraw */
		}
	}
	
	public CircleView getCircle_view() {
		return circle_view;
	}

	public void setCircle_view(CircleView circle_view) {
		this.circle_view = circle_view;
	}

	@Override
	protected Integer getNumeroIstanze() {
		// TODO Auto-generated method stub
		return SquareActivity.NUMERO_ISTANZE_ATTIVE;
	}

	@Override
	protected void setNumeroIstanze(Integer numeroIstanze) {
		
		SquareActivity.NUMERO_ISTANZE_ATTIVE = numeroIstanze;
	}

}
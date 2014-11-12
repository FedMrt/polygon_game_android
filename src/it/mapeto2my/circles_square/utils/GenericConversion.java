package it.mapeto2my.circles_square.utils;

import it.mapeto2my.circles_square.R;
import it.mapeto2my.circles_square.exceptions.ResflexReadingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;


public class GenericConversion {

	
	/* Metodo che restituisceu na lista di mappe dati in input un array di chiavi di mappa 
	 * ed un array di array di oggetti ... ogni array di oggetti contiene valori corrispondenti 
	 * ad una sola chiave entro l'array di chiavi; l'insieme di tutti i valori 
	 * degli array di oggetti ad indice fissato rappresenta l'insieme dei valori delle chiavi 
	 * di una singola mappa Es: 
	 * 
	 * String[] keys = {"nome_colore","valore_colore"}
	 * Object[] objsArrUno = {"bianco","verde"}
	 * Object[] objsArrUno = {123,456}
	 *  
	 *  Dà origine alla lista di mappe {{{"nome_colore","bianco"},{"valore_colore",123}},
	 *  								{{"nome_colore","verde"},{"valore_colore",456}}}
	 * 
	 * */
	public static List<Map<String,Object>> getMapsList(String[] keys, Object[]... objectsArrays){
		
		List<Map<String,Object>> mapsList = new ArrayList<Map<String,Object>>();
		int objectsArraysCommonlength = objectsArrays[0].length;
		
		for (int i=0; i < objectsArraysCommonlength;i++) {
			
			for(int j=0; j < objectsArrays.length; j++){
				
				Map<String,Object> mappa = new HashMap<String,Object>();
				mappa.put(keys[j], objectsArrays[j][i]);
				mapsList.add(mappa);
			}
		}
		
		return mapsList;
	}
	
	
	public static Integer[] getColorIntArray(Resources res,String[] colorStringArray){
		
		Integer[] colorIntArray = new Integer[colorStringArray.length];
		
		try{
			for(int i=0;i<colorStringArray.length;i++){
					colorIntArray[i] = res.getColor(R.color.class.getField(colorStringArray[i]).getInt(null));	
			}
		}catch(Exception ex){
			throw new ResflexReadingException();
		}
		
		return colorIntArray;
	}


	public static Drawable[] getShapeDrawableArray(Resources res,String[] shapeStringArray){
		
		Drawable[] shapeDrawableArray = new Drawable[shapeStringArray.length];
		
		try{
			for(int i=0;i<shapeStringArray.length;i++){
				shapeDrawableArray[i] = res.getDrawable(R.drawable.class.getField(shapeStringArray[i]).getInt(null));	
			}
		}catch(Exception ex){
			throw new ResflexReadingException();
		}
		
		return shapeDrawableArray;
	}
}

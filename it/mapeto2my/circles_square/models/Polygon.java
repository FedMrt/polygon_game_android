package it.mapeto2my.circles_square.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/*
 * Classe che rappresenta un generico poligono; in una accezione estesa rappresenta sia un generico poligono 
 * che una circonferenza
 * 
 */
public abstract class Polygon {

	/* Coordinate vertici nel sistema di coordinate M i.e. 
	 * il sistema avente origine coincidente con il vertice superiore sx del 
	 * display e assi paralleli ai lati del display */
	protected float[] vertex;
	
	/* Angolo di rotazione iniziale rispetto l'asse y negativo del sistema 
	 * di coordinare M */
	protected float initRotationAngle;
	
	/* Campo di controllo esecuzione di almeno una rotazione del poligono */
	protected boolean atLeastOneRotationPerformed = false; 
	
	/* Metodo disegno */
	public void draw(Canvas canvas, int dimension, int centerEventX, int centerEventY,double rotationAngle, Paint paint){
		drawOnCanvas(canvas, dimension, centerEventX, centerEventY, rotationAngle, paint);
		initRotationAngle = (float)rotationAngle;
	}
	
	/* Metodo disegno */
	protected abstract void drawOnCanvas(Canvas canvas, int dimension, int centerEventX, int centerEventY,double rotationAngle, Paint paint);
	
	/* Metodo che ricava le coordinate dei vertici del poligono 
	 * nel sistema di riferimento di CE (i.e. Centro Evento) a partire dalle coordinate dei vertici 
	 * nel sistema M
	 * */
	protected float[] getVertexInCenterEventSystem(float[] centerEvent){
			
		float[] vertexInCenterEventSystem = new float[vertex.length];
		
		for(int i = 0; i < vertexInCenterEventSystem.length; i = i + 2){
			
			float[] temp = translation(centerEvent, new float[]{vertex[i],vertex[i+1]},true);
			
			vertexInCenterEventSystem[i] = temp[0];
			vertexInCenterEventSystem[i+1] = temp[1];
			
			Log.d("CIRCLE_VIEW_DBG_RA", "Vertex coordinates in CE System: " + vertexInCenterEventSystem[i] + " - " + vertexInCenterEventSystem[i+1]);
			
		}
		
		return vertexInCenterEventSystem;
	}
	
	
	/* Metodo che fissa il valore delle coordinate dei vertici del poligono 
	 * nel sistema di riferimento M a partire dalle coordinate dei vertici nel 
	 * sistema di riferimento CE  */
	protected void setVertexInMainSystem(float[] centerEvent, float[] vertexInCenterEventSystem){
			
		for(int i = 0; i < vertexInCenterEventSystem.length; i = i + 2){
			
			float[] temp = translation(centerEvent, new float[]{vertexInCenterEventSystem[i],vertexInCenterEventSystem[i+1]},false);
			
			vertex[i] = temp[0];
			vertex[i+1] = temp[1];
			
			Log.d("CIRCLE_VIEW_DBG_RA", "Vertex coordinates in MA System: " + vertex[i] + " - " + vertex[i+1]);
			
		}
	}
	
	
	/* Metodo che ricava le coordinate dei vertici del poligono 
	 * nel sistema di riferimento CE dopo una rotazione dei vertici  */
	protected float[] rotateVertexInCenterEventSystem(float[] vertexInCenterEventSystem,double rotationAngle){
		
		if(rotationAngle == 0)
			return vertexInCenterEventSystem;
		
		atLeastOneRotationPerformed = true;
		
		float[] rotatedVertexInCenterEventSystem = new float[vertexInCenterEventSystem.length];
		
		Log.d("CIRCLE_VIEW_DBG_RA", "performing rotation of: " + rotationAngle);
		
		for(int i = 0; i < vertexInCenterEventSystem.length; i = i + 2){
			
			float[] temp = rotationInCenterEventSystem(
								new float[]{vertexInCenterEventSystem[i],vertexInCenterEventSystem[i+1]}, 
									rotationAngle);
			
			
			rotatedVertexInCenterEventSystem[i] = temp[0];
			rotatedVertexInCenterEventSystem[i + 1] = temp[1];
			
			Log.d("CIRCLE_VIEW_DBG_RA", "Vertex coordinates rotated in CE system: " + rotatedVertexInCenterEventSystem[i] + " - " + rotatedVertexInCenterEventSystem[i+1]);
		}
		
		return rotatedVertexInCenterEventSystem;
		
	}
	
	
	/* Metodo che trasforma una coppia di coordinate nel sistema di 
	 * riferimento Main in una coppia nel sistema CE: il legame tra i due sistemi 
	 * è una traslazione ed una inversione dell'asse delle ordinate */
	private float[] translation(float[] centerEvent,float[] point, boolean fromMainToCenter){
		
		float xPoint = 0;
		float yPoint = 0;
		
		if(fromMainToCenter){
			
			xPoint = point[0] - centerEvent[0];
			yPoint = (-1) * (point[1] - centerEvent[1]);
			
		}else{
			
			xPoint = point[0] + centerEvent[0];
			yPoint = (-1) * point[1] + centerEvent[1];
		}
		
		return new float[]{xPoint,yPoint};
	}
	
	/* Metodo che ricava le coordinate di un generico punto nel sistema di riferimento 
	 * CE data una rotazione del sistema di angolo pari a rotationAngle, rispetto alla
	 * retta perpendicolare il piano xy e passante per l'origine degli assi. 
	 *  */
	private float[] rotationInCenterEventSystem(float[] pointInCenterEventSystem, double rotationAngle){
		
		if(rotationAngle == 0)
			return pointInCenterEventSystem;
		
		float[] point = new float[2];
		
		float xPointInCenterEventSystem = (float)(pointInCenterEventSystem[0] * Math.cos(rotationAngle) + 
											pointInCenterEventSystem[1] * Math.sin(rotationAngle));
		
		float yPointInCenterEventSystem = (float)(pointInCenterEventSystem[1] * Math.cos(rotationAngle) -
											pointInCenterEventSystem[0] * Math.sin(rotationAngle));

		point[0] = xPointInCenterEventSystem;
		point[1] = yPointInCenterEventSystem;
		
		return point;
	}

	
	public void setInitRotationAngle(float initRotationAngle) {
		this.initRotationAngle = initRotationAngle;
	}
	
	
	public void setAtLeastOneRotationPerformed(boolean atLeastOneRotationPerformed) {
		this.atLeastOneRotationPerformed = atLeastOneRotationPerformed;
	}
}

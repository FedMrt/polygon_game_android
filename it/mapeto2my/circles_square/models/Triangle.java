package it.mapeto2my.circles_square.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/*
 * Classe che rappresenta un triangolo
 * */
public class Triangle extends Polygon{

	
	@Override
	public void drawOnCanvas(Canvas canvas, 
								int dimension, 
									int centerEventX,
										int centerEventY, 
											double rotationAngle, 
												Paint paint) {
		// TODO Auto-generated method stub
		
		float[] centerEvent = new float[]{centerEventX,centerEventY};
		float[] vertexCeRt = null;
		
		if(vertex == null){
			vertex = new float[6];
		}
		
		if(!atLeastOneRotationPerformed){
		
			int segmentoMaxCentroVertice = (int) (dimension * Math.pow(Math.cos(Math.PI / 6), -1));
			int segmentoMinCentroVertice = (int) (segmentoMaxCentroVertice * Math.sin(Math.PI / 6));
			float v1x = centerEventX; 						     /* coordinata x vertice superiore */
			float v1y = centerEventY - segmentoMaxCentroVertice; /* coordinata y vertice superiore */
			
			float v2x = centerEventX - dimension; /* coordinata x vertice inferiore sx */
			float v2y = centerEventY + segmentoMinCentroVertice; /* coordinata y vertice inferiore sx */
		
			float v3x = centerEventX + dimension; /* coordinata x vertice inferiore dx */
			float v3y = v2y; /* coordinata y vertice inferiore dx */
		
			vertex[0] = v1x; vertex[1] = v1y; vertex[2] = v2x;vertex[3] = v2y;vertex[4] = v3x; vertex[5] = v3y;
		}
		
		float[] vertexCe = getVertexInCenterEventSystem(centerEvent);
		
		Log.d("CIRCLE_VIEW_DBG_RA", "rotationAngle: " + rotationAngle);
		Log.d("CIRCLE_VIEW_DBG_RA", "initRotationAngle: " + initRotationAngle);
		vertexCeRt = rotateVertexInCenterEventSystem(vertexCe, rotationAngle - initRotationAngle );
		
		setVertexInMainSystem(centerEvent, vertexCeRt);
		
		
		Path percorsoTriangolo = new Path();
		percorsoTriangolo.moveTo(vertex[0], vertex[1]);
		percorsoTriangolo.lineTo(vertex[2], vertex[3]);
		percorsoTriangolo.lineTo(vertex[4], vertex[5]);

		canvas.drawPath(percorsoTriangolo, paint);
		
	}
}

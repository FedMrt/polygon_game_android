package it.mapeto2my.circles_square.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/*
 * Classe che rappresenta un quadrato
 * */
public class Square extends Polygon{

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
			vertex = new float[8];
		}
		
		if(!atLeastOneRotationPerformed){
			float v1x = centerEventX - dimension;	/* coordinata x vertice superiore sx */
			float v1y = centerEventY - dimension;   /* coordinata y vertice superiore sx */
			
			float v2x = centerEventX + dimension; /* coordinata x vertice superiore dx */
			float v2y =v1y; 					  /* coordinata y vertice superiore dx */
	
			float v3x = v2x; 					  /* coordinata x vertice inferiore dx */
			float v3y = centerEventY + dimension; /* coordinata y vertice inferiore dx */
	
			float v4x = v1x; 					  /* coordinata x vertice inferiore sx */
			float v4y = v3y; 					  /* coordinata y vertice inferiore sx */
	
			vertex[0] = v1x; vertex[1] = v1y; vertex[2] = v2x;vertex[3] = v2y;vertex[4] = v3x; vertex[5] = v3y;
			vertex[6] = v4x; vertex[7] = v4y;	
		}	
			
		float[] vertexCe = getVertexInCenterEventSystem(centerEvent);
		vertexCeRt = rotateVertexInCenterEventSystem(vertexCe, rotationAngle - initRotationAngle);
		setVertexInMainSystem(centerEvent, vertexCeRt);
		
		Path percorsoQuadrato = new Path();
		percorsoQuadrato.moveTo(vertex[0], vertex[1]);
		percorsoQuadrato.lineTo(vertex[2], vertex[3]);
		percorsoQuadrato.lineTo(vertex[4], vertex[5]);
		percorsoQuadrato.lineTo(vertex[6], vertex[7]);

		canvas.drawPath(percorsoQuadrato, paint);
		
	}
}

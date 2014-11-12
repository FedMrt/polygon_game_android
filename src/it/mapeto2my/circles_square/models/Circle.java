package it.mapeto2my.circles_square.models;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * Classe che rappresenta un cerchio
 * */
public class Circle extends Polygon{

	
	@Override
	public void drawOnCanvas(Canvas canvas, 
								int dimension, 
									int centerEventX,
										int centerEventY, 
											double rotationAngle,
												Paint paint) {
		
		canvas.drawCircle(centerEventX, centerEventY, dimension, paint);
	}

}

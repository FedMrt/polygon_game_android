package it.mapeto2my.circles_square.views;


import it.mapeto2my.circles_square.models.Circle;
import it.mapeto2my.circles_square.models.Polygon;
import it.pepo.circles_square.R;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;

public class CircleView extends View{

	private static final int MIN_CIRCLE_WIDTH = 30;  /* Larghezza minima vista */
	private static final int MIN_CIRCLE_HEIGHT = 30; /* Altezza minima vista */
	
	private int centerX; 		  /* Coordinata x del centro della vista */
	private int centerY; 		  /* Coordinata y del centro della vista */
	private double rotationAngle; /* Angolo di rotazione */
	
	private String shapeName; /* Nome forma rappresentata dalla nostra vista (Es: cerchio, triangolo o quadrato)*/
	private int shapeNumber;  /* Valore numerico associato alla forma rappresentata dalla nostra vista (Es: 0 cerchio, 1 triangolo, 2 quadrato)*/
	private int shapeColor;   /* Colore associato alla forma rappresentata dalla nostra vista */
	
	private Polygon polygon;  /* L'istanza polygon associata alla nostra vista  */
	
	private int windowTitleSize; /* Altezza del titolo della finestra in cui vengono incluse la nostra vista ed il suo contenitore */
	
	private boolean redraw = false; /* Flag che indica la necessità o meno di ridisegnare la vista */
	
	private OnTouchShapeListener onTouchShapeListener; /* Campo di istanza che rappresenta l'unico listener di tipo OnTouchShape associato alla nostra vista */
	
	private Thread monitoringEventThread; /* Campo di istanza che rappresenta il thread di monitoraggio onTouchEvent e di ridisegno della vista */

	public CircleView(Context context) {
		super(context);	
	}
	
	
	
	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/* Otteniamo l'insieme di attributi custom della nostra vista i.e. quelli definiti attraverso grammatica nuova*/
		TypedArray arr = context.obtainStyledAttributes(attrs,R.styleable.it_pepo_circles_square_views_CircleView);
		
		shapeColor = arr.getColor(R.styleable.it_pepo_circles_square_views_CircleView_view_color, Color.YELLOW); /* Colore forma via valore attributo */
		shapeNumber = 0; /* Numero forma di default pari a zero i.e. il cerchio */
		shapeName = getResources().getStringArray(R.array.shapes_array)[0]; /* Il nome della forma di default è "cerchio" */
		
		polygon = new Circle(); /* La forma di default è il cerchio */
		
		windowTitleSize = (int)context.obtainStyledAttributes(R.style.AppTheme, new int[]{android.R.attr.windowTitleSize}).getDimension(0, 0F);/* Dimenione titolo */
		
	}
	
	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);	
	}

	/* Segue il metodo onMeasure ... metodo richiamato dalla View contenitore 
	 * per interrogare la vista corrente; il contenitore informa il 
	 * contenuto sullo spazio a sua disposizione; alla luce di ciò il contenuto
	 * si dimensiona e restituisce la coppia di dimensioni al contenitore chiamante;
	 * il contenitore chiama il metodo onMeasure del contenuto attraverso invocazione
	 * measureChild(View child, int width, int height)
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d("CIRCLE_VIEW_DBG", "Call:onMeasure on " + this);

		/* I parametri formali widthMeasureSpec ed heightMeasureSpec rappresentano larghezza ed 
		 * altezza massima dell'area assegnata alla vista corrente: la vista corrente dovrebbe 
		 * assegnarsi delle dimensioni che non eccedano quelle dell'area assegnatale (l'area entro
		 * cui dovrebbe disegnarsi); i parametri hanno valore intero ... per convertirli in formato 
		 * numerico human-readable (px) e per dettagliare il significato delle dimensioni
		 * (UNSPECIFIED, EXACTLY, AT_MOST) è necessario invocare metodi "convertitori" */
		String widthMeasureSpecMode = (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) ? "AT_MOST"
				: ((MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) ? "EXACTLY"
						: "UNSPECIFIED");/* SPECIFICA larghezza */
		String heightMeasureSpecMode = (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) ? "AT_MOST"
				: ((MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) ? "EXACTLY"
						: "UNSPECIFIED");/* SPECIFICA altezza */
		
		int widthMeasureSpecVal = MeasureSpec.getSize(widthMeasureSpec); /* VALORE larghezza */
		int heightMeasureSpecVal = MeasureSpec.getSize(heightMeasureSpec); /* VALORE altezza */
		
		
		
		
		/* Recuperiamo valori layout_width e layout_height da xml*/
		int viewWidth = getLayoutParams().width;
		int viewHeight = getLayoutParams().height;
		
		/*
		 * La vista corrente, alla luce delle informazioni dimensionali di cui sopra,
		 * fornitele dalla View parent; si assegna una larghezza ed una altezza; 
		 * nel nostro caso tale assegnazione prevede altezza e larghezza pari 
		 * rispettivamente ad altezza e larghezza attributi xml solo qualora le specifiche di altezza e larghezza 
		 * fornite dal contenitore fossero pari a EXACTLY o AT_MOST e qualora altezza e larghezza xml 
		 * fossero minori rispettivamente a valori altezza e larghezza fornite dal contenitore.In caso 
		 * contrario si assegnano dimensioni pari a MIN_CIRCLE_WIDTH è MIN_CIRCLE_HEIGHT
		 */
		if((widthMeasureSpecMode.equals("EXACTLY") || widthMeasureSpecMode.equals("ALMOST")) &&
			(heightMeasureSpecMode.endsWith("EXACTLY") || heightMeasureSpecMode.equals("AT_MOST")))
				if(viewWidth > widthMeasureSpecVal && viewHeight > heightMeasureSpecVal)
					setMeasuredDimension(widthMeasureSpecVal, heightMeasureSpecVal);	
				else
					setMeasuredDimension(viewWidth, viewHeight);
		else{
			setMeasuredDimension(MIN_CIRCLE_WIDTH, MIN_CIRCLE_HEIGHT);
		}
	}
	
	
	/* Segue il metodo onDraw ... metodo richiamato dalla View contenitore 
	 * per disegnare la vista corrente entro un'area assegnatale dal contenitore
	 * stesso; il contenitore chiama il metodo onDraw del contenuto attraverso 
	 * invocazione child.layout(int left, int top, int right,int bottom) */
	@SuppressLint({ "DrawAllocation", "Recycle" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Log.d("CIRCLE_VIEW_DBG", "Call:onDraw on " + this);

		int measuredViewHeight = getMeasuredHeight();
		int measuredViewWidth = getMeasuredWidth();

		if(!redraw){
			/* Caso non ridisegno */
			centerX = (int)getX() + measuredViewWidth / 2;
			centerY = (int)getY() + measuredViewHeight / 2;
			rotationAngle = 0;
		}
		
		Paint polygonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		polygonPaint.setColor(shapeColor);
		
		polygon.draw(canvas, measuredViewWidth / 2, centerX, centerY, rotationAngle,polygonPaint);
	} 
	
	/* Metodo onTouchEvent, scatta al click in un punto generico della superficie dello schermo 
	 * per cui valga la seguente condizione: dato il punto di tocco, le rette parallele ai lati 
	 * dello schermo del device passanti per il punto di tocco stesso, definiscano un'area entro la quale 
	 * il canvas in cui la vista sia inscritta, risulti completamente contenuto */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean retVal = super.onTouchEvent(event);

		if (onTouchShapeListener != null) {/* In realtà di listener di tipo OnTouchSphere ce ne è uno ed uno solo */
			
			boolean startThread = false;
			
			printEventInfo(event);
			
			float viewX = !redraw?(getX() + getMeasuredWidth()/2):centerX;  /* Coordinata X del centro della vista  */
			float viewY = !redraw?(getY() + getMeasuredHeight()/2):centerY; /* Coordinata Y del centro della vista */
			
			float eventX = 0;
			float eventY = 0;
			
			
			eventX = event.getX(); /* Coordinata X del punto in cui l'evento è occorso */
			eventY = event.getY(); /* Coordinata Y del punto in cui l'evento è occorso */
		
			/* Distanza tra il punto in cui è occorso l'evento ed il centro della vista */
			double distance = Math.sqrt(Math.pow(eventX - viewX, 2) + Math.pow(eventY - viewY, 2)); 
			
			Log.d("CIRCLE_VIEW_DBG", "distance :" + distance);
			
			if(distance <= getMeasuredWidth()/2){
				startThread = true;	
			}
			
		
			/* Distanza minore del raggio della circonferenza ... invocazione del metodo  
			 * onTouchShape del listener */
			if(startThread){
				Log.d("CIRCLE_VIEW_DBG", "Start monitoringEventThread");
				onTouchShapeListener.onTouchShape(this);
				monitoringEventThread = new Thread(new CheckMotion(new CheckMotionHandler(event))); 
				monitoringEventThread.start();
			}
		}
		
		return retVal;
	}
	

	/* Interfaccia interna che codifica un listener di tipo TouchSphere */
	public interface OnTouchShapeListener {
		
		boolean onTouchShape(CircleView sender);
	}	

	/* Metodo per l'assegnazione di un listener OnTouchSphere alla vista; tale 
	 * listener sarà associato ad un generico evento touch occorso entro la 
	 * superficie della vista-circonferenza; l'assegnazione all'evento 
	 * avviene in seno al metodo onTouchEvent */
	public void setOnTouchShapeListener(OnTouchShapeListener l) {
		onTouchShapeListener = l;
	}
	
	/* Classe che definisce una classe di threads di monitoraggio tocco schermo dispositivo */
	private class CheckMotion implements Runnable{

		private static final int DELAY = 10;
		private CheckMotionHandler checkMotionHandler;
		
		public CheckMotion(CheckMotionHandler checkMotionHandler) {
			
			this.checkMotionHandler = checkMotionHandler;
		}
		
		@Override
		public void run() {
			
			try {
				
				while(true){
					Thread.sleep(DELAY);
					checkMotionHandler.sendMessage(new Message());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	/* Classe che definisce una classe di thread handlers per il thread di monitoraggio tocco schermo dispositivo;
	 * l'handler monitora ed assegna ai campi di istanza centerX e centerY i valori rispettivamente di 
	 * coordinata X occorrenza evento onTouch e coordinata Y .... 
	 *  */
	@SuppressLint("HandlerLeak")
	private class CheckMotionHandler extends Handler{
		
		private MotionEvent event;
		
		public CheckMotionHandler(MotionEvent event) {
			
			this.event = event;
		}
	
		@Override
		public void handleMessage(Message msg) {
			
			redraw = true;
			
			if(event.getPointerCount() == 1){ /* Tocco singolo */
				
				centerX = (int)event.getX();
				centerY = (int)event.getY() - 2 * windowTitleSize;
			
			}else if(event.getPointerCount() > 1){ /* Multi tocco, per noi solo doppio */
				
				PointerCoords outPointerCoords = new PointerCoords();
				event.getPointerCoords(event.getPointerId(0), outPointerCoords);
				
				float firstFingerX = outPointerCoords.x; 
				float firstFingerY = outPointerCoords.y;
				
				Log.d("CIRCLE_VIEW_DBG", "firstFingerX: " + firstFingerX);
				Log.d("CIRCLE_VIEW_DBG", "firstFingerY: " + firstFingerY);
				
				event.getPointerCoords(event.getPointerId(1), outPointerCoords);
				
				float secondFingerX = outPointerCoords.x; 
				float secondFingerY = outPointerCoords.y;
				
				Log.d("CIRCLE_VIEW_DBG", "secondFingerX: " + secondFingerX);
				Log.d("CIRCLE_VIEW_DBG", "secondFingerY: " + secondFingerY);
				
				if(Math.sqrt(Math.pow(secondFingerX - firstFingerX, 2) + Math.pow(secondFingerY - firstFingerY, 2)) <= getLayoutParams().width){
					rotationAngle = Math.acos( - (secondFingerY - firstFingerY ) / 
						     		Math.sqrt(Math.pow((secondFingerY - firstFingerY ), 2) + Math.pow((secondFingerX - firstFingerX), 2)));  
					
					centerX = (int)((secondFingerX + firstFingerX) / 2);
					centerY = (int)((secondFingerY + firstFingerY) / 2) - 2 * windowTitleSize;
					
					Log.d("CIRCLE_VIEW_DBG", "event centerX: " + centerX);
					Log.d("CIRCLE_VIEW_DBG", "event centerY: " + centerY);
				}
				
			}
			
			
			
			CircleView.this.invalidate();
			
			boolean stopThread = printEventInfo(event);
			if(stopThread){
				polygon.setInitRotationAngle(0);
				rotationAngle = 0;
				polygon.setAtLeastOneRotationPerformed(false);
				monitoringEventThread.interrupt();
			}
		}
	}
	
	
	
	/* Metodo per il print log-cat delle informazioni relative ad un evento onTouch occorso; sia esso 
	 * interno all'area della circonferenza rappresentata dalla vista, sia esso esterno */
	private boolean printEventInfo(MotionEvent event){
		
		boolean stopPrint = false;
		
		String eventName = "";
		
		float eventX = event.getX();
		float eventY = event.getY();
		
		Log.d("CIRCLE_VIEW_DBG", "MOTION_EVENT_X:" + eventX);
		Log.d("CIRCLE_VIEW_DBG", "MOTION_EVENT_Y:" + eventY);
		
		switch(event.getAction()){
		
			case MotionEvent.ACTION_CANCEL: eventName = "ACTION_CANCEL"; break;
			case MotionEvent.ACTION_DOWN: eventName = "ACTION_DOWN"; break;
			case MotionEvent.ACTION_HOVER_ENTER: eventName = "ACTION_HOVER_ENTER"; break;
			case MotionEvent.ACTION_HOVER_EXIT: eventName = "ACTION_HOVER_EXIT"; break;
			case MotionEvent.ACTION_HOVER_MOVE: eventName = "ACTION_HOVER_MOVE"; break;
			case MotionEvent.ACTION_MASK: eventName = "ACTION_MASK"; break;
			case MotionEvent.ACTION_MOVE: eventName = "ACTION_MOVE"; break;
			case MotionEvent.ACTION_OUTSIDE: eventName = "ACTION_OUTSIDE"; break;
			case MotionEvent.ACTION_SCROLL: eventName = "ACTION_SCROLL"; break;
			case MotionEvent.ACTION_UP: eventName = "ACTION_UP"; stopPrint = true; break;
		}
	
		Log.d("CIRCLE_VIEW_DBG", "MOTION_EVENT_TYPE:" + eventName);
		
		return stopPrint;
	} 
	
	public int getShapeColor(){
		return shapeColor;
	}
	
	public void setShapeColor(int color){
		shapeColor=color;
		invalidate();
	}

	public int getShapeNumber() {
		return shapeNumber;
	}

	public void setShapeNumber(int number) {
		shapeNumber = number;
		invalidate();
	}

	public String getShapeName() {
		return shapeName;
	}

	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
}

package it.mapeto2my.circles_square.viewgroups;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

public class SquareLayout extends ViewGroup{

	
	public SquareLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d("SQUARE_LAYOUT_DBG", "Call:onMeasure on " + this);
		
		/* I parametri formali widthMeasureSpec ed heightMeasureSpec rappresentano larghezza ed 
		 * altezza massima dell'area assegnata alla vista corrente dal suo contenitore;
		 * i parametri hanno valore intero ... per convertirli in formato 
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
		
		/* Imponiamo che la vista corrente sia in grado di calcolare lo spazio da assegnare ad ogni figlio 
		 * se e solo se il suo diretto contenitore indichi specifiche di altezza e larghezza 
		 * pari a EXACTLY o AT_MOST. In questo caso la vista corrente si assegna le dimensioni 
		 * indicatale dal contenitore */
		if((widthMeasureSpecMode.equals("EXACTLY") || widthMeasureSpecMode.equals("ALMOST")) &&
				(heightMeasureSpecMode.endsWith("EXACTLY") || heightMeasureSpecMode.equals("AT_MOST"))){
			
			/* La vista si assegna dimensioni indicatele dal suo contenitore */
			setMeasuredDimension(widthMeasureSpecVal, heightMeasureSpecVal);
			
			/* La vista chiede ad ogni figlio di calcolare le se dimensioni a fronte dello spazio che 
			 * la vista stessa gli assegna */
			int childCount = getChildCount();
			
			for(int i=0; i< childCount;i++){ /* Loop sugli elementi figlio */
				if (getChildAt(i).getVisibility() != GONE) /* Elemento figlio visibile */
					
					/* Richiesta al figlio i-esimo che si misuri ... esecuzione del metodo onMeasure(int,int) 
					 * della classe figlio; al figlio vengono passate le informazioni sulla larghezza e sulla 
					 * altezza che la vista attuale dedica ad esso; nel nostro caso lo spazio dedicato è uguale
					 * per ciasqun figlio ... poniamo comunque il vincolo del rendering di un unico figlio .. pertanto 
					 * richiediamo la misurazione unicamente al primo figlio  ... */
					if(i==0)
						measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec); /*L'unico figlio ha tutto lo spazio a disposizione */
					else{
						break;
						/* measureChild(getChildAt(i), widthMeasureSpec/childCount, heightMeasureSpec/childCount); */ //TODO per integrazione multi-child
					}
			}	
		}
	}
	
	
	/* Metodo in seno il quale forzare il disegno dei figli; vengono generate quattro quantità i.e. 
	 * left, top, right e bottom che rappresentano il rettangolo 
	 * (avente come origine degli assi il vertice sx in alto dell'elemento contenitore il nostro view group) 
	 * ricavato dall'elemento contenitore del nostro view group come area che delimiti il posizionamento dei figli 
	 * del view group stesso */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		Log.d("SQUARE_LAYOUT_DBG", "Call:onLayout on " + this);
		
		int l0=l;//TODO per integrazione multi-child
		
		for(int i=0; i < getChildCount(); i++){
			
			
			int viewHeight = getChildAt(i).getMeasuredWidth();/* Larghezza riportata dal figlio i-esimo */
			int viewWidth = getChildAt(i).getMeasuredHeight();/* Altezza riportata dal figlio i-esimo */
			
			
			/* *********** NOTA BENE ************ */
			/* Posizionamento dei figli in seno al layout ... poniamo il vincolo di rendering solo del primo figlio ... 
			 * il primo figlio ha a disposizione tutto lo spazio dedicato al nostro view group*/
			if(i==0)
				getChildAt(i).layout(l, t, r, b);
			else 
				break;
			
			l+= viewWidth;//TODO per integrazione multi-child
			if(l + viewWidth > r){//TODO per integrazione multi-child
				l = l0;//TODO per integrazione multi-child
				t = t + viewHeight;//TODO per integrazione multi-child
			}
		}	
	}
}
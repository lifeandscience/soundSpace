package org.ncmls.soundspace;

import java.net.ConnectException;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import org.ncmls.soundspace.OSCConnection;
import org.ncmls.soundspace.AccelerometerListener;
import org.ncmls.soundspace.AccelerometerManager;
import org.ncmls.soundspace.R;

import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
@SuppressWarnings("unused")

public class soundSpace extends Activity 
implements Runnable, OnTouchListener, AccelerometerListener {

	   private static final String TAG      = "soundSpace";
	   private static final int DELAYTIME   = 6;
	   private static final int RESPONSE_MS = 700;
	   
	   
	public class timmer implements Runnable {
		private boolean _running = true;
		private Handler _handler;
		private Runnable _runnable;

		public timmer(Runnable runnable, Handler handler) {
			_runnable = runnable;
			_handler = handler;
		}
		public void setRunning(boolean running) {_running = running;}

		public void run() {
			while(_running) {
				try { Thread.sleep(RESPONSE_MS); } catch(InterruptedException ex) {}
				if( _running ) _handler.post( _runnable );
			}
		}
	}
	timmer tim;
	Thread th;
	Handler handler = new Handler();
	int timmerDecay = 0;
	

	   private static Context CONTEXT;
	   static int lastSquare = 1;
	   static int activityLevel = 0;
	   private static OSCConnection osccon = null;
	   static float mx,my,mz;
	   static ImageButton lastButton = null;
	   private static final int buttonIds[] =
		   { 0,
		   R.id.imageButton1,
		   R.id.imageButton2,
		   R.id.imageButton3,
		   R.id.imageButton4,
		   R.id.imageButton5,
		   R.id.imageButton6,
		   R.id.imageButton7,
		   R.id.imageButton8,
		   R.id.imageButton9
		   };
	   private static int level[] = { 0,0,0,0,0,0,0,0,0,0 };
	   private static final int LISTENING = 1;
	   private static final int TALKING   = 2;
	   private static int mode = TALKING;
	   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ImageView view = (ImageView) findViewById(R.layout.main);
        setContentView(R.layout.main);
        // view.setOnTouchListener(this);
        CONTEXT = this;
    	/* Debug.startMethodTracing(); */
        for(int i = 1; i<10; i++)
        {
        	ImageButton b=(ImageButton)findViewById(buttonIds[i]);
        	if (b != null)
        	{
        		b.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        		        Log.i(TAG, "start onClick()");
        				onTap((ImageButton)v);
        		        Log.i(TAG, "end onClick()");
        			}
        		});	
        	}
        }

        osccon = new OSCConnection();
        try {
  		osccon.connect("10.0.0.109:7000");
        } catch (ConnectException e) {	e.printStackTrace();	}

    	tim = new timmer(this, handler);
    	(th = new Thread(tim)).start();
        Log.i(TAG, "end of onCreate");
        } // END of onCreate()
             
        public boolean onTouch(View v, MotionEvent event) {
      	    if (true) return true;
            ImageView view = (ImageView) v;
            CharSequence pos; 

            switch (event.getAction() & 0xFF) {
            case MotionEvent.ACTION_DOWN:
               
               break;
            case MotionEvent.ACTION_UP:
               break;
               
            case MotionEvent.ACTION_MOVE:
        		timmerDecay = DELAYTIME;
               break;
            }
            return true; // indicate event was handled
         }

        protected void onResume() {
     	   super.onResume();
     	   if (AccelerometerManager.isSupported()){
     		   AccelerometerManager.startListening(this);
     	   }
        }
        protected void onDestroy() {
     	   super.onDestroy();
     	   if (AccelerometerManager.isListening()){
     		   AccelerometerManager.stopListening();
     	   }
        }
        @Override
        public void onStart() {
                super.onStart(); 
        }

        @Override
        public void onRestart() {
                super.onRestart(); 
        }
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options, menu);
            return true;
        }
        public static Context getContext() {
     	   return CONTEXT;
        }
    	public void onAccelerationChanged(float x, float y, float z) {
    		if (Math.abs(x-mx) > 4.5 || Math.abs(y-my) > 4.5 || Math.abs(z-mz) > 4.5)
    		{
    		}
    		mx =x; my = y; mz =z;	
    	}
    	public void onShake(float force) {
   		 if (force > 0.9)
   			 {
   			 	openOptionsMenu();
   			 }
    	}
    	private int buttonNumber(ImageButton b)
    	{
            if (b == null) 
            {
            	return (0);
            }
            
			int id = b.getId();
    		for(int i=1;i<10;i++)
    			if ( id == buttonIds[i])
    			{
    				return i;
    			}
			return 0;
    	} // End of buttonNumber()
    	
    	public void onTap(ImageButton b)
    	{
    		int newSquare = buttonNumber(b);
    		int oldSquare = buttonNumber(lastButton);
    		b.setSelected(true);
    		if (newSquare == oldSquare)
    		{
    			if (level[oldSquare] < 3) level[oldSquare]++;
                osccon.send("/activity", oldSquare, level[oldSquare]);
    		}
    		else
    		{
                level[newSquare] = 1;
                osccon.send("/activity", newSquare, level[newSquare]);
    		}

    		if (level[oldSquare] == 0) b.setAlpha(255);
    		b.setAlpha(80*level[newSquare]);
    		lastButton = b;
    		timmerDecay = DELAYTIME;
    	}  // End of onTap()
    	
    	
    	static int nexttime = 0;
    	public void run() {
    		if (timmerDecay < 0) timmerDecay = 0;
    		if (timmerDecay > 0) timmerDecay--;
    		if (nexttime == 1)
    		{
    	     int doneDecay = 1;
    		 for (int i=1; i<10; i++)
    		 {
    			ImageButton b = (ImageButton) findViewById(buttonIds[i]);

    			b.setAlpha(255);
                osccon.send("/activity", i, 0);
                if (level[i] > 0) { level[i]--; doneDecay = 0; }
                osccon.send("/activity", i, level[i]);
    			if (level[i] == 0) 
    				{
    					b.setSelected(false);
    					b.setAlpha(255);
    				}
    			else
    					b.setAlpha(80*level[i]);
    		 }
    		 if (doneDecay == 1) nexttime = 0;
    		} else {
    		 if (timmerDecay == 0) nexttime = 1;
    		}
    	} // End of run()
}
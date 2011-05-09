package org.ncmls.soundspace;

import java.net.ConnectException;

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
import android.os.SystemClock;
import android.util.Log;
@SuppressWarnings("unused")

public class soundSpace extends Activity 
implements OnTouchListener, AccelerometerListener {
	
	   private static final String TAG = "soundSpace";
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
	   
	   private static final int LISTENING = 1;
	   private static final int TALKING   = 2;
	   private static int mode = TALKING;
	   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ImageView view = (ImageView) findViewById(R.layout.main);
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
        Log.i(TAG, "end of onCreate");
        } // END of onCreate()
             
        public boolean onTouch(View v, MotionEvent event) {
      	    if (true) return true;
            ImageView view = (ImageView) v;
            CharSequence pos; 

            switch (event.getAction() & 0xFF) {
            case MotionEvent.ACTION_DOWN:

               lastSquare = (int)event.getX();
               pos = new String("("+lastSquare+","+") ");
               osccon.send("/activity", lastSquare, 3);
               
               break;
            case MotionEvent.ACTION_UP:
               osccon.send("/activity", lastSquare, 0);
               break;
               
            case MotionEvent.ACTION_MOVE:

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
   		 if (force > 2.0)
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
    	}
    	
    	public void onTap(ImageButton b)
    	{
    		int newSquare = buttonNumber(b);
    		int oldSquare = buttonNumber(lastButton);
    		if (newSquare == oldSquare)
    		{
    			if (activityLevel < 3) activityLevel++;
                osccon.send("/activity", oldSquare, activityLevel);
    		}
    		else
    		{
    			if(lastButton != null)
    			{
    				lastButton.setSelected(false);
    				lastButton.setAlpha(255);
                    osccon.send("/activity", oldSquare, 0);
    			}
                activityLevel = 1;
                osccon.send("/activity", newSquare, activityLevel);
    		}
    		b.setSelected(true);
    		b.setAlpha(80*activityLevel);
    		lastButton = b;
    	}

}
package org.ncmls.soundspace;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import org.ncmls.soundspace.R;

public class soundSpace extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	/* Debug.startMethodTracing(); */
        ImageButton button  = (ImageButton) findViewById(R.id.imageButton3);
        if (button != null)
        {
        	button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		ImageButton button2  = (ImageButton) findViewById(R.id.imageButton2);
            	button2.setSelected(true);
            	}
        	});	
        }
        
        button  = (ImageButton) findViewById(R.id.imageButton6);
        if (button != null)
        {
        	button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		ImageButton button2  = (ImageButton) findViewById(R.id.imageButton2);
            	button2.setSelected(false);
            	}
        	});	
        }
        
        
        };

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
        
}
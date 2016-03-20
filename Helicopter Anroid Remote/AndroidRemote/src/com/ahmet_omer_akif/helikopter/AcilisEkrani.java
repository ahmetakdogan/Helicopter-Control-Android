package com.ahmet_omer_akif.helikopter;

import android.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.media.MediaPlayer;
import android.os.Bundle;

public class AcilisEkrani extends Activity{
	
	MediaPlayer muzik;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acilis_ekrani);
		muzik = MediaPlayer.create(AcilisEkrani.this,R.raw.yga);
		muzik.start();
        setTitle("Loading.....");
		
		Thread acilis = new Thread(){
			
			public void run() {
				
				try {
					sleep(2000);	
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					Intent intent = new Intent(AcilisEkrani.this,HelikopterActivity.class);
					startActivity(intent);
				}
			}
		};
		acilis.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		muzik.release();
		finish();
	}

}

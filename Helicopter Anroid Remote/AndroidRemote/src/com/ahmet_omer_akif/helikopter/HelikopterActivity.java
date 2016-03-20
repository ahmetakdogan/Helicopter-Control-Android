
package com.ahmet_omer_akif.helikopter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet_omer_akif.helikopter.R;

public class HelikopterActivity extends Activity implements OnClickListener {
	private TextView logview;

	private ImageView forwardArrow, backArrow, rightArrow, leftArrow, stop,connect, deconnect, up, down;
	private BluetoothAdapter mBluetoothAdapter = null;
    private WebView camera;
	
	private String[] logArray = null;

	private BtInterface bt = null;
	
	static final String TAG = "tag";
	static final int REQUEST_ENABLE_BT = 3;
	
	//bluetooth aray�z�nden mesajlar� dinler ve bunlar� kaydeder(log)
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("receivedData");
            addToLog(data);            
        }
    };
    
    //Bluetooth ba�lant�s�n�n durumunu kontrol eder.
    final Handler handlerStatus = new Handler() {
        public void handleMessage(Message msg) {
            int status = msg.arg1;
            if(status == BtInterface.CONNECTED) {
            	addToLog("Helikoptere ba�land�");
            } else if(status == BtInterface.DISCONNECTED) {
            	addToLog("Ba�lant� kesildi");
            }
        }
    };
    
    //sadece son mesajlar g�sterilir 
    private void addToLog(String message){
    	for (int i = 1; i < logArray.length; i++){
        	logArray[i-1] = logArray[i];
        }
        logArray[logArray.length - 1] = message;
        
        logview.setText("");
        for (int i = 0; i < logArray.length; i++){
        	if (logArray[i] != null){
        		logview.append(logArray[i] + "\n");
        	}
        }    	
    }
    
    //activity olu�turuldu�unda �a�r�l�r.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_helikopter);
   
        
 
        // listener atamalar�
        
        logview = (TextView)findViewById(R.id.logview);
        //g�sterilecek son 3 mesaj belirlendi.
        logArray = new String[3];
     
        connect = (ImageView) findViewById(R.id.imageView1);
        connect.setOnClickListener(this);
        deconnect = (ImageView) findViewById(R.id.imageView2);
        deconnect.setOnClickListener(this);
        forwardArrow = (ImageView)findViewById(R.id.forward_arrow);
        forwardArrow.setOnClickListener(this);
        backArrow = (ImageView)findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(this);
        rightArrow = (ImageView)findViewById(R.id.right_arrow);
        rightArrow.setOnClickListener(this);
        leftArrow = (ImageView)findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(this);
        stop = (ImageView)findViewById(R.id.stop);
        stop.setOnClickListener(this);
        up = (ImageView) findViewById(R.id.btnGazVer);
        up.setOnClickListener(this);
        down = (ImageView) findViewById(R.id.btnGazKes);
        down.setOnClickListener(this);
      
    	camera = (WebView)findViewById(R.id.webView1);  // g�r�nt� al�n�r	
    	camera.getSettings().setJavaScriptEnabled(true);
    	
		// Sayfan�n y�klendi�inin anla��lmas� i�in ProgressDialog a��yoruz.
				final ProgressDialog progressDialog = ProgressDialog.show(this, "Kamera",
						"Y�kleniyor...", true);
		 
				camera.setWebViewClient(new WebViewClient() {
		 
					// Sayfa Y�klenirken bir hata olu�ursa kullan�c�y� uyar�yoruz.
					public void onReceivedError(WebView view, int errorCode,
							String description, String failingUrl) {
						Toast.makeText(getApplicationContext(), "G�r�nt� Y�klenemedi!",
								Toast.LENGTH_LONG).show();
					}
		 
					// Sayfan�n y�klenme i�lemi bitti�inde progressDialog'u kapat�yoruz.
					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						if (progressDialog.isShowing())
							progressDialog.dismiss();
					}
				});
		 
				//Web sayfam�z�n url'ini webView'e y�kl�yoruz.
				camera.loadUrl("http://www.google.com");
     
    }   ///// onCreate end
    
   
    @Override
    public void onResume() {
        super.onResume();
        //telefonda bluetooth kontrol� yapar
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	Log.v(TAG, "bluetooth deste�i yok");
        }
		else{
        	if (!mBluetoothAdapter.isEnabled()){
        		//Bluetooth aktif de�ilse istek yollar
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}
        	else{
        		//Bluetooth aktif edildi daha sonra t�m bluetooth ba�lant�laru i�in nesnesi ba�lat�ld�.
        		bt = new BtInterface(handlerStatus, handler);
            }
        }
    }
    
    //bluetooth ba�lant�s� aktif de�ilse aktif etmek i�in �a�r�l�r.
	protected void onActivityResult(int requestCode, int resultCode, Intent moreData){
    	if (requestCode == REQUEST_ENABLE_BT){
    		if (resultCode == Activity.RESULT_OK){
    			//Bluetooth aktif edildi daha sonra t�m bluetooth ba�lant�laru i�in nesnesi ba�lat�ld�.
    			bt = new BtInterface(handlerStatus, handler);
    		}
    		else if (resultCode == Activity.RESULT_CANCELED)
    			Log.v(TAG, "BT aktif de�il");
    		else
    			Log.v(TAG, " sonu� yok");
    	}
    	else{
    		Log.v(TAG, "bilinmiyor");    	
    	}
     }
	
	// butonlar�n click edilmesi
	
	@Override
	public void onClick(View v) {
		if(v == connect) {
			addToLog("Helikoptere ba�lan�l�yor...");
			bt.connect();			
		} 
		else if(v == deconnect) {
			addToLog("Ba�lant� kesiliyor...");
			bt.close();			
		}
		else if(v == forwardArrow) {
			addToLog("ileri");
			bt.sendData("F");
		}
		else if(v == backArrow) {
			addToLog("geri");
			bt.sendData("B");
		}
		else if(v == rightArrow) {
			addToLog("sa�a");
			bt.sendData("R");
		}
		else if(v == leftArrow) {
			addToLog("sola");
			bt.sendData("L");
		}
		else if(v == up) {
			addToLog("yukar�");
			bt.sendData("G");
		}
		
		else if(v == down) {
			addToLog("a�a��");
			bt.sendData("K");
		}
		else if(v == stop) {
			addToLog("dur");
			bt.sendData("S");
		}

	}
	
}

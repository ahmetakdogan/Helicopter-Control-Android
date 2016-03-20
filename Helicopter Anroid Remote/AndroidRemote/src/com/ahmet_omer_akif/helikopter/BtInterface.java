
package com.ahmet_omer_akif.helikopter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BtInterface {
	
	// Bluetooth nesneleri
	private BluetoothDevice device = null;
	private BluetoothSocket socket = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private InputStream receiveStream = null;
	private BufferedReader receiveReader = null;
	private OutputStream sendStream = null;	
	
	// gelen mesajlarý dinleyecek thread
	private ReceiverThread receiverThread;

	// main activity ile haberleþme
	Handler handlerStatus, handlerMessage;
	
	public static int CONNECTED = 1;
	public static int DISCONNECTED = 2;
	static final String TAG = "tag";	

	public BtInterface(Handler hstatus, Handler h) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		handlerStatus = hstatus;
		handlerMessage = h;		
	}
	
	//main activityden çaðrýldýðý zaman cihaza baðlantýyý setler
	public void connect() {
		
		Set<BluetoothDevice> setpairedDevices = mBluetoothAdapter.getBondedDevices();
    	BluetoothDevice[] pairedDevices = (BluetoothDevice[]) setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);
	
		boolean found = false;
		for(int i=0;i<pairedDevices.length;i++) {
			if(pairedDevices[i].getName().contains("HC-05")) {
				device = pairedDevices[i];
				try {
					// seri haberleþme için standart olan string
					socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					receiveStream = socket.getInputStream();
					receiveReader = new BufferedReader(new InputStreamReader(receiveStream));
					sendStream = socket.getOutputStream();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				found = true;
				break;
			}
		}
		if(found == false){
			Log.v(TAG, " tag yok");
		}
		
		receiverThread = new ReceiverThread(handlerMessage);
		// baðlanmayý deneyeck thread
		new Thread() {
			@Override public void run() {
				try {
				    
					socket.connect();
					Message msg = handlerStatus.obtainMessage();
					msg.arg1 = CONNECTED;
	                handlerStatus.sendMessage(msg);

					
					receiverThread.start();
					
				} 
				catch (IOException e) {
					Log.v("N", "Baðlantý yok : "+e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	// soketin kapatýlýp durumun güncellenmesi
	public void close() {
		try {
			socket.close();
			receiverThread.interrupt();
			
			Message msg = handlerStatus.obtainMessage();
			msg.arg1 = DISCONNECTED;
			handlerStatus.sendMessage(msg);
            
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
		public void sendData(String data) {
			try {
				sendStream.write(data.getBytes());
		        sendStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	private class ReceiverThread extends Thread {
		Handler handler;
		
		ReceiverThread(Handler h) {
			handler = h;
		}
		
		@Override public void run() {
			while(socket != null) {
				if (isInterrupted()){
						try {
							join();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				try {
					if(receiveStream.available() > 0) {
						String dataToSend = ""; 									
						
						dataToSend = receiveReader.readLine();
						if (dataToSend != null){
							Log.v(TAG, dataToSend);
							Message msg = handler.obtainMessage();
							Bundle b = new Bundle();
							b.putString("receivedData", dataToSend);
			                msg.setData(b);
			                handler.sendMessage(msg);
			                dataToSend = "";
						}
						
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}

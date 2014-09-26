
package com.example.proto.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

public class Tools {
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
	
    @SuppressLint("NewApi")
	public static int generateViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }
    
    //byte[]의 특정 위치의 값 가져오기 
  	public static final byte[] getBytes(byte[] src, int offset, int length){
  		byte[] dest = new byte[length];
  		System.arraycopy(src, offset, dest, 0, length);
  		return dest;
  	}	

  	//byte[] to int
  	public static int byteArrayToInt(byte[] src){
  		final int size = Integer.SIZE / 8;
  		ByteBuffer buff = ByteBuffer.allocate(size);
  		final byte[] newBytes = new byte[size];
  		for (int i = 0; i < size; i++) {
  			if (i + src.length < size) {
  				newBytes[i] = (byte) 0x00;
  			} else {
  				newBytes[i] = src[i + src.length - size];
  			}
  		}
  		buff = ByteBuffer.wrap(newBytes);
  		buff.order(ByteOrder.LITTLE_ENDIAN); // Endian에 맞게 세팅
  		return buff.getInt();
  	}
  	
  	//byte[] to String
  	public static String byteArrayToString(byte[] src){
  		try {
  			return new String(src, "UTF-8");
  		} catch (UnsupportedEncodingException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return null;
  	}
}

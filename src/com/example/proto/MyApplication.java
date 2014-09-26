/** 
 * <PRE>
 * Comment : <br>
 * @author : drugghanzi@funnc.com
 * @version 
 * @see 
 * </PRE>
*/
package com.example.proto;

import android.app.Application;

import com.example.proto.util.Prefs;

public class MyApplication extends Application{
	
	@Override
	public void onCreate(){
		super.onCreate();
		Prefs.initPrefs(this);  // jkoh 이전 코드와 어떤 차이?
	}
}

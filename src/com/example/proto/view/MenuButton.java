
package com.example.proto.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MenuButton extends Button{
	
	private String name;
	private String code;
	private long distintId;

	public MenuButton(Context context) {
        super(context);       
    }
   
    public MenuButton(Context context, AttributeSet attrs) {
        this(context,attrs,0);        
    }    
   
    public MenuButton(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);       
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the distintId
	 */
	public long getDistintId() {
		return distintId;
	}

	/**
	 * @param distintId the distintId to set
	 */
	public void setDistintId(long distintId) {
		this.distintId = distintId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	

}

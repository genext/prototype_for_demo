
package com.example.proto.dao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AdJSON extends ExpiredCheckableObject{
	@SerializedName("ad")
	private List<Ad> ads;	
	
	/**
	 * @return the ads
	 */
	public List<Ad> getAds() {
		return ads;
	}

	/**
	 * @param ads the ads to set
	 */
	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}

	public class Ad{
		public String name;
		public String src;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the src
		 */
		public String getSrc() {
			return src;
		}
		/**
		 * @param src the src to set
		 */
		public void setSrc(String src) {
			this.src = src;
		}
		
	}
}

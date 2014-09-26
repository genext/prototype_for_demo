
package com.example.proto.dao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProgramInfoJSON extends ExpiredCheckableObject{
	
	private String staff;
	private String stereo;
	
	@SerializedName("detail")
	private List<Detail> details;		

	/**
	 * @return the staff
	 */
	public String getStaff() {
		return staff;
	}

	/**
	 * @param staff the staff to set
	 */
	public void setStaff(String staff) {
		this.staff = staff;
	}

	/**
	 * @return the stereo
	 */
	public String getStereo() {
		return stereo;
	}

	/**
	 * @param stereo the stereo to set
	 */
	public void setStereo(String stereo) {
		this.stereo = stereo;
	}

	/**
	 * @return the details
	 */
	public List<Detail> getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(List<Detail> details) {
		this.details = details;
	}

	public class Detail{
		private String title;
		private long id;
		private String duration;
		private int price;
		private String channel;
		private String explanation;

		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
/**
		 * @param no the no to set
		 */
/*		public void setNo(int no) {
			this.no = no;
		}		
*/		/**
		 * @return the id
		 */
		public long getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(long id) {
			this.id = id;
		}
		/**
		 * @return the duration
		 */
		public String getDuration() {
			return duration;
		}
		/**
		 * @param duration the duration to set
		 */
		public void setDuration(String duration) {
			this.duration = duration;
		}
		/**
		 * @return the price
		 */
		public int getPrice() {
			return price;
		}
		/**
		 * @param price the price to set
		 */
		public void setPrice(int price) {
			this.price = price;
		}
		/**
		 * @return the channel
		 */
		public String getChannel() {
			return channel;
		}
		/**
		 * @param channel the channel to set
		 */
		public void setChannel(String channel) {
			this.channel = channel;
		}
		/**
		 * @return the explanation
		 */
		public String getExplanation() {
			return explanation;
		}
		/**
		 * @param explanation the explanation to set
		 */
		public void setExplanation(String explanation) {
			this.explanation = explanation;
		}
		
		
	}
}


package com.example.proto.dao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProgramJSON extends ExpiredCheckableObject{
	
	@SerializedName("program")
	private List<Program> programs;	
	
	/**
	 * @return the programs
	 */
	public List<Program> getPrograms() {
		return programs;
	}
	/**
	 * @param programs the programs to set
	 */
	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}

	public class Program{
		
		private String name;
		private String code;
		private String up;
		private String grade;
		private String imgSrc;
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
		/**
		 * @return the up
		 */
		public String getUp() {
			return up;
		}
		/**
		 * @param up the up to set
		 */
		public void setUp(String up) {
			this.up = up;
		}
		/**
		 * @return the grade
		 */
		public String getGrade() {
			return grade;
		}
		/**
		 * @param grade the grade to set
		 */
		public void setGrade(String grade) {
			this.grade = grade;
		}
		/**
		 * @return the imgSrc
		 */
		public String getImgSrc() {
			return imgSrc;
		}
		/**
		 * @param imgSrc the imgSrc to set
		 */
		public void setImgSrc(String imgSrc) {
			this.imgSrc = imgSrc;
		}
		
		
	}
}

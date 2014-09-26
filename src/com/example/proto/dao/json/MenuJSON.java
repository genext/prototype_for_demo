
package com.example.proto.dao.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MenuJSON extends ExpiredCheckableObject{
	private String version;	
	
	@SerializedName("menu")
	private List<Menu> menus;	
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the menus
	 */
	public List<Menu> getMenus() {
		return menus;
	}

	/**
	 * @param menus the menus to set
	 */
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public class Menu{
		
		@SerializedName("menu_1st")
		private String firstMenuName;
		
		@SerializedName("menu_2nd")
		private List<SecondMenu> secondMenu;	
		
		/**
		 * @return the firstMenuName
		 */
		public String getFirstMenuName() {
			return firstMenuName;
		}

		/**
		 * @param firstMenuName the firstMenuName to set
		 */
		public void setFirstMenuName(String firstMenuName) {
			this.firstMenuName = firstMenuName;
		}

		/**
		 * @return the secondMenu
		 */
		public List<SecondMenu> getSecondMenu() {
			return secondMenu;
		}

		/**
		 * @param secondMenu the secondMenu to set
		 */
		public void setSecondMenu(List<SecondMenu> secondMenu) {
			this.secondMenu = secondMenu;
		}

		public class SecondMenu{
			private String name;
			private String code;
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
			
			
		}
	}
	
	
}

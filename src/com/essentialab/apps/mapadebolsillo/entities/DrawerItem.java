package com.essentialab.apps.mapadebolsillo.entities;


public class DrawerItem {
	public int iconId_pressed;
	public int iconId_unpressed;
	public String title;
	
	public DrawerItem(int iconId_pressed, int iconId_unpressed, String title) {
		super();
		this.iconId_pressed = iconId_pressed;
		this.iconId_unpressed = iconId_unpressed;
		this.title = title;
	}
}

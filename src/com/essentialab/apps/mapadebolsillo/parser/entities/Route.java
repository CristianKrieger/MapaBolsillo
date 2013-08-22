package com.essentialab.apps.mapadebolsillo.parser.entities;

public class Route {
	public String agency_id;
	public String route_short_name;
	public String route_long_name;
	public String route_desc;
	public String route_type;
	public String route_url;
	public String route_color;
	public String route_text_color;
	public String route_bikes_allowed;
	public String route_id;
	public Stop[] stops;
	
	public Route(){
		
	}
	
	public Route(String agency_id, String route_short_name,
			String route_long_name, String route_desc, String route_type,
			String route_url, String route_color, String route_text_color,
			String route_bikes_allowed, String route_id, Stop[] stops) {
		super();
		this.agency_id = agency_id;
		this.route_short_name = route_short_name;
		this.route_long_name = route_long_name;
		this.route_desc = route_desc;
		this.route_type = route_type;
		this.route_url = route_url;
		this.route_color = route_color;
		this.route_text_color = route_text_color;
		this.route_bikes_allowed = route_bikes_allowed;
		this.route_id = route_id;
		this.stops = stops;
	}
}

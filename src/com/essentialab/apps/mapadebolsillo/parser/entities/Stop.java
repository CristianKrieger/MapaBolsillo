package com.essentialab.apps.mapadebolsillo.parser.entities;

public class Stop {
	public String stop_id;
	public String stop_code;
	public String stop_name;
	public String stop_desc;
	public String stop_lat;
	public String stop_lon;
	public String zone_id;
	public String stop_url;
	public String location_type;
	public String parent_station;
	public String wheelchair_boarding;
	public String stop_direction;
	public String route_id;
	public String to_stop_id;
	
	public Stop(String stop_id, String stop_code, String stop_name,
			String stop_desc, String stop_lat, String stop_lon, String zone_id,
			String stop_url, String location_type, String parent_station,
			String wheelchair_boarding, String stop_direction, String route_id,
			String to_stop_id) {
		super();
		this.stop_id = stop_id;
		this.stop_code = stop_code;
		this.stop_name = stop_name;
		this.stop_desc = stop_desc;
		this.stop_lat = stop_lat;
		this.stop_lon = stop_lon;
		this.zone_id = zone_id;
		this.stop_url = stop_url;
		this.location_type = location_type;
		this.parent_station = parent_station;
		this.wheelchair_boarding = wheelchair_boarding;
		this.stop_direction = stop_direction;
		this.route_id = route_id;
		this.to_stop_id = to_stop_id;
	}
}

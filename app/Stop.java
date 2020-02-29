package application;

public class Stop implements Comparable<Stop> {

	private int id;
	private double lon;
	private double lat;
	private String name;

	public Stop(int id, double lon, double lat, String name) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		return "Stop: id = " + id + " name = " + name;
	}

	@Override
	public int compareTo(Stop o) {
		return this.name.compareTo(o.name);
	}

}

package application;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

	private static ArrayList<Stop> stops;
	private static Connection conn;
	private static final String QUERY_CHECK_ROUTE = "SELECT DISTINCT name FROM routes WHERE id IN (SELECT s1.route_id FROM"
			+ "(SELECT * FROM mapping WHERE stop_id = %d) s1 JOIN" + "(SELECT * FROM mapping WHERE stop_id = %d) s2 ON s1.route_id = s2.route_id)";

	private static final String QUERY_LOAD_STOPS = "SELECT * FROM stops";
	private static final String QUERY_MAX_MAPPING = "SELECT MAX(route_id) FROM mapping";
	private static final String QUERY_MAX_ROUTES = "SELECT MAX(id) FROM routes";
	private static final String QUERY_INSERT_ID_ID = "INSERT INTO mapping VALUES (%d, %d)";
	private static final String QUERY_INSERT_ID_STRING = "INSERT INTO routes VALUES (%d, '%s')";

	private static DatabaseConnector instance;

	/**
	 * Private constructor for singleton design pattern.
	 */
	private DatabaseConnector() {

	}

	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
			instance.connect();
		}
		return instance;
	}

	private void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/softarch", "root", "test");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets stop by id in the form of "name id"
	 * 
	 * @param name
	 * @return
	 */
	public Stop getStopById(String name) {
		if (stops == null) {
			loadAllStops();
		}
		int id = Integer.valueOf(name.substring(name.lastIndexOf(" ") + 1));
		for (Stop s : stops) {
			if (s.getId() == id) {
				return s;
			}
		}
		return null;
	}

	public Stop getStopById(int id) {
		if (stops == null) {
			loadAllStops();
		}
		for (Stop s : stops) {
			if (s.getId() == id) {
				return s;
			}
		}
		return null;
	}

	public Stop getStopByName(String name) {
		if (stops == null) {
			loadAllStops();
		}
		for (Stop s : stops) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	public ArrayList<Integer> loadStopIds() {
		if (stops == null) {
			loadAllStops();
		}
		ArrayList<Integer> res = new ArrayList<>();
		for (Stop s : stops) {
			res.add(s.getId());
		}
		return res;
	}

	public ArrayList<String> loadStopNames() {
		if (stops == null) {
			loadAllStops();
		}
		ArrayList<String> res = new ArrayList<>();
		for (Stop s : stops) {
			res.add(s.getName());
		}
		return res;
	}

	public ArrayList<String> checkRoute(Stop s1, Stop s2) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format(QUERY_CHECK_ROUTE, s1.getId(), s2.getId()));
			rs.first();
			while (!rs.isAfterLast()) {
				String name = rs.getString(1);
				result.add(name);
				rs.next();
			}
		} catch (Exception e) {
		}
		return result;
	}

	public ArrayList<Stop> loadAllStops() {
		if (stops != null) {
			return stops;
		}
		stops = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY_LOAD_STOPS);
			rs.first();
			while (!rs.isAfterLast()) {
				int id = rs.getInt(1);
				double lon = rs.getDouble(2);
				double lat = rs.getDouble(3);
				String name = rs.getString(4);
				if (!name.equals("")) {
					stops.add(new Stop(id, lon, lat, name));
				}
				rs.next();
			}
		} catch (Exception e) {
		}
		return stops;
	}

	public void insertRoute(String name, ArrayList<Stop> routeStops) {
		try {
			ResultSet rs1 = conn.createStatement().executeQuery(QUERY_MAX_MAPPING);
			ResultSet rs2 = conn.createStatement().executeQuery(QUERY_MAX_ROUTES);
			rs1.first();
			rs2.first();
			int maxRouteId = Math.max(rs1.getInt(1), rs2.getInt(1));
			rs1.close();
			rs2.close();
			int newId = maxRouteId + 1;
			for (Stop s : routeStops) {
				conn.createStatement().execute(String.format(QUERY_INSERT_ID_ID, newId, s.getId()));
			}
			StringBuilder sb = new StringBuilder(name);
			sb.append(": ");
			sb.append(routeStops.get(0).getName());
			for (int i = 1; i < routeStops.size(); i++) {
				sb.append(" => ");
				sb.append(routeStops.get(i).getName());
			}
			conn.createStatement().execute(String.format(QUERY_INSERT_ID_STRING, newId, sb));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

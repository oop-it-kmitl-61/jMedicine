package core;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Gets latitude and longitude by a public IP address.
 */
public class LocationHelper {

  public static double[] getLocation() {
    double[] result = null;

    //Get IP Address
    String systemipaddress = "";
    try {
      URL url_name = new URL("http://bot.whatismyipaddress.com");
      BufferedReader sc =
          new BufferedReader(new InputStreamReader(url_name.openStream()));
      systemipaddress = sc.readLine().trim();
    }
    catch (Exception e) {
      systemipaddress = "Cannot Execute Properly";
    }

    //Get lat, lng
    File database = new File("src/core/mapdb/GeoLite2-City.mmdb");
    DatabaseReader reader = null;
    try {
      reader = new DatabaseReader.Builder(database).build();
    } catch (IOException e) {
      e.printStackTrace();
    }

    InetAddress ipAddress = null;
    try {
      ipAddress = InetAddress.getByName(systemipaddress);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    CityResponse response = null;
    try {
      response = reader.city(ipAddress);
      Location location = response.getLocation();
      result = new double[] {location.getLatitude(), location.getLongitude()};
    } catch (IOException e) {
      e.printStackTrace();
    } catch (GeoIp2Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}

import com.teamdev.jxmaps.GeocoderCallback;
import com.teamdev.jxmaps.GeocoderRequest;
import com.teamdev.jxmaps.GeocoderResult;
import com.teamdev.jxmaps.GeocoderStatus;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapViewOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.swing.MapView;

public class Maps extends MapView {
  public Maps(MapViewOptions options) {
    super(options);
    setOnMapReadyHandler(new MapReadyHandler() {
      @Override
      public void onMapReady(MapStatus status) {
        if (status == MapStatus.MAP_STATUS_OK) {
          final Map map = getMap();
          map.setZoom(5.0);
          GeocoderRequest request = new GeocoderRequest(map);
          request.setAddress("Bangkok, TH");

          getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {
            @Override
            public void onComplete(GeocoderResult[] result, GeocoderStatus status) {
              if (status == GeocoderStatus.OK) {
//                map.setCenter(result[0].getGeometry().getLocation());
//                Marker marker = new Marker(map);
//                marker.setPosition(result[0].getGeometry().getLocation());
//
//                final InfoWindow window = new InfoWindow(map);
//                window.setContent("Hello, World!");
//                window.open(map, marker);
              }
            }
          });
        }
      }
    });
  }
}

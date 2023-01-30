package view;

import javafx.scene.layout.Pane;
import model.input.OnlineWeather;
import org.controlsfx.control.WorldMapView;

public class MapPane {
    private Pane pane;
    private final OnlineWeather onlineWeather;
    private WorldMapView worldMapView;

    public MapPane(OnlineWeather onlineWeather) {
        this.onlineWeather = onlineWeather;
        this.worldMapView = new WorldMapView();
        this.pane = new Pane(worldMapView);
    }

    public Pane getPane() {
        return pane;
    }

    public void updatePane() {

    }

}

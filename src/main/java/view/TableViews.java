package view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.object.Datum;

public class TableViews {
    public TableView<Datum> getCurrentWeathers(ObservableList<Datum> data) {
        TableView<Datum> tableView = new TableView<>();

        TableColumn<Datum, String> n = new TableColumn<>("City");
        n.setCellValueFactory(new PropertyValueFactory<>("cityName"));

        TableColumn<Datum, Double> t = new TableColumn<>("Temperature");
        t.setCellValueFactory(new PropertyValueFactory<>("temp"));

        TableColumn<Datum, Double> w = new TableColumn<>("Wind Speed");
        w.setCellValueFactory(new PropertyValueFactory<>("windSpd"));

        TableColumn<Datum, Double> wd = new TableColumn<>("Wind Direction");
        wd.setCellValueFactory(new PropertyValueFactory<>("windDir"));

        TableColumn<Datum, Double> c = new TableColumn<>("Clouds");
        c.setCellValueFactory(new PropertyValueFactory<>("clouds"));

        TableColumn<Datum, Double> p = new TableColumn<>("Precipitation");
        p.setCellValueFactory(new PropertyValueFactory<>("precip"));

        TableColumn<Datum, Double> a = new TableColumn<>("Air Quality");
        a.setCellValueFactory(new PropertyValueFactory<>("aqi"));

        tableView.getColumns().setAll(n,t,w,wd,c,p,a);
        tableView.setItems(data);
        tableView.setPrefWidth(1015);
        tableView.setPrefHeight(800);
        return tableView;
    }
}

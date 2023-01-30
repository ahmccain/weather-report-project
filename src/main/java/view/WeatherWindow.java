package view;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.ModelFacade;
import model.object.City;
import org.controlsfx.control.WorldMapView;
import org.controlsfx.control.textfield.TextFields;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WeatherWindow {
    private final ModelFacade modelFacade;
    private final Scene scene;
    private final Pane pane;
    private final WorldMapView worldMapView;
    private final TableViews tableViews;
    private final ScrollPane searchedCityPane;
    private final ScrollPane searchedCitiesPane;
    private final Pane mapPane;
    private boolean showMap;

    public WeatherWindow(ModelFacade modelFacade) {
        this.modelFacade = modelFacade;
        this.worldMapView = new WorldMapView();
        this.mapPane = new Pane(worldMapView);
        this.tableViews = new TableViews();
        this.showMap = true;
        this.searchedCityPane = new ScrollPane();
        this.searchedCitiesPane = new ScrollPane();
        searchedCityPane.setPrefWidth(400);
        VBox vBox = new VBox(buildSearchAndButtons(), mapPane);

        HBox hBox = new HBox(vBox, searchedCityPane);
        this.pane = new Pane(hBox);
        this.scene = new Scene(pane);
        buildKeyListeners();
    }

    /**
     * Builds the top left grid of search bars and buttons. Calls on model functionality.
     *
     * @return The grid of search bars and buttons.
     */
    public Node buildSearchAndButtons() {
        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(30, 30, 0, 30));

        TextField cityTextField = new TextField();
        TextFields.bindAutoCompletion(
                cityTextField,
                modelFacade.getCities()
        );

        ChoiceBox<City> removeCityChoiceBox = new ChoiceBox<>();
        removeCityChoiceBox.setPrefWidth(620);
        Button weatherButton = new Button("View Weather");
        weatherButton.setPrefWidth(120);
        weatherButton.setOnAction((event -> {
            String text = cityTextField.getText();
            Text t = new Text();

            try {
                if (modelFacade.inCache(text)) {
                    Alert alert = new Alert(Alert.AlertType.NONE, "Cache data found. Do you want to use cache?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            t.setText(modelFacade.getWeatherCache(true).toString());
                        }
                        if (response == ButtonType.NO) {
                            t.setText(modelFacade.getWeatherCache(false).toString());
                            updateIcon();
                        }
                    });
                } else {
                    t.setText(modelFacade.getWeatherNoCache().toString());
                    updateIcon();
                }
                if (modelFacade.tempDiffBiggerThanGap()) {
                    createAlert("This temperature is really different", Alert.AlertType.INFORMATION);
                }
                searchedCityPane.setContent(t);
                worldMapView.getLocations().add(new WorldMapView.Location(modelFacade.getSearchedCity().getLocation().lat(), modelFacade.getSearchedCity().getLocation().lon()));
                removeCityChoiceBox.getItems().setAll(modelFacade.getSearchedCities());
                cityTextField.clear();
                if (!showMap) {
                    searchedCitiesPane.setContent(tableViews.getCurrentWeathers(FXCollections.observableList(modelFacade.getSearchedCitiesWeather())));
                    this.mapPane.getChildren().add(searchedCitiesPane);
                }
            } catch (NumberFormatException e) {
                createAlert("Select an auto-completed city", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                createAlert("Null or empty string entered", Alert.AlertType.ERROR);
            } catch (IllegalStateException e) {
                createAlert(e.getMessage(), Alert.AlertType.ERROR);
            }
        }));

        Button allWeatherButton = new Button("View All Weather");
        allWeatherButton.setPrefWidth(120);
        allWeatherButton.setOnAction(event -> {
            this.mapPane.getChildren().removeAll();

            if (!showMap) {
                allWeatherButton.setText("View All Weather");
                this.mapPane.getChildren().setAll(worldMapView);
                showMap = true;
            } else {
                allWeatherButton.setText("View Map");
                searchedCitiesPane.setContent(tableViews.getCurrentWeathers(FXCollections.observableList(modelFacade.getSearchedCitiesWeather())));
                this.mapPane.getChildren().add(searchedCitiesPane);
                showMap = false;
            }
        });

        Button removeButton = new Button("Remove City Data");
        removeButton.setPrefWidth(120);
        removeButton.setOnAction(event -> {
            if (removeCityChoiceBox.getValue() == null) {
                createAlert("No city selected", Alert.AlertType.ERROR);
            }
            String text = removeCityChoiceBox.getValue().toString();
            modelFacade.removeCityFromSearch(text);
            if (!showMap) {
                searchedCitiesPane.setContent(tableViews.getCurrentWeathers(FXCollections.observableList(modelFacade.getSearchedCitiesWeather())));
                this.mapPane.getChildren().add(searchedCitiesPane);
            }
            worldMapView.getLocations().removeIf(location -> (location.getLatitude() == modelFacade.getRemovedCity().getLocation().lat() && location.getLongitude() == modelFacade.getRemovedCity().getLocation().lon()));
            removeCityChoiceBox.getItems().setAll(modelFacade.getSearchedCities());
            removeCityChoiceBox.setValue(null);
        });

        Button clearButton = new Button("Clear All Data");
        clearButton.setPrefWidth(120);
        clearButton.setOnAction(event -> {
            modelFacade.clear();
            worldMapView.getLocations().clear();
            if (!showMap) {
                searchedCitiesPane.setContent(tableViews.getCurrentWeathers(FXCollections.observableList(modelFacade.getSearchedCitiesWeather())));
                this.mapPane.getChildren().add(searchedCitiesPane);
            }
            removeCityChoiceBox.getItems().setAll(modelFacade.getSearchedCities());
            removeCityChoiceBox.setValue(null);
        });

        Button reportButton = new Button("Send Email Report");
        reportButton.setPrefWidth(120);
        reportButton.setOnAction(event -> sendReport());

        Button clearCacheButton = new Button("Clear Cache");
        clearCacheButton.setPrefWidth(120);
        clearCacheButton.setOnAction(event -> modelFacade.clearCache());

        TextField tempGapField = new TextField();
        tempGapField.setPromptText("Enter number between 2 and 10");
        Button tempGapButton = new Button("Update Gap");
        tempGapButton.setPrefWidth(120);
        tempGapButton.setOnAction(event -> {
            try {
                modelFacade.setTempGap(tempGapField.getText());
            } catch (NumberFormatException e) {
                createAlert("Must be a whole number", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                createAlert("Value must be between 2 and 10, inclusive", Alert.AlertType.ERROR);
            }
        });

        grid.add(new Label("Search city"), 0, 0);
        grid.add(new Label("Remove city"), 0, 1);
        grid.add(new Label("Temperature Gap"), 0, 3);
        grid.add(cityTextField, 1, 0);
        grid.add(removeCityChoiceBox, 1, 1);
        grid.add(tempGapField, 1, 3);
        grid.add(weatherButton, 2, 0);
        grid.add(removeButton, 2, 1);
        grid.add(reportButton, 2, 2);
        grid.add(tempGapButton, 2, 3);
        grid.add(allWeatherButton, 3, 0);
        grid.add(clearButton, 3, 1);
        grid.add(clearCacheButton, 3, 2);
        GridPane.setHgrow(cityTextField, Priority.ALWAYS);
        GridPane.setHgrow(removeCityChoiceBox, Priority.ALWAYS);
        root.setTop(grid);
        return grid;
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * Triggered when user clicks send report. Follows up with email entering.
     */
    private void sendReport() {
        TextInputDialog emailInput = new TextInputDialog();
        emailInput.setHeaderText("Enter email");
        if (emailInput.showAndWait().isPresent()) {
            String email = emailInput.getResult();
            if (modelFacade.sendReport(email)) {
                createAlert("Email sent to " + email + "\nBe sure to check your junk/spam folder", Alert.AlertType.INFORMATION);
            } else {
                createAlert("Does not contain a valid address", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Updates the world map to display weather icon at city location.
     */
    private void updateIcon() {
        String iconString = modelFacade.getSearchedCityWeather().getWeather().getIcon();
        try {
            Image image = new Image(new FileInputStream("src/main/resources/icons/" + iconString + ".png"));
            worldMapView.setLocationViewFactory(location -> {
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(30);
                imageView.setFitWidth(30);
                imageView.getStyleClass().add("location");
                imageView.setTranslateX(-4);
                imageView.setTranslateY(-4);
                return imageView;
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    /**
     * Builds the screenshot functionality.
     */
    private void buildKeyListeners() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                WritableImage image = pane.snapshot(null, null);
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                File file = fileChooser.showSaveDialog(scene.getWindow());
                if (file != null) {
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image,
                                null), "png", file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
    }
}

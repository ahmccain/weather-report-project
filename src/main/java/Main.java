import javafx.application.Application;
import javafx.stage.Stage;
import model.ModelFacade;
import model.ModelFacadeImpl;
import model.input.OfflineWeather;
import model.input.OnlineWeather;
import model.input.WeatherManagerImpl;
import model.input.WeatherStrategy;
import model.input.cache.OfflineWeatherCache;
import model.input.cache.OnlineWeatherCache;
import model.input.cache.WeatherCache;
import model.output.EmailManagerImpl;
import model.output.EmailStrategy;
import model.output.OfflineEmail;
import model.output.OnlineEmail;
import view.WeatherWindow;

import java.util.Random;

public class Main extends Application {
    private static boolean onlineInput;
    private static boolean onlineOutput;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Two arguments needed");
            return;
        }
        onlineInput = args[0].equals("online");
        onlineOutput = args[1].equals("online");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        WeatherStrategy weatherStrategy;
        EmailStrategy emailStrategy;
        WeatherCache cache;
        if (onlineInput) {
            weatherStrategy = new OnlineWeather();
            cache = new OnlineWeatherCache();
        } else {
            weatherStrategy = new OfflineWeather();
            cache = new OfflineWeatherCache();
        }
        if (onlineOutput) {
            emailStrategy = new OnlineEmail();
        } else {
            emailStrategy = new OfflineEmail();
        }
        WeatherManagerImpl weatherManager = new WeatherManagerImpl(weatherStrategy, cache);
        EmailManagerImpl emailManager = new EmailManagerImpl(emailStrategy, new Random());
        ModelFacade modelFacade = new ModelFacadeImpl(weatherManager, emailManager);
        WeatherWindow weatherWindow = new WeatherWindow(modelFacade);
        primaryStage.setScene(weatherWindow.getScene());
        primaryStage.setTitle("Weather application");
        primaryStage.show();

    }
}

package model.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentWeather {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("minutely")
    @Expose
    private List<Object> minutely = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public CurrentWeather(List<Datum> data) {
        this.data = data;
    }

    public List<Datum> getData() {
        return data;
    }
}
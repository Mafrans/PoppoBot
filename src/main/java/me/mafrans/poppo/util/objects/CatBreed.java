package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.web.HTTPUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CatBreed {
    private static List<CatBreed> allBreeds;
    private String id;
    private String name;
    private String url;
    private String[] temperament;
    private String description;
    private String origin;
    private int[] lifespan;
    private double[] weight;

    public CatBreed() {
    }

    public CatBreed(String id, String name, String url, String[] temperament, String description, String origin, int[] lifespan, double[] weight) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.temperament = temperament;
        this.description = description;
        this.origin = origin;
        this.lifespan = lifespan;
        this.weight = weight;
    }

    public static List<CatBreed> getAllBreeds() {
        return allBreeds;
    }

    public static void setAllBreeds(List<CatBreed> allBreeds) {
        CatBreed.allBreeds = allBreeds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getTemperament() {
        return temperament;
    }

    public void setTemperament(String[] temperament) {
        this.temperament = temperament;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin() {
        return origin.replace("United States", "The United States");
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int[] getLifespan() {
        return lifespan;
    }

    public void setLifespan(int[] lifespan) {
        this.lifespan = lifespan;
    }

    public double[] getWeight() {
        return weight;
    }

    public void setWeight(double[] weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CatBreed getBreed(String id) {
        for(CatBreed catBreed : getAllBreeds()) {
            if(catBreed.getId().equals(id)) {
                return catBreed;
            }
        }
        return null;
    }

    public static List<CatBreed> getBreedsByName(String name) {
        List<CatBreed> catBreeds = new ArrayList<>();
        for(CatBreed catBreed : getAllBreeds()) {
            if(catBreed.getName().toLowerCase().startsWith(name.toLowerCase())) {
                catBreeds.add(catBreed);
            }
        }
        return catBreeds;
    }

    public static List<CatBreed> cacheBreeds() throws IOException {
        JSONArray jsonArray = new JSONArray(HTTPUtil.GET("https://api.thecatapi.com/v1/breeds", new HashMap<>()).replace(" ", ""));
        allBreeds = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CatBreed catBreed = new CatBreed();
            catBreed.setId(jsonObject.getString("id"));
            catBreed.setName(jsonObject.getString("name"));
            catBreed.setDescription(jsonObject.getString("description"));
            if(jsonObject.get("wikipedia_url") instanceof String) {
                catBreed.setUrl(jsonObject.getString("wikipedia_url"));
            }
            catBreed.setTemperament(jsonObject.getString("temperament").split(", "));
            catBreed.setOrigin(jsonObject.getString("origin"));

            int[] lifeSpan = new int[] {
                    Integer.parseInt(jsonObject.getString("life_span").replace(" ", "").split("-")[0]),
                    Integer.parseInt(jsonObject.getString("life_span").replace(" ", "").split("-")[1])
            };
            catBreed.setLifespan(lifeSpan);

            if(jsonObject.has("weight_imperial")) {
                int[] weightImperial = new int[]{
                        Integer.parseInt(jsonObject.getString("weight_imperial").replace(" ", "").split("-")[0]),
                        Integer.parseInt(jsonObject.getString("weight_imperial").replace(" ", "").split("-")[1])
                };
                UnitConverter toKilos = NonSI.POUND.getConverterTo(SI.KILOGRAM);
                double minWeight = toKilos.convert(Measure.valueOf(weightImperial[0], NonSI.POUND).doubleValue(NonSI.POUND));
                double maxWeight = toKilos.convert(Measure.valueOf(weightImperial[1], NonSI.POUND).doubleValue(NonSI.POUND));
                catBreed.setWeight(new double[] {minWeight, maxWeight});
            }

            allBreeds.add(catBreed);
        }

        return allBreeds;
    }
}
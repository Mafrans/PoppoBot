package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.web.HTTPUtil;
import org.apache.commons.lang3.StringUtils;
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

public class DogBreed {
    private static List<DogBreed> allBreeds;
    private int id;
    private String name;
    private String url;
    private String[] temperament;
    private String description;
    private String bredFor;
    private int[] lifespan;
    private String stringLifespan;
    private double[] weight;
    private String stringWeight;

    public DogBreed() {
    }

    public DogBreed(int id, String name, String url, String[] temperament, String description, String bredFor, int[] lifespan, double[] weight) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.temperament = temperament;
        this.description = description;
        this.bredFor = bredFor;
        this.lifespan = lifespan;
        this.weight = weight;
    }

    public String getBredFor() {
        return bredFor;
    }

    public void setBredFor(String bredFor) {
        this.bredFor = bredFor;
    }

    public String[] getTemperament() {
        return temperament;
    }

    public void setTemperament(String[] temperament) {
        this.temperament = temperament;
    }

    public String getStringLifespan() {
        return stringLifespan;
    }

    public void setStringLifespan(String stringLifespan) {
        this.stringLifespan = stringLifespan;
    }

    public String getStringWeight() {
        return stringWeight;
    }

    public void setStringWeight(String stringWeight) {
        this.stringWeight = stringWeight;
    }

    public static List<DogBreed> getAllBreeds() {
        return allBreeds;
    }

    public static void setAllBreeds(List<DogBreed> allBreeds) {
        DogBreed.allBreeds = allBreeds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() throws IOException {
        if(getUrl() == null) return null;
        return HTTPUtil.getWikipediaThumbnail(getUrl().split("/")[4]);
    }

    public static DogBreed getBreed(int id) {
        for(DogBreed catBreed : getAllBreeds()) {
            if(catBreed.getId() == id) {
                return catBreed;
            }
        }
        return null;
    }

    public static List<DogBreed> getBreedsByName(String name) {
        List<DogBreed> dogBreeds = new ArrayList<>();
        for(DogBreed dogBreed : getAllBreeds()) {
            if(dogBreed.getName().toLowerCase().startsWith(name.toLowerCase())) {
                dogBreeds.add(dogBreed);
            }
        }

        if(dogBreeds.size() == 0) {
            for(DogBreed dogBreed : getAllBreeds()) {
                if(dogBreed.getName().split(" ").length > 1) {
                    if (dogBreed.getName().split(" ")[1].toLowerCase().startsWith(name.toLowerCase())){
                        dogBreeds.add(dogBreed);
                    }
                }
            }
        }
        return dogBreeds;
    }

    public static List<DogBreed> cacheBreeds() throws IOException {


        JSONArray jsonArray = new JSONArray(HTTPUtil.GET("https://api.thedogapi.com/v1/breeds", new HashMap<>()).replace(" ", ""));
        allBreeds = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DogBreed dogBreed = new DogBreed();
            dogBreed.setId(jsonObject.getInt("id"));
            dogBreed.setName(StringUtils.join(GUtil.splitTitleCase(jsonObject.getString("name")), " "));

            JSONObject details = HTTPUtil.getJSON("https://api.thedogapi.com/v1/breeds/" + dogBreed.getId(), new HashMap<>());

            String wikiurl = "https://en.wikipedia.org/wiki/" + dogBreed.getName().replace(" ", "_") + "_dog";
            if(HTTPUtil.getResponse(wikiurl) >= 200 && HTTPUtil.getResponse(wikiurl) < 300) {
                dogBreed.setUrl(wikiurl);
                dogBreed.setDescription(HTTPUtil.getWikipediaDescription(wikiurl.split("/")[4]));
            }
            else {
                wikiurl = "https://en.wikipedia.org/wiki/" + dogBreed.getName().replace(" ", "_");
                if(HTTPUtil.getResponse(wikiurl) >= 200 && HTTPUtil.getResponse(wikiurl) < 300) {
                    dogBreed.setUrl(wikiurl);
                    dogBreed.setDescription(HTTPUtil.getWikipediaDescription(wikiurl.split("/")[4]));
                }
                else {
                    if(dogBreed.getName().split(" ").length > 1) {
                        wikiurl = "https://en.wikipedia.org/wiki/" + dogBreed.getName().split(" ")[1];
                        if (HTTPUtil.getResponse(wikiurl) >= 200 && HTTPUtil.getResponse(wikiurl) < 300) {
                            dogBreed.setUrl(wikiurl);
                            dogBreed.setDescription(HTTPUtil.getWikipediaDescription(wikiurl.split("/")[4]));
                        }
                    }
                }
            }
            if(details.has("temperament") && details.get("temperament") instanceof String  && !details.getString("temperament").equalsIgnoreCase("undefined")) {
                dogBreed.setTemperament(details.getString("temperament").split(", "));
            }

            if(details.has("life_span") && details.get("life_span") instanceof String  && !details.getString("life_span").equalsIgnoreCase("undefined")) {
                int[] lifeSpan;
                try {
                    if (details.getString("life_span").replace("years", "").replace(" ", "").split("-").length > 1) {
                        lifeSpan = new int[]{
                                Integer.parseInt(details.getString("life_span").replace("years", "").replace(" ", "").split("-")[0]),
                                Integer.parseInt(details.getString("life_span").replace("years", "").replace(" ", "").split("-")[1])
                        };
                    } else {
                        lifeSpan = new int[]{
                                Integer.parseInt(details.getString("life_span").replace("years", "").replace(" ", "").split("-")[0]),
                                Integer.parseInt(details.getString("life_span").replace("years", "").replace(" ", "").split("–")[0]),
                        };
                    }
                    dogBreed.setLifespan(lifeSpan);
                }
                catch (Exception ex) {
                    dogBreed.setStringLifespan(details.getString("life_span"));
                }
            }

            if(details.has("weight") && details.get("weight") instanceof String  && !details.getString("weight").equalsIgnoreCase("undefined")) {
                double[] weight;
                try {
                    if(details.getString("weight").replace("kgs", "").replace(" ", "").split("-").length > 1) {
                        weight = new double[]{
                                Double.parseDouble(details.getString("weight").replace("kgs", "").replace(" ", "").split("-")[0]),
                                Double.parseDouble(details.getString("life_span").replace("kgs", "").replace(" ", "").split("-")[1])
                        };
                    }
                    else {
                        weight = new double[]{
                                Double.parseDouble(details.getString("weight").replace("kgs", "").replace(" ", "").split("-")[0]),
                                Double.parseDouble(details.getString("life_span").replace("kgs", "").replace(" ", "").split("-")[01])
                        };
                    }
                    dogBreed.setWeight(weight);
                }
                catch (Exception ex) {
                    dogBreed.setStringLifespan(details.getString("life_span"));
                }
            }

            if(details.has("bred_for") && details.get("bred_for") instanceof String && !details.getString("bred_for").equalsIgnoreCase("undefined")) {
                dogBreed.setBredFor(details.getString("bred_for"));
            }

            System.out.println("Initialized " + dogBreed.getName() + " (" + i + "/" + jsonArray.length() + ")");
            allBreeds.add(dogBreed);
        }

        return allBreeds;
    }
}
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DogBreed {
    private static List<DogBreed> allBreeds;
    private String id;
    private String name;
    private String subName;
    private String url;
    private String imageUrl;

    public DogBreed() {
    }

    public DogBreed(String id, String name, String subName, String url, String imageUrl) {
        this.id = id;
        this.name = name;
        this.subName = subName;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
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
    public String getDisplayName() {
        return (subName != null && subName.length() > 1 ? GUtil.capitalize(subName) + " " : "")+ GUtil.capitalize(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public static DogBreed getBreed(String id) {
        for(DogBreed dogBreed : getAllBreeds()) {
            if(dogBreed.getId().equals(id)) {
                return dogBreed;
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
        return dogBreeds;
    }

    public static List<DogBreed> cacheBreeds() throws IOException {
        JSONObject jsonObject = new JSONObject(HTTPUtil.GET("https://dog.ceo/api/breeds/list/all", new HashMap<>())).getJSONObject("message");
        allBreeds = new ArrayList<>();
        int c = 0;
        for(String breed : jsonObject.keySet()) {
            JSONArray subBreeds = jsonObject.getJSONArray(breed);
            c++;

            if(subBreeds.length() > 0) {
                for (int i = 0; i < subBreeds.length(); i++) {
                    DogBreed dogBreed = new DogBreed();
                    dogBreed.setName(breed);
                    dogBreed.setSubName(subBreeds.getString(i));
                    int response = HTTPUtil.getResponse("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getSubName()) + "_" + GUtil.capitalize(dogBreed.getName()));
                    if(response >= 200 && response <= 399) {
                        dogBreed.setUrl("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getSubName()) + "_" + GUtil.capitalize(dogBreed.getName()));
                    }
                    else {
                        response = HTTPUtil.getResponse("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()) + "_dog");
                        if(response >= 200 && response <= 399) {
                            dogBreed.setUrl("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()) + "_dog");
                        }
                        else {
                            response = HTTPUtil.getResponse("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()));
                            if(response >= 200 && response <= 399) {
                                dogBreed.setUrl("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()));
                            }
                        }
                    }
                    dogBreed.setId(dogBreed.getSubName() + "_" + dogBreed.getName());

                    if(dogBreed.getUrl() != null) {
                        dogBreed.setImageUrl(HTTPUtil.getWikipediaThumbnail(dogBreed.getUrl().split("/")[dogBreed.getUrl().split("/").length-1]));
                    }
                    System.out.println("Initialized Dog Breed: " + dogBreed.getDisplayName() + " (" + c + "/" + (jsonObject.keySet().size() + 1) + ")");

                    allBreeds.add(dogBreed);
                }
            }
            else {
                DogBreed dogBreed = new DogBreed();
                dogBreed.setName(breed);
                int response = HTTPUtil.getResponse("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()) + "_dog");
                if(response >= 200 && response <= 399) {
                    dogBreed.setUrl("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()) + "_dog");
                }
                else {
                    response = HTTPUtil.getResponse("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()));
                    if(response >= 200 && response <= 399) {
                        dogBreed.setUrl("https://en.wikipedia.org/wiki/" + GUtil.capitalize(dogBreed.getName()));
                    }
                }
                dogBreed.setId(dogBreed.getName());

                if(dogBreed.getUrl() != null) {
                    dogBreed.setImageUrl(HTTPUtil.getWikipediaThumbnail(dogBreed.getUrl().split("/")[dogBreed.getUrl().split("/").length-1].replace("_", " ")));
                }
                System.out.println("Initialized Dog Breed: " + dogBreed.getDisplayName() + " (" + c + "/" + (jsonObject.keySet().size() + 1) + ")");

                allBreeds.add(dogBreed);
            }

        }

        return allBreeds;
    }

    @Override
    public String toString() {
        return Arrays.toString(new Object[] {
                id,
                name,
                subName,
                url,
                imageUrl
        });
    }
}

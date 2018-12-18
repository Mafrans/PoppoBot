package me.mafrans.poppo.util.objects;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cat {
    private List<CatBreed> breeds;
    private List<CatCategory> categories;
    private String id;
    private String url;

    public Cat() {
    }

    public Cat(List<CatBreed> breeds, List<CatCategory> categories, String id, String url) {
        this.breeds = breeds;
        this.categories = categories;
        this.id = id;
        this.url = url;
    }

    public List<CatBreed> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<CatBreed> breeds) {
        this.breeds = breeds;
    }

    public List<CatCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<CatCategory> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Cat parseCat(JSONObject jsonObject) {
        Cat cat = new Cat();
        cat.setId(jsonObject.getString("id"));
        cat.setUrl(jsonObject.getString("url"));
        List<CatBreed> breeds = new ArrayList<>();
        for (int i = 0; i < jsonObject.getJSONArray("breeds").length(); i++) {
            breeds.add(CatBreed.getBreed(jsonObject.getJSONArray("breeds").getJSONObject(i).getString("id")));
        }
        cat.setBreeds(breeds);

        List<CatCategory> categories = new ArrayList<>();
        if(jsonObject.has("categories")) {
            for (int i = 0; i < jsonObject.getJSONArray("categories").length(); i++) {
                categories.add(CatCategory.getCategory(jsonObject.getJSONArray("categories").getJSONObject(i).getInt("id")));
            }
        }
        cat.setCategories(categories);

        return cat;
    }

    @Override
    public String toString() {
        return Arrays.toString(new Object[] {
                breeds,
                categories,
                id,
                url
        });
    }
}

package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.web.HTTPUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CatCategory {
    private static List<CatCategory> allCategories;

    private int id;
    private String name;

    public CatCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CatCategory() {
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

    public static CatCategory getCategory(int id) {
        for(CatCategory catCategory : getAllCategories()) {
            if(catCategory.getId() == id) {
                return catCategory;
            }
        }
        return null;
    }

    public static List<CatCategory> getCategoriesByName(String name) {
        List<CatCategory> categoryList = new ArrayList<>();
        for(CatCategory catCategory : getAllCategories()) {
            if(catCategory.getName().toLowerCase().startsWith(name.toLowerCase())) {
                categoryList.add(catCategory);
            }
        }
        return categoryList;
    }

    public static List<CatCategory> cacheCategories() throws IOException {
        JSONArray jsonArray = new JSONArray(HTTPUtil.GET("https://api.thecatapi.com/v1/categories", new HashMap<>()));
        allCategories = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            allCategories.add(new CatCategory(jsonObject.getInt("id"), jsonObject.getString("name")));
        }

        return allCategories;
    }

    public static List<CatCategory> getAllCategories() {
        return allCategories;
    }

    public static void setAllCategories(List<CatCategory> allCategories) {
        CatCategory.allCategories = allCategories;
    }
}

package me.mafrans.poppo.util.objects;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dog {
    private List<DogBreed> breeds;
    private String url;

    public Dog() {
    }

    public Dog(List<DogBreed> breeds, String id, String url) {
        this.breeds = breeds;
        this.url = url;
    }

    public List<DogBreed> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<DogBreed> breeds) {
        this.breeds = breeds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Dog parseDog(JSONObject jsonObject) {
        Dog dog = new Dog();
        UrlValidator urlValidator = new UrlValidator();
        String message = jsonObject.getString("message");
        if(!urlValidator.isValid(message)) {
            System.out.println("Error! " + jsonObject.getString("message"));
            return null;
        }
        dog.setUrl(message);
        List<DogBreed> breeds = new ArrayList<>();
        breeds.add(DogBreed.getBreed(message.split("/")[4]));

        dog.setBreeds(breeds);
        return dog;
    }

    @Override
    public String toString() {
        return Arrays.toString(new Object[] {
                breeds,
                url
        });
    }
}

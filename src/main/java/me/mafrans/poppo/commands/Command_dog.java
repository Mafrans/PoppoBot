package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.objects.Dog;
import me.mafrans.poppo.util.objects.DogBreed;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Command_dog implements ICommand {

    private String[] messagesSingle = new String[]{
            "It's a ${display_name}!",
            "${display_name}s are cute, aren't they?",
            "I'm sure that ${display_name} took a long time for the API to handle.",
            "Here's some information about ${display_name}.",
            "You should also see my cat!",
            "Woof!",
            "Remmy sit! Speak!",
            "Remmy says: Fuck!",
            "Remmy says: Woof!",

    };

    @Override
    public String getName() {
        return "dog";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Sends a random dog picture.", "dog [breed]", Arrays.asList("puppy", "doggo", "woof"), false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        Random random = new Random();

        if (args.length == 0) {
            sendRandomDog(channel);
            return true;
        }

        DogBreed searchBreed = DogBreed.getBreedsByName(StringUtils.join(args, " ")).get(0);
        if(searchBreed == null) {
            sendRandomDog(channel);
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(searchBreed.getName(), searchBreed.getUrl());
        if(searchBreed.getImageUrl() != null) {
            embedBuilder.setThumbnail(searchBreed.getImageUrl());
        }

        if(searchBreed.getUrl() != null && HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length - 1]) != null) {
            if (HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length - 1]).length() > 1000) {
                embedBuilder.addField("Description", HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length - 1]).substring(0, 1000) + "...", false);
            }
            else {
                embedBuilder.addField("Description", HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length - 1]), false);
            }
        }

        if(searchBreed.getBredFor() != null) {
            embedBuilder.addField("Bred For", searchBreed.getBredFor(), true);
        }

        if(searchBreed.getLifespan() != null) {
            embedBuilder.addField("Lifespan", searchBreed.getLifespan()[0] + " to " + searchBreed.getLifespan()[1] + " years", true);
        }
        else if(searchBreed.getStringLifespan() != null) {
            embedBuilder.addField("Lifespan", searchBreed.getStringLifespan(), true);
        }

        if(searchBreed.getWeight() != null) {
            embedBuilder.addField("Weight", searchBreed.getWeight()[0] + " to " + searchBreed.getWeight()[1] + "kg", true);
        }
        else if(searchBreed.getStringWeight() != null) {
            embedBuilder.addField("Weight", searchBreed.getStringWeight(), true);
        }

        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setFooter("Provided by The DOG API", null);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }

    public void sendRandomDog(TextChannel channel) throws IOException {
        Random random = new Random();

        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", Main.config.dog_api_token);

        if(new Date().getTime() % 666 == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Little King, little puppet.");
            embedBuilder.setImage("https://i.imgur.com/h5OIVbm.gif");
            embedBuilder.setColor(new Color(120, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
        }

        Dog dog = null;
        int i = 0;
        while(dog == null) {
            String stringJson = HTTPUtil.GET("https://api.thedogapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=1", header);
            System.out.println(stringJson);
            JSONObject jsonObject = new JSONArray(stringJson).getJSONObject(0);
            dog = Dog.parseDog(jsonObject);

            if(i == 10) {
                channel.sendMessage("Sorry, we can't reach the Dog API right now!").queue();
                return;
            }
            i++;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setImage(dog.getUrl());

        /*if (dog.getBreeds().size() > 0) {
            DogBreed dogBreed = dog.getBreeds().get(0);
            embedBuilder.setTitle("Information about " + dogBreed);
        }*/

        embedBuilder.setFooter("Provided by the Dog API", null);
        channel.sendMessage(embedBuilder.build()).queue();
    }
}

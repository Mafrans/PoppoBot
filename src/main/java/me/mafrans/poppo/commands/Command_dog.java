package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Dog;
import me.mafrans.poppo.util.objects.DogBreed;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;

@Id("commands::dog")
public class Command_dog implements ICommand {

    private String[] messagesSingle = new String[]{
            "It's a ${breed_c}!",
            "${breed_c}s are cute, aren't they?",
            "I'm sure that ${display_name} took a long time for the API to handle.",
            "Here's some information about ${breed_c}s.",
            "You should also see my cat!",
            "Woof!",
            "Remmy sit! Speak!",
            "Dogs are nice animals.",
            "Finally, a dog breed that is ${temperament_l}.",
    };

    @Override
    public String getName() {
        return "dog";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Sends a random dog picture.", "dog [breed]", new String[] {"puppy", "doggo", "woof"}, false);
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

        String wikipediaDescription = HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length - 1]);
        if(searchBreed.getUrl() != null && wikipediaDescription != null) {
            if (wikipediaDescription.length() > 1000) {
                embedBuilder.addField("Description", wikipediaDescription.substring(0, 1000) + "...", false);
            }
            else {
                embedBuilder.addField("Description", wikipediaDescription, false);
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

        embedBuilder.setTitle(parseVariables(messagesSingle[random.nextInt(messagesSingle.length)], searchBreed, channel.getGuild())[0]);
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setFooter("Provided by The DOG API", null);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }

    private void sendRandomDog(TextChannel channel) throws IOException {
        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", Main.config.dog_api_token);

        if(new Date().getTime() % 666 == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Little King, little puppet.");
            embedBuilder.setImage("https://i.imgur.com/h5OIVbm.gif");
            embedBuilder.setColor(new Color(120, 0, 0));
            channel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        Dog dog = null;
        int i = 0;
        while(dog == null) {
            String stringJson = HTTPUtil.GET("https://api.thedogapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=1", header);
            System.out.println(stringJson);

            if(!stringJson.equalsIgnoreCase("200")) {
                channel.sendMessage("Could not connect to the API, please try again").queue();
            }

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

    private String[] parseVariables(String string, DogBreed breed, Guild guild) {
        Random random = new Random();
        String out = string;

        Map<String, String> replacements = new HashMap<>();
        replacements.put("breed", breed.getName());

        String temperament = breed.getTemperament()[random.nextInt(breed.getTemperament().length)];
        replacements.put("temperament", temperament);

        String temperaments = GUtil.joinNatural(breed.getTemperament());
        replacements.put("temperaments", temperaments);

        Member randomMember = guild.getMembers().get(random.nextInt(guild.getMembers().size()));
        replacements.put("random_member", randomMember.getEffectiveName());

        String christmasPresent = GUtil.getChristmasPresent();
        replacements.put("christmas_present", christmasPresent);

        String origin = breed.getBredFor();
        replacements.put("bred_for", origin);

        for(String key : replacements.keySet()) {
            String value = replacements.get(key);

            out = out.replace("${" + key + "}", value)
                    .replace("${" + key + "_l}", value.toLowerCase())
                    .replace("${" + key + "_u}", value.toUpperCase())
                    .replace("${" + key + "_c}", GUtil.capitalize(value))
                    .replace("${" + key + "_wc}", GUtil.capitalizeWords(value));
        }

        return new String[] {out, breed.getUrl()};
    }
}

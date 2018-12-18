package me.mafrans.poppo.commands;

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
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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
    private String[] messagesBoth = new String[] {
            "It's a ${display_name}!",
            "This is not just a ${name}, it's a ${display_name}!",
            "${display_name}s are cute, aren't they?",
            "It's a ${name}, but it's also a ${sub_name}!",
            "\"You often find ${name} at the ${sub_name} idfk xd.\" -Fionn",
            "You should also see my cat!",
            "Woof!",
            "Remmy sit! Speak!",
            "Remmy says: Fuck!",
            "Remmy says: Woof!"
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
        embedBuilder.setAuthor(searchBreed.getDisplayName(), searchBreed.getUrl());
        if(searchBreed.getImageUrl() != null) {
            embedBuilder.setThumbnail(searchBreed.getImageUrl());
        }

        embedBuilder.addField("Description", HTTPUtil.getWikipediaDescription(searchBreed.getUrl().split("/")[searchBreed.getUrl().split("/").length-1]) + "\n\u00AD", false);
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setFooter("Provided by the DaaS (Dogs-as-a-Service) API", null);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }

    public void sendRandomDog(TextChannel channel) throws IOException {
        Random random = new Random();

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
            String stringJson = HTTPUtil.GET("http://dog.ceo/api/breeds/image/random", new HashMap<>());
            System.out.println(stringJson);
            JSONObject jsonObject = new JSONObject(stringJson);
            dog = Dog.parseDog(jsonObject);

            if(i == 10) {
                channel.sendMessage("Sorry, we can't reach the DaaS API right now!").queue();
                return;
            }
            i++;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setImage(dog.getUrl());

        if (dog.getBreeds().size() > 0) {
            DogBreed dogBreed = dog.getBreeds().get(0);
            if(dogBreed.getSubName() != null) {
                embedBuilder.setTitle(messagesSingle[random.nextInt(messagesSingle.length)].replace("${display_name}", dogBreed.getDisplayName()).replace("${name}", dogBreed.getName()), dogBreed.getUrl());
            }
            else {
                embedBuilder.setTitle(messagesBoth[random.nextInt(messagesBoth.length)].replace("${display_name}", dogBreed.getDisplayName()).replace("${name}", dogBreed.getName()).replace("${sub_name}", dogBreed.getSubName()), dogBreed.getUrl());
            }
        }

        embedBuilder.setFooter("Provided by the DaaS (Dogs-as-a-Service) API", null);
        channel.sendMessage(embedBuilder.build()).queue();
    }
}

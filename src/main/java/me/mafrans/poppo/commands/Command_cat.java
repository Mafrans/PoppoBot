package me.mafrans.poppo.commands;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.objects.Cat;
import me.mafrans.poppo.util.objects.CatBreed;
import me.mafrans.poppo.util.objects.CatCategory;
import me.mafrans.poppo.util.web.HTTPUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

@Id("commands::cat")
public class Command_cat implements ICommand {
    private String[] messages = new String[] {
            "Oh look, it's a ${breed_c} from ${origin_c}!",
            "Did you know? ${breed_c}s originally come from ${origin_c}.",
            "It's a super-${temperament_l} ${breed}!",
            "Its practically common knowledge that ${breed_c}s are ${temperaments}. Right?",
            "Maybe your friend ${random_member_c} should get themselves a ${breed_c}?",
            "Apparently, that's supposed to be a ${breed_c}.",
            "Do cat races even matter anyway?",
            "DID YOU HEAR ME? I GOT YOU A PICTURE OF A ${breed_u}.",
            "It's a cat, what did you expect?",
            "I know you wanted ${christmas_present_l} for christmas, but we could only afford a ${breed_l}.",
    };
    private static final Map<Integer, String> categoryMessages;

    static {
        categoryMessages = new HashMap<>();
        categoryMessages.put(5, "Cats and boxes, the ultimate combo!");
        categoryMessages.put(15, "Why would a cat wear clothes? I don't know either!");
        categoryMessages.put(1, "It's a cat in a hat! Just like in the books!");
        categoryMessages.put(14, "Sinks? What do they have to do with cats?");
        categoryMessages.put(2, "Cats in space. You know, laser kittens. That kind of stuff.");
        categoryMessages.put(4, "Everything is cooler with sunglasses.");
        categoryMessages.put(7, "Oh my, how classy!");
    }



    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Sends a random cat picture.", "cat [breed]", new String[] {"kitty", "catto", "kitten"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        Random random = new Random();

        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", Main.config.cat_api_token);

        if (args.length == 0) {
            sendRandomCat(channel, header);
            return true;
        }
        CatBreed searchBreed = CatBreed.getBreedsByName(StringUtils.join(args, " ")).get(0);
        if(searchBreed == null) {
            sendRandomCat(channel, header);
            return true;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        String[] titleInfo = parseVariables(messages[random.nextInt(messages.length)], searchBreed, channel.getGuild());
        embedBuilder.setAuthor(titleInfo[0], searchBreed.getUrl());

        String imageUrl = HTTPUtil.getWikipediaThumbnail(searchBreed.getUrl().replace("https://en.wikipedia.org/wiki/", "").replace("_(cat)", "_cat"));
        if(imageUrl != null) {
            embedBuilder.setThumbnail(imageUrl);
        }
        embedBuilder.addField("Description", searchBreed.getDescription() + "\n\u00AD", false);
        embedBuilder.addField("Origin", searchBreed.getOrigin() + "\n\u00AD", true);
        embedBuilder.addField("Temperament", GUtil.joinNaturalExt(searchBreed.getTemperament(), 2, "\n") + "\n\u00AD", true);
        embedBuilder.addField("Lifespan", searchBreed.getLifespan()[0] + " to " + searchBreed.getLifespan()[1] + " years", true);
        embedBuilder.addField("Weight", (GUtil.round(searchBreed.getWeight()[0], 1) + " to " + GUtil.round(searchBreed.getLifespan()[1], 1)).replace(".0", "") + "kg", true);
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setFooter("Provided by The Cat API", null);

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }

    private void sendRandomCat(TextChannel channel, Map<String, String> header) throws IOException {
        Random random = new Random();
        JSONObject jsonObject = new JSONArray(HTTPUtil.GET("https://api.thecatapi.com/v1/images/search?size=full", header)).getJSONObject(0);
        Cat cat = Cat.parseCat(jsonObject);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setImage(cat.getUrl());

        if (cat.getBreeds().size() > 0) {
            String[] titleInfo = parseVariables(messages[random.nextInt(messages.length)], cat, channel.getGuild());
            embedBuilder.setTitle(titleInfo[0], titleInfo[1]);
        }
        else if (cat.getCategories().size() > 0) {
            CatCategory catCategory = cat.getCategories().get(random.nextInt(cat.getCategories().size()));
            if(catCategory != null) {
                System.out.println(catCategory.getId() + ": " + catCategory.getName());
                embedBuilder.setTitle(categoryMessages.get(catCategory.getId()));
            }
        }

        embedBuilder.setFooter("Provided by The Cat API", null);
        channel.sendMessage(embedBuilder.build()).queue();
    }

    private String[] parseVariables(String string, Cat cat, Guild guild) {
        Random random = new Random();
        String out = string;

        Map<String, String> replacements = new HashMap<>();
        CatBreed breed = cat.getBreeds().get(random.nextInt(cat.getBreeds().size()));
        replacements.put("breed", breed.getName());

        String temperament = breed.getTemperament()[random.nextInt(breed.getTemperament().length)];
        replacements.put("temperament", temperament);

        String temperaments = GUtil.joinNatural(breed.getTemperament());
        replacements.put("temperaments", temperaments);

        Member randomMember = guild.getMembers().get(random.nextInt(guild.getMembers().size()));
        replacements.put("random_member", randomMember.getEffectiveName());

        String christmasPresent = GUtil.getChristmasPresent();
        replacements.put("christmas_present", christmasPresent);

        String origin = breed.getOrigin();
        replacements.put("origin", origin);

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

    private String[] parseVariables(String string, CatBreed breed, Guild guild) {
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

        String origin = breed.getOrigin();
        replacements.put("origin", origin);

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
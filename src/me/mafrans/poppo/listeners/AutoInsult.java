package me.mafrans.poppo.listeners;

import me.mafrans.poppo.util.config.ConfigEntry;
import me.mafrans.poppo.util.config.ServerPrefs;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AutoInsult implements Runnable {
    @Override
    public void run() {
        if(!ConfigEntry.AUTOINSULT_USE.getBoolean()) return;

        for(Guild guild : ServerPrefs.serverPrefList.keySet()) { // Iterate over all guilds
            if(!ServerPrefs.USE_AUTOINSULT.getString(guild).equalsIgnoreCase("true")) return;
            Random random = new Random();
            if(random.nextInt(100) + 1 > ConfigEntry.AUTOINSULT_RATE.getInteger()) return;

            List<OnlineStatus> ignoredStatuses = Arrays.asList(
                    OnlineStatus.OFFLINE,
                    OnlineStatus.INVISIBLE,
                    OnlineStatus.UNKNOWN
            );

            List<Member> qMembers = new ArrayList<>();

            for(Member member : guild.getMembers()) {
                if(ignoredStatuses.contains(member.getOnlineStatus())) {
                    return;
                }

                if(member.getNickname().toLowerCase().contains("q")) {
                    qMembers.add(member);
                }
            }

            if(qMembers.size() < 2) return;

            for(Member member : qMembers) {
                guild.getDefaultChannel().sendMessage(Insult.getSlashB(member.getNickname())).queue();
            }
        }
    }
}

class Insult {
    private static List<String> slashb = Arrays.asList("%s is the guy who tells the cripple ahead of him in line to hurry up." +
            "%s is first to get to the window to see the car accident outside." +
            "%s is the one who wrote your number on the mall's bathroom wall." +
            "%s is a failing student who makes passes at his young, attractive English teacher." +
            "%s is the guy loitering on Park Ave. that is always trying to sell you something." +
            "%s is the one who handed his jizz-drenched clothes to Good Will." +
            "%s is one who introduced you first to Goatse." +
            "%s is a hot incest dream that you'll try to forget for days." +
            "%s is the only one of your group of friends to be secure in his sexuality and say anything." +
            "%s is the guy without ED who still likes trying Viagra." +
            "%s is the best friend that tags along for your first date and cock-blocks throughout night. The decent girl you're trying to bag walks out on the date,  laughs and takes you home when you're drunk, and you wake up to several hookers in your house who %s called for you." +
            "%s is a friend that constantly asks you to try mutual masturbation with him." +
            "%s is the guy who calls a suicide hotline to hit on the advisor" +
            "%s is nuking the hard-drive next time someone knocks on his door." +
            "%s is the one who left a used condom outside the schoolyard." +
            "%s is the voice in your head that tells you that it doesn't matter if she's drunk." +
            "%s is the friend who constantly talks about your mom's rack." +
            "%s is the only one who understands what the hell you saying." +
            "%s is someone who would pay a hooker to eat his ass, and only that." +
            "%s is the uncle who has touched you several times." +
            "%s is still recovering in the hospital, after trying something he saw in a hentai." +
            "%s is the guy urinating in the corner of a subway car." +
            "%s is that dream you want to forget where you fucked all your closest friends");

    public static String getSlashB(String name) {
        Random random = new Random();
        return slashb.get(random.nextInt(slashb.size())).replace("%s", name);
    }
}
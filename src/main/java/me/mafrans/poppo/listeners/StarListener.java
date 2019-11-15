package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.Feature;
import me.mafrans.poppo.util.Id;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

@Id("stars::generate")
public class StarListener extends ListenerAdapter {
    private List<String> timers = new ArrayList<>();
    private Map<String, Message> messages = new HashMap<>();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!Feature.isEnabled(event.getGuild(), "stars::generate")) return;

        if(event.getAuthor().isBot()) return;

        if(timers.contains(event.getAuthor().getId())) return;

        if(!event.getMessage().getContentDisplay().contains(" ")) return;
        if(event.getMessage().getContentDisplay().length() < 10) return;
        for(String prefix : Main.config.command_prefix) {
            if(event.getMessage().getContentDisplay().toLowerCase().startsWith(prefix)) return;
        }

        if(Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).isEmpty()) {
            DataUser dataUser = new DataUser(Collections.singletonList(event.getAuthor().getName()), event.getAuthor().getId(), "Currently Online", event.getAuthor().getAvatarUrl(), 1);
            System.out.println(dataUser.getStars());
            Main.userList.put(new SQLDataUser(dataUser));
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataUser dataUser = Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).get(0);
        SQLDataUser sqlDataUser = new SQLDataUser(dataUser);
        sqlDataUser.setStars(dataUser.getStars() + 1);
        Main.userList.put(sqlDataUser);
        System.out.println(Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).get(0));

        timers.add(event.getAuthor().getId());

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            timers.remove(event.getAuthor().getId());
        });
        thread.start();
    }
}

package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;

public class StarListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        if(Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).isEmpty()) {
            DataUser dataUser = new DataUser(Arrays.asList(event.getAuthor().getName()), event.getAuthor().getId(), "Currently Online", event.getAuthor().getAvatarUrl(), 1);
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
    }
}

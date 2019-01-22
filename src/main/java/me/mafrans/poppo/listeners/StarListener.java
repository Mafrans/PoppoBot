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
        if(Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).isEmpty()) {
            DataUser dataUser = new DataUser(Arrays.asList(event.getAuthor().getName()), event.getAuthor().getId(), "Currently Online", event.getAuthor().getAvatarUrl(), 1);
            Main.userList.put(new SQLDataUser(dataUser));
            return;
        }

        DataUser dataUser = Main.userList.getUsersFrom("uuid", event.getAuthor().getId()).get(0);
        dataUser.setStars(dataUser.getStars() + 1);
        Main.userList.put(new SQLDataUser(dataUser));
    }
}

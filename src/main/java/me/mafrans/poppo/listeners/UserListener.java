package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import me.mafrans.poppo.util.GUtil;
import me.mafrans.poppo.util.config.DataUser;
import me.mafrans.poppo.util.config.SQLDataUser;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.ZoneOffset;
import java.util.*;

public class UserListener extends ListenerAdapter {
    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        if(Main.userList.getUsersFrom("uuid", event.getUser().getId()).size() == 0) {
            DataUser dataUser = new DataUser(Arrays.asList(event.getUser().getName()), event.getUser().getId(), "Currently Online", event.getUser().getAvatarUrl());
            Main.userList.put(new SQLDataUser(dataUser));
            return;
        }

        DataUser dataUser = Main.userList.getUsersFrom("uuid", event.getUser().getId()).get(0);
        List<String> names = new ArrayList<>(dataUser.getNames());
        System.out.println("Old Names: " + names.toString());
        if(!names.contains(event.getUser().getName())) {
            names.add(event.getUser().getName());
        }

        dataUser.setNames(names);
        System.out.println("New Names: " + dataUser.getNames());

        Main.userList.put(new SQLDataUser(dataUser));
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        if(Main.userList.getUsersFrom("uuid", event.getUser().getId()).size() == 0) {
            DataUser dataUser = new DataUser(Arrays.asList(event.getUser().getName()), event.getUser().getId(), "Currently Online", event.getUser().getAvatarUrl());
            Main.userList.put(new SQLDataUser(dataUser));
        }

        OnlineStatus prevOnlineStatus = event.getOldOnlineStatus();
        OnlineStatus currentOnlineStatus = event.getGuild().getMember(event.getUser()).getOnlineStatus();

        if((prevOnlineStatus == OnlineStatus.ONLINE || prevOnlineStatus == OnlineStatus.DO_NOT_DISTURB) && (currentOnlineStatus == OnlineStatus.IDLE || currentOnlineStatus == OnlineStatus.INVISIBLE || currentOnlineStatus == OnlineStatus.OFFLINE)) {
            Calendar cal = Calendar.getInstance();
            String date = GUtil.currentParsedDate(ZoneOffset.UTC);

            DataUser dataUser = Main.userList.getUsersFrom("uuid", event.getUser().getId()).get(0);
            dataUser.setLastOnlineTag(date);
            Main.userList.put(new SQLDataUser(dataUser));
        }
        else if((prevOnlineStatus == OnlineStatus.IDLE || prevOnlineStatus == OnlineStatus.INVISIBLE || prevOnlineStatus == OnlineStatus.OFFLINE) && (currentOnlineStatus == OnlineStatus.ONLINE || currentOnlineStatus == OnlineStatus.DO_NOT_DISTURB)) {
            DataUser dataUser = Main.userList.getUsersFrom("uuid", event.getUser().getId()).get(0);
            dataUser.setLastOnlineTag("Currently Online");
            Main.userList.put(new SQLDataUser(dataUser));
        }
    }
}

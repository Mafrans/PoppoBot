package me.mafrans.poppo.listeners;

import me.mafrans.poppo.Main;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class UserListener extends ListenerAdapter {
    @Override
    public void onUserNameUpdate(UserNameUpdateEvent event) {
        try {
            Main.userSpecificData.setUserOnly(event.getUser());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        OnlineStatus prevOnlineStatus = event.getPreviousOnlineStatus();
        OnlineStatus currentOnlineStatus = event.getGuild().getMember(event.getUser()).getOnlineStatus();

        if((prevOnlineStatus == OnlineStatus.ONLINE || prevOnlineStatus == OnlineStatus.DO_NOT_DISTURB) && (currentOnlineStatus == OnlineStatus.IDLE || currentOnlineStatus == OnlineStatus.INVISIBLE || currentOnlineStatus == OnlineStatus.OFFLINE)) {
            Calendar cal = Calendar.getInstance();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()));

            try {
                Main.userSpecificData.setUser(event.getUser(), date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if((prevOnlineStatus == OnlineStatus.IDLE || prevOnlineStatus == OnlineStatus.INVISIBLE || prevOnlineStatus == OnlineStatus.OFFLINE) && (currentOnlineStatus == OnlineStatus.ONLINE || currentOnlineStatus == OnlineStatus.DO_NOT_DISTURB)) {
            try {
                Main.userSpecificData.setUser(event.getUser(), "Currently Online");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

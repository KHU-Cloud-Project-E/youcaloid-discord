package com.noeun.youcaloid.bot;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotEventListener extends ListenerAdapter{

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // TODO Auto-generated method stub
        super.onMessageReceived(event);
        User user = event.getAuthor();
        String message = event.getMessage().getContentRaw();
        System.out.println(user.getId() + " : "+ message);
    }
}

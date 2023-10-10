package com.noeun.youcaloid.bot;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class BotEventListener extends ListenerAdapter{

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // TODO Auto-generated method stub
        
        User user = event.getAuthor();
        String message = event.getMessage().getContentRaw();
        System.out.println(event.getChannel().getName() + " _ " +user.getId() + " : "+ message);
        event.getChannel().sendMessage("receive");

        AudioChannelUnion connectedChannel = event.getMember().getVoiceState().getChannel();
        if(connectedChannel != null){
            AudioManager audioManager = event.getGuild().getAudioManager();
            if((!audioManager.isConnected()) && event.getChannel().getName().equals("ttsvoice")){
                audioManager.openAudioConnection(connectedChannel);
            }
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // TODO Auto-generated method stub
        //super.onSlashCommandInteraction(event);
        switch (event.getName()){
            case "test":
                event.reply("test command!").queue();
                break;
        }
    }
}

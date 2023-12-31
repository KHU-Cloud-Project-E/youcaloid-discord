package com.noeun.youcaloid.bot;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.noeun.youcaloid.db.DataBaseService;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class BotEventListener extends ListenerAdapter{

    private HashMap<AudioManager, Date> connectTime;

    public class Gcollect extends Thread {
        @Override
        public void run(){
            while(true){
                //System.out.println("collector 1 mv");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Date nowDate = new Date();
                for(Entry<AudioManager, Date> i : connectTime.entrySet()){
                    System.out.println(i.getKey().getGuild());
                    if(i.getValue().getTime() + 600000 < nowDate.getTime()){
                        i.getKey().closeAudioConnection();
                        System.out.println("garbage collector successfully out of channel of guild "+i.getKey().getGuild());
                        connectTime.remove(i.getKey());
                    }
                }
            }
        }
    }

    private final DataBaseService dataBaseService;

    public BotEventListener(){
        this.connectTime = new HashMap<>();
        this.dataBaseService = new DataBaseService();
        Thread gc = new Gcollect();
        gc.start();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        String message = event.getMessage().getContentRaw();
        System.out.println(event.getChannel().getName() + " _ " +user.getId() + " : "+ message);
        //event.getChannel().sendMessage("receive").queue();

        AudioChannelUnion connectedChannel = event.getMember().getVoiceState().getChannel();
        if(connectedChannel != null){
            AudioManager audioManager = event.getGuild().getAudioManager();
            if((!audioManager.isConnected()) && event.getChannel().getName().equals("ttsvoice")){
                audioManager.openAudioConnection(connectedChannel);
                connectTime.put(audioManager, new Date());
            }
            if(event.getChannel().getName().equals("ttsvoice")){
            try{
            String audioChannelId = audioManager.getConnectedChannel().getId();
            connectTime.put(audioManager, new Date());
                if( audioManager.isConnected() && connectedChannel.getId().equals(audioChannelId)){
                    String urlmessage = "http://youcal-voice-service:5000/aitts?modelid="+dataBaseService.getModelId(event.getGuild().getId(), user.getId())+"&textmessage=";
                    urlmessage = urlmessage + message.replace(" ", "%20");
                    System.out.println(urlmessage);
                    playvoice(urlmessage, event.getGuild());
                }
            }catch(NullPointerException e){
                System.out.println("bot is not in voicechannel.");
            }  
        }
        }
        

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //super.onSlashCommandInteraction(event);
        String userId;
        String guildId;
        String modelId;
        int macroNum;
        int rst;
        switch (event.getName()){
            case "test":
                event.reply("test command!").queue();
                break;
            case "setvoice":
                userId = event.getMember().getId();
                guildId = event.getGuild().getId();
                modelId = event.getOption("modelid", OptionMapping::getAsString);
                System.out.println(guildId +" "+userId+" "+modelId);
                rst = dataBaseService.addModelId(guildId, userId, modelId);
                if(rst == 0) event.reply("invalid model id.").queue();
                else event.reply("successfully change your model to "+dataBaseService.nowModel(guildId, userId)).queue();
                break;
            case "setmacro":
                userId = event.getMember().getId();
                modelId = event.getOption("modelid", OptionMapping::getAsString);
                macroNum = Integer.parseInt(event.getOption("macronumber", OptionMapping::getAsString));
                rst = dataBaseService.setMacro(userId, macroNum, modelId);
                if(rst == 0) event.reply("invalid model id.").queue();
                else event.reply("successfully register model " +dataBaseService.getModelDec(modelId) + "to macronumber "+ String.valueOf(macroNum)).queue();
                break;
            case "changevoice":
                userId = event.getMember().getId();
                guildId = event.getGuild().getId();
                macroNum = Integer.parseInt(event.getOption("macronumber", OptionMapping::getAsString));
                rst = dataBaseService.changeModel(guildId, userId, macroNum);
                if(rst == 0) event.reply("invalid model id.").queue();
                else event.reply("successfully change your model to "+dataBaseService.nowModel(guildId, userId)).queue();
                break;
            case "getmacro":
                userId = event.getMember().getId();
                event.reply(dataBaseService.getMacro(userId)).queue();
                break;
        }
    }

    private void playvoice(String textMessage, Guild guild){
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.loadAndPlay(guild, textMessage);
    }
}

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

    private HashMap<String, Date> connectTime;

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
                for(Entry<String, Date> i : connectTime.entrySet()){
                    System.out.println(i.getKey());
                    if(i.getValue().getTime() + 600000 < nowDate.getTime()){
                        System.out.println("garbage collector successfully out of channel of guild "+i.getKey());
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
                connectTime.put(event.getGuild().getIconId(), new Date());
            }
            try{
            String audioChannelId = audioManager.getConnectedChannel().getId();
            connectTime.put(event.getGuild().getIconId(), new Date());
                if( audioManager.isConnected() && connectedChannel.getId().equals(audioChannelId)){
                    String urlmessage = "http://localhost:5000/aitts?modelid="+dataBaseService.getModelId(event.getGuild().getId(), user.getId())+"&textmessage=";
                    urlmessage = urlmessage + message.replace(" ", "%20");
                    System.out.println(urlmessage);
                    playvoice(urlmessage, event.getGuild());
                }
            }catch(NullPointerException e){
                System.out.println("bot is not in voicechannel.");
            }  
        }
        

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //super.onSlashCommandInteraction(event);
        switch (event.getName()){
            case "test":
                event.reply("test command!").queue();
                break;
            case "setvoice":
                String userId = event.getMember().getId();
                String guildId = event.getGuild().getId();
                String modelId = event.getOption("modelid", OptionMapping::getAsString);
                System.out.println(guildId +" "+userId+" "+modelId);
                int rst = dataBaseService.addModelId(guildId, userId, modelId);
                if(rst == 0) event.reply("invalid model id.").queue();
                else event.reply("successfully change your model to "+dataBaseService.nowModel(guildId, userId)).queue();
                break;
        }
    }

    private void playvoice(String textMessage, Guild guild){
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.loadAndPlay(guild, textMessage);
    }
}

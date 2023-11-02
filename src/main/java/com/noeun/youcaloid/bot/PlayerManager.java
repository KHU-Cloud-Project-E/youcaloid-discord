package com.noeun.youcaloid.bot;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildAudioManager> audioManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager(){
        this.audioManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildAudioManager getAudioManager(Guild guild){
        return this.audioManagers.computeIfAbsent(guild.getIdLong(), (guildId) ->{
            final GuildAudioManager guildAudioManager = new GuildAudioManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildAudioManager.getSendHandler());

            return guildAudioManager;
        });
    }

    public void loadAndPlay(Guild guild, String textMessage){
        final GuildAudioManager audioManager = this.getAudioManager(guild);

        this.audioPlayerManager.loadItemOrdered(audioManager, textMessage, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                audioManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            //    for(AudioTrack i : playlist.getTracks()){
            //     audioManager.scheduler.queue(i);
            //    }
            }

            @Override
            public void noMatches() {
                
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                
            }
            
        });
    }

    public static PlayerManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}

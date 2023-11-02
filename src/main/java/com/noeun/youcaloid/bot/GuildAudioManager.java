package com.noeun.youcaloid.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildAudioManager {
    private final AudioPlayer audioPlayer;

    public final trackScheduler scheduler;

    private final BotAudioSendHandler sendHandler;

    public GuildAudioManager(AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new trackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new BotAudioSendHandler(this.audioPlayer);
        this.audioPlayer.setVolume(100);
    }
    public BotAudioSendHandler getSendHandler(){
        return this.sendHandler;
    }
}

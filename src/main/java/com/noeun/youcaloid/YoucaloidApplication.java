package com.noeun.youcaloid;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.noeun.youcaloid.bot.BotEventListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

@SpringBootApplication
public class YoucaloidApplication {

	static private String key;

	@Value("${bot.key}")
	public void setKeyValue(String value){
		key = value;
	}

	private static JDA jdaBuild() throws LoginException{
		System.out.println("discode bot key is : "+key);
		JDABuilder builder = JDABuilder.createDefault(key);
		builder.setActivity(Activity.playing("아직 개발중"));
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
		builder.addEventListeners(new BotEventListener());

		return builder.build();
	}

	private static void commandUpdate(JDA jda){
		jda.updateCommands().addCommands(
			Commands.slash("test","test command"),
			Commands.slash("setvoice","/setvoice {your voice model id}}")
			.addOption(OptionType.STRING, "modelid", "Id for tts model", true)
		).queue();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(YoucaloidApplication.class, args);
		JDA jda;
		try{
			jda = jdaBuild();
			commandUpdate(jda);
			
		} catch (LoginException e){
			System.out.println("discord bot login error!");
		}

	}

}

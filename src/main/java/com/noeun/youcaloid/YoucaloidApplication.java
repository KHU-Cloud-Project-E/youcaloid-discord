package com.noeun.youcaloid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

@SpringBootApplication
public class YoucaloidApplication {

	static private String key;

	@Value("${bot.key}")
	public void setKeyValue(String value){
		key = value;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(YoucaloidApplication.class, args);
		System.out.println("discode bot key is : "+key);
		JDABuilder builder = JDABuilder.createDefault(key);
		builder.setActivity(Activity.playing("아직 개발중"));
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

		JDA jda = builder.build();

	}

}

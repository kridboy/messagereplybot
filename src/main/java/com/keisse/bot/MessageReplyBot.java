package com.keisse.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MessageReplyBot {
    //Worst practice, insert token from file ((safety reasons))
    public static String TOKEN=null;
    {
        try {
            TOKEN = Files.readString(Path.of("token.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws LoginException {
        new MessageReplyBot().run();
    }
    void run()throws LoginException{
        if(TOKEN!=null){
            var jda = buildJDA(TOKEN);
            jda.addEventListener(new HiraganaGameListener());
        }
        else{
            throw new RuntimeException("Token is null!");
        }
    }

    JDA buildJDA(String token)throws LoginException{
        return JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("Type !hiragana"))
                .build();
    }
}

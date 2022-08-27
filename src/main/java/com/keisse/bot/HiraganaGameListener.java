package com.keisse.bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//Most basic implementation of the Hiragana Game, for testing purposes, this needs serious work to be put into actual action
public class HiraganaGameListener extends ListenerAdapter {
    public static final String RETURNFEED_REGEX = "\r\n";
    public static final String START_SIGNAL = "!hiragana";
    public static final Random RANDOM = new Random();
    private boolean gameStarted = false;
    private final Map<String, String> hiraganaMap;
    private Map.Entry<String, String> currentHiraganaTest = null;

    {
        hiraganaMap = createHiraganaMap();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        //Check if message is not from bot, otherwise it tries to check own messages
        if(!msg.getAuthor().isBot()){
            if (gameStarted) {
                if (!msg.getContentRaw().equals(currentHiraganaTest.getValue())){
                    System.out.println(msg);
                    event.getChannel().sendMessage("wrong, try again!").queue();

                }

                if (msg.getContentRaw().equals(currentHiraganaTest.getValue())) {
                    event.getChannel().sendMessage("Correct, good job!").queue();
                    gameStarted = false;
                }
            }
            //this check needs to be last, otherwise the code above will execute after first message, we don't want that
            if (msg.getContentRaw().equals("!hiragana")&& !gameStarted) {
                gameStarted = true;
                var key = findNewKey();
                event.getChannel().sendMessage(String.format("Provide Romaji for: [%s]", key)).queue();
            }
        }
    }

    private String findNewKey() {
        var list = List.copyOf(hiraganaMap.keySet());
        var key = list.get(RANDOM.nextInt(list.size()));
        currentHiraganaTest = new AbstractMap.SimpleEntry<>(key, hiraganaMap.get(key));
        return key;
    }

    private static Map<String, String> createHiraganaMap() {
        Map<String, String> hiraganaMap = null;
        String hiraString = null;

        try {
            hiraString = Files.readString(Path.of("src/main/resources/hiragana.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); //Just stop if some IOException pops up, it won't happen if file is there anyway
        }
        if (hiraString != null) {
            hiraganaMap = new HashMap<>();
            var hiraStrArr = hiraString.split(RETURNFEED_REGEX + RETURNFEED_REGEX);
            for (var str : hiraStrArr) {
                var strSplit = str.split(RETURNFEED_REGEX);
                hiraganaMap.put(strSplit[0], strSplit[1]);
            }
        }
        return hiraganaMap;
    }
}
package com.example;

import java.util.Random;

public class RandomMotivationMoneyQuotes {

    private static String[] quotes = new String[] {
            "Rule No.1: Never lose money. Rule No.2: Never forget rule No.1.",
            "Friends and good manners will carry you where money won't go.",
            "Success comes to those who dedicate everything to their passion in life. To be successful, it is also very important to be humble and never let fame or money travel to your head.",
            "Never spend your money before you have earned it.",
            "Success isn't measured by money or power or social rank. Success is measured by your discipline and inner peace."
    };

    public static String randomQuote(){
        return quotes[new Random().nextInt(quotes.length-1)];
    }
}

package Components;

import java.util.ArrayList;
import java.util.List;

public class Generate {
    public static List<Card> generateDeck() {
        List<Card> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(suit, value));
            }
        }
        // Add the Joker
        deck.add(new Card("Joker", ""));
        return deck;
    }

    public static List<Player> createPlayers(int numPlayers) {
        List<Player> players = new ArrayList<>();
        Object lock = new Object();
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i + 1), lock, players, i);
            players.add(player);
//            Thread thread = new Thread(new Simulate(player, players, player.lock));
            Thread thread = new Thread(String.valueOf(player));
            thread.start();
        }
        return players;
    }

    public static void dealCards(List<Player> players, List<Card> deck, int numPlayers) {
        // Distribute 13 cards to each player
        int cardsPerPlayer = 52/numPlayers;
        int cardIndex = 0;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player player : players) {
                player.receiveCard(deck.get(cardIndex++));
            }
        }
    }
}

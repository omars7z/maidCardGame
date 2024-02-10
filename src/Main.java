import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

import Components.Deck;
import Components.Card;
import Components.Player;
import Utilities.Functions;

class Main {
    public static void main(String[] args) {
        Object lock = new Object();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        int numPlayers = scanner.nextInt();
        CyclicBarrier barrier = new CyclicBarrier(numPlayers);

        List<Player> players = Deck.createPlayers(numPlayers);
        List<Card> deck = Deck.generateDeck();

        Collections.shuffle(deck);
        Deck.dealCards(players, deck, numPlayers);

        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();

        // create instances of Simulate and start them in separate threads
        List<Thread> simulateThreads = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Simulate simulate = new Simulate(players.get(i), players, lock, barrier, 0);
            Thread thread = new Thread(simulate);
            simulateThreads.add(thread);
            thread.start();
        }

        for (int i = 0; i< players.size(); i++) {
            try {
                simulateThreads.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (Player player : players) {
            if (!player.getDeck().isEmpty()) {
                System.out.println(player.name + " lost!");
            }
            System.out.println(player.name + ": " + player.getDeck());
        }
    }
}
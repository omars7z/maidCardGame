import Components.Card;
import Components.Deck;
import Components.Player;
import Utilities.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        int numPlayers = scanner.nextInt();

        Object lock = new Object();
        CyclicBarrier barrier = new CyclicBarrier(numPlayers);

        List<Player> players = Deck.createPlayers(numPlayers);
        List<Card> deck = Deck.generateDeck();

        GameManager gameManager = new GameManager(players);

        Collections.shuffle(deck);
        Deck.dealCards(players, deck, numPlayers);

        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();


        // create instances of Threads and start them in separate threads
        List<Thread> simulateThreads = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Threads simulate = new Threads(players.get(i), players, lock, barrier, 0, gameManager);
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
        }
        for (Player player : players) {
            System.out.println(player.name + ": " + player.getDeck());
        }
    }
}

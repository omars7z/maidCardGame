import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Scanner;

import Components.Generate;
import Components.Card;
import Components.Player;
import Utilities.Functions;

class Main {
    public static void main(String[] args) {
        Object lock = new Object();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        int numPlayers = scanner.nextInt();

        List<Player> players = Generate.createPlayers(numPlayers);
        List<Card> deck = Generate.generateDeck();

        Collections.shuffle(deck);
        Generate.dealCards(players, deck, numPlayers);

        // display players decks and remove initial pairs
        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();
        for (Player player : players) {
            Functions.removeInitialPairs(player);
        }
        System.out.println();

        // create instances of Simulate and start them in separate threads
        List<Thread> simulateThreads = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Simulate simulate = new Simulate(players.get(i), players, lock);
            Thread thread = new Thread(simulate);
            simulateThreads.add(thread);
            thread.start();
        }

        // play the game
        int currentPlayerIndex = 0, losingPlayerIndex = -1;
        boolean gameEnded = false;
        while (!gameEnded) {
            synchronized (lock) {
                lock.notify(); // notify the current player to play
            }

            try {
                Thread.sleep(1000); // delay for readability
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean currentPlayerHasJoker = players.get(currentPlayerIndex).getDeck().stream().anyMatch(card -> card.getValue().equals("Joker"));
            int finalCurrentPlayerIndex = currentPlayerIndex; //utilized streams to iterate through collections without needing loops
            boolean allOtherPlayersEmpty = players.stream().filter(player -> !player.equals(players.get(finalCurrentPlayerIndex))).allMatch(Player::isEmptyDeck);

            if (allOtherPlayersEmpty && !currentPlayerHasJoker) {
                System.out.println("\nGame ended! All players have emptied their hands.");
                losingPlayerIndex = currentPlayerIndex;
                gameEnded = true;
            }

            // synchronized player index when iterating across all threads
            synchronized (lock) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        for (Thread thread : simulateThreads) {
            thread.interrupt();
        }

        System.out.println("Player: " + (losingPlayerIndex + 1) + " lost!\n");
        for (Player player : players) {
            System.out.println(player.name + ": " + player.getDeck());
        }
    }
}
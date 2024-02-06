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

        // Display each player's deck
        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();

        // Print initial pairs discarded by each player
        for (Player player : players) {
            Functions.removeInitialPairs(player);
        }
        System.out.println();

        // Create instances of Simulate and start them in separate threads
        List<Thread> simulateThreads = new ArrayList<>();
        for (Player player : players) {
            Simulate simulate = new Simulate(player, players, lock);
            Thread thread = new Thread(simulate);
            thread.start();
            simulateThreads.add(thread);
        }

        // Play the game
        int currentPlayerIndex = 0, losingPlayerIndex = -1;
        boolean gameEnded = false;
        while (!gameEnded) {
            synchronized (lock) {
                lock.notify(); // Notify the current player to play
            }

            try {
                Thread.sleep(1000); // Introduce delay for better readability
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean currentPlayerHasJoker = players.get(currentPlayerIndex).getDeck().stream().anyMatch(card -> card.getValue().equals("Joker"));
            int finalCurrentPlayerIndex = currentPlayerIndex;
            boolean allOtherPlayersEmpty = players.stream().filter(player -> !player.equals(players.get(finalCurrentPlayerIndex))).allMatch(Player::isEmptyDeck);
            // If all other players have empty decks and the current player is not the one with the Joker, determine the losing player (surely the one after losses)
            if (allOtherPlayersEmpty && !currentPlayerHasJoker) {
                System.out.println("\nGame ended! All other players have emptied their hands.");
                losingPlayerIndex = currentPlayerIndex;
                gameEnded = true;
            }

            synchronized (lock) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        // Stop all player threads
        for (Thread thread : simulateThreads) {
            thread.interrupt();
        }

        // Print the index of the winning player
        System.out.println("Player: " + (losingPlayerIndex + 1) + " lost!");

        // Print final decks of all players
        System.out.println();
        for (Player player : players) {
            System.out.println(player.name + ": " + player.getDeck());
        }
    }
}
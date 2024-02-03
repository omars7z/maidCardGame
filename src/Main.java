import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Scanner;

import Components.Generate;
import Components.Card;
import Components.Player;

class Main {
    public static void main(String[] args) {
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
            player.removeInitialPairs();
        }
        System.out.println();

        // Create instances of Simulate and start them in separate threads
        List<Thread> simulateThreads = new ArrayList<>();
        for (Player player : players) {
            Simulate simulate = new Simulate(player, players, player.lock);
            Thread thread = new Thread(simulate);
            thread.start();
            simulateThreads.add(thread);
        }

        // Play the game
        boolean gameEnded = false;
        int currentPlayerIndex = 0;
        while (!gameEnded) {
            Player currentPlayer = players.get(currentPlayerIndex);
            synchronized (currentPlayer.lock) {
                currentPlayer.lock.notify(); // Notify the current player to play
            }
            try {
                Thread.sleep(1000); // Introduce delay for better readability
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check if the current player's deck is empty to end the game
            if (currentPlayer.getDeck().isEmpty()) {
                gameEnded = true;
                System.out.println("Game Over! " + currentPlayer.name + " lost!");
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
}
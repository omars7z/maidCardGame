import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Scanner;

import Components.Generate;
import Components.Card;
import Components.Player;
import Utilities.Functions;

class Main implements Runnable {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players:");
        int numPlayers = scanner.nextInt();

        List<Player> players = Generate.createPlayers(numPlayers);
        List<Card> deck = Generate.generateDeck();
        Collections.shuffle(deck);

        Generate.dealCards(players, deck, numPlayers);

        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();

        for (Player player : players) {
            Functions.removeInitialPairs(player);
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
        int currentPlayerIndex = 0, losingPlayerIndex = -1;
        boolean gameEnded = false;
        while (!gameEnded) {
            Player currentPlayer = players.get(currentPlayerIndex);
            synchronized (currentPlayer.lock) {
                currentPlayer.lock.notify(); // Notify the current player to play
            }
            try {
                Thread.sleep(1000); //  dela
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // If all other players have empty decks and the current player is not the one with the Joker, determine the losing player (surely the one after losses)
            boolean currentPlayerHasJoker = currentPlayer.getDeck().stream().anyMatch(card -> card.getValue().equals("Joker"));
            boolean allOtherPlayersEmpty = players.stream().filter(player -> !player.equals(currentPlayer)).allMatch(Player::isEmptyDeck);
            if (allOtherPlayersEmpty || currentPlayerHasJoker) {
                losingPlayerIndex = currentPlayerIndex;
                gameEnded = true;
                System.out.println("\nGame ended! " + currentPlayer.name + " is the loser.");
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        // Stop all player threads
        for (Thread thread : simulateThreads) {
            thread.interrupt();
        }

        System.out.println("Player: " + (losingPlayerIndex + 1) + " lost!\n");
        for (Player player : players) {
            System.out.println(player.name + ": " + player.getDeck());
        }
    }

    public static void main(String[] args) {
        Thread mainThread = new Thread(new Main());
        mainThread.start();
    }
}

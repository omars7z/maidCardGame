import Components.Card;
import Components.Player;
import Utilities.Functions;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Simulate implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;
    private Player losingPlayer;

    public static int currentPlayerIndex;
    public CyclicBarrier barrier;

    public Simulate(Player player, List<Player> players, Object lock, CyclicBarrier barrier, int currentPlayerIndex) {
        this.player = player;
        this.players = players;
        this.lock = lock;
        this.barrier = barrier;
        this.currentPlayerIndex = currentPlayerIndex;
    }

    boolean isGameEnded() {
        int playersWithCards = 0;
        for (Player p : players) {
            if (!p.getDeck().isEmpty()) {
                playersWithCards++;
            }
        }
        return playersWithCards == 1;
    }

    private boolean hasLoser() {
        if (isGameEnded()) {
            for (Player p : players) {
                if (!p.getDeck().isEmpty() && p.getDeck().contains(new Card("Joker", ""))) {
                    losingPlayer = p;
                    return true;
                }
            }
        }
        return false;
    }

    boolean isMyTurn(int currentPlayerIndex) {
        return currentPlayerIndex == player.playerIndex;
    }

    @Override
    public void run() {
        // discardInitialPairs
        Functions.removeInitialPairs(player);
        System.out.println();
        // use cyclicBarrier
        try {
            barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (lock) {
            while (!isGameEnded() && !hasLoser()) {
                if (isMyTurn(currentPlayerIndex)) {
                    playTurn();
                    currentPlayerIndex++;
                    currentPlayerIndex %= players.size()-1;
                    lock.notifyAll();
                } else {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void playTurn() {
//        System.out.println("PLAYER HAND SIZE " + player.hand.size());
        Functions.drawCardFromPrevPlayer(player, player.hand, players, player.playerIndex);
        Functions.discardMatchingPairs(player, player.hand, player.discarded);
    }
}

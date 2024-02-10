import Components.Card;
import Components.Player;
import Utilities.Functions;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Threads implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;
    private Player losingPlayer;

    public static int currentPlayerIndex;
    public CyclicBarrier barrier;
    private final GameManager gameManager;

    public Threads(Player player, List<Player> players, Object lock, CyclicBarrier barrier, int currentPlayerIndex, GameManager gameManager) {
        this.player = player;
        this.players = players;
        this.lock = lock;
        this.barrier = barrier;
        this.currentPlayerIndex = currentPlayerIndex;
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        // discardInitialPairs
        Functions.removeInitialPairs(player);
        // use cyclicBarrier
        try {
            barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (lock) {
            while (!gameManager.isGameEnded() && !gameManager.hasLoser()) {
                if (gameManager.isMyTurn(currentPlayerIndex, player)) {
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
        Functions.drawCardFromPrevPlayer(player, player.hand, players, player.playerIndex);
        Functions.discardMatchingPairs(player, player.hand, player.discarded);
    }
}

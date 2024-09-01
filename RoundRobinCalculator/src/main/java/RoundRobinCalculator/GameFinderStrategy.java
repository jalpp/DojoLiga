package RoundRobinCalculator;

/**
 * The interface Game finder strategy.
 */
public interface GameFinderStrategy {

    /**
     * Find game boolean.
     *
     * @param player1 the player 1
     * @param player2 the player 2
     * @param color1  the color 1
     * @param color2  the color 2
     * @return the boolean
     */
    public boolean findGame(String player1, String player2, String color1, String color2);


}

package com.slethron.geneticoptimization.util;

import com.slethron.geneticoptimization.domain.BitString;
import com.slethron.geneticoptimization.domain.Knapsack;
import com.slethron.geneticoptimization.domain.NQueensBoard;
import com.slethron.geneticoptimization.domain.SudokuBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomGeneratorUtil {
    private static final SplittableRandom RANDOM = new SplittableRandom();

    private RandomGeneratorUtil() { }
    
    private static final int UTF_16_UPPER_BOUND = 127;
    private static final int UTF_16_LOWER_BOUND = 32;
    /**
     * Generates a string containing random UTF-16 characters. An arbitrary integer within the bounds of
     * 127 (exclusive) and 32 (inclusive) can be converted into a char using the method toChars(int)
     * provided via the class java.lang.Character.
     *
     * @param length The length of the string being generated.
     * @return The generated random string
     */
    public static String generateRandomString(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> RANDOM.nextInt(UTF_16_UPPER_BOUND - UTF_16_LOWER_BOUND) + UTF_16_LOWER_BOUND)
                .map(Character::toChars)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
    
    /**
     * Generates a BitString containing random boolean values.
     *
     * @param length The length of the string of bits to be generated
     * @return The generated random BitString
     */
    public static BitString generateRandomBitString(int length) {
        var bits = new boolean[length];
        
        for (var i = 0; i < length; i++) {
            bits[i] = RANDOM.nextBoolean();
        }
        
        return new BitString(bits);
    }
    
    /**
     * Generates an NQueensBoard object containing random integer values in each of it's columns that are
     * within the bounds of the board length.
     *
     * @param n The side length (or n) of the N-Queens board.
     * @return The generated random N-Queens board object
     */
    public static NQueensBoard generateRandomNQueensBoard(int n) {
        var board = new int[n];
        for (var i = 0; i < n; i++) {
            board[i] = RANDOM.nextInt(n);
        }
        
        return new NQueensBoard(board);
    }
    
    /**
     * Generates a knapsack object containing some of the items from a specified list of items. The items
     * are permutated in a way such that they are:
     * 1) random
     * 2) not all of the items specified in the list are also in the knapsack
     *
     * It is necessary to have remaining items in the list after execution of this method.
     * IllegalArgumentException is thrown if the case where the items to put are exhausted is met.
     *
     * @param maxWeight  The maxWeight of the knapsack object being generated
     * @param itemsToPut The items to randomly put in the bag
     * @return The generated random Knapsack object
     */
    public static Knapsack generateRandomKnapsack(int maxWeight, List<Knapsack.KnapsackItem> itemsToPut) {
        var items = new ArrayList<>(itemsToPut);
        var knapsack = new Knapsack(maxWeight);
        for (var i = items.size(); i >= 1; i--) {
            var itemToPut = items.get(RANDOM.nextInt(items.size()));
            if (knapsack.put(itemToPut)) {
                items.remove(itemToPut);
            }
        }
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Total value of items to put in knapsack must be greater than the max"
                    + " weight of the knapsack.");
        }
        
        return knapsack;
    }
    
    private static final int MIN_REQ_NUM_FILLED_CELLS = 25;
    /**
     * Generates a random Sudoku board object with the specified number of filled static cells, such that the board
     * exists in an unsolved state. The board is solvable.
     * @param numberOfFilledCells The number of filled cells between
     * @return The resulting random SudokuBoard object
     */
    public static SudokuBoard generateRandomSudokuBoard(int numberOfFilledCells) {
        if (numberOfFilledCells < MIN_REQ_NUM_FILLED_CELLS) {
            throw new IllegalArgumentException("This generator can only generate boards with minimum "
                    + MIN_REQ_NUM_FILLED_CELLS + " filled cells.");
        } else if (numberOfFilledCells > SudokuBoard.SIZE * SudokuBoard.SIZE) {
            throw new IllegalArgumentException("Cannot generate a board with number of filled cells greater than the "
                    + "number of total cells.");
        }
    
        var board = SudokuUtil.generateRandomSolvedSudokuBoard();
    
        var numberOfEmptyCells = 0;
        var totalNumberOfCells = SudokuBoard.SIZE * SudokuBoard.SIZE;
        while (numberOfEmptyCells < totalNumberOfCells - numberOfFilledCells) {
            var row = RANDOM.nextInt(SudokuBoard.SIZE);
            var column = RANDOM.nextInt(SudokuBoard.SIZE);
            var removed = board.get(row, column);
        
            if (removed == SudokuBoard.EMPTY) {
                continue;
            }
        
            board.remove(row, column);
            numberOfEmptyCells++;
        
            if (!SudokuUtil.isSolvable(board)) {
                board.set(row, column, removed);
                numberOfEmptyCells--;
            }
        }
    
        for (var row = 0; row < SudokuBoard.SIZE; row++) {
            for (var column = 0; column < SudokuBoard.SIZE; column++) {
                if (board.get(row, column) != SudokuBoard.EMPTY) {
                    board.setStatic(row, column);
                }
            }
        }
    
        return board;
    }
}

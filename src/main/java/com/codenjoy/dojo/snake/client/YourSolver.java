package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.logger.Logger;
import com.codenjoy.dojo.snake.solver.SnakeAlgorithm;

import java.util.Date;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        try {
            long startTime = new Date().getTime();
            System.out.println("STOPWATCH STARTED");
            this.board = board; // default code
//        System.out.println(board.toString());// default code
            if (board.isGameOver()) {
                Logger.getInstance().logLoss(board);
                return Direction.UP.toString();
            }

            SnakeAlgorithm algorithm = new SnakeAlgorithm(board);

            String move = algorithm.makeMove();
            long usedTime = new Date().getTime() - startTime;
            System.out.println("USED TIME: " + (usedTime) + "ms");

            if (usedTime >= 900) {
                Logger.getInstance().logTimeException( "TIME: " + usedTime + "ms\n" + board.toString());
            }
            return move;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().logError(e, board);
            return Direction.UP.toString();
        }
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://206.81.16.237/codenjoy-contest/board/player/ejt0ofmn4b8re6hr7nvw?code=6567403124593564385",
                new YourSolver(new RandomDice()),
                new Board());
    }

}

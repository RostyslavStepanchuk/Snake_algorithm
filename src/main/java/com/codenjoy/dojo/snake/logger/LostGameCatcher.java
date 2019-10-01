package com.codenjoy.dojo.snake.logger;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;

import java.util.ArrayList;
import java.util.List;

public class LostGameCatcher {
    public void catchGameLoss(Board board) {
        List<Point> barriers = new ArrayList<>(board.getBarriers());
        barriers.remove(board.getHead());
        barriers.removeAll(board.getStones());
        if (board.isGameOver() || barriers.contains(board.getHead())) {
            throw new RuntimeException("GAME WAS LOST");
        }
    }
}

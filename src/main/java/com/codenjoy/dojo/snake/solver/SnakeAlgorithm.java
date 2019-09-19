package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;

import java.util.Optional;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.Direction.UP;

public class SnakeAlgorithm {
    private final Board board;

    public SnakeAlgorithm(Board board) {
        this.board = board;
    }

    public void ConsiderMove(Board board) {

    }

    private String getMoveString(Point dest) {
        Point me = board.getHead();
        if (me.getX()>dest.getX()) return LEFT.toString();
        if (me.getX()<dest.getX()) return RIGHT.toString();
        if (me.getY()>dest.getY()) return DOWN.toString();
        if (me.getY()<dest.getY()) return UP.toString();
        throw new RuntimeException(String.format("Unknown destination: me: %s, des: %s ", me.toString(), dest.toString()));
    }

    public String makeMove(){
        FieldData fieldData = new FieldData(board);
        if (!fieldData.isValid()) {
            return UP.toString(); // just useless direction to return something;
        }
        Navigator nav = new Navigator(fieldData);

        Optional<Route> shortest = nav.getShortestRoute();

        if (shortest.isPresent()) {
            Route r = shortest.get();
            return getMoveString(r.getLast());
        } else {
            return getMoveString(nav.nearestSafeRandom());
        }
    }
}

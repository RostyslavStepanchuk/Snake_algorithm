package com.codenjoy.dojo.snake.mycode.helpers;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client.Board;

import java.util.*;


import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.Direction.UP;

public class Lifebuoy {
    private final Board board;

    public Lifebuoy(Board board) {
        this.board = board;
    }

    private String getMoveString(Point dest) {
        if (dest == null) return UP.toString();
        Point me = board.getHead();
        if (me.getY()<dest.getY()) return UP.toString();
        if (me.getX()<dest.getX()) return RIGHT.toString();
        if (me.getX()>dest.getX()) return LEFT.toString();
        else return DOWN.toString();
    }

    public String makeLastHopeMove(){
        return getMoveString(getNearestRandomSafe());
    }

    private Point getNearestRandomSafe() {
        List<Point> moves = new ArrayList<Point>() {{
            add(new PointImpl(0, -1));
            add(new PointImpl(-1, 0));
            add(new PointImpl(1, 0));
            add(new PointImpl(0, 1));
        }};

        List<Point> barriers = board.getBarriers();
        barriers.removeAll(board.getStones());
        Point head = board.getHead();
        Random dice = new Random();
        Point target;
        while (moves.size() > 0) {
            int i = dice.nextInt(moves.size());
            target = new PointImpl(head){{ change(moves.get(i));}};
            if (!barriers.contains(target)) {
                return target;
            }
            moves.remove(target);
        }
        return null;
    }

}

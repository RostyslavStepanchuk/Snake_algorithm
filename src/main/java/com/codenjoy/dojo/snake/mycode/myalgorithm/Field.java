package com.codenjoy.dojo.snake.mycode.myalgorithm;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.mycode.helpers.FieldData;
import com.codenjoy.dojo.snake.mycode.helpers.Route;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class Field {
    private final static int DIMX = 15;
    private final static int DIMY = 15;
    private final int[][] field;

    final static int BARRIER = -5;
    final static int RESERVE = -4;
    final static int HEAD = -10;
    final static int SNAKE_STEP = -10;
    final static int SNAKE_LIMITER = -9;

    private Iterator<Point> snakeTail;

    Field(FieldData data){
        this.field = new int[DIMY][DIMX];
        snakeTail = data.getSnake().descendingIterator();
        setEnvironment(data);
    }

    int get(Point point) {
        return field[point.getY()][point.getX()];
    }

    void set(Point point, int val) {
        field[point.getY()][point.getX()] = val;
    }

    private void setEnvironment (FieldData data) {
        setBarriers(data.getWalls());
        setStones(data.getStones());
        setSnake(data.getSnake());
    }

    private void setSnake(LinkedList<Point> snake) {
        int counter = HEAD;
        for (Point point : snake) {
            set(point, counter);
            counter += SNAKE_STEP;
        }
    }

    void resetSnake(LinkedList<Point> snake) {
        setSnake(snake);
        snakeTail = snake.descendingIterator();
    }

    private void setBarriers(List<Point> barriers) {
        barriers.forEach(point -> set(point, BARRIER));
    }

    void setReserved(List<Point> barriers) {
        barriers.forEach(point -> set(point, RESERVE));
    }

    private void setStones(List<Point> stones) {
        stones.forEach(point -> set(point, BARRIER));
    }

    void moveSnakeTail() {
        if (snakeTail.hasNext()) {
            set(snakeTail.next(), 0);
        }
    }

    boolean isAvailable(Point point) { return get(point) == 0; }

    boolean isMark(Point point) { return get(point) > 0; }

    boolean isSafe(Point point) {
        return get(point) >= 0;
    }

    boolean isSnake(Point point) { return get(point) < SNAKE_LIMITER; }

    boolean isBarrier(Point point) { return get(point) < 0; }

    private String strElement(Point p, Route path) {
        int val = get(p);
        if (val == BARRIER) return "XXX";
        if (val < SNAKE_LIMITER) return String.format("%3s", String.valueOf(val/10));
        if (!path.isEmpty()){
            if (path.contains(p)) {
                if (p.equals(path.getFirst())) return "  ☺";
                return String.format("%3d", val);
            }
            return " . ";
        }
        return String.format("%3d", val);

    }

    void markPath(Route path) {
        int[] marker = new int[1];
        marker[0] = 1;

        path.descendingIterator().forEachRemaining(point -> {
            set(point, marker[0]);
            marker[0]++;
        });
    }

    @Override
    public String toString(){
        return toString(new Route());
    }

    public String toString(Route route){
        StringBuilder sb = new StringBuilder();
        for (int c = -1; c < DIMX; c++){
            sb.append(String.format("%3s", String.valueOf(c)));
        }
        sb.append("\n");
        for (int y = 0; y < DIMY; y++) {
            sb.append(String.format("%3s", String.valueOf(y)));
            for (int x = 0; x < DIMX; x++) {
                Point p = new PointImpl(x, y);
                sb.append(strElement(p, route));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}

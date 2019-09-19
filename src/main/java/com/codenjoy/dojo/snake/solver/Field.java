package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.List;

class Field {
    private final static int DIMX = 15;
    private final static int DIMY = 15;
    private final int[][] field;

    final static int BARRIER = -5;
    final static int HEAD = -10;
    final static int SNAKE_STEP = -10;
    final static int SNAKE_LIMITER = -9;

    Field(FieldData data){
        this.field = new int[DIMY][DIMX];
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

    void setSnake(List<Point> snake) {
        int counter = HEAD;
        for (Point point : snake) {
            set(point, counter);
            counter += SNAKE_STEP;
        }
    }

    void setBarriers(List<Point> barriers) {
        barriers.forEach(point -> set(point, BARRIER));
    }

    void setStones(List<Point> stones) {
        stones.forEach(point -> set(point, BARRIER));
    }

    boolean isSafe(Point point) {
        return get(point) >= 0;
    }

    private String strElement(Point p, Route path) {
        int val = get(p);
        if (val == BARRIER) return "XXX";
        if (val < SNAKE_LIMITER) return String.format("%3s", String.valueOf(val));
//        if (p.equals(start)) return " s ";
        if (!path.isEmpty()){
            if (path.contains(p)) return String.format("%3d", val);
            if (p.equals(path.getLast())) return " ☺ ";
            return " - ";
        }
        return String.format("%3d", val);

    }

    void print(){
        print(new Route());
    }

    void markPath(Route path) {
        int[] marker = new int[1];
        marker[0] = 1;

        path.descendingIterator().forEachRemaining(point -> {
            set(point, marker[0]);
            marker[0]++;
        });
    }
    
    void print(Route path) {
        for (int y = 0; y < DIMY; y++) {
            for (int x = 0; x < DIMX; x++) {
                Point p = new PointImpl(x, y);
                System.out.print(strElement(p, path));
            }
            System.out.println();
        }
        System.out.println();
    }
}

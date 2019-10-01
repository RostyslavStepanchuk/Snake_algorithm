package com.codenjoy.dojo.snake.sandbox;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;

import java.util.LinkedList;
import java.util.List;

public class BoardMock extends Board {
    private final List<Point> apples;
    private final List<Point> stones;
    private final List<Point> snake;
    private final List<Point> walls;
    private final Point head;

    public BoardMock(List<Point> snake, Point head, List<Point> apples, List<Point> stones,  List<Point> walls) {
        this.snake = snake;
        this.head = head;
        this.apples = apples;
        this.stones = stones;
        this.walls = walls;
    }

    @Override
    public List<Point> getApples() {
        return apples;
    }

    @Override
    public Point getHead() {
        return head;
    }

    @Override
    public List<Point> getSnake() {
        return snake;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public List<Point> getStones() {
        return stones;
    }

    @Override
    public List<Point> getWalls() {
        return walls;
    }
}

package com.codenjoy.dojo.snake.helpers;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;

import java.util.LinkedList;
import java.util.List;

public final class FieldData {
    private final LinkedList<Point> snake;
    private Point head;
    private final List<Point> apples;
    private final List<Point> stones;
    private final List<Point> walls;

    public List<Point> getWalls() {
        return walls;
    }

    public FieldData(Board board, LinkedList<Point> snake) {
        this(snake, board.getHead(), board.getApples(), board.getStones(), board.getWalls());
    }

    public FieldData(LinkedList<Point> snake, Point head, List<Point> apples, List<Point> stones, List<Point> walls) {
        this.snake = snake;
        this.head = head;
        this.apples = apples;
        this.stones = stones;
        this.walls = walls;
    }

    public LinkedList<Point> getSnake() {
        return snake;
    }

    public List<Point> getApples() {
        return apples;
    }

    public List<Point> getStones() {
        return stones;
    }

    public Point getHead() {
        return head;
    }

    public void considerStoneAsSecondApple(){
        apples.addAll(stones);
        stones.clear();
    }
}

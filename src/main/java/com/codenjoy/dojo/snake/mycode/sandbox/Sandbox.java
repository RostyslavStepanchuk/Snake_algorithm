package com.codenjoy.dojo.snake.mycode.sandbox;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.mycode.myalgorithm.SnakeAlgorithm;

import java.util.*;

public class Sandbox {
    private LinkedList<Point> snake;
    private List<Point> walls;
    private List<Point> stones;
    private List<Point> apples;

    public Sandbox(){
        // \[(\d+,\d+)\],
        //     SNAIL
//        int[][] snakePoints = {{8,9},{9,9},{10,9},{10,8},{10,7},{10,6},{9,6},{8,6},{7,6},{6,6},{5,6},{4,6},{4,7},{4,8},{4,9},{4,10},{5,10},{6,10},{7,10},{8,10},{9,10},{10,10}};
//  UP AGAINST THE WALL
//        int[][] snakePoints = {{1,4},{2,4},{3,4},{3,3},{4,3},{5,3},{6,3},{7,3},{8,3},{9,3},{10,3},{11,3},{12,3},{13,3},{13,4},{12,4},{12,5},{13,5},{13,6},{13,7},{13,8},{13,9},{13,10},{13,11},{12,11},{11,11},{10,11},{9,11}};
//  RESEARCHING COUNTAVAILABLE SURROUNDINGS
//        int[][] snakePoints = {{2,12},{2,11},{2,10},{2,9},{1,9},{1,8},{1,7},{1,6},{1,5},{1,4},{1,3},{1,2},{2,2},{3,2},{4,2},{5,2},{6,2},{7,2},{8,2},{8,3},{8,4},{7,4},{6,4},{5,4},{4,4},{4,3},{3,3},{2,3},{2,4},{2,5},{2,6},{2,7},{2,8},{3,8},{4,8},{4,9},{4,10},{4,11},{4,12},{5,12},{6,12},{7,12},{8,12},{9,12},{9,13},{10,13},{10,12},{10,11},{10,10},{10,9},{10,8},{11,8},{12,8},{12,9}};
// NOT OPTIMAL WAY OUT
//        int [][] snakePoints = {{2,9},{1,9},{1,10},{1,11},{1,12},{1,13},{2,13},{3,13},{4,13},{5,13},{6,13},{7,13},{8,13},{9,13},{10,13},{11,13},{11,12},{12,12},{12,13},{13,13},{13,12},{13,11},{13,10},{13,9},{12,9},{12,10},{12,11},{11,11},{11,10},{11,9},{11,8},{12,8},{13,8},{13,7},{12,7},{11,7},{10,7},{9,7},{8,7},{8,8},{8,9},{8,10},{7,10},{7,9},{7,8},{6,8},{5,8},{4,8},{3,8},{2,8},{1,8},{1,7}};

        int[][] snakePoints ={{11,13},{12,13},{12,12},{12,11},{12,10},{12,9},{11,9},{10,9},{9,9},{8,9},{8,8},{7,8},{7,7},{8,7},{9,7},{9,8},{10,8},{11,8},{12,8},{12,7},{13,7},{13,6},{12,6},{11,6},{11,7},{10,7},{10,6},{10,5},{10,4},{10,3},{9,3},{8,3},{7,3},{6,3},{5,3},{4,3},{3,3},{3,4},{4,4},{5,4},{5,5},{5,6},{5,7},{5,8},{5,9},{5,10},{5,11},{5,12},{6,12},{7,12},{8,12},{9,12},{10,12},{5,8}};
        snake = buildSnake(snakePoints);
        getWalls();
        apples = new ArrayList<Point>(){{add(new PointImpl(2,2));}};
        stones = new ArrayList<Point>(){{add(new PointImpl(2,13));}};

    }

    private LinkedList<Point> buildSnake(int[][] points){
        LinkedList<Point> result = new LinkedList<>();
        for (int[] point: points) {
            result.add(new PointImpl(point[0], point[1]));
        }
        return result;
    }

    private List<Point> getWalls(){
        walls = new ArrayList<>();
        for (int y = 0; y < 15 ; y++) {
            for (int x = 0; x < 15 ; x++) {
                if (y == 0 || x == 0 || y == 14 || x == 14){
                    walls.add(new PointImpl(x,y));
                }
            }
        }
        return walls;
    }
    
    private void simulateMove(){
        SnakeAlgorithm testAlgorithm = new SnakeAlgorithm(new BoardMock(snake, snake.getFirst(), apples, stones, walls));
        Point next = testAlgorithm.makeSandboxMove();
        snake.addFirst(next);
        snake.removeLast();
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Sandbox sandbox = new Sandbox();
        for (;;) {
            scan.nextLine();
            sandbox.simulateMove();
        }

    }
}

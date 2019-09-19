package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

import static com.codenjoy.dojo.snake.model.Elements.*;

public class SnakeBuilder {

    public static final int CONNECTION_HORIZONTAL = 10;
    public static final int CONNECTION_VERTICAL = 20;
    public static final int OFFSET_PLUS_X = 0;
    public static final int OFFSET_MINUS_X = 1;
    public static final int OFFSET_PLUS_Y = 2;
    public static final int OFFSET_MINUS_Y = 3;
    public static final int NOT_NEAR = -1;
    
    public static final Map<SymbolsMatchKey, List<Elements>> matchingSymbols = new HashMap<SymbolsMatchKey, List<Elements>>(){{
        put(new SymbolsMatchKey(HEAD_DOWN, OFFSET_PLUS_Y), Arrays.asList(TAIL_END_UP, TAIL_VERTICAL, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));

        put(new SymbolsMatchKey(HEAD_UP, CONNECTION_VERTICAL), Arrays.asList(TAIL_END_DOWN, TAIL_VERTICAL, TAIL_RIGHT_UP, TAIL_LEFT_UP));
        put(new SymbolsMatchKey(HEAD_UP, CONNECTION_HORIZONTAL), Collections.emptyList());
        put(new SymbolsMatchKey(HEAD_LEFT, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(HEAD_LEFT, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_END_RIGHT, TAIL_HORIZONTAL, TAIL_LEFT_DOWN, TAIL_LEFT_UP));
        put(new SymbolsMatchKey(HEAD_RIGHT, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(HEAD_RIGHT, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_END_LEFT, TAIL_HORIZONTAL, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP));

        put(new SymbolsMatchKey(TAIL_END_DOWN, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_DOWN, CONNECTION_HORIZONTAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_UP, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_UP, CONNECTION_HORIZONTAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_RIGHT, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_RIGHT, CONNECTION_HORIZONTAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_LEFT, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_END_LEFT, CONNECTION_HORIZONTAL), Collections.emptyList());

        put(new SymbolsMatchKey(TAIL_HORIZONTAL, CONNECTION_VERTICAL), Collections.emptyList());
        put(new SymbolsMatchKey(TAIL_HORIZONTAL, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_END_RIGHT, TAIL_END_LEFT, TAIL_LEFT_UP, TAIL_RIGHT_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN, TAIL_HORIZONTAL));
        put(new SymbolsMatchKey(TAIL_VERTICAL, CONNECTION_VERTICAL), Arrays.asList(TAIL_END_DOWN, TAIL_END_UP, TAIL_LEFT_UP, TAIL_RIGHT_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN, TAIL_VERTICAL));
        put(new SymbolsMatchKey(TAIL_VERTICAL, CONNECTION_HORIZONTAL), Collections.emptyList());

        put(new SymbolsMatchKey(TAIL_LEFT_DOWN, CONNECTION_VERTICAL), Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_RIGHT_UP, TAIL_LEFT_UP));
        put(new SymbolsMatchKey(TAIL_LEFT_DOWN, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_UP, TAIL_RIGHT_DOWN));

        put(new SymbolsMatchKey(TAIL_RIGHT_DOWN, CONNECTION_VERTICAL), Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_LEFT_UP, TAIL_RIGHT_UP));
        put(new SymbolsMatchKey(TAIL_RIGHT_DOWN, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN));

        put(new SymbolsMatchKey(TAIL_LEFT_UP, CONNECTION_VERTICAL), Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));
        put(new SymbolsMatchKey(TAIL_LEFT_UP, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP));

        put(new SymbolsMatchKey(TAIL_RIGHT_UP, CONNECTION_VERTICAL), Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_RIGHT_DOWN, TAIL_LEFT_DOWN));
        put(new SymbolsMatchKey(TAIL_RIGHT_UP, CONNECTION_HORIZONTAL), Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN));
    }};
    
    public static final Map<Elements, List<Integer>> allowedOffsets = new HashMap<Elements, List<Integer>>(){{
        put(HEAD_DOWN, Collections.singletonList());
        put(HEAD_LEFT, Collections.singletonList(OFFSET_PLUS_X));
        put(HEAD_RIGHT, Collections.singletonList(OFFSET_MINUS_X));
        put(HEAD_UP, Collections.singletonList(OFFSET_MINUS_Y));
        put(TAIL_END_DOWN, Collections.emptyList());
        put(TAIL_END_LEFT, Collections.emptyList());
        put(TAIL_END_UP, Collections.emptyList());
        put(TAIL_END_RIGHT, Collections.emptyList());
        put(TAIL_HORIZONTAL, Arrays.asList(OFFSET_MINUS_X, OFFSET_PLUS_X));
        put(TAIL_VERTICAL, Arrays.asList(OFFSET_PLUS_Y, OFFSET_MINUS_Y));
        put(TAIL_LEFT_DOWN, Arrays.asList(OFFSET_MINUS_X, OFFSET_MINUS_Y));
        put(TAIL_LEFT_UP, Arrays.asList(OFFSET_MINUS_X, OFFSET_PLUS_Y));
        put(TAIL_RIGHT_DOWN, Arrays.asList(OFFSET_PLUS_X, OFFSET_MINUS_Y));
        put(TAIL_RIGHT_UP, Arrays.asList(OFFSET_PLUS_X, OFFSET_PLUS_Y));
    }};

    public List<Point> build(Board board, List<Point> boardSnake, Point head) {
        LinkedList<Point> result = new LinkedList<>();
        LinkedList<Point> origin = new LinkedList<>(boardSnake);

        //TODO Delete check and refactor head adding to result
//        result.add(origin.pollFirst());

        origin.forEach(System.out::println);
        Point h = origin.pollFirst();
        if (!h.equals(head)  ) {
            throw new RuntimeException(String.format("First item %s from board.getSnake is not head %s", h, head));
        } else {
            result.add(h);
        }

        //TODO delete console messages and counter
        int counter =0;
        while (!origin.isEmpty() && counter < 300) {
            Point p = origin.pollFirst();
            if (pointsConnected(p, result.getLast(), board)) {
                System.out.printf("POINT %s WAS ADDED TO POINT %s\n",p, result.getLast());
                result.add(p);
            } else {
                origin.add(p);
            }
            counter++;

        }
        if (counter == 300) throw new RuntimeException("Connections overflow");
        System.out.println(counter);
        return result;
    }

    private  boolean pointsConnected(Point connectWhat, Point connectTo, Board board){
        int offset = pointsRelation(connectWhat, connectTo);
        if (offset != NOT_NEAR) {
            Elements whatElement = board.getAt(connectWhat);
            if (offsetIsValid(whatElement, offset)) {
                return symbolsConnectable(connectWhat, new SymbolsMatchKey(board.getAt(connectTo), getConnectionDirection(offset)) , board);
            }
            return false;
        }
        return false;
    }


    //TODO remove console messages and variable result
    private boolean symbolsConnectable (Point connectWhat, SymbolsMatchKey matchKey, Board board) {
        Elements current = board.getAt(connectWhat);
        List<Elements> relevantConnections = matchingSymbols.get(matchKey);
        boolean result = relevantConnections.stream().anyMatch(element -> element == current);
        System.out.printf("Target item: %c, ", matchKey.getElement().ch());
        System.out.printf("candidate %c matching with ", current.ch());
        relevantConnections.forEach(System.out::print);
        System.out.println(". Result: " + result);
        return result;
    }

    //TODO remove console messages and variable result
    private int pointsRelation(Point p1, Point p2) {
        System.out.printf("Getting relation between %s and %s\n", p1, p2);
        int xOffset = p1.getX()-p2.getX();
        int yOffset = p1.getY()-p2.getY();
        if (Math.abs(xOffset) - Math.abs(yOffset) == 1){
            if (xOffset == -1) return OFFSET_MINUS_X;
            if (xOffset == 1) return OFFSET_PLUS_X;
            if (yOffset == -1) return OFFSET_MINUS_Y;
            if (yOffset == 1 ) return OFFSET_PLUS_Y;
            throw new RuntimeException(String.format("Invalid offsets x:%d , y:%d", xOffset, yOffset));
        }  else return NOT_NEAR;
    }

    private boolean offsetIsValid(Elements el, int offset) {
        return allowedOffsets.get(el).stream().anyMatch(integer -> integer == offset);
    }

    private int getConnectionDirection(int offset) {
        if (offset == OFFSET_MINUS_X || offset == OFFSET_PLUS_X) return CONNECTION_HORIZONTAL;
        if (offset == OFFSET_MINUS_Y || offset == OFFSET_PLUS_Y) return CONNECTION_VERTICAL;
        throw new RuntimeException("Invalid offset: " + offset);
    }





}

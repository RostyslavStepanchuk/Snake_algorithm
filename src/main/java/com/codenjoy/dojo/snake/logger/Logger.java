package com.codenjoy.dojo.snake.logger;

import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.helpers.Route;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Consumer;

public class Logger {
    private static int errorCount = 0;
    private static final int HISTORY_MOVES_LIMIT = 30;
    private static Logger instance = new Logger();
    private static final File file = new File("./log", "snake.txt");
    private static LinkedList<String> boardHistory = new LinkedList<>();
    private final StringBuilder moveDescription = new StringBuilder();
    public static Logger getInstance() {
        return instance;
    }


    private SimpleDateFormat template = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Logger() {
    }

    static {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to get logger file");
        }
    }

    public void saveMoveData(String board) {
        boardHistory.add(board);
        if (boardHistory.size() > HISTORY_MOVES_LIMIT) {
            boardHistory.removeFirst();
        }
    }

    public void addLogItem(String caseName, String details){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(logDivider(caseName));
            out.write(details);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public void logError(Exception e) {
        try {
            File errorLogFile = new File("./log/errors", "error" + ++errorCount + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(errorLogFile, true));
            out.write(logDivider("Error"));
            out.write(e.getMessage() + "\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element: stackTrace) {
                out.write(element.toString() + "\n");
            }
            for (String move: boardHistory) {
                out.write(move);
            }
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public String logDivider(String caseName) {
        return "\n" + template.format(new Date()) + ": ===========================" + caseName.toUpperCase() + "===========================" + "\n";
    }

    public StringBuilder getMoveDescription(){
        return moveDescription;
    }

    public void describeMoveResult(Route result) {
        moveDescription.append("RESULT ROUTE: ");
        result.forEach(moveDescription::append);
        moveDescription.append("\n");
        moveDescription.append("NEXT MOVE: ").append(result.getLast()).append("\n");
    }

}

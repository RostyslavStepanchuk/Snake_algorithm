package com.codenjoy.dojo.snake.logger;

import com.codenjoy.dojo.snake.client.Board;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    private static Logger instance = new Logger();
    private static final File file = new File("./log", "snake.txt");
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

    public void logLoss(Board board){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(formatLogData(LogTypes.LOSS, "=========================== GAME LOST ======================================="));
            out.write(board.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public void logSnakeSortFail(String sortHistory){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(formatLogData(LogTypes.SNAKE_SORT_FAIL, "=========================== SNAKE SORT FAIL ======================================="));
            out.write(sortHistory);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public void logTimeException(String sortHistory){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(formatLogData(LogTypes.SNAKE_SORT_FAIL, "=========================== LONG TIME EXECUTION ======================================="));
            out.write(sortHistory);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public void logProblem(String problem, String message){
//        try {
//            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
//            out.write(formatLogData(LogTypes.SNAKE_SORT_FAIL, "===========================" + problem.toUpperCase() + "======================================="));
//            out.write(message);
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error while logging file");
//        }
    }

    public void logSystemError(Exception e) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(formatLogData(LogTypes.ERROR, "=========================== SYSTEM ERROR ======================================="));
            out.write(e.getMessage());
            out.write(Arrays.toString(e.getStackTrace()));
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public void logError(Exception e, Board board) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(formatLogData(LogTypes.ERROR, "=========================== ERROR ======================================="));
            out.write(e.getMessage());
            out.write(Arrays.toString(e.getStackTrace()));
            out.write(board.toString());
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error while logging file");
        }
    }

    public String formatLogData(Enum<LogTypes> type, String message) {
        return "\n" + template.format(new Date()) + String.format("[%s] ", type.name()) + message + "\n";
    }

}

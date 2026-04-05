package org.falexgl.utils.log;

import org.falexgl.cli.menu.MainMenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.*;
import java.util.stream.Stream;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("AppLogger");

    static {
        try {
            FileHandler fileHandler = new FileHandler("app_%g.log", 1024 * 1024, 5, true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    return String.format(
                            "%1$tF %1$tT [%2$s] %3$s%n",
                            logRecord.getMillis(),
                            logRecord.getLevel().getName(), // INFO, WARNING, SEVERE
                            logRecord.getMessage()
                    );
                }
            });
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.out.println("Error handling log file: " + e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(java.util.logging.Level level, String message, Exception e) {
        logger.log(level, message, e);
    }

    public static void showLogs() {
        printLastLines(0, true); // 0 = all logs
    }

    public static void showLogsInDaemonMode() {
        printLastLines(0, false); // 0 = all logs
    }

    public static void printLogsToConsole(int maxLines) {
        printLastLines(maxLines, false);
    }

    public static void printLastLines(int maxLines, boolean waitForEnter) {
        Path root = Paths.get("").toAbsolutePath();
        String regex = "app_(\\d+)\\.log";

        try (var stream = Files.list(root)) {
            Optional<Path> latestFile = stream
                    .filter(p -> p.getFileName().toString().matches(regex))
                    .max(Comparator.comparingInt(p -> {
                        String name = p.getFileName().toString();
                        return Integer.parseInt(name.replaceAll("app_|\\.log", ""));
                    }));

            if (latestFile.isEmpty()) {
                System.out.println("[INFO] No log file found.");
                if (waitForEnter) goBack();
                return;
            }

            List<String> allLines = Files.readAllLines(latestFile.get());
            List<String> toShow = (maxLines > 0 && allLines.size() > maxLines)
                    ? allLines.subList(allLines.size() - maxLines, allLines.size())
                    : allLines;

            System.out.println();
            System.out.println("=== LOG: " + latestFile.get().getFileName()
                    + (maxLines > 0 ? " (last " + toShow.size() + " lines)" : "") + " ===");
            toShow.forEach(System.out::println);
            System.out.println("=== END OF LOG ===");
            System.out.println();

        } catch (IOException e) {
            AppLogger.error(Level.SEVERE, "Error reading files in directory: ", e);
        }

        if (waitForEnter) goBack();
    }

    private static void goBack() {
        Scanner sc =  new Scanner(System.in);
        System.out.println("Press enter to continue...");
        sc.nextLine();
        MainMenu.getInstance().initMenu();
    }
}

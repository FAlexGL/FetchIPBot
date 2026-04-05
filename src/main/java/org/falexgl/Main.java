package org.falexgl;

import org.falexgl.bot.FetchIPBot;
import org.falexgl.cli.menu.MainMenu;
import org.falexgl.utils.log.AppLogger;
import org.falexgl.utils.settings.SettingFileHelper;

public class Main {

    static final String VERSION     = "1.0.0";
    static final String APP_NAME    = "FetchIPBot";
    static final String DESCRIPTION = "Monitor your public IP and notify changes via Telegram.";

    public static void main(String[] args) {

        if (args.length == 0) {
            AppLogger.info("App initiated");
            SettingFileHelper.checkOrCreateSettingFile();
            MainMenu.getInstance().initMenu();
            return;
        }

        switch (args[0]) {

            case "--daemon" -> {
                if (args.length < 2) {
                    printError("--daemon requires a period in minutes. Example: fetchipbot --daemon 5");
                    System.exit(1);
                }
                try {
                    int period = Integer.parseInt(args[1]);
                    if (period <= 0) throw new NumberFormatException();

                    AppLogger.info("Initiating daemon mode...");
                    SettingFileHelper.checkOrCreateSettingFile();
                    FetchIPBot.getInstance().initFetchIpBot(period);
                    AppLogger.info("App initiated in daemon mode. Period: " + period + " min.");

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        FetchIPBot.getInstance().stopBot();
                        AppLogger.info("Daemon stopped via shutdown hook.");
                    }));

                    Thread.currentThread().join();

                } catch (NumberFormatException e) {
                    printError("Period must be a positive integer. Example: fetchipbot --daemon 5");
                    System.exit(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            case "--version", "-v" -> System.out.println(APP_NAME + " v" + VERSION);

            case "--description", "--desc" -> {
                System.out.println(APP_NAME + " v" + VERSION);
                System.out.println(DESCRIPTION);
            }

            case "--help", "-h" -> printHelp();

            case "--logs", "-logs" -> {
                if (args.length > 1) {
                    int lines = Integer.parseInt(args[1]);
                    if (lines <= 0) throw new NumberFormatException();
                    AppLogger.printLastLines(lines, false);
                } else {
                    AppLogger.showLogsInDaemonMode();
                }
            }

            default -> {
                printError("Unknown option: " + args[0]);
                System.out.println("Run 'fetchipbot --help' for usage.");
                System.exit(1);
            }
        }
    }

    // -------------------------------------------------------------------------

    private static void printHelp() {
        System.out.println();
        System.out.println(APP_NAME + " v" + VERSION + " — " + DESCRIPTION);
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("  fetchipbot [OPTION]");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("  (none)              Launch interactive menu");
        System.out.println("  --daemon <minutes>  Start bot in daemon mode (used by systemd)");
        System.out.println("  --version,  -v      Show version number");
        System.out.println("  --description       Show app name and description");
        System.out.println("  --help,     -h      Show this help message");
        System.out.println();
        System.out.println("SERVICE COMMANDS (managed via bash wrapper):");
        System.out.println("  --start             Start the daemon");
        System.out.println("  --stop              Stop the daemon");
        System.out.println("  --restart           Restart the daemon");
        System.out.println("  --status            Show daemon status");
        System.out.println("  --logs [N]          Show last N log lines (default: 50 | 0 = all)");
        System.out.println("  --logs-live         Follow logs in real time");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  fetchipbot                  # interactive menu");
        System.out.println("  fetchipbot --start          # start daemon");
        System.out.println("  fetchipbot --logs 100       # last 100 log lines");
        System.out.println("  fetchipbot --status         # daemon status");
        System.out.println();
    }

    private static void printError(String msg) {
        System.err.println("[ERROR] " + msg);
    }
}
package org.falexgl.utils.settings;

import org.falexgl.models.Credentials;
import org.falexgl.utils.log.AppLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;

public class SettingFileHelper {

    static final Path route = Paths.get("/opt/fetchipbot/setting.txt");
    static final String firstLine = "TOKEN:";
    static final String secondLine = "CHAT_ID:";


    public static void checkOrCreateSettingFile() {
        if (!Files.exists(route)) {
            AppLogger.error(Level.WARNING, "No config file found.", null);
            createFile();
            checkFile();
        } else {
            checkFile();
        }
    }

    public static Credentials getCredentials() {
        String token = "";
        String chatId = "";
        try (BufferedReader br = Files.newBufferedReader(route)) {
            token = br.readLine().substring(firstLine.length());
            chatId = br.readLine().substring(secondLine.length());
        } catch (IOException e) {
            AppLogger.error(Level.SEVERE, "Error getting credentials", e);
            closeApp();
        }
        return new Credentials(token, chatId);
    }

    private static void createFile() {
        Scanner sc = new Scanner(System.in);

        System.out.println("CREATING SETTING FILE AT /opt/fetchipbot/...");
        System.out.print("Enter the token's bot: ");
        String token = sc.nextLine();
        System.out.print("Enter the chat ID: ");
        String chatId = sc.nextLine();
        try (BufferedWriter writer = Files.newBufferedWriter(route)) {
            writer.write(firstLine + token);
            writer.newLine();
            writer.write(secondLine + chatId);
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
            AppLogger.error(Level.SEVERE, "Error creating file", e);
            closeApp();
        }
        AppLogger.info("Setting file created.");
        System.out.println("Setting file created.");
    }

    private static void checkFile() {
        try (BufferedReader br = Files.newBufferedReader(route)) {
            String firstLineFromFile = br.readLine();
            String secondLineFromFile = br.readLine();

            if (firstLineFromFile == null ||
                    secondLineFromFile == null ||
                    !firstLineFromFile.startsWith(firstLine) ||
                    !secondLineFromFile.startsWith(secondLine)) {
                System.out.println("The file has an invalid format, please delete it and run the app again to create it.");
                AppLogger.error(Level.SEVERE, "The file has an invalid format, please delete it and run the app again to create it.", null);
                closeApp();
            }
        } catch (IOException e) {
            System.out.println("Reading file error: " + e.getMessage());
            AppLogger.error(Level.SEVERE, "Reading file error.", e);
            closeApp();
        }
    }

    private static void closeApp() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Press any key to close the app...");
        sc.nextLine();
        AppLogger.info("App Closed.");
        System.exit(1);
    }
}

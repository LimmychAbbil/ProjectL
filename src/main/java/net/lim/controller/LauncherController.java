package net.lim.controller;

import javafx.application.Platform;
import net.lim.controller.tasks.BackgroundReceiverTask;
import net.lim.controller.tasks.DownloadFilesService;
import net.lim.controller.tasks.FileCheckerService;
import net.lim.model.FileManager;
import net.lim.model.Settings;
import net.lim.model.connection.Connection;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Limmy on 28.04.2018.
 */
public class LauncherController implements Controller {
    public static final String DEFAULT_COMMAND = "notepad"; //fixme
    private final StageController stageController;
    private FileController fileController;

    private DownloadFilesService downloadService;
    private FileCheckerService fileCheckerService;

    public static String token;

    private SettingsController settingsController;

    public LauncherController(StageController stageController) {
        this.stageController = stageController;
    }

    @Override
    public void init() {
        this.fileCheckerService = new FileCheckerService(fileController);
        this.downloadService = new DownloadFilesService(fileController);
    }

    protected void startFileChecking(String userName) {
        fileCheckerService.start();
        stageController.getOrCreateBasicView().getProgressView().getTextMessageProperty().bind(fileCheckerService.messageProperty());
        fileCheckerService.setOnSucceeded(e -> {
            boolean filesOK = fileCheckerService.getValue();
            if (filesOK) {
                stageController.getOrCreateBasicView().getProgressView().getTextMessageProperty().unbind();
                stageController.getOrCreateBasicView().getProgressView().getTextMessageProperty().setValue("Launching");
                startTask(stageController.createWaitingTask(1000));
                try {
                    launchGame(userName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                createDownloadTask(userName);
            }
        });
    }

    private void launchGame(String login) throws Exception {
        boolean isCustomDirUsed = StringUtils.isNotEmpty(Settings.getInstance().getFilesDir());
        String useCMDCommand = "cmd.exe /c ";
        String goToDiskCommand = isCustomDirUsed ? Settings.getInstance().getFilesDir().substring(0, Settings.getInstance().getFilesDir().indexOf(":") + 1)
                : "C:";
        String commandSeparator = " && ";
        String goToDirCommand;
        if (isCustomDirUsed) {
            goToDirCommand = "cd " + Settings.getInstance().getFilesDir();
        } else {
            goToDirCommand = "cd " + FileManager.DEFAULT_DIRECTORY;
        }
        StringBuilder fullLaunchCommandBuilder = new StringBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            fullLaunchCommandBuilder.append(useCMDCommand).append(goToDiskCommand).append(commandSeparator).append(goToDirCommand).append(commandSeparator);
        } else {
            fullLaunchCommandBuilder.append("sh ").append(goToDirCommand).append(" ; ");
        }


        fullLaunchCommandBuilder.append(
                ConnectionController.getInstance().getConnection()
                        .getServerLaunchCommand(stageController.getLoginController().getSelectedServer()));

        Process launch = Runtime.getRuntime().exec(fullLaunchCommandBuilder.toString());

        startSTDThreads(launch);
        if (launch.isAlive() || launch.exitValue() == 0) {
            Platform.exit();
        }
    }

    private void startSTDThreads(Process launch) {
        Thread tSTDOut = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(launch.getInputStream()))){
                while (bufferedReader.ready()) {
                    System.out.println(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread tSTDErr = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(launch.getErrorStream()))){
                while (bufferedReader.ready()) {
                    System.err.println(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tSTDOut.setDaemon(true);
        tSTDErr.setDaemon(true);

        tSTDOut.start();
        tSTDErr.start();
    }

    private void createDownloadTask(String userName) {
        downloadService.reset();
        downloadService.start();

        stageController.getOrCreateBasicView().getProgressView().getTextMessageProperty().bind(downloadService.messageProperty());
        downloadService.setOnSucceeded(event -> {
            startFileChecking(userName);
        });
        downloadService.setOnFailed(e -> {
            startTask(stageController.createWaitingTask(5 * 1000));
        });
    }

    public void initFileController(Connection connection) {
        this.fileController = new FileController(connection);
    }

    public BackgroundReceiverTask createAndStartBackgroundReceiverTask() {
        BackgroundReceiverTask readServerImageTask = new BackgroundReceiverTask(ConnectionController.getInstance().getConnection(), fileController);
        startTask(readServerImageTask);
        return readServerImageTask;
    }

    public SettingsController getOrCreateSettingController() {
        if (settingsController == null) {
            settingsController = new SettingsController(ConnectionController.getInstance(), fileController);
        }


        return settingsController;
    }

    public void createOrUpdateDownloadFileService() {
        this.downloadService = new DownloadFilesService(fileController);
    }
}

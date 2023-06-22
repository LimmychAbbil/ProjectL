package net.lim.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import net.lim.LLauncher;
import net.lim.controller.tasks.BackgroundReceiverTask;
import net.lim.controller.tasks.DownloadFilesService;
import net.lim.controller.tasks.FileCheckerService;
import net.lim.model.FileManager;
import net.lim.model.Settings;
import net.lim.model.adv.Advertisement;
import net.lim.model.adv.AdvertisementReceiver;
import net.lim.model.adv.RestAdvertisementReceiver;
import net.lim.model.connection.Connection;
import net.lim.view.NewsPane;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

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

    public void hideNewsButtonPressed(ScrollPane pane) {
        pane.setVisible(!pane.isVisible());
    }

    public void linkPressed(URL url) {
        LLauncher.getFXHostServices().showDocument(url.toString());
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
        /*fullLaunchCommandBuilder.append("java ");
            if (Settings.getInstance().getXmx() != 0) {
                fullLaunchCommandBuilder.append("-Xms").append(Settings.getInstance().getXmx()).append("m ");
            } else {
                fullLaunchCommandBuilder.append("-Xms").append(Settings.DEFAULT_XMS_MB_SIZE).append("m ");
            }

        fullLaunchCommandBuilder.append("-XX:+UseConcMarkSweepGC ")
            .append("-XX:-UseAdaptiveSizePolicy ")
            .append("-Xmn128M ")
            .append("-Djava.library.path=\".minecraft\\versions\\ForgeOptiFine 1.8.9\\natives\" ")
            .append("-cp \".minecraft\\libraries\\net\\minecraftforge\\forge\\1.8.9-11.15.1.2318-1.8.9\\forge-1.8.9-11.15.1.2318-1.8.9.jar;.minecraft\\libraries\\net\\minecraft\\launchwrapper\\1.12\\launchwrapper-1.12.jar;.minecraft\\libraries\\org\\ow2\\asm\\asm-all\\5.0.3\\asm-all-5.0.3.jar;.minecraft\\libraries\\com\\typesafe\\akka\\akka-actor_2.11\\2.3.3\\akka-actor_2.11-2.3.3.jar;.minecraft\\libraries\\com\\typesafe\\config\\1.2.1\\config-1.2.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-actors-migration_2.11\\1.1.0\\scala-actors-migration_2.11-1.1.0.jar;.minecraft\\libraries\\org\\scala-lang\\scala-compiler\\2.11.1\\scala-compiler-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\plugins\\scala-continuations-library_2.11\\1.0.2\\scala-continuations-library_2.11-1.0.2.jar;.minecraft\\libraries\\org\\scala-lang\\plugins\\scala-continuations-plugin_2.11.1\\1.0.2\\scala-continuations-plugin_2.11.1-1.0.2.jar;.minecraft\\libraries\\org\\scala-lang\\scala-library\\2.11.1\\scala-library-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-parser-combinators_2.11\\1.0.1\\scala-parser-combinators_2.11-1.0.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-reflect\\2.11.1\\scala-reflect-2.11.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-swing_2.11\\1.0.1\\scala-swing_2.11-1.0.1.jar;.minecraft\\libraries\\org\\scala-lang\\scala-xml_2.11\\1.0.2\\scala-xml_2.11-1.0.2.jar;.minecraft\\libraries\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;.minecraft\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.6\\jopt-simple-4.6.jar;.minecraft\\libraries\\java3d\\vecmath\\1.5.2\\vecmath-1.5.2.jar;.minecraft\\libraries\\net\\sf\\trove4j\\trove4j\\3.0.3\\trove4j-3.0.3.jar;.minecraft\\libraries\\optifine\\OptiFine\\1.8.9_HD_U_I7\\OptiFine-1.8.9_HD_U_I7.jar;.minecraft\\libraries\\org\\tlauncher\\netty\\1.6\\netty-1.6.jar;.minecraft\\libraries\\oshi-project\\oshi-core\\1.1\\oshi-core-1.1.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\jna\\3.4.0\\jna-3.4.0.jar;.minecraft\\libraries\\net\\java\\dev\\jna\\platform\\3.4.0\\platform-3.4.0.jar;.minecraft\\libraries\\com\\ibm\\icu\\icu4j-core-mojang\\51.2\\icu4j-core-mojang-51.2.jar;.minecraft\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.6\\jopt-simple-4.6.jar;.minecraft\\libraries\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;.minecraft\\libraries\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;.minecraft\\libraries\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;.minecraft\\libraries\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;.minecraft\\libraries\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;.minecraft\\libraries\\io\\netty\\netty-all\\4.0.23.Final\\netty-all-4.0.23.Final.jar;.minecraft\\libraries\\com\\google\\guava\\guava\\17.0\\guava-17.0.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-lang3\\3.3.2\\commons-lang3-3.3.2.jar;.minecraft\\libraries\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;.minecraft\\libraries\\commons-codec\\commons-codec\\1.9\\commons-codec-1.9.jar;.minecraft\\libraries\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;.minecraft\\libraries\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;.minecraft\\libraries\\com\\google\\code\\gson\\gson\\2.2.4\\gson-2.2.4.jar;.minecraft\\libraries\\org\\tlauncher\\authlib\\1.5.21\\authlib-1.5.21.jar;.minecraft\\libraries\\com\\mojang\\realms\\1.7.59\\realms-1.7.59.jar;.minecraft\\libraries\\org\\apache\\commons\\commons-compress\\1.8.1\\commons-compress-1.8.1.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpclient\\4.3.3\\httpclient-4.3.3.jar;.minecraft\\libraries\\commons-logging\\commons-logging\\1.1.3\\commons-logging-1.1.3.jar;.minecraft\\libraries\\org\\apache\\httpcomponents\\httpcore\\4.3.2\\httpcore-4.3.2.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-api\\2.0-beta9\\log4j-api-2.0-beta9.jar;.minecraft\\libraries\\org\\apache\\logging\\log4j\\log4j-core\\2.0-beta9\\log4j-core-2.0-beta9.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.4-nightly-20150209\\lwjgl-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.4-nightly-20150209\\lwjgl_util-2.9.4-nightly-20150209.jar;.minecraft\\libraries\\tv\\twitch\\twitch\\6.5\\twitch-6.5.jar;.minecraft\\versions\\ForgeOptiFine 1.8.9\\ForgeOptiFine 1.8.9.jar\" ")
            .append("-Dminecraft.applet.TargetDirectory=.minecraft -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true ")
            .append("net.minecraft.launchwrapper.Launch ")
            .append("--username ").append(login).append(" ")
            .append("--version \"ForgeOptiFine 1.8.9\" ")
            .append("--gameDir .minecraft ")
            .append("--assetsDir .minecraft\\assets ")
            .append("--assetIndex 1.8 ")
            .append("--uuid 00000000-0000-0000-0000-000000000000 ")
            .append("--accessToken null ")
            .append("--userProperties {\"token\":[\"").append(token).append("\"]} ")
            .append("--userType mojang ")
            .append("--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker ");

        if (StringUtils.isNotEmpty(getServerURL())) {
            fullLaunchCommandBuilder.append("--server ").append(selectedServer.getIp()).append(" ");
            fullLaunchCommandBuilder.append(" --port ").append(selectedServer.getPort());
        } */

        Process launch = Runtime.getRuntime().exec(fullLaunchCommandBuilder.toString());

        if (launch.isAlive() || launch.exitValue() == 0) {
            startSTDThreads(launch);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(launch.getInputStream()));
            while (inputStreamReader.ready()) {
                System.err.println(inputStreamReader.readLine());
            }
            Platform.exit();
        } else {
            BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(launch.getErrorStream()));
            while (errStreamReader.ready()) {
                System.err.println(errStreamReader.readLine());
            }
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

    public void fillNewsFlow(NewsPane newsPane) {
        if (ConnectionController.getInstance().getConnection() != null) {
            AdvertisementReceiver advertisementReceiver = new RestAdvertisementReceiver(ConnectionController.getInstance().getConnection());
            List<Advertisement> allAds = advertisementReceiver.receiveAdvertisements();
            newsPane.clearTextFlow();

            for (Advertisement ad : allAds) {
                newsPane.putNewToArea(ad);
            }
        }
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

package net.lim.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO folders are not released.
public class FileManager {
    public static final String DEFAULT_DIRECTORY = System.getProperty("user.home") + File.separator + ".LServer" + File.separator;
    private final long ftpPort;
    private String ftpHostURL;
    private String ftpUserName;
    private List<String> ignoredFiles;
    private Path defaultDir;
    private JSONObject remoteHashInfo;
    private int progressCounter;
    private FTPClient ftpClient;

    public FileManager(JSONObject serverInfo) {
        this.ftpHostURL = (String) serverInfo.get("host");
        this.ftpPort = (long) serverInfo.get("port");
        this.ftpUserName = (String) serverInfo.get("ftpUser");


        prepateDefaultDir();
    }

    private void prepateDefaultDir() {
        try {
            defaultDir = Paths.get(DEFAULT_DIRECTORY);
            if (!Files.exists(defaultDir)) {
                Files.createDirectory(defaultDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDefaultDirectory() {
        return DEFAULT_DIRECTORY;
    }

    public List<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    @SuppressWarnings("unchecked")
    public void parseIgnoredFiles(JSONArray ignoredFilesList) {
        ignoredFiles = new ArrayList<>(ignoredFilesList);
    }

    public boolean checkFiles() {
        JSONObject localHashInfo = getLocalFilesHash();
        if (localHashInfo.size() != remoteHashInfo.size()) {
            System.out.println("Size differ: " + localHashInfo.size() + " but expected " + remoteHashInfo.size());
            printDiff(localHashInfo, remoteHashInfo);
            return false;
        }
        for (Object localFileNameObject : localHashInfo.keySet()) {
            Object localFileHash = localHashInfo.get(localFileNameObject);
            Object remoteFileHah = remoteHashInfo.get(localFileNameObject);
            if (!localFileHash.equals(remoteFileHah)) {
                System.out.println(localFileNameObject + " differ");
                return false;
            }
        }
        return true;
    }

    private void printDiff(JSONObject localHashInfo, JSONObject remoteHashInfo) {
        if (localHashInfo.size() > remoteHashInfo.size()) {
            localHashInfo.keySet().stream().filter(key -> !remoteHashInfo.containsKey(key))
                    .forEach(o -> System.out.println(o + " is not allowed locally"));
        }
    }

    private JSONObject getLocalFilesHash() {
        JSONObject fileListHashes = new JSONObject();

        List<String> allLocalFilePaths = getAllLocalFiles(defaultDir);
        for (String fileName : allLocalFilePaths) {
            Path localFile = Paths.get(defaultDir.toString(), fileName).toAbsolutePath();
            fileListHashes.put(fileName, computeMD5ForFile(localFile));
        }
        return fileListHashes;
    }

    private String computeMD5ForFile(Path localFile) {
        try (InputStream is = new FileInputStream(localFile.toFile())) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllLocalFiles(Path file) {
        if (!Files.isDirectory(file)) {
            throw new IllegalArgumentException("File " + file.toAbsolutePath().toString() + " should be directory");
        }
        List<String> allFilePaths = new ArrayList<>();
        File[] files = file.toFile().listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                if (!ignoredFiles.contains("/" + defaultDir.relativize(f.toPath()).toString().replaceAll("\\\\", "/") + "/")) {
                    allFilePaths.addAll(getAllLocalFiles(f.toPath()));
                }
            } else {
                if (!ignoredFiles.contains("/" + defaultDir.relativize(f.toPath()).toString().replaceAll("\\\\", "/")))
                allFilePaths.add("/" + (defaultDir.relativize(f.toPath()).toString()).replaceAll("\\\\", "/"));
            }
        }
        return allFilePaths;
    }

    public void setRemoteHashInfo(JSONObject fullHashInfo) {
        this.remoteHashInfo = fullHashInfo;
    }

    public void deleteFiles(Path currentDir) {
        File[] listFiles = currentDir.toFile().listFiles();
        if (listFiles != null) {
            for (File f : listFiles) {
                if (f.isDirectory()) {
                    if (!isDirIgnored(f.toPath())) {
                        deleteFiles(f.toPath());
                        //dir will be deleted only if empty
                        f.delete();
                    }
                } else {
                    try {
                        Files.delete(f.toPath());
                    } catch (IOException e) {
                        //TODO logger
                    }
                }
            }
        }
    }

    private boolean isDirIgnored(Path currentDir) {
        return ignoredFiles.contains(defaultDir.relativize(currentDir).toString());
    }

    public void initFTPConnection() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ftpHostURL, (int) ftpPort);
        boolean loginSuccess = ftpClient.login(ftpUserName, null);
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        if (!loginSuccess) {
            throw new RuntimeException("FTP Server unavailable");
        }
    }

    public void downloadFile(String fileName) throws IOException {
        downloadFile(fileName, fileName);

    }

    public void downloadFile(String remoteFileName, File localFile) throws IOException {
        downloadFile(remoteFileName, localFile.getAbsolutePath());
    }

    public void downloadFile(String remoteFileName, String localFilePath) throws IOException {
        FileOutputStream fileWriter = null;
        progressCounter++;
        byte[] buffer = new byte[1024];
        try (InputStream stream = ftpClient.retrieveFileStream(new String(remoteFileName.getBytes("UTF-8"), "ISO-8859-1"))) {
            Path localFile = Paths.get(defaultDir.toString(), localFilePath);
            if (!Files.exists(localFile.getParent())) {
                Files.createDirectories(localFile.getParent());
            }
            fileWriter = new FileOutputStream(localFile.toFile());
            int read = 0;
            while ((read = stream.read(buffer, 0, buffer.length)) != -1) {
                fileWriter.write(buffer, 0, read);
            }
            fileWriter.flush();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
        ftpClient.completePendingCommand();
    }

    public void closeFTPConnection() throws IOException {
        if (ftpClient != null) {
            ftpClient.quit();
        }
    }

    public int getProgressCounter() {
        return progressCounter;
    }

    public int getTotalFilesSize() {
        return getFileNames().size();
    }

    public Collection<String> getFileNames() {
        return remoteHashInfo.keySet();
    }
}

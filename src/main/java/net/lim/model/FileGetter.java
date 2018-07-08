package net.lim.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//TODO folders are not released.
public class FileGetter {
    private static final String DEFAULT_DIRECTORY = System.getProperty("user.home") + File.separator + ".LServer/";
    private String ftpHostURL;
    private String ftpUserName;
    private List<String> ignoredDirs;
    private Path defaultDir;
    private JSONObject remoteHashInfo;

    public FileGetter(JSONObject serverInfo) {
        this.ftpHostURL = (String) serverInfo.get("host") + (long) serverInfo.get("port");
        this.ftpUserName = (String) serverInfo.get("ftpUser");
    }

    public void parseIgnoredDirs(JSONArray ignoredDirsList) {
        ignoredDirs = new ArrayList<>(ignoredDirsList);
    }

    public boolean checkFiles() {
        JSONObject localHashInfo = getLocalFilesHash();
        if (localHashInfo.size() != remoteHashInfo.size()) {
            return false;
        }
        for (Object localFileNameObject : localHashInfo.keySet()) {
            Object localFileHash = localHashInfo.get(localFileNameObject);
            Object remoteFileHah = remoteHashInfo.get(localFileNameObject);
            if (!localFileHash.equals(remoteFileHah)) {
                return false;
            }
        }
        return true;
    }

    private JSONObject getLocalFilesHash() {
        JSONObject fileListHashes = new JSONObject();
        try {
            defaultDir = Paths.get(DEFAULT_DIRECTORY);
            if (!Files.exists(defaultDir)) {
                    Files.createDirectory(defaultDir);
            }
            List<String> allLocalFilePaths = getAllLocalFiles(defaultDir);
            for (String fileName: allLocalFilePaths) {
                Path localFile = Paths.get(defaultDir.toString(), fileName).toAbsolutePath();
                fileListHashes.put(fileName, computeMD5ForFile(localFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileListHashes;
    }

    private String computeMD5ForFile(Path localFile) {
        try (InputStream is = new FileInputStream(localFile.toFile())) {
            return DigestUtils.md5Hex(is);
        }
        catch (IOException e) {
            //TODO logger
            return null;
        }
    }

    private List<String> getAllLocalFiles(Path file) {
        if (!Files.isDirectory(file)) {
            throw new IllegalArgumentException("File " + file.toAbsolutePath().toString() + " should be directory");
        }
        List<String> allFilePaths = new ArrayList<>();
        File[] files = file.toFile().listFiles();
        for (File f: files) {
            if (f.isDirectory()) {
                if (!ignoredDirs.contains(defaultDir.relativize(f.toPath()).toString())) {
                    allFilePaths.addAll(getAllLocalFiles(f.toPath()));
                }
            } else {
                allFilePaths.add("/" + (defaultDir.relativize(f.toPath()).toString()).replaceAll("\\\\", "/"));
            }
        }
        return allFilePaths;
    }

    public void setRemoteHashInfo(JSONObject fullHashInfo) {
        this.remoteHashInfo = fullHashInfo;
    }
}

package com.github.ovenal.robotrunner;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.InputStream;
import java.util.Properties;

public class RunParameters {
    private ConfigurationContext context;
    private Properties properties;
    private String workingDirectory;
    private String fileName;
    private String filePath;
    private String testCaseName;
    private String scriptPath;
    private String scriptParameters;

    public RunParameters(ConfigurationContext context) {
        this.context = context;
        readConfig();
        parseLocation();
        scriptPath = properties.getProperty("robot.location", "/usr/local/lib/python3.7/site-packages/robot/run.py");
        scriptParameters = properties.getProperty("script.parameters", "--loglevel DEBUG --outputdir ./output");
    }

    private void parseLocation() {
        Location location = context.getLocation();
        VirtualFile file = location.getVirtualFile();
        workingDirectory = location.getProject().getBasePath();
        filePath = file.getPath().replace(workingDirectory, ".");
        fileName = file.getName();
        testCaseName = getTestCaseName(location);
    }

    private String getTestCaseName(Location location) {
        // TODO: extract the test case from location object
       return "";
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public String getRunName() {
        return getFileName();
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getScriptParameters() {
        return String.format("%s%s %s",
                scriptParameters,
                getTestCaseName().isEmpty() ? "" : String.format(" -t \"%s\"", getTestCaseName()),
                getFilePath());
    }

    private void readConfig() {
        properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            properties.load(in);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}

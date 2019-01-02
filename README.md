# Overview
Plugin for running Robot Framework tests in PyCharm.
It adds new items to the context menu for .robot files, it's expected that Python plugin for Intellij IDEA should be installed.
The following steps are performed:
  - create a Python run configuration with the parameters of current robot-file;
  - if the configuration with such name exists it's just reused;
  - run/debug this configuration.
The configuration parameters (like the path to robot/run.py and script parameters) are placed in robot-run*.jar/config.properties file.

# Requirements
PyCharm or Intellij IDEA with Python plugin should be used.

# How to run
Building of the plugin:
```groovy
gradle buildPlugin
```

Generated plugin (robot-run*.jar file) can be installed via Plugins in IDE Preferences.  
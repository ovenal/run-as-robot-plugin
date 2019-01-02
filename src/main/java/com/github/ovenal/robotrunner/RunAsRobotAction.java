package com.github.ovenal.robotrunner;

import com.intellij.execution.*;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonConfigurationType;
import com.jetbrains.python.run.PythonRunConfiguration;


public class RunAsRobotAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        boolean isDebug = anActionEvent.getPresentation().getText().contains("Debug as");
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = getRunConfiguration(anActionEvent);
        runConfiguration(anActionEvent, runnerAndConfigurationSettings, isDebug);
    }

    private void runConfiguration(AnActionEvent anActionEvent, RunnerAndConfigurationSettings settings, boolean isDebug) {
        final DataContext dataContext = anActionEvent.getDataContext();
        final ConfigurationContext context = ConfigurationContext.getFromContext(dataContext);
        final RunManagerEx runManager = (RunManagerEx) context.getRunManager();
        runManager.setTemporaryConfiguration(settings);
        runManager.setSelectedConfiguration(settings);

        Executor executor = getExecutor(isDebug);
        ExecutionUtil.runConfiguration(settings, executor);
    }

    private Executor getExecutor(boolean isDebug) {
        for (Executor executor : ExecutorRegistry.getInstance().getRegisteredExecutors()) {
            if (isDebug && executor instanceof DefaultDebugExecutor) {
                return executor;
            } else if (!isDebug && executor instanceof DefaultRunExecutor) {
                return executor;
            }
        }
        throw new RuntimeException("Can't find appropriate executor");
    }

    private RunnerAndConfigurationSettings getRunConfiguration(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        ConfigurationContext context = ConfigurationContext.getFromContext(anActionEvent.getDataContext());
        RunParameters runParameters = new RunParameters(context);
        String runName = runParameters.getRunName();
        for (RunnerAndConfigurationSettings runnerAndConfigurationSettings : RunManager.getInstance(project).getConfigurationSettingsList((ConfigurationType) PythonConfigurationType.getInstance())) {
            if (runnerAndConfigurationSettings.getName().equals(runName)) {
                return runnerAndConfigurationSettings;
            }
        }

        for (ConfigurationType configurationType : RunManager.getInstance(project).getConfigurationFactories()) {
            if (configurationType instanceof PythonConfigurationType) {
                for (ConfigurationFactory configurationFactory : configurationType.getConfigurationFactories()) {
                    RunnerAndConfigurationSettings settings = RunManager.getInstance(project).createConfiguration(runName, configurationFactory);
                    PythonRunConfiguration runConfiguration = (PythonRunConfiguration)settings.getConfiguration();
                    runConfiguration.setScriptName(runParameters.getScriptPath());
                    runConfiguration.setScriptParameters(runParameters.getScriptParameters());
                    runConfiguration.setWorkingDirectory(runParameters.getWorkingDirectory());
                    return settings;
                }
            }
        }
        throw new RuntimeException("Can't find Python configuration type");
    }

}



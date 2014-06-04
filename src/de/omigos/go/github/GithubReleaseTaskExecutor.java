package de.omigos.go.github;

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;

import java.util.Map;

public class GithubReleaseTaskExecutor implements TaskExecutor {
    private static final String REVISION_VARIABLE = "GIT_REVISION";
    private static final String PRERELEASE_VARIABLE = "PRERELEASE";
    private static final String RELEASE_NAME = "GO_PIPELINE_LABEL";

    @Override
    public ExecutionResult execute(TaskConfig taskConfig, TaskExecutionContext taskExecutionContext) {
        Console console = taskExecutionContext.console();

        try {
            Map<String, String> environment = taskExecutionContext.environment().asMap();
            String revision = environment.get(REVISION_VARIABLE);
            String oAuthToken = taskConfig.getValue(GithubReleaseTask.INPUT_OAUTH_TOKEN);
            String repo = taskConfig.getValue(GithubReleaseTask.INPUT_REPO);


            release(revision, oAuthToken, repo, console, environment);

            return ExecutionResult.success("Revision created");
        } catch (Exception e) {
            return ExecutionResult.failure("Failed to create release: " + e.getMessage(), e);
        }
    }

    private void release(
            String revision,
            String oAuthToken,
            String repo,
            Console console,
            Map<String, String> environment
    ) throws Exception {
        boolean prerelease = getPrerelease(environment);

        String name = environment.get(RELEASE_NAME);

        GitHub github = GitHub.connect(null, oAuthToken);
        GHRelease release = github.getRepository(repo)
                .createRelease(revision)
                .prerelease(prerelease)
                .name(name)
                .body("Release created by GO")
                .create();
        console.printLine("Created Release " + release.getUrl());
    }

    private boolean getPrerelease(Map<String, String> environment) {
        boolean prerelease = false;
        if (environment.containsKey(PRERELEASE_VARIABLE)) {
            prerelease = Boolean.parseBoolean(environment.get(PRERELEASE_VARIABLE));
        }
        return prerelease;
    }
}

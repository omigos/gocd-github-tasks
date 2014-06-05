package de.omigos.go.github;

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GithubReleaseTaskExecutor implements TaskExecutor {
    private static final String REVISION_VARIABLE = "GO_REVISION";
    private static final String RELEASE_NAME = "GO_PIPELINE_LABEL";

    private static final Pattern ENVIRONMENT_PATTERN = Pattern.compile("^\\$\\{([\\w_]+)\\}$");

    @Override
    public ExecutionResult execute(TaskConfig taskConfig, TaskExecutionContext taskExecutionContext) {
        Console console = taskExecutionContext.console();

        console.printEnvironment(taskExecutionContext.environment().asMap(), taskExecutionContext.environment().secureEnvSpecifier());

        try {
            Map<String, String> environment = taskExecutionContext.environment().asMap();
            String revision = environment.get(REVISION_VARIABLE);
            String oAuthToken = taskConfig.getValue(GithubReleaseTask.INPUT_OAUTH_TOKEN);
            String repo = taskConfig.getValue(GithubReleaseTask.INPUT_REPO);
            boolean prerelease = Boolean.parseBoolean(taskConfig.getValue(GithubReleaseTask.INPUT_PRERELEASE));

            oAuthToken = replaceEnvironmentVariables(oAuthToken, environment);

            release(revision, oAuthToken, repo, prerelease, console, environment);

            return ExecutionResult.success("Revision created");
        } catch (Exception e) {
            return ExecutionResult.failure("Failed to create release: " + e.getMessage(), e);
        }
    }

    private String replaceEnvironmentVariables(String oAuthToken, Map<String, String> environment) {
        Matcher envMatcher = ENVIRONMENT_PATTERN.matcher(oAuthToken.trim());
        if (envMatcher.matches()) {
            return environment.get(envMatcher.group(1));
        }
        return oAuthToken;
    }

    private void release(
            String revision,
            String oAuthToken,
            String repo,
            boolean prerelease,
            Console console,
            Map<String, String> environment
    ) throws Exception {

        String name = environment.get(RELEASE_NAME);

        console.printLine("Connecting to github...");
        GitHub github = GitHub.connect(null, oAuthToken);
        GHRepository repository = github.getRepository(repo);
        console.printLine("Creating release...");

        GHRelease release = repository
            .createRelease(name)
            .commitish(revision)
            .prerelease(prerelease)
            .draft(true)
            .name(name)
            .body("Release created by GO")
            .create();
        console.printLine("Created Release " + release.getUrl());
    }
}

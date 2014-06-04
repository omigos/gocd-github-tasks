package de.omigos.go.github;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import com.thoughtworks.go.plugin.api.task.TaskView;
import org.apache.commons.io.IOUtils;

@Extension
public class GithubReleaseTask implements Task {
    public static final String INPUT_OAUTH_TOKEN = "OAuthToken";
    public static final String INPUT_REPO = "Repo";

    @Override
    public TaskConfig config() {
        TaskConfig config = new TaskConfig();
        config.addProperty(INPUT_OAUTH_TOKEN);
        config.addProperty(INPUT_REPO);
        return config;
    }

    @Override
    public TaskExecutor executor() {
        return new GithubReleaseTaskExecutor();
    }

    @Override
    public TaskView view() {
        TaskView taskView = new TaskView() {
            @Override
            public String displayValue() {
                return "Github Release";
            }

            @Override
            public String template() {
                try {
                    return IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8");
                } catch (Exception e) {
                    return "Failed to find template: " + e.getMessage();
                }
            }
        };
        return taskView;
    }

    @Override
    public ValidationResult validate(TaskConfig configuration) {
        ValidationResult validationResult = new ValidationResult();
        if (configuration.getValue(INPUT_OAUTH_TOKEN) == null) {
            validationResult.addError(new ValidationError(INPUT_OAUTH_TOKEN, "Converter Type cannot be empty"));
        }
        if (configuration.getValue(INPUT_REPO) == null) {
            validationResult.addError(new ValidationError(INPUT_REPO, "Output Directory cannot be empty"));
        }

        return validationResult;
    }
}

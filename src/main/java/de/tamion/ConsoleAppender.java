package de.tamion;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Plugin(name = "ErrorUploaderAppender", category = "Core", elementType = "appender", printObject = true)
public class ConsoleAppender extends AbstractAppender {
    public ConsoleAppender() {
        super("ErrorUploaderAppender", null,
                PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss} %level]: %msg").build());
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void append(LogEvent e) {
        if(e.getLevel().equals(Level.ERROR) || e.getLevel().equals(Level.FATAL)) {
            Bukkit.getScheduler().runTaskAsynchronously(ErrorUploader.getPlugin(), () -> {
                List<String> result = new ArrayList<>();
                try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File("./logs/latest.log"), StandardCharsets.UTF_8)) {
                    String line = "";
                    while ((line = reader.readLine()) != null && result.size() < 50) {
                        result.add(line);
                    }
                    Collections.reverse(result);
                    ErrorUploader.getPlugin().getLogger().info("Uploaded Logs: https://pastes.dev/" + HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                            .uri(URI.create("https://api.pastes.dev/post"))
                            .POST(HttpRequest.BodyPublishers.ofString(result.stream().map(n -> String.valueOf(n)).collect(Collectors.joining("\n")))).build(), HttpResponse.BodyHandlers.ofString()).body().replaceAll("\\{\"key\":\"", "").replaceAll("\"}", ""));
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}

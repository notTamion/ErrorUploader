package de.tamion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class ErrorUploader extends JavaPlugin {

    private static ErrorUploader plugin;

    @Override
    public void onEnable() {
        ((Logger) LogManager.getRootLogger()).addAppender(new ConsoleAppender());
        plugin = this;
    }

    @Override
    public void onDisable() {
        Logger log = (Logger) LogManager.getRootLogger();
        if(log.getAppenders().containsKey("ErrorUploaderAppender")) {
            log.removeAppender(log.getAppenders().get("ErrorUploaderAppender"));
        }
    }

    public static ErrorUploader getPlugin() {
        return plugin;
    }
}

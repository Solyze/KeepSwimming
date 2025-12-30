package net.solyze.keepswimming;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.solyze.keepswimming.config.ConfigInfo;
import net.solyze.keepswimming.config.KeepSwimmingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class KeepSwimming implements ModInitializer {

    public static final String
            MOD_ID = "keepswimming",
            MOD_DISPLAY = "Keep Swimming";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_DISPLAY);
    public static KeepSwimming INSTANCE;

    private final Gson gson;
    private final HashMap<String, Object> configs = new HashMap<>();

    public KeepSwimming() {
        LOGGER.info("{} initializing...", MOD_DISPLAY);
        gson = new GsonBuilder().setPrettyPrinting().create();;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        this.loadConfigs();
        this.saveConfigs();
        LOGGER.info("{} has been initialized.", MOD_DISPLAY);
    }

    public void loadConfigs() {
        LOGGER.info("Loading configs...");
        this.loadConfig(KeepSwimmingConfig.class);
    }

    public void saveConfigs() {
        LOGGER.info("Saving configs...");
        this.saveConfig(KeepSwimmingConfig.class);
    }

    public void loadConfig(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(ConfigInfo.class)) {
            LOGGER.error("\"{}\" has not been annotated with {}!", clazz.getName(), ConfigInfo.class.getSimpleName());
            return;
        }
        ConfigInfo config = clazz.getAnnotation(ConfigInfo.class);
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), config.name() + ".json");
        LOGGER.info(file.getAbsolutePath());
        if (!file.getParentFile().exists()) //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        try {
            if (!file.exists() && file.createNewFile()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{}");
                    LOGGER.info("Config created: \"{}.json\"", config.name());
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Could not create config \"{}\"!", file.getAbsolutePath(), ex);
        }
        try (FileReader reader = new FileReader(file)) {
            this.configs.put(config.name(), this.gson.fromJson(reader, clazz));
            LOGGER.info("Config loaded: \"{}.json\"", config.name());
        } catch (IOException ex) {
            LOGGER.error("Could not load config \"{}.json\"!", config.name(), ex);
        }
    }

    public void saveConfig(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(ConfigInfo.class)) {
            LOGGER.error("\"{}\" has not been annotated with {}!", clazz.getName(), ConfigInfo.class.getSimpleName());
            return;
        }
        ConfigInfo config = clazz.getAnnotation(ConfigInfo.class);
        Object object = this.configs.get(config.name());
        if (object == null) return;
        String json = gson.toJson(object);
        String path = new File(FabricLoader.getInstance().getConfigDir().toFile(),
                config.name() + ".json").getAbsolutePath();
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json);
            LOGGER.info("Config saved: \"{}.json\"", config.name());
        } catch (IOException ex) {
            LOGGER.error("Could not save config \"{}.json\"!", config.name(), ex);
        }
    }

    public Optional<Object> getConfig(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(ConfigInfo.class)) {
            LOGGER.error("\"{}\" has not been annotated with {}!", clazz.getName(), ConfigInfo.class.getSimpleName());
            return Optional.empty();
        }
        ConfigInfo config = clazz.getAnnotation(ConfigInfo.class);
        return Optional.ofNullable(configs.get(config.name()));
    }
}

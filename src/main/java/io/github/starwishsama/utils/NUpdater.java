package io.github.starwishsama.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.github.starwishsama.utils.data.GithubObject;
import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 * @author Nameless
 */
public class NUpdater {

    private GithubObject updateInfoCache;
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private static String downloadDir;
    private static final String browserUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36";
    private static CustomBranch branch = CustomBranch.NIGHTLY;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyMMdd");
    private final SlimefunPlugin plugin;
    
    public NUpdater(SlimefunPlugin plugin) {
        this.plugin = plugin;
        downloadDir = plugin.getServer().getUpdateFolder();
    }
    
    /**
     * 下载文件
     *
     * @param address  下载地址
     * @param fileName 下载文件的名称
     */
    public void downloadUpdate(String address, String fileName) {
        File file = new File(downloadDir.replace("update", "plugins"), fileName);

        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) (url.openConnection());
            conn.setConnectTimeout(5_000);
            conn.setReadTimeout(5_000);
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", browserUA);

            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 2048);
            byte[] data = new byte[2048];
            int x;
            while ((x = in.read(data, 0, 2048)) >= 0) {
                bos.write(data, 0, x);
            }

            bos.close();
            in.close();
            fos.close();

            plugin.getFile().deleteOnExit();
            SlimefunPlugin.logger().info(ChatColors.color("&a自动更新已完成, 重启服务端后即可更新到最新版本"));
        } catch (Exception e) {
            if (!file.delete()) {
                SlimefunPlugin.logger().log(Level.SEVERE, e, () -> "无法删除损坏文件: " + fileName);
            }

            if (e.getCause() instanceof SocketTimeoutException) {
                SlimefunPlugin.logger().log(Level.SEVERE, e, () -> "在下载时发生了错误: 连接超时");
                return;
            }

            SlimefunPlugin.logger().log(Level.SEVERE, e, () -> "在下载时发生了错误");
        }
    }

    /**
     * 使用 Github API 获取 Releases 信息
     *
     * @return Github Beans
     */
    private List<GithubObject> getReleaseBean() {
        try {
            URL url = new URL("https://api.github.com/repos/StarWishsama/Slimefun4/releases");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5_000);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Slimefun 4 Update Checker by StarWishsama");
            conn.setDoOutput(true);

            int code = conn.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String cache;
                StringBuilder result = new StringBuilder();
                while ((cache = br.readLine()) != null) {
                    result.append(cache);
                }

                conn.disconnect();

                return gson.fromJson(result.toString().trim(), new TypeToken<List<GithubObject>>() {
                }.getType());
            } else {
                conn.disconnect();
                SlimefunPlugin.logger().log(Level.WARNING, "连接至 Github 服务器出错, 状态码: " + code);
            }
        } catch (IOException e) {
            SlimefunPlugin.logger().log(Level.WARNING, "连接至 Github 服务器出错, 错误信息: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            SlimefunPlugin.logger().log(Level.WARNING, "无法解析获取的数据, 错误信息: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * 获取最新版本的信息
     *
     * @return 最新的 Bean
     */
    private GithubObject getGithubBean() {
        List<GithubObject> beans = getReleaseBean();

        if (!beans.isEmpty()) {
            if (branch == CustomBranch.DEV) {
                updateInfoCache = beans.get(0);
                return beans.get(0);
            } else if (branch == CustomBranch.STABLE) {
                for (GithubObject bean : beans) {
                    if (!bean.isPreRelease()) {
                        updateInfoCache = bean;
                        return bean;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        GithubObject bean = getCache();

        if (bean != null) {
            try {
                if (isOldVersion(SlimefunPlugin.getVersion(), bean.getTagName())) {
                    String updateInfo = "有更新了 | " + bean.getTagName() + " 现已发布\n正在自动下载更新中, 下载完成后重启服务器生效";
                    SlimefunPlugin.logger().info(updateInfo);
                    downloadUpdate(getCache().getAssets().get(0).getDownloadUrl(), getCache().getAssets().get(0).getName());
                } else {
                    SlimefunPlugin.logger().info(ChatColors.color("&a你正在使用最新版本 " + SlimefunPlugin.getVersion()));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                SlimefunPlugin.logger().log(Level.SEVERE, ChatColors.color("&c无法解析版本号, 报错信息: " + e.getLocalizedMessage()));
            }
        } else {
            SlimefunPlugin.logger().info("无法获取到更新信息");
        }
    }

    private boolean isOldVersion(String current, String versionToCompare) {
        Validate.notEmpty(current, "Current version code can't be null!");
        Validate.notEmpty(versionToCompare, "Compare version code can't be empty!");

        try {
            LocalDateTime currentVersion = LocalDateTime.parse(current.split("-")[2], dateFormat);
            LocalDateTime comparedVersion = LocalDateTime.parse(versionToCompare.split("-")[2], dateFormat);
            return currentVersion.isBefore(comparedVersion);
        } catch (NumberFormatException | DateTimeParseException e) {
            return false;
        }
    }

    private GithubObject getCache() {
        return updateInfoCache == null ? getGithubBean() : updateInfoCache;
    }

    public void autoSelectBranch() {
        String version = plugin.getDescription().getVersion().toLowerCase(Locale.ROOT);

        if (version.contains("stable")) {
            branch = CustomBranch.STABLE;
        } else if (version.contains("dev")) {
            branch = CustomBranch.DEV;
        } else {
            branch = CustomBranch.NIGHTLY;
        }
    }

    public CustomBranch getBranch() {
        return branch;
    }

    public boolean isStable() {
        return branch == CustomBranch.STABLE;
    }
}

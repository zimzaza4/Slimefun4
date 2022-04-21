package ren.natsuyuk1.slimefunextra;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.logging.Level;

public class UpdateChecker {
    private static final String UPDATE_CHECK_URL = "https://gitee.com/api/v5/repos/StarWishsama/Slimefun4/releases/latest";

    public static void checkUpdate() {
        String currentVersion = Slimefun.getVersion();

        // Only available for release user
        if (!currentVersion.contains("release")) {
            return;
        }

        try {
            HttpResponse<JsonNode> response = Unirest.get(UPDATE_CHECK_URL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .asJson();

            if (response.isSuccess()) {
                JsonNode node = response.getBody();

                if (Slimefun.instance() != null) {
                    String latestVersionCode = node.getObject().getString("tag_name");

                    String[] versionCodeSplit = currentVersion.split("-");
                    String version = null;
                    for (String str : versionCodeSplit) {
                        if (str.startsWith("20")) {
                            String[] code = str.split("\\.");
                            if (code.length < 2) {
                                Slimefun.logger().warning("无法识别当前版本: " + currentVersion);
                                return;
                            }
                            version = code[0] + "." + code[1];
                        }
                    }

                    if (version == null) {
                        Slimefun.logger().warning("无法识别当前版本: " + currentVersion);
                        return;
                    }

                    if (latestVersionCode.compareTo(version) > 0) {
                        Slimefun.logger().info("新版本 " + latestVersionCode + " 已发布，请前往 https://gitee.com/StarWishsama/Slimefun4/releases 更新.");
                    } else {
                        Slimefun.logger().info("你正在使用最新版本 " + currentVersion + ".");
                    }

                }
            }
        } catch (Exception e) {
            Slimefun.logger().log(Level.WARNING, "在检查更新时发生错误", e);
        }
    }
}

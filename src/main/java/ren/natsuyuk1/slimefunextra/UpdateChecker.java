package ren.natsuyuk1.slimefunextra;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class UpdateChecker {
    private static final String UPDATE_CHECK_URL = "https://gitee.com/api/v5/repos/StarWishsama/Slimefun4/releases/latest";

    public static void checkUpdate() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(UPDATE_CHECK_URL)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .asJson();

            if (response.isSuccess()) {
                JsonNode node = response.getBody();

                String versionCode = node.getObject().getString("tag_name");

                if (Slimefun.instance() != null) {
                    String currentVersion = Slimefun.instance().getDescription().getVersion();
                    String currentYear = currentVersion.split("\\.")[0];
                    String currentMonth = currentVersion.split("\\.")[1];

                    if (currentVersion.contains("exp")) {
                        return;
                    }

                    // Get last string
                    String actualVersionCode = currentVersion.split("-")[2];
                    String year = actualVersionCode.split("\\.")[0];
                    String month = actualVersionCode.split("\\.")[1];

                    if (Integer.parseInt(year) > Integer.parseInt(currentYear)
                            || (Integer.parseInt(year) == Integer.parseInt(currentYear)
                            && Integer.parseInt(month) > Integer.parseInt(currentMonth))) {
                        Slimefun.logger().info("新版本 " + versionCode + " 已发布，请前往 https://gitee.com/StarWishsama/Slimefun4/releases 更新.");
                    }
                }
            }
        } catch (UnirestException ignored) {}
    }
}

package dojo.bot.Controller.User;

import chariot.Client;
import chariot.model.*;

import java.util.List;

public class Profile extends UserObject {

    private String sayProfile = "";

    public Profile(Client client, String userParsing) {
        super(client, userParsing);
    }

    public String getBlitzRatings() {
        return getRating(Enums.PerfType.blitz, "\uD83D\uDD25 **Blitz**: ?");
    }

    public String getRapidRatings() {
        return getRating(Enums.PerfType.rapid, "\uD83D\uDC07 **Rapid**: ?");
    }

    public int getSingleRapidRating() {
        return getSingleRating(Enums.PerfType.rapid);
    }

    public String getBulletRatings() {
        return getRating(Enums.PerfType.bullet, "\uD83D\uDD2B **Bullet**: ?");
    }

    public String getClassicalRatings() {
        return getRating(Enums.PerfType.classical, "\uD83D\uDC22 **Classical**: ?");
    }

    public int getSingleClassicalRating() {
        return getSingleRating(Enums.PerfType.classical);
    }

    public int getSingleBlitzRating() {
        return getSingleRating(Enums.PerfType.blitz);
    }

    public String getUserProfile() {
        try {
            One<User> userResult = this.getClient().users().byId(this.getUserID(), params -> params.withTrophies(true));
            if (userResult.isPresent()) {
                User user = userResult.get();
                String statusEmoji = this.getClient().users().statusByIds(this.getUserID()).stream().toList().get(0).online() ? "\uD83D\uDFE2" : "\uD83D\uDD34";
                if (user.tosViolation()) return "This user has violated Lichess Terms of Service";
                if (user.disabled()) return "This account is closed";

                StringBuilder sayRewards = new StringBuilder();
                user.trophies().orElse(List.of()).forEach(trophy -> sayRewards.append(new UserTrophy(trophy).getImageLink()).append("\n"));

                String embedRewards = sayRewards.length() > 0 ? "\n\n ** \uD83D\uDCA0 Trophies:** \n\n" + sayRewards : "";

                this.sayProfile = String.format("%s %s %s\n**All Games**: %d\n\n** ⚔️ Won:** %d\n** \uD83D\uDE14 Loss:** %d\n** \uD83E\uDD1D Draw:** %d\n\n** ♗ Playing:** %d\n\n \uD83D\uDCB9 **Ratings**: \n%s\n%s\n%s\n%s%s",
                        user.title().orElse(""), user.id(), statusEmoji, user.accountStats().all(), user.accountStats().win(), user.accountStats().loss(), user.accountStats().draw(), user.accountStats().playing(),
                        this.getBlitzRatings(), this.getRapidRatings(), this.getBulletRatings(), this.getClassicalRatings(), embedRewards);
            } else {
                return "User Not Present, Please try again";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown Error..";
        }
        return sayProfile;
    }

    private String getRating(Enums.PerfType perfType, String defaultRating) {
        One<PerformanceStatistics> userPerf = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), perfType);
        if (userPerf.isPresent() && !userPerf.get().perf().glicko().provisional()) {
            return String.format(" %s **%s**: %d", defaultRating.split(" ")[0], perfType.name(), userPerf.get().perf().glicko().rating().intValue());
        }
        return defaultRating;
    }

    private int getSingleRating(Enums.PerfType perfType) {
        One<PerformanceStatistics> userPerf = this.getClient().users().performanceStatisticsByIdAndType(this.getUserID(), perfType);
        if (userPerf != null && userPerf.isPresent() && !userPerf.get().perf().glicko().provisional()) {
            return userPerf.get().perf().glicko().rating().intValue();
        }
        return -1;
    }
}

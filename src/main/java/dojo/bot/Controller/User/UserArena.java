package dojo.bot.Controller.User;

import chariot.Client;
import chariot.model.Arena;
import chariot.model.One;
import chariot.model.Swiss;
import dojo.bot.Controller.Standing.SwissResults;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The type User arena.
 */
public class UserArena {

    private Client client;
    private EmbedBuilder embedBuilder;
    private String arenaID;

    /**
     * Instantiates a new User arena.
     *
     * @param client  the client
     * @param arenaID the arena id
     */
    public UserArena(Client client, String arenaID) {
        this.client = client;
        this.arenaID = arenaID;
    }


    /**
     * Gets user arena.
     *
     * @return the user arena
     */
    public EmbedBuilder getUserArena() {

        try {

            String[] spliturl = this.arenaID.split("tournament/");
            System.out.println(Arrays.toString(spliturl));
            this.embedBuilder = new EmbedBuilder();
            String[] emojileaderboard = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "\uD83D\uDD1F"};
            String[] podium = {"\uD83C\uDFC6", "\uD83E\uDD48", "\uD83E\uDD49"};

            String touryID = "";

            for (String a : spliturl) {

                touryID = a;

            }



            One<Arena> arenaResult1 = client.tournaments().arenaById(touryID);

            if (arenaResult1.isPresent() && spliturl[0].startsWith("http") && spliturl[0].contains("lichess")) {

                Arena arena = arenaResult1.get();

                System.out.println(arena);



                String name = arena.fullName();

                int numPlayers = arena.nbPlayers();

                int timeLeft = arena.minutes();

                Arena.Perf perf = arena.perf();

                String perfname = perf.name();

                StringBuilder stand = new StringBuilder();
                StringBuilder standPodium = new StringBuilder();


                Arena.Standing standing = arena.standing();




                List<Arena.Standing.Player> players = standing.players();

                for (int i = 0; i < 3; i++) {

                    standPodium.append(podium[i]).append(" ").append(players.get(i).name()).append("  ").append(players.get(i).rating()).append("  **").append(players.get(i).score()).append("** ").append(players.get(i).team()).append("\n ");


                }


                for (int i = 0; i < players.size(); i++) {

                    stand.append(emojileaderboard[i]).append(" ").append(players.get(i).name()).append("  ").append(players.get(i).rating()).append("  **").append(players.get(i).score()).append("** ").append(players.get(i).team()).append("\n ");


                }


                this.embedBuilder = new EmbedBuilder();
                this.embedBuilder.setColor(Color.BLACK);
                this.embedBuilder.setTitle( "\uD83C\uDF96️ " + name  + " \uD83C\uDF96️");
                this.embedBuilder.setDescription("** Tournament Name:** " + name + "\n **Variant:** " + perfname  + "\n **Time Duration :** " + timeLeft + " mins" + "\n **Total Players:** " + numPlayers + "\n **Standings:**" + "\n  **Rank**  **Username**  **Rating:**  **Score** \n \n " + " **Podium** \n " + standPodium + "\n\n **Leaderboard** \n" + stand + "\n" + "[View on Lichess](" + this.arenaID + ")");


            }else{
                this.embedBuilder = new EmbedBuilder();
                String[] splitswiss = this.arenaID.split("swiss/");

                String touryIDSwiss = "";

                for (String a : splitswiss) {

                    touryIDSwiss = a;

                }


                One<Swiss> swissResult = this.client.tournaments().swissById(touryIDSwiss);

                if(swissResult.isPresent()){
                    SwissResults swissResults = new SwissResults(client, touryIDSwiss);
                    return swissResults.getLinkResults();
                }

                return this.embedBuilder.setDescription("Error occurred, please provide valid Lichess URL");
            }

        }catch (Exception e){
            e.printStackTrace();
            this.embedBuilder = new EmbedBuilder();
            return this.embedBuilder.setDescription("Error occurred, please provide valid Lichess arena url");
        }

        return this.embedBuilder;


    }


}


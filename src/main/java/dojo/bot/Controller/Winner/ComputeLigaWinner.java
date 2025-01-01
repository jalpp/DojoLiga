package dojo.bot.Controller.Winner;

import dojo.bot.Controller.League.Time_Control;
import dojo.bot.Controller.League.Type;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Timer;
import java.util.TimerTask;

import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;

public class ComputeLigaWinner {


    /**
     * Computes the liga winner for given policy
     * @param event Discord trigger event
     * @param policy winner policy
     */

    public void computeLigaWinners(SlashCommandInteractionEvent event, WinnerPolicy policy){
        if(isDiscordAdmin(event)) {
            DisplayLoading(event);
            switch (event.getOptionsByName("time-picker").get(0).getAsString()) {

                case "bz" -> InternalWinnerFinderOnTimeControl(event, policy, Time_Control.BLITZ);

                case "ra" -> InternalWinnerFinderOnTimeControl(event, policy, Time_Control.RAPID);

                case "cla" -> InternalWinnerFinderOnTimeControl(event, policy, Time_Control.CLASSICAL);

            }

        }else{
            event.reply("Your not an admin!").queue();
        }
    }


    /**
     * Internal service to find winner for given policy, time control, tournament type and year
     * @param event Discord trigger event
     * @param policy winner policy
     * @param timeControl chess time control
     * @param tournamentType chess tournament type
     * @param year year the liga was played in
     */

    public void InternalWinnerFinder(SlashCommandInteractionEvent event, WinnerPolicy policy, Time_Control timeControl, Type tournamentType, int year){

        switch (event.getOptionsByName("month-picker").get(0).getAsString()){

            case "jan" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 1, year)).queue();

            case "feb" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 2, year)).queue();

            case "marc" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 3, year)).queue();

            case "apr" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 4, year)).queue();

            case "may" -> event.getChannel().sendMessage(policy.findWinner(timeControl,tournamentType, 5, year)).queue();

            case "june" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 6, year)).queue();

            case "july" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 7, year)).queue();

            case "aug" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 8, year)).queue();

            case "sep" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 9, year)).queue();

            case "oct" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 10, year)).queue();

            case "nov" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 11, year)).queue();

            case "dec" -> event.getChannel().sendMessage(policy.findWinner(timeControl, tournamentType, 12, year)).queue();


        }

    }

    /**
     * Internal service to find winner on given time control
     * @param event Discord trigger event
     * @param policy winner policy
     * @param timeControl chess time control
     */

    public void InternalWinnerFinderOnTimeControl(SlashCommandInteractionEvent event, WinnerPolicy policy, Time_Control timeControl){

        switch (event.getOptionsByName("type-picker").get(0).getAsString()) {
            case "gp" -> {
                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                    case "2025" -> InternalWinnerFinder(event,policy,timeControl,Type.COMB_GRAND_PRIX,2025);

                    case "2024" -> InternalWinnerFinder(event,policy,timeControl,Type.COMB_GRAND_PRIX,2024);

                    case "2023" -> InternalWinnerFinder(event,policy,timeControl,Type.COMB_GRAND_PRIX,2023);
                }
            }

            case "ar" -> {
                switch (event.getOptionsByName("year-picker").get(0).getAsString()){
                    case "2025" -> InternalWinnerFinder(event,policy,timeControl,Type.ARENA,2025);

                    case "2024" -> InternalWinnerFinder(event,policy,timeControl,Type.ARENA,2024);

                    case "2023" -> InternalWinnerFinder(event,policy,timeControl,Type.ARENA,2023);
                }

            }
            case "sw" -> {
                switch (event.getOptionsByName("year-picker").get(0).getAsString()){

                    case "2025" -> InternalWinnerFinder(event,policy,timeControl,Type.SWISS,2025);

                    case "2024" -> InternalWinnerFinder(event,policy,timeControl,Type.SWISS,2024);

                    case "2023" -> InternalWinnerFinder(event,policy,timeControl,Type.SWISS,2023);
                }

            }
        }


    }

    /**
     * Loading service which beeps in discord channel load animation
     */

    public void DisplayLoading(SlashCommandInteractionEvent event){
        event.reply("Computing Please wait!").queue(msg ->
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = 0;

                @Override
                public void run() {
                    msg.editOriginal("Loading" + " beep beep boop boop ".repeat(count % 4)).queue();
                    count++;
                    if (count > 20) {
                        timer.cancel();
                    }
                }
            }, 0, 1000);


        });
    }



}

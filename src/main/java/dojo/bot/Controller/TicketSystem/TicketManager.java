package dojo.bot.Controller.TicketSystem;

import dojo.bot.Controller.Discord.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdmin;
import static dojo.bot.Controller.Discord.DiscordAdmin.isDiscordAdminButton;

public class TicketManager {


    public void createEntryTicket(SlashCommandInteractionEvent event){

        if(isDiscordAdmin(event)){
            event.replyEmbeds(
                    new EmbedBuilder().setTitle("Training Program Ticket System")
                            .setDescription("Please click on following buttons to open a ticket" +
                                    "\n\n" +
                                    "**\uD83E\uDD4B Ask Senseis (Jesse, David, Kostya)**\n Ask the Sensei questions about chess" +
                                    "\n\n" +
                                    "**\uD83D\uDCD5 Training Program Feedback** \n Provide Training Program feedback" +
                                    "\n\n" +
                                    "**\uD83D\uDEE0\uFE0F Tech Feedback** \n Provide tech feedback" +
                                    "\n\n" +
                                    "**\uD83C\uDF10 FAQ** \n Read Training Program FAQ")
                            .setColor(Color.CYAN)
                            .setFooter("Note: if you get a message “application did’t respond” it means that there is server outage or the dev @nmp is doing maintenance, if you get this message please create a ticket 3/5 hours after. Thanks!")
                            .setThumbnail(Helper.DOJO_LOGO)
                            .build()
            ).addActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("sen", "\uD83E\uDD4B Ask Sensei"), net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("tp", "\uD83D\uDCD5 Training Program Feedback"), net.dv8tion.jda.api.interactions.components.buttons.Button.danger("tech", "\uD83D\uDEE0\uFE0F  Tech Feedback"), net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://www.chessdojo.club/help", "\uD83C\uDF10 Training Program FAQ")).queue();
        }else{
            event.reply("Sorry you are not an admin!").setEphemeral(true).queue();
        }


    }



    public void ticketFormSystem(ModalInteractionEvent event, String senseiChannelId, String techChannelID, String tpchannel){
        String id = event.getModalId();

        switch (id){
            case "sensei-modal" -> {
                event.reply("Question Ticket issued! Please wait for Sensei to get back to you!").setEphemeral(true).queue();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setThumbnail(Helper.DOJO_LOGO);
                builder.setTitle("\uD83E\uDD4B Sensei Questions Ticket");
                builder.setDescription("**Content:** \n  > "  + Objects.requireNonNull(event.getValue("asks")).getAsString());
                builder.setColor(Color.ORANGE);
                builder.addField("Ticket Number: ", "#" + generateRandomTicketNumber(new Random()), true);
                builder.addField("Author @:", event.getUser().getAsMention(), true);
                builder.addField("Author name: ", "@" + event.getUser().getEffectiveName(), true);
                Role getAsMention = event.getGuild().getRolesByName("sensei", true).get(0);
                String mention = getAsMention.getAsMention();
                event.getGuild().getTextChannelById(senseiChannelId).sendMessage(mention).queue();
                event.getGuild().getTextChannelById(senseiChannelId).sendMessageEmbeds(builder.build()).addActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.success("reply", "↩\uFE0F Sensei reply")).queue(msg -> msg.addReaction(Emoji.fromFormatted("✅")).queue());

            }
            case "tp-modal" -> {
                event.reply("Feedback Ticket issued! Please wait for our Admins to get back to you!").setEphemeral(true).queue();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setThumbnail(Helper.DOJO_LOGO);
                builder.addField("Ticket Number: ", "#" + generateRandomTicketNumber(new Random()), true);
                builder.addField("Author @:", event.getUser().getAsMention(), true);
                builder.addField("Author name: ", "@" + event.getUser().getEffectiveName(), true);
                builder.setTitle("\uD83D\uDCD5 Training Program Feedback Ticket");
                builder.setDescription("**Content:** \n  > " + Objects.requireNonNull(event.getValue("askt")).getAsString());
                builder.setColor(Color.WHITE);
                event.getGuild().getTextChannelById(tpchannel).sendMessageEmbeds(builder.build()).addActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.success("reply", "↩\uFE0F Admin reply")).queue(msg -> msg.addReaction(Emoji.fromFormatted("⬆\uFE0F")).queue());

            }


            case "tech-modal" -> {
                event.reply("Feedback Ticket issued! Please wait for our Admins to get back to you!").setEphemeral(true).queue();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setThumbnail(Helper.DOJO_LOGO);
                builder.addField("Ticket Number: ", "#" + generateRandomTicketNumber(new Random()), true);
                builder.addField("Author @:", event.getUser().getAsMention(), true);
                builder.addField("Author name: ", "@" + event.getUser().getEffectiveName(), true);
                builder.setTitle("\uD83D\uDEE0\uFE0F Tech Feedback Ticket");
                builder.setDescription( "**Content:** \n  > " + Objects.requireNonNull(event.getValue("asktech")).getAsString());
                builder.setColor(Color.GREEN);
                event.getGuild().getTextChannelById(techChannelID).sendMessageEmbeds(builder.build()).addActionRow(Button.success("reply", "↩\uFE0F Admin reply")).queue(msg -> msg.addReaction(Emoji.fromFormatted("⬆\uFE0F")).queue());

            }

            case "reply-modal" -> {
                event.replyEmbeds(
                        new EmbedBuilder().setThumbnail(event.getUser().getAvatarUrl()).setTitle(event.getUser().getEffectiveName() + "'s answer to ticket " + event.getMessage().getEmbeds().get(0).getFields().get(0).getValue()).setColor(Color.BLUE)
                                .setDescription("**Content:** \n  > " + Objects.requireNonNull(event.getValue("reply-m")).getAsString()).build()).queue();
                event.getChannel().sendMessage(event.getMessage().getEmbeds().get(0).getFields().get(1).getValue() + " Your Ticket has been answered!").queue();
            }



        }

    }



    public void sentTheForms(ButtonInteraction event){
        switch (event.getComponentId()){
            case "sen" -> {
                Modal modal = buildForm(
                        "asks",
                        "Ask Sensei question",
                        "Ask Question",
                        "sensei-modal",
                        "Sensei Questions"
                );
                event.replyModal(modal).queue();
            }
            case "tp" -> {
                Modal modal = buildForm(
                        "askt",
                        "provide feedback",
                        "Provide Feedback",
                        "tp-modal",
                        "Training Program Feedback"
                );

                event.replyModal(modal).queue();

            }
            case "tech" -> {
                Modal modal = buildForm(
                        "asktech",
                        "provide feedback",
                        "Provide Tech Feedback",
                        "tech-modal",
                        "Tech Feedback"
                );

                event.replyModal(modal).queue();
            }

            case "reply" -> {

                if(isDiscordAdminButton(event)){
                    Modal modal = buildForm(
                            "reply-m",
                            "Reply to ticket",
                            "Reply to ticket",
                            "reply-modal",
                            "Reply"
                    );

                    event.replyModal(modal).queue();

                }else{
                    event.reply("Your not an admin!").setEphemeral(true).queue();
                }

            }

        }
    }



    public Modal buildForm(String textId, String labelInfo, String insideInfo, String modalId, String modalTitle){

        TextInput wtext = TextInput.create(textId, labelInfo, TextInputStyle.PARAGRAPH)
                .setPlaceholder(insideInfo)
                .setMinLength(5)
                .setMaxLength(3000)
                .setRequired(true)

                .build();


        return Modal.create(modalId, modalTitle)
                .addActionRow(wtext)
                .build();

    }


    private static int generateRandomTicketNumber(Random random) {
        // You can customize the range of the ticket number based on your requirements
        int lowerBound = 100;
        int upperBound = 999;

        // Generate a random number within the specified range
        return lowerBound + random.nextInt(upperBound - lowerBound + 1);
    }




}
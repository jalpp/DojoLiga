package dojo.bot.Controller.TicketSystem;

import dojo.bot.Controller.Discord.Helper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static dojo.bot.Controller.Discord.DiscordAdmin.*;

/**
 * Manages the ticket system for the Discord bot.
 */
public class TicketManager {

    public static final Dotenv dotenv = Dotenv.load();

    private final String entryTicketDesc = """
            Please click on following buttons to open a ticket

            **\uD83E\uDD4B Ask Senseis (Jesse, David, Kostya)**
             Ask the Sensei questions about chess

            **\uD83D\uDCD5 Training Program Feedback**
             Provide Training Program feedback

            **\uD83D\uDEE0️ Tech General Feedback**
             Provide general tech feedback about ChessDojo.club or Training Program Discord
             like improvements/feature requests for Discord or the website
             please add <|| spoiler ||> for tactics test answers!

            **🐛 Tech Bug Feedback**
             Provide technical bugs feedback about Chessdojo.club or Training Program Discord
             like broken features that are new or new error you encountered

            **\uD83C\uDF10 FAQ**
             Read Training Program FAQ
            """;

    private final String entryTicketFooter = "Note: if you get a message “application didn’t respond” it means that there is server outage or the dev @nmp is doing maintenance, if you get this message please create a ticket 3/5 hours after. Thanks!";

    /**
     * Builds the embed message for the ticket system.
     *
     * @return the EmbedBuilder with the ticket system information
     */
    private EmbedBuilder ticketBuilder() {
        return new EmbedBuilder().setTitle("\uD83C\uDF9F Training Program Ticket System")
                .setDescription(entryTicketDesc)
                .setColor(Color.ORANGE)
                .setFooter(entryTicketFooter)
                .setThumbnail(Helper.DOJO_LOGO);
    }

    /**
     * Creates an entry ticket from a message received event.
     *
     * @param event the MessageReceivedEvent
     */
    public void createEntryTicketFromMsg(MessageReceivedEvent event) {
        if (isDiscordAdminMessage(event)) {
            event.getChannel().sendMessageEmbeds(
                   ticketBuilder().build()
            ).addActionRow(
                    Button.primary("sen", "\uD83E\uDD4B Ask Sensei"),
                    Button.secondary("tp", "\uD83D\uDCD5 Training Program Feedback"),
                    Button.primary("tech", "\uD83D\uDEE0 Tech General Feedback"),
                    Button.danger("bug", "\uD83D\uDC1B Tech Bug Feedback"),
                    Button.link("https://www.chessdojo.club/help", "\uD83C\uDF10 Training Program FAQ")
            ).queue();
        } else {
            event.getChannel().sendMessage("Sorry you are not an admin!").queue();
        }
    }

    /**
     * Creates an entry ticket from a slash command interaction event.
     *
     * @param event the SlashCommandInteractionEvent
     */
    public void createEntryTicket(SlashCommandInteractionEvent event) {
        if (isDiscordAdmin(event)) {
            event.replyEmbeds(
                    ticketBuilder().build()
            ).addActionRow(
                    Button.primary("sen", "\uD83E\uDD4B Ask Sensei"),
                    Button.secondary("tp", "\uD83D\uDCD5 Training Program Feedback"),
                    Button.primary("tech", "\uD83D\uDEE0 Tech General Feedback"),
                    Button.danger("bug", "\uD83D\uDC1B Tech Bug Feedback"),
                    Button.link("https://www.chessdojo.club/help", "\uD83C\uDF10 Training Program FAQ")
            ).queue();
        } else {
            event.reply("Sorry you are not an admin!").setEphemeral(true).queue();
        }
    }

    /**
     * Handles the ticket form system based on the modal interaction event.
     *
     * @param event the ModalInteractionEvent
     * @param senseiChannelId the ID of the Sensei channel
     * @param techChannelID the ID of the Tech channel
     * @param tpchannel the ID of the Training Program channel
     * @param tacchannel the ID of the Tactics channel
     * @param techBugID the ID of the Tech Bug channel
     */
    public void ticketFormSystem(ModalInteractionEvent event, String senseiChannelId, String techChannelID, String tpchannel, String tacchannel, String techBugID) {
        switch (event.getModalId()) {
            case "sensei-modal" -> SenseiTicketHandler(event, senseiChannelId);
            case "tp-modal" -> TpTicketHandler(event, tpchannel, tacchannel);
            case "tech-modal" -> TechGenTicketHandler(event, techChannelID);
            case "reply-modal" -> ReplyTicketHandler(event);
            case "bug-modal" -> TechBugTicketHandler(event, techBugID);
        }
    }

    /**
     * Handles the reply ticket.
     *
     * @param event the ModalInteractionEvent
     */
    private void ReplyTicketHandler(ModalInteractionEvent event) {
        event.replyEmbeds(
                new EmbedBuilder().setThumbnail(event.getUser().getAvatarUrl())
                        .setTitle(event.getUser().getEffectiveName() + "'s answer to ticket " + event.getMessage().getEmbeds().get(0).getFields().get(0).getValue())
                        .setColor(Color.BLUE)
                        .setDescription("**Content:** \n  > " + Objects.requireNonNull(event.getValue("reply-m")).getAsString())
                        .build()
        ).queue();
        event.getChannel().sendMessage(event.getMessage().getEmbeds().get(0).getFields().get(1).getValue() + " Your Ticket has been answered!").queue();
    }

    /**
     * Handles the ticket creation process.
     *
     * @param event the ModalInteractionEvent
     * @param channelId the ID of the channel where the ticket will be sent
     * @param title the title of the ticket
     * @param description the description of the ticket
     * @param color the color of the ticket embed
     * @param ticketPrefix the prefix for the ticket number
     * @param ticketRole the role to mention for the ticket
     */
    private void handleTicket(ModalInteractionEvent event, String channelId, String title, String description, Color color, String ticketPrefix, String ticketRole) {
        event.reply("Feedback Ticket issued! Please wait for our Admins to get back to you!").setEphemeral(true).queue();
        String ticketNumber = ticketPrefix + "#" + generateRandomTicketNumber(new Random());
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setThumbnail(event.getUser().getAvatarUrl())
                .addField("#\uFE0F⃣ Ticket Number: ", ticketNumber, true)
                .addField("\uD83D\uDC64 Author @:", event.getUser().getAsMention(), true)
                .addField("\uD83D\uDC64 Author name: ", "@" + event.getUser().getEffectiveName(), true);

        if(!ticketPrefix.equalsIgnoreCase("tp")){
            Role getAsMention = event.getGuild().getRolesByName(ticketRole, true).get(0);
            String mention = getAsMention.getAsMention();
            event.getGuild().getTextChannelById(channelId).sendMessage(mention).queue();
        }

        AtomicReference<String> messageID = new AtomicReference<>("");
        if(ticketPrefix.equalsIgnoreCase("tech_bug")) {
            event.getGuild().getTextChannelById(channelId).sendMessageEmbeds(builder.build()).setActionRow(Button.success("reply", "Admin Reply")).queue(message -> {
                messageID.set(message.getId());
            });
            String DiscordURL = "https://discord.com/channels/" + event.getGuild().getId() + "/" + channelId + "/" + messageID;
            GithubManager.createIssue(dotenv.get("GITHUB_TOKEN_PROD"), dotenv.get("GITHUB_OWNER"), dotenv.get("GITHUB_REPO"), title + " " + ticketNumber, description + "\n\n" + builder.getFields().getLast().getName() + " " + event.getUser().getEffectiveName() + "\n" + DiscordURL);
        }
        else {
            event.getGuild().getTextChannelById(channelId).sendMessageEmbeds(builder.build()).setActionRow(Button.success("reply", "Admin Reply")).queue();
        }
    }

    /**
     * Handles the Tech Bug ticket.
     *
     * @param event the ModalInteractionEvent
     * @param techBugID the ID of the Tech Bug channel
     */
    private void TechBugTicketHandler(ModalInteractionEvent event, String techBugID) {
        String description = "**\uD83D\uDCD3 Steps to reproduce:** \n  > " + Objects.requireNonNull(event.getValue("bugst")).getAsString()
                + "\n\uD83D\uDD28 Platform: " + Objects.requireNonNull(event.getValue("bugpl")).getAsString()
                + "\n\uD83D\uDCF1 Device Type: " + Objects.requireNonNull(event.getValue("bugde")).getAsString()
                + "\n\uD83C\uDF10 Browser Type: " + Objects.requireNonNull(event.getValue("bugbr")).getAsString();
        handleTicket(event, techBugID, "\uD83D\uDC1B" + Objects.requireNonNull(event.getValue("bugtl")).getAsString(), description, Color.RED, "Tech_BUG", "developer");
    }

    /**
     * Handles the Tech General ticket.
     *
     * @param event the ModalInteractionEvent
     * @param techChannelID the ID of the Tech channel
     */
    private void TechGenTicketHandler(ModalInteractionEvent event, String techChannelID) {
        String description = "**\uD83D\uDCAC Content:** \n  > " + Objects.requireNonNull(event.getValue("asktech")).getAsString();
        handleTicket(event, techChannelID, "\uD83D\uDEE0\uFE0F Tech Feedback Ticket", description, Color.GREEN, "TECHGEN", "developer");
    }

    /**
     * Handles the Training Program ticket.
     *
     * @param event the ModalInteractionEvent
     * @param tpchannel the ID of the Training Program channel
     * @param tacchannel the ID of the Tactics channel
     */
    private void TpTicketHandler(ModalInteractionEvent event, String tpchannel, String tacchannel) {
        String description = "**\uD83D\uDCAC Content:** \n  > " + Objects.requireNonNull(event.getValue("askt")).getAsString();
        if (description.toLowerCase().contains("tactics test")) {
            handleTicket(event, tacchannel, "\uD83D\uDCD5 Training Program Feedback Ticket", description, Color.WHITE, "TP", "tp");
        } else {
            handleTicket(event, tpchannel, "\uD83D\uDCD5 Training Program Feedback Ticket", description, Color.WHITE, "TP", "tp");
        }
    }

    /**
     * Handles the Sensei ticket.
     *
     * @param event the ModalInteractionEvent
     * @param senseiChannelId the ID of the Sensei channel
     */
    private void SenseiTicketHandler(ModalInteractionEvent event, String senseiChannelId) {
        String description = "**\uD83D\uDCAC Content:** \n  > " + Objects.requireNonNull(event.getValue("asks")).getAsString();
        handleTicket(event, senseiChannelId, "\uD83E\uDD4B Sensei Questions Ticket", description, Color.ORANGE, "SENSEI", "sensei");
    }

    /**
     * Sent the forms based on the button interaction event.
     *
     * @param event the ButtonInteraction
     */
    public void sentTheForms(ButtonInteraction event){
        switch (event.getComponentId()){
            case "sen" -> {
                Modal modal = buildForm(
                        "asks",
                        "Ask Sensei question",
                        "Please only ask chess questions to Dojo Sensei!",
                        "sensei-modal",
                        "Sensei Questions"
                );

                event.replyModal(modal).queue();
            }
            case "tp" -> {
                Modal modal = buildForm(
                        "askt",
                        "Provide Feedback",
                        "Please only provide feedback about Training Program, add spoiler for tactic test answers",
                        "tp-modal",
                        "Training Program Feedback"
                );

                event.replyModal(modal).queue();

            }
            case "tech" -> {
                Modal modal = buildForm(
                        "asktech",
                        "Provide Tech Feedback",
                        "Please only provide only Tech feedback related to Discord or ChessDojo.club",
                        "tech-modal",
                        "Tech Feedback"
                );

                event.replyModal(modal).queue();
            }

            case "bug" -> {
                try {
                    Modal modal1 = buildTechForm("bug", "bug-modal", "Bug Report");
                    System.out.println("YO");
                    event.replyModal(modal1).queue();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
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

    /**
     * Builds a form modal.
     *
     * @param textId the ID of the text input
     * @param labelInfo the label information
     * @param insideInfo the placeholder information
     * @param modalId the ID of the modal
     * @param modalTitle the title of the modal
     * @return the built Modal
     */
    private Modal buildForm(String textId, String labelInfo, String insideInfo, String modalId, String modalTitle) {
        TextInput wtext = TextInput.create(textId, labelInfo, TextInputStyle.PARAGRAPH)
                .setPlaceholder(insideInfo)
                .setMinLength(5)
                .setMaxLength(3000)
                .setRequired(true)
                .build();
        return Modal.create(modalId, modalTitle).addActionRow(wtext).build();
    }

    /**
     * Builds a tech form modal.
     *
     * @param textId the ID of the text input
     * @param modalId the ID of the modal
     * @param modalTitle the title of the modal
     * @return the built Modal
     */
    private Modal buildTechForm(String textId, String modalId, String modalTitle) {
        TextInput ttect = TextInput.create(textId + "tl", "Enter Short Title", TextInputStyle.SHORT)
                .setMaxLength(10)
                .setPlaceholder("provide short title")
                .setMaxLength(50)
                .setRequired(true)
                .build();
        TextInput wtext = TextInput.create(textId + "pl", "Enter Platform", TextInputStyle.SHORT)
                .setMinLength(5)
                .setPlaceholder("chessdojo.club/Discord")
                .setMaxLength(200)
                .setRequired(true)
                .build();
        TextInput wtextl = TextInput.create(textId + "st", "Enter Steps", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setPlaceholder("Please only provide only Bug report related to Discord or ChessDojo.club")
                .setMaxLength(3000)
                .setRequired(true)
                .build();
        TextInput wtextdevice = TextInput.create(textId + "de", "Enter Device", TextInputStyle.SHORT)
                .setPlaceholder("Mobile/Desktop/Tablet")
                .setMinLength(5)
                .setMaxLength(200)
                .setRequired(true)
                .build();
        TextInput wtextbrowser = TextInput.create(textId + "br", "Enter Browser", TextInputStyle.SHORT)
                .setMinLength(4)
                .setPlaceholder("Chrome/Edge/etc")
                .setMaxLength(200)
                .setRequired(true)
                .build();
        return Modal.create("bug-modal", modalTitle)
                .addActionRow(ttect)
                .addActionRow(wtext)
                .addActionRow(wtextl)
                .addActionRow(wtextdevice)
                .addActionRow(wtextbrowser)
                .build();
    }

    /**
     * Generates a random ticket number.
     *
     * @param random the Random instance
     * @return the generated ticket number
     */
    private static int generateRandomTicketNumber(Random random) {
        return 100 + random.nextInt(900);
    }
}


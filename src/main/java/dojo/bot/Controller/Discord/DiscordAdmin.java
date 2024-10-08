package dojo.bot.Controller.Discord;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

/**
 * The type Discord admin.
 */
public class DiscordAdmin {

    /**
     * Discord Action containing admin User ids, checks if a user is admin or not
     *
     * @param event Slash command event
     * @return true for is user admin or false
     */
    public static boolean isDiscordAdmin(GenericCommandInteractionEvent event) {
        return event.getUser().getId().equalsIgnoreCase("403639644887056406") ||
                event.getUser().getId().equalsIgnoreCase("893527007051259954") ||
                event.getUser().getId().equalsIgnoreCase("338307632232398850") ||
                event.getUser().getId().equalsIgnoreCase("403217126820675594") ||
                event.getUser().getId().equalsIgnoreCase("683031755271569528") ||
                event.getUser().getId().equalsIgnoreCase("321893042485460992");
    }


    /**
     * Is discord admin button boolean.
     *
     * @param event the event
     * @return the boolean
     */
    public static boolean isDiscordAdminButton(ButtonInteraction event){
        return event.getUser().getId().equalsIgnoreCase("403639644887056406") ||
                event.getUser().getId().equalsIgnoreCase("893527007051259954") ||
                event.getUser().getId().equalsIgnoreCase("338307632232398850") ||
                event.getUser().getId().equalsIgnoreCase("403217126820675594") ||
                event.getUser().getId().equalsIgnoreCase("683031755271569528") ||
                event.getUser().getId().equalsIgnoreCase("321893042485460992")
                || event.getUser().getId().equalsIgnoreCase("476559788822626305");
    }

    /**
     * Is discord admin message boolean.
     *
     * @param event the event
     * @return the boolean
     */
    public static boolean isDiscordAdminMessage(MessageReceivedEvent event){
        return event.getMember().getUser().getId().equalsIgnoreCase("403639644887056406") ||
                event.getMember().getUser().getId().equalsIgnoreCase("893527007051259954") ||
                event.getMember().getUser().getId().equalsIgnoreCase("338307632232398850") ||
                event.getMember().getUser().getId().equalsIgnoreCase("403217126820675594") ||
                event.getMember().getUser().getId().equalsIgnoreCase("683031755271569528") ||
                event.getMember().getUser().getId().equalsIgnoreCase("321893042485460992")
                || event.getMember().getUser().getId().equalsIgnoreCase("476559788822626305");
    }

    /**
     * Is discord admin modal boolean.
     *
     * @param event the event
     * @return the boolean
     */
    public static boolean isDiscordAdminModal(ModalInteractionEvent event){
        return event.getMember().getUser().getId().equalsIgnoreCase("403639644887056406") ||
                event.getMember().getUser().getId().equalsIgnoreCase("893527007051259954") ||
                event.getMember().getUser().getId().equalsIgnoreCase("338307632232398850") ||
                event.getMember().getUser().getId().equalsIgnoreCase("403217126820675594") ||
                event.getMember().getUser().getId().equalsIgnoreCase("683031755271569528") ||
                event.getMember().getUser().getId().equalsIgnoreCase("321893042485460992")
                || event.getMember().getUser().getId().equalsIgnoreCase("476559788822626305");
    }





}

package fi.natroutter.foxframe.bot.listeners;

import fi.natroutter.foxframe.FoxFrame;
import fi.natroutter.foxframe.bot.DiscordBot;
import fi.natroutter.foxframe.bot.command.AutoComplete;
import fi.natroutter.foxframe.bot.command.DiscordCommand;
import fi.natroutter.foxlib.logger.FoxLogger;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Routes autocomplete interactions to the source the command registered for that option.
 *
 * <p>Every path answers the interaction. An unanswered one leaves the user's client showing
 * "loading options" until it times out, with nothing to say what went wrong.
 */
public class AutoCompleteListener extends ListenerAdapter {

    private final DiscordBot bot;
    private final FoxLogger logger = FoxFrame.getLogger();

    public AutoCompleteListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (bot.commands() == null || bot.commands().isEmpty()) {
            return;
        }

        for (DiscordCommand cmd : bot.commands()) {
            if (!event.getName().equalsIgnoreCase(cmd.getName())) {
                continue;
            }

            AutoComplete source = cmd.autoCompleteFor(event.getFocusedOption().getName());
            if (source == null) {
                event.replyChoices(List.of()).queue();
                return;
            }

            try {
                event.replyChoices(valid(source.suggest(event.getFocusedOption().getValue(), event))).queue();
            } catch (Exception e) {
                logger.error("Autocomplete failed for /" + cmd.getName()
                        + " option " + event.getFocusedOption().getName(), e);
                event.replyChoices(List.of()).queue();
            }
            return;
        }
    }

    /**
     * Drops anything Discord would reject and caps the list, so one oversized entry cannot fail the
     * whole reply.
     */
    private static List<Command.Choice> valid(List<Command.Choice> choices) {
        if (choices == null) {
            return List.of();
        }

        List<Command.Choice> valid = new ArrayList<>();
        for (Command.Choice choice : choices) {
            if (choice == null) {
                continue;
            }
            String name = choice.getName();
            if (name == null || name.isEmpty() || name.length() > Command.Choice.MAX_NAME_LENGTH) {
                continue;
            }
            if (choice.getType() == OptionType.STRING
                    && choice.getAsString().length() > Command.Choice.MAX_STRING_VALUE_LENGTH) {
                continue;
            }

            valid.add(choice);
            if (valid.size() == OptionData.MAX_CHOICES) {
                break;
            }
        }
        return valid;
    }
}
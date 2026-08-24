package fi.natroutter.foxframe.bot.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Supplies the choices offered while a user types into one option of a command.
 *
 * <p>Registered per option with {@link DiscordCommand #autoComplete(String, AutoComplete)}. The
 * framework routes the interaction, caps and validates what comes back, and always answers it, so
 * an implementation only has to decide what to suggest.
 */
@FunctionalInterface
public interface AutoComplete {

    /**
     * @param query what the user has typed so far — never null, may be empty
     * @param event the raw interaction, for sources that depend on who is asking or where
     * @return the choices to offer, best first; trimmed to Discord's limits by the framework
     */
    List<Command.Choice> suggest(String query, CommandAutoCompleteInteractionEvent event);

    /** Suggests from a collection of names, read fresh on every keystroke. */
    static AutoComplete fromStrings(Supplier<? extends Collection<String>> values) {
        return from(values, value -> new Command.Choice(value, value));
    }

    /**
     * Suggests from a collection of names, re-reading them at most once per {@code ttl}.
     *
     * <p>The cache holds the built choice list, not the answer to any one query: filtering still
     * happens on every keystroke, so suggestions stay responsive to what is being typed while the
     * source behind them is only walked occasionally.
     */
    static AutoComplete fromStrings(Duration ttl, Supplier<? extends Collection<String>> values) {
        return from(ttl, values, value -> new Command.Choice(value, value));
    }

    /** Suggests from anything that can be turned into a choice. Return null to skip an item. */
    static <T> AutoComplete from(Supplier<? extends Collection<T>> values,
                                 Function<T, Command.Choice> asChoice) {
        return (query, event) -> filter(build(values.get(), asChoice), query);
    }

    /** As {@link #from(Supplier, Function)}, rebuilding the choices at most once per {@code ttl}. */
    static <T> AutoComplete from(Duration ttl, Supplier<? extends Collection<T>> values,
                                 Function<T, Command.Choice> asChoice) {
        AtomicReference<List<Command.Choice>> cached = new AtomicReference<>(List.of());
        AtomicLong rebuildAt = new AtomicLong(0);

        return (query, event) -> {
            long now = System.currentTimeMillis();
            if (now >= rebuildAt.get()) {
                // Two threads can rebuild at once; the work is identical, so let them.
                cached.set(build(values.get(), asChoice));
                rebuildAt.set(now + ttl.toMillis());
            }
            return filter(cached.get(), query);
        };
    }

    /**
     * Case-insensitive match on the choice's display name, with prefix matches ahead of the rest.
     * Exposed because a custom source usually wants the same behaviour over its own list.
     */
    static List<Command.Choice> filter(Collection<Command.Choice> choices, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            return List.copyOf(choices);
        }

        List<Command.Choice> starts = new ArrayList<>();
        List<Command.Choice> contains = new ArrayList<>();
        for (Command.Choice choice : choices) {
            String name = choice.getName().toLowerCase(Locale.ROOT);
            if (name.startsWith(needle)) {
                starts.add(choice);
            } else if (name.contains(needle)) {
                contains.add(choice);
            }
        }
        starts.addAll(contains);
        return starts;
    }

    private static <T> List<Command.Choice> build(Collection<T> values, Function<T, Command.Choice> asChoice) {
        return values.stream().map(asChoice).filter(Objects::nonNull).toList();
    }
}
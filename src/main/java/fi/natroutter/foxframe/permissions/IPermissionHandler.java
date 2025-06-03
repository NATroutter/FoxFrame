package fi.natroutter.foxframe.permissions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class IPermissionHandler {

    public abstract void has(Member member, Guild guild, INode node, Consumer<Boolean> result);

    public CompletableFuture<Boolean> has(Member member, Guild guild, INode nodes) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        has(member,guild,nodes, result::complete);
        return result;
    }

}

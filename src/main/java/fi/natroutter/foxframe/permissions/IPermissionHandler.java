package fi.natroutter.foxframe.permissions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class IPermissionHandler {

    public abstract void has(Member member, Guild guild, INode node, Runnable success, Runnable failed);

    public void has(Member member, Guild guild, INode node, Runnable success) {
        has(member,guild,node,success, ()->{});
    }

    public CompletableFuture<Boolean> has(Member member, Guild guild, INode node) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        has(member, guild, node,
                () -> future.complete(true),     // on success
                () -> future.complete(false)     // on failure
        );
        return future;
    }

}

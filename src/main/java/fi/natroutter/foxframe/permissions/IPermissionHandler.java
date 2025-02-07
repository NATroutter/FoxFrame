package fi.natroutter.foxframe.permissions;

import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface IPermissionHandler {

    void has(Member member, INode node, Consumer<Boolean> action);
    CompletableFuture<Boolean> has(Member member, INode nodes);

}

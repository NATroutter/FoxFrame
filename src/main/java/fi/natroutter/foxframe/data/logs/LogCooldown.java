package fi.natroutter.foxframe.data.logs;

import fi.natroutter.foxlib.cooldown.Cooldown;
import fi.natroutter.foxlib.cooldown.CooldownEntry;
import fi.natroutter.foxlib.logger.types.ILogData;

import java.util.concurrent.TimeUnit;

public class LogCooldown<T> implements ILogData {

    private String data;

    public LogCooldown(Cooldown<T> cooldown, T target) {
        CooldownEntry entry = cooldown.getEntry(target);

        long remaining = cooldown.getCooldown(target, entry.timeUnit());
        long now = System.currentTimeMillis();

        String unit = entry.timeUnit().name();
        String unitCap = unit.substring(0, 1).toUpperCase() + unit.substring(1);

        data = "(remaining:"+remaining+" time:"+entry.time()+" unit:"+unitCap+" start:"+entry.start()+", now:"+now+")";
    }

    @Override
    public String key() {
        return "Cooldown";
    }

    @Override
    public Object data() {
        return data;
    }
}

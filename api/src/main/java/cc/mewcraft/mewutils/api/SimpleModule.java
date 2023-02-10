package cc.mewcraft.mewutils.api;

import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class SimpleModule implements Terminable, Listener {

    private CompositeTerminable terminable;

    public SimpleModule(final CompositeTerminable terminable) {
        this.terminable = terminable;
    }

    @Override public void close() throws Exception {
        HandlerList.unregisterAll(this);
    }

}

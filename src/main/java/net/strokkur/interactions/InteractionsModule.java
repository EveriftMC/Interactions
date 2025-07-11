package net.strokkur.interactions;

import com.google.inject.AbstractModule;
import net.strokkur.interactions.config.InteractionsConfig;
import net.strokkur.interactions.config.InteractionsConfigImpl;
import net.strokkur.interactions.gui.InteractionsMenu;
import net.strokkur.interactions.gui.InteractionsMenuFastInv;

class InteractionsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(InteractionsConfig.class).toInstance(new InteractionsConfigImpl());
        bind(InteractionsMenu.class).to(InteractionsMenuFastInv.class);
    }
}

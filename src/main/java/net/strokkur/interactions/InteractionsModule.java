/*
 * CigarPlugin - Smoke a cigar with style!
 * Copyright (C) 2025  Strokkur24
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

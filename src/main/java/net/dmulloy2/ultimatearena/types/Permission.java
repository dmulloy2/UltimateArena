/**
 * UltimateArena - fully customizable PvP arenas
 * Copyright (C) 2012 - 2015 MineSworn
 * Copyright (C) 2013 - 2015 dmulloy2
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.ultimatearena.types;

import lombok.Getter;
import net.dmulloy2.swornapi.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
public enum Permission implements IPermission
{
	ABANDON,
	CLASS,
	CLASSLIST,
	CREATE,
	DELETE,
	DISABLE,
	DISLIKE,
	ENABLE,
	INFO,
	JOIN,
	JOIN_FORCE,
	JOIN_FULL,
	KICK,
	LIKE,
	LIST,
	OPTION,
	PAUSE,
	RELOAD,
	SETPOINT,
	SPECTATE,
	START,
	STATS,
	STOP,
	TELEPORT,
	UNDO,
	VERSION,

	BUILD,
	BYPASS,
	;

	private final String node;
	Permission()
	{
		this.node = toString().toLowerCase().replaceAll("_", ".");
	}
}

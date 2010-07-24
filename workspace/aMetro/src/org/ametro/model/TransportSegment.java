/*
 * http://code.google.com/p/ametro/
 * Transport map viewer for Android platform
 * Copyright (C) 2009-2010 Roman.Golovanov@gmail.com and other
 * respective project committers (see project home page)
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
 *  
 */
package org.ametro.model;

public class TransportSegment {

	public static final int TYPE_INVISIBLE = 1;
	
	public static final String INVISIBLE = "invisible";
	
	public int id;
	public int lineId;
	public int mapId;
	
	public int stationFromId;
	public int stationToId;

	public Integer delay;

	public int flags;
	
	public Model owner;

    public String toString() {
        return "[ID:" + id + ";FROM:" + owner.texts[owner.stations[stationFromId].name] + ";TO:" + owner.texts[owner.stations[stationToId].name] + ";LINE:" + owner.texts[owner.lines[lineId].name] + ";DELAY:" + delay + "]";
    }	
	
}

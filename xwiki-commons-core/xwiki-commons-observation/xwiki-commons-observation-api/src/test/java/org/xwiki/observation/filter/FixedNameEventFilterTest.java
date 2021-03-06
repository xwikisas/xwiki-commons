/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.observation.filter;

import org.junit.jupiter.api.Test;
import org.xwiki.observation.event.filter.AlwaysMatchingEventFilter;
import org.xwiki.observation.event.filter.FixedNameEventFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for {@link FixedNameEventFilter}.
 *
 * @version $Id$
 */
public class FixedNameEventFilterTest
{
    @Test
    void testFixedNameEventFilterTest()
    {
        FixedNameEventFilter filter = new FixedNameEventFilter("filter");

        assertFalse(filter.equals(null));
        assertFalse(filter.equals(AlwaysMatchingEventFilter.INSTANCE));
        assertFalse(filter.equals(new FixedNameEventFilter("filter2")));

        assertEquals(filter, filter);
        assertEquals(filter, new FixedNameEventFilter("filter"));
    }
}

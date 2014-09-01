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
package org.xwiki.observation;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.observation.event.ActionExecutionEvent;
import org.xwiki.observation.event.AllEvent;
import org.xwiki.observation.event.Event;
import org.xwiki.observation.internal.DefaultObservationManager;
import org.xwiki.test.jmock.JMockRule;

/**
 * Unit tests for {@link ObservationManager}.
 * 
 * @version $Id$
 */
public class ObservationManagerTest
{
    @Rule
    public final JMockRule mockery = new JMockRule();

    private ObservationManager manager;

    @Before
    public void setUp()
    {
        this.manager = new DefaultObservationManager();
    }

    @Test
    public void testNotifyWhenMatching()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event event = this.mockery.mock(Event.class);
        
        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(event)));
            oneOf(listener).onEvent(event, "some source", "some data");
            oneOf(event).matches(event); will(returnValue(true));
        }});
        
        this.manager.addListener(listener);
        Assert.assertSame(listener, this.manager.getListener("mylistener"));
        this.manager.notify(event, "some source", "some data");
    }

    @Test
    public void testRemoveListener()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event event = this.mockery.mock(Event.class);
        
        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(event)));
            never(listener).onEvent(with(any(Event.class)), with(any(Object.class)), with(any(Object.class)));
        }});
        
        this.manager.addListener(listener);
        this.manager.removeListener("mylistener");
        this.manager.notify(event, null);
    }

    @Test
    public void testAddEvent() throws Exception
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event initialEvent = this.mockery.mock(Event.class, "initial");
        final Event afterEvent = this.mockery.mock(Event.class, "after");
        final Event notifyEvent = this.mockery.mock(Event.class, "notify");

        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(initialEvent)));
            oneOf(listener).onEvent(with(any(Event.class)), with(nullValue()), with(nullValue()));
            
            // Since the observation returns the first matching event, return false from initialEvent so that 
            // afterEvent is called.
            oneOf(initialEvent).matches(with(same(notifyEvent))); will(returnValue(false));
            oneOf(afterEvent).matches(with(same(notifyEvent))); will(returnValue(true));
        }});
        
        this.manager.addListener(listener);
        this.manager.addEvent("mylistener", afterEvent);
        this.manager.notify(notifyEvent, null);
    }

    @Test
    public void testAddEventWithNoInitialEvent() throws Exception
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event afterEvent = this.mockery.mock(Event.class, "after");
        final Event notifyEvent = this.mockery.mock(Event.class, "notify");

        this.mockery.checking(new Expectations() {{
            allowing(listener).getName();
            will(returnValue("mylistener"));
            allowing(listener).getEvents();
            will(returnValue(Collections.emptyList()));
            oneOf(listener).onEvent(with(any(Event.class)), with(nullValue()), with(nullValue()));

            oneOf(afterEvent).matches(with(same(notifyEvent)));
            will(returnValue(true));
        }});

        this.manager.addListener(listener);
        this.manager.addEvent("mylistener", afterEvent);
        this.manager.notify(notifyEvent, null);
    }

    @Test
    public void testRemoveEvent()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event initialEvent = this.mockery.mock(Event.class, "initial");
        final Event afterEvent = this.mockery.mock(Event.class, "after");
        final Event notifyEvent = this.mockery.mock(Event.class, "notify");

        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(initialEvent)));
            
            // Since the observation returns the first matching event, return false from initialEvent so that 
            // the second event can be called (if there's a second event - in our case it'll be removed but
            // we still want the test to fail if that doesn't work).
            oneOf(initialEvent).matches(with(same(notifyEvent))); will(returnValue(false));
            
            // Ensure that the afterEvent is never called since we're adding it and removing it
            never(afterEvent);
        }});
        
        this.manager.addListener(listener);
        this.manager.addEvent("mylistener", afterEvent);
        this.manager.removeEvent("mylistener", afterEvent);
        this.manager.notify(notifyEvent, null);
    }
    
    /**
     * Verify that we can register two listeners on the same event and they'll both receive the event.
     */
    @Test
    public void testRegisterSeveralListenersForSameEvent()
    {
        final EventListener listener1 = this.mockery.mock(EventListener.class, "listener1");
        final EventListener listener2 = this.mockery.mock(EventListener.class, "listener2");
        final Event event = this.mockery.mock(Event.class, "event");
        final Event notifyEvent = this.mockery.mock(Event.class, "notify");

        this.mockery.checking(new Expectations() {{
            allowing(listener1).getName(); will(returnValue("listener 1"));
            allowing(listener2).getName(); will(returnValue("listener 2"));
            allowing(listener1).getEvents(); will(returnValue(Arrays.asList(event)));
            allowing(listener2).getEvents(); will(returnValue(Arrays.asList(event)));

            allowing(event).matches(with(same(notifyEvent))); will(returnValue(true));
            oneOf(listener1).onEvent(with(any(Event.class)), with(nullValue()), with(nullValue()));
            oneOf(listener2).onEvent(with(any(Event.class)), with(nullValue()), with(nullValue()));
        }});

        this.manager.addListener(listener1);
        this.manager.addListener(listener2);
        this.manager.notify(notifyEvent, null);
    }
    
    /**
     * Verify that we can register a listener to receive any kind of event using AllEvent event type.
     */
    @Test
    public void testRegisterListenerForAllEvents()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event event = this.mockery.mock(Event.class);
        
        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(AllEvent.ALLEVENT)));
            oneOf(listener).onEvent(event, "some source", "some data");
        }});
        
        this.manager.addListener(listener);
        Assert.assertSame(listener, this.manager.getListener("mylistener"));
        this.manager.notify(event, "some source", "some data");
    }
    
    /**
     * Verify that a warning is logged is we try to register a listener with the same name.
     */
    @Test
    public void testRegisterSameListenerSeveralTimes()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Logger logger = this.mockery.mock(Logger.class);

        ReflectionUtils.setFieldValue(this.manager, "logger", logger);

        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(AllEvent.ALLEVENT)));
            // The check is performed here, we verify that a warning is correctly logged
            oneOf(logger).warn(with(containsString("listener is overwritting a previously registered listener")),
                with(any(Object[].class)));
        }});
        
        this.manager.addListener(listener);
        // Will raise log warning on the next line 
        this.manager.addListener(listener);
        
        // Verify that no log is logged if we remove the listener before re-registering it
        this.manager.removeListener("mylistener");
        // Next line will not log any warning
        this.manager.addListener(listener);
    }
    
    /**
     * Verify that we can register a listener with two events of the same type but with not matching the same thing and it will receive both events.
     */
    @Test
    public void testRegisterListenerForTwoEventsOfSameType()
    {
        final EventListener listener = this.mockery.mock(EventListener.class);
        final Event eventMatcher1 = new ActionExecutionEvent("action1");
        final Event eventMatcher2 = new ActionExecutionEvent("action2");
        
        this.mockery.checking(new Expectations() {{
            allowing(listener).getName(); will(returnValue("mylistener"));
            allowing(listener).getEvents(); will(returnValue(Arrays.asList(eventMatcher1, eventMatcher2)));

            oneOf(listener).onEvent(with(eventMatcher1), with(any(Object.class)), with(any(Object.class)));
            oneOf(listener).onEvent(with(eventMatcher2), with(any(Object.class)), with(any(Object.class)));
        }});

        this.manager.addListener(listener);
        this.manager.notify(eventMatcher1, "some source", "some data");
        this.manager.notify(eventMatcher2, "some source", "some data");
    }
}

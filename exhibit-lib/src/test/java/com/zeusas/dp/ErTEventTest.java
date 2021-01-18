/**
 * 
 */
package com.zeusas.dp;

import org.junit.Test;

import com.google.common.eventbus.EventBus;

/**
 * @author pengbo
 *
 */
public class ErTEventTest {

	@Test
    public void testReceiveEvent() throws Exception {

        EventBus eventBus = new EventBus("test");
        ErtEventListener listener = new ErtEventListener();

        eventBus.register(listener);

        eventBus.post(new ErTEvent(200));
        eventBus.post(new ErTEvent(300));
        eventBus.post(new ErTEvent(400));

        System.out.println("LastMessage:"+listener.getlastMessage());
        ;
    }
}

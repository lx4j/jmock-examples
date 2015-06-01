package test;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import util.Announcer;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.any;

/**
 * Created by L.x on 15-6-1.
 */
public class AnnouncerTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private final Object anEvent = new Object();
    private Announcer<Listener> announcer = Announcer.to(Listener.class);
    @Mock
    private Listener listener;

    @Test
    public void notifyAllSubscribedListeners() throws Exception {
        expectSubscribedListenersToBeNotified(10);

        announcer.proxy().onChanged(anEvent);
    }

    @Test
    public void stopNotifyRemovedListeners() throws Exception {
        expectSubscribedListenersToBeNotified(10);
        announcer.add(listener);

        expectNotToBeNotified(listener);

        announcer.remove(listener);
        announcer.proxy().onChanged(anEvent);
    }

    @Test
    public void notifyAllSubscribedListenersEvenIfOthersThrowsException() throws Exception {
        announcer.add(listener);
        expectSubscribedListenersToBeNotified(10);

        context.checking(new Expectations() {{
            allowing(listener).onChanged(with(anyEvent()));
            will(throwException(new RuntimeException()));
        }});

        announcer.proxy().onChanged(anEvent);
    }

    public interface Listener {
        void onChanged(Object anEvent);
    }

    private void expectSubscribedListenersToBeNotified(int count) {
        for (Listener listener : mockListeners(count)) {
            announcer.add(listener);
            expectToBeNotified(listener);
        }
    }

    private List<Listener> mockListeners(int count) {
        List<Listener> listeners = new ArrayList<Listener>();
        for (int i = 0; i < count; i++) {
            final Listener listener = context.mock(Listener.class, format("listener-%d", i));
            listeners.add(listener);
        }
        return listeners;
    }

    private void expectNotToBeNotified(final Listener listener) {
        context.checking(new Expectations() {{
            never(listener).onChanged(with(anyEvent()));
        }});
    }

    private Matcher<Object> anyEvent() {
        return any(Object.class);
    }

    private void expectToBeNotified(final Listener listener) {
        context.checking(new Expectations() {{
            oneOf(listener).onChanged(with(anEvent));
        }});
    }
}

package io.github.xesam.android.views.status;

import org.junit.Before;
import org.junit.Test;

import android.view.View;

import static org.junit.Assert.*;

public class StatusCoordinatorTest {

    private StatusCoordinator<String> coordinator;
    private MockView contentView;
    private MockView loadingView;
    private MockView emptyView;
    private MockView errorView;

    @Before
    public void setUp() {
        coordinator = new StatusCoordinator<String>();
        contentView = new MockView();
        loadingView = new MockView();
        emptyView = new MockView();
        errorView = new MockView();
    }

    @Test
    public void testRegisterStatus() {
        coordinator.registerStatus("content", contentView);

        assertTrue(coordinator.getRegisteredStatuses().contains("content"));
        assertEquals("content", coordinator.getCurrentStatus());
        assertTrue(contentView.isVisible());
    }

    @Test
    public void testRegisterMultipleStatus() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView)
                .registerStatus("empty", emptyView);

        assertEquals(3, coordinator.getRegisteredStatuses().size());
        assertEquals("content", coordinator.getCurrentStatus());
        assertTrue(contentView.isVisible());
        assertFalse(loadingView.isVisible());
        assertFalse(emptyView.isVisible());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullView() {
        coordinator.registerStatus("content", null);
    }

    @Test
    public void testSetStatus() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        coordinator.setStatus("loading");

        assertEquals("loading", coordinator.getCurrentStatus());
        assertFalse(contentView.isVisible());
        assertTrue(loadingView.isVisible());
    }

    @Test
    public void testSetSameStatus() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        coordinator.setStatus("content");
        contentView.reset();
        coordinator.setStatus("content");

        assertFalse(contentView.wasVisibilityChanged());
    }

    @Test
    public void testSetStatusNotFound() {
        coordinator.registerStatus("content", contentView);

        coordinator.setStatus("not_exist");

        assertEquals("content", coordinator.getCurrentStatus());
        assertTrue(contentView.isVisible());
    }

    @Test
    public void testSetStatusNotFoundWithListener() {
        coordinator.registerStatus("content", contentView);

        final String[] notFoundStatus = {null};
        coordinator.setOnStatusNotFoundListener(status -> {
            notFoundStatus[0] = status;
        });

        coordinator.setStatus("not_exist");

        assertEquals("not_exist", notFoundStatus[0]);
        assertEquals("content", coordinator.getCurrentStatus());
    }

    @Test
    public void testGetViewForStatus() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        assertEquals(contentView, coordinator.getViewForStatus("content"));
        assertEquals(loadingView, coordinator.getViewForStatus("loading"));
        assertNull(coordinator.getViewForStatus("not_exist"));
    }

    @Test
    public void testStatusChangeListener() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        final String[] oldStatus = {null};
        final String[] newStatus = {null};

        coordinator.addOnStatusChangeListener((old, newS) -> {
            oldStatus[0] = old;
            newStatus[0] = newS;
        });

        coordinator.setStatus("loading");

        assertEquals("content", oldStatus[0]);
        assertEquals("loading", newStatus[0]);
    }

    @Test
    public void testRemoveStatusChangeListener() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        final int[] callCount = {0};
        OnStatusChangeListener<String> listener = (old, newS) -> {
            callCount[0]++;
        };

        coordinator.addOnStatusChangeListener(listener);
        coordinator.removeOnStatusChangeListener(listener);

        coordinator.setStatus("loading");

        assertEquals(0, callCount[0]);
        assertEquals("loading", coordinator.getCurrentStatus());
    }

    @Test
    public void testRemoveAllStatusChangeListeners() {
        coordinator.registerStatus("content", contentView)
                .registerStatus("loading", loadingView);

        final int[] callCount = {0};
        coordinator.addOnStatusChangeListener((old, newS) -> callCount[0]++);
        coordinator.addOnStatusChangeListener((old, newS) -> callCount[0]++);
        coordinator.removeAllStatusChangeListeners();

        coordinator.setStatus("loading");

        assertEquals(0, callCount[0]);
        assertEquals("loading", coordinator.getCurrentStatus());
    }

    private static class MockView extends View {
        private boolean visible = false;
        private boolean visibilityChanged = false;

        public MockView() {
            super(null);
        }

        @Override
        public void setVisibility(int visibility) {
            visibilityChanged = true;
            this.visible = (visibility == View.VISIBLE);
        }

        public boolean isVisible() {
            return visible;
        }

        public boolean wasVisibilityChanged() {
            return visibilityChanged;
        }

        public void reset() {
            visibilityChanged = false;
        }
    }
}

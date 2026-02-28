package io.github.xesam.android.views.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultiStatusHelperTest {

    @Mock
    private ViewGroup mockContainerView;

    @Mock
    private Context mockContext;

    @Mock
    private AttributeSet mockAttrs;

    @Mock
    private View mockContentView;

    @Mock
    private View mockLoadingView;

    @Mock
    private Resources mockResources;

    @Mock
    private TypedArray mockTypedArray;

    private MultiStatusHelper helper;

    @Before
    public void setUp() {
        lenient().when(mockContainerView.getContext()).thenReturn(mockContext);
        lenient().when(mockContext.obtainStyledAttributes(any(AttributeSet.class), any(int[].class))).thenReturn(mockTypedArray);
        lenient().when(mockTypedArray.getString(anyInt())).thenReturn("status_");
        lenient().when(mockTypedArray.getBoolean(anyInt(), anyBoolean())).thenReturn(false);
        helper = new MultiStatusHelper(mockContainerView, mockContext, mockAttrs);
    }

    @Test
    public void testRegisterStatus() {
        helper.registerStatus("content", mockContentView);

        assertTrue(helper.getRegisteredStatuses().contains("content"));
    }

    @Test
    public void testRegisterStatusByViewId() {
        when(mockContainerView.findViewById(android.R.id.content)).thenReturn(mockContentView);

        helper.registerStatusByViewId("content", android.R.id.content);

        assertTrue(helper.getRegisteredStatuses().contains("content"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterStatusByViewIdNotFound() {
        when(mockContainerView.findViewById(android.R.id.content)).thenReturn(null);

        helper.registerStatusByViewId("content", android.R.id.content);
    }

    @Test
    public void testSetStatus() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        helper.setStatus("loading");

        assertEquals("loading", helper.getCurrentStatus());
    }

    @Test
    public void testGetViewForStatus() {
        helper.registerStatus("content", mockContentView);

        assertEquals(mockContentView, helper.getViewForStatus("content"));
        assertNull(helper.getViewForStatus("not_exist"));
    }

    @Test
    public void testAddStatusChangeListener() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        final String[] oldStatus = {null};
        final String[] newStatus = {null};

        helper.addOnStatusChangeListener((old, newS) -> {
            oldStatus[0] = old;
            newStatus[0] = newS;
        });

        helper.setStatus("loading");

        assertEquals("content", oldStatus[0]);
        assertEquals("loading", newStatus[0]);
    }

    @Test
    public void testRemoveStatusChangeListener() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        helper.setStatus("content");

        helper.removeAllStatusChangeListeners();

        helper.setStatus("loading");

        assertEquals("loading", helper.getCurrentStatus());
    }

    @Test
    public void testRemoveAllStatusChangeListeners() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        helper.addOnStatusChangeListener((old, newS) -> {});
        helper.addOnStatusChangeListener((old, newS) -> {});
        helper.removeAllStatusChangeListeners();

        helper.setStatus("loading");

        assertEquals("loading", helper.getCurrentStatus());
    }

    @Test
    public void testSetOnStatusNotFoundListener() {
        final String[] notFoundStatus = {null};

        helper.setOnStatusNotFoundListener(status -> {
            notFoundStatus[0] = status;
        });

        helper.setStatus("not_exist");

        assertEquals("not_exist", notFoundStatus[0]);
    }

    @Test
    public void testAddStatusAlias() {
        helper.registerStatus("content", mockContentView);
        helper.setStatus("content");
        helper.addStatusAlias("main", "content");

        assertEquals("content", helper.getCurrentStatus());
    }

    @Test
    public void testGetCurrentStatus() {
        helper.registerStatus("content", mockContentView);

        assertEquals("content", helper.getCurrentStatus());
    }

    @Test
    public void testGetRegisteredStatuses() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        assertEquals(2, helper.getRegisteredStatuses().size());
    }

    @Test
    public void testStatusChangeWithErrorHandler() {
        helper.registerStatus("content", mockContentView)
                .registerStatus("loading", mockLoadingView);

        final Exception[] caughtException = {null};
        helper.addOnStatusChangeListener((old, newS) -> {
            throw new RuntimeException("Test exception");
        });
        helper.setErrorHandler(exception -> {
            caughtException[0] = exception;
        });

        helper.setStatus("loading");

        assertNotNull(caughtException[0]);
        assertEquals("loading", helper.getCurrentStatus());
    }
}

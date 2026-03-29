package es.ants.felixgm.trmsim_wsn.gui.support;

import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.Rectangle;

public final class MessageConsoleHelper {
    private static final int BOTTOM_TOLERANCE_PX = 16;

    private MessageConsoleHelper() {
    }

    public static void appendMessage(JTextArea textArea, String message) {
        if (textArea == null || message == null || message.isEmpty()) {
            return;
        }

        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, textArea);
        Point previousViewPosition = (viewport != null) ? viewport.getViewPosition() : null;
        boolean wasNearBottom = isNearBottom(textArea, viewport);

        textArea.append(message);

        if (viewport == null) {
            if (wasNearBottom) {
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            return;
        }

        Point targetPosition = wasNearBottom ? null : previousViewPosition;
        SwingUtilities.invokeLater(() -> {
            if (wasNearBottom) {
                scrollToBottom(textArea, viewport);
            } else if (targetPosition != null) {
                viewport.setViewPosition(targetPosition);
            }
        });
    }

    private static boolean isNearBottom(JTextArea textArea, JViewport viewport) {
        if (viewport == null) {
            return true;
        }
        Rectangle viewRect = viewport.getViewRect();
        int contentHeight = textArea.getHeight();
        return viewRect.y + viewRect.height >= contentHeight - BOTTOM_TOLERANCE_PX;
    }

    private static void scrollToBottom(JTextArea textArea, JViewport viewport) {
        int targetY = Math.max(0, textArea.getHeight() - viewport.getHeight());
        viewport.setViewPosition(new Point(0, targetY));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}

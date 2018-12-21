/*******************************************************************************
 * Copyright (c) 2012, 2018 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Original authors and others - initial API and implementation
 *     Dirk Fauth <dirk.fauth@googlemail.com> - Bug 454506
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.painter.cell;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommand;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * TextPainter that draws text into a cell horizontally. Can handle word
 * wrapping and/or word cutting and/or automatic calculation and resizing of the
 * cell width and height if the text does not fit into the cell.
 */
public class TextPainter extends AbstractTextPainter {

    /**
     * Flag to configure whether the preferred height is calculated on the
     * wrapped text or on the text that would be rendered without wrapping.
     * Default is <code>false</code>.
     */
    private boolean calculateWrappedHeight = false;

    /**
     * Creates a {@link TextPainter} that does not wrap text and paints the
     * background.
     */
    public TextPainter() {
        this(false, true);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     */
    public TextPainter(boolean wrapText, boolean paintBg) {
        this(wrapText, paintBg, 0);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     * @param spacing
     *            The space between text and cell border
     */
    public TextPainter(boolean wrapText, boolean paintBg, int spacing) {
        this(wrapText, paintBg, spacing, false);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     * @param calculate
     *            tells the text painter to calculate the cell borders regarding
     *            the content
     */
    public TextPainter(boolean wrapText, boolean paintBg, boolean calculate) {
        this(wrapText, paintBg, 0, calculate);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     * @param calculateByTextLength
     *            tells the text painter to calculate the cell border by
     *            containing text length. For horizontal text rendering, this
     *            means the width of the cell is calculated by content, for
     *            vertical text rendering the height is calculated
     * @param calculateByTextHeight
     *            tells the text painter to calculate the cell border by
     *            containing text height. For horizontal text rendering, this
     *            means the height of the cell is calculated by content, for
     *            vertical text rendering the width is calculated
     */
    public TextPainter(boolean wrapText, boolean paintBg,
            boolean calculateByTextLength, boolean calculateByTextHeight) {
        this(wrapText, paintBg, 0, calculateByTextLength, calculateByTextHeight);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     * @param spacing
     *            The space between text and cell border
     * @param calculate
     *            tells the text painter to calculate the cell borders regarding
     *            the content
     */
    public TextPainter(boolean wrapText, boolean paintBg, int spacing, boolean calculate) {
        super(wrapText, paintBg, spacing, calculate);
    }

    /**
     * @param wrapText
     *            split text over multiple lines
     * @param paintBg
     *            skips painting the background if is FALSE
     * @param spacing
     *            The space between text and cell border
     * @param calculateByTextLength
     *            tells the text painter to calculate the cell border by
     *            containing text length. For horizontal text rendering, this
     *            means the width of the cell is calculated by content, for
     *            vertical text rendering the height is calculated
     * @param calculateByTextHeight
     *            tells the text painter to calculate the cell border by
     *            containing text height. For horizontal text rendering, this
     *            means the height of the cell is calculated by content, for
     *            vertical text rendering the width is calculated
     */
    public TextPainter(boolean wrapText, boolean paintBg, int spacing,
            boolean calculateByTextLength, boolean calculateByTextHeight) {
        super(wrapText, paintBg, spacing, calculateByTextLength, calculateByTextHeight);
    }

    @Override
    public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
        setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
        return getLengthFromCache(gc, convertDataType(cell, configRegistry)) + (this.spacing * 2) + 1;
    }

    @Override
    public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
        setupGCFromConfig(gc, CellStyleUtil.getCellStyle(cell, configRegistry));
        String text = convertDataType(cell, configRegistry);
        if (!this.calculateWrappedHeight) {
            return gc.textExtent(text).y + (this.spacing * 2) + 1 + (getNumberOfNewLines(text) - 1) * this.lineSpacing;
        } else {
            Rectangle adjustedBounds = cell.getLayer().getLayerPainter().adjustCellBounds(
                    cell.getColumnPosition(),
                    cell.getRowPosition(),
                    cell.getBounds());
            text = getTextToDisplay(cell, gc, adjustedBounds.width, text);
            int numberOfNewLines = getNumberOfNewLines(text);
            return (gc.getFontMetrics().getHeight() * numberOfNewLines) + (this.lineSpacing * (numberOfNewLines - 1)) + (this.spacing * 2);
        }
    }

    @Override
    public void paintCell(ILayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {
        if (this.paintBg) {
            super.paintCell(cell, gc, rectangle, configRegistry);
        }

        if (this.paintFg) {
            Rectangle originalClipping = gc.getClipping();
            gc.setClipping(rectangle.intersection(originalClipping));

            IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
            setupGCFromConfig(gc, cellStyle);

            int fontHeight = gc.getFontMetrics().getHeight();
            String text = convertDataType(cell, configRegistry);

            // Draw Text
            text = getTextToDisplay(cell, gc, rectangle.width, text);

            int numberOfNewLines = getNumberOfNewLines(text);

            // if the content height is bigger than the available row height
            // we're extending the row height (only if word wrapping is enabled)
            int contentHeight = (fontHeight * numberOfNewLines) + (this.lineSpacing * (numberOfNewLines - 1)) + (this.spacing * 2);
            int contentToCellDiff = (cell.getBounds().height - rectangle.height);

            if (performRowResize(contentHeight, rectangle)) {
                ILayer layer = cell.getLayer();
                int rowPosition = cell.getRowPosition();
                if (cell.isSpannedCell()) {
                    // if spanned only resize bottom row and reduce height by
                    // upper row heights to resize to only the necessary height
                    rowPosition = cell.getOriginRowPosition() + cell.getRowSpan() - 1;
                    for (int i = cell.getOriginRowPosition(); i < rowPosition; i++) {
                        contentHeight -= layer.getRowHeightByPosition(i);
                    }
                }
                layer.doCommand(
                        new RowResizeCommand(layer, rowPosition, contentHeight + contentToCellDiff, true));
            }

            if (numberOfNewLines == 1) {
                int contentWidth = Math.min(getLengthFromCache(gc, text), rectangle.width);

                gc.drawText(
                        text,
                        rectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, rectangle, contentWidth) + this.spacing,
                        rectangle.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, rectangle, contentHeight) + this.spacing,
                        SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER);

                // start x of line = start x of text
                int x = rectangle.x
                        + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, rectangle, contentWidth)
                        + this.spacing;
                // y = start y of text
                int y = rectangle.y
                        + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, rectangle, contentHeight)
                        + this.spacing;
                int length = gc.textExtent(text).x;
                paintDecoration(cellStyle, gc, x, y, length, fontHeight);
            } else {
                // draw every line by itself because of the alignment, otherwise
                // the whole text is always aligned right
                int yStartPos = rectangle.y + CellStyleUtil.getVerticalAlignmentPadding(cellStyle, rectangle, contentHeight);
                String[] lines = text.split("\n"); //$NON-NLS-1$
                for (String line : lines) {
                    int lineContentWidth = Math.min(getLengthFromCache(gc, line), rectangle.width);

                    gc.drawText(
                            line,
                            rectangle.x + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, rectangle, lineContentWidth) + this.spacing,
                            yStartPos + this.spacing,
                            SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER);

                    // start x of line = start x of text
                    int x = rectangle.x
                            + CellStyleUtil.getHorizontalAlignmentPadding(cellStyle, rectangle, lineContentWidth)
                            + this.spacing;
                    // y = start y of text
                    int y = yStartPos + this.spacing;
                    int length = gc.textExtent(line).x;
                    paintDecoration(cellStyle, gc, x, y, length, fontHeight);

                    // after every line calculate the y start pos new
                    yStartPos += fontHeight;
                    yStartPos += this.lineSpacing;
                }
            }

            gc.setClipping(originalClipping);
            resetGC(gc);
        }
    }

    @Override
    protected void setNewMinLength(ILayerCell cell, int contentWidth) {
        int cellLength = cell.getBounds().width;
        if (cellLength < contentWidth) {
            ILayer layer = cell.getLayer();
            int columnPosition = cell.getColumnPosition();
            if (cell.isSpannedCell()) {
                // if spanned only resize rightmost column and reduce width by
                // left column widths to resize to only the necessary width
                columnPosition = cell.getOriginColumnPosition() + cell.getColumnSpan() - 1;
                for (int i = cell.getOriginColumnPosition(); i < columnPosition; i++) {
                    contentWidth -= layer.getColumnWidthByPosition(i);
                }
            }
            layer.doCommand(
                    new ColumnResizeCommand(layer, columnPosition, contentWidth, true));
        }
    }

    @Override
    protected int calculatePadding(ILayerCell cell, int availableLength) {
        return cell.getBounds().width - availableLength;
    }

    /**
     * Checks if a row resize needs to be triggered.
     *
     * @param contentHeight
     *            The necessary height to show the content completely
     * @param rectangle
     *            The available rectangle to render to
     * @return <code>true</code> if a row resize needs to be performed,
     *         <code>false</code> if not
     */
    protected boolean performRowResize(int contentHeight, Rectangle rectangle) {
        return ((contentHeight > rectangle.height) && this.calculateByTextHeight);
    }

    /**
     *
     * @return Whether the preferred height is calculated on the wrapped text or
     *         on the text that would be rendered without wrapping. Default is
     *         <code>false</code>.
     * @since 1.6
     */
    public boolean isCalculateWrappedHeight() {
        return this.calculateWrappedHeight;
    }

    /**
     * Configure how
     * {@link #getPreferredHeight(ILayerCell, GC, IConfigRegistry)} calculates
     * the height. Either based on the text as is, or taking text-wrapping into
     * account if enabled.
     *
     * @param calculateWrappedHeight
     *            <code>true</code> if the preferred height should be calculated
     *            on the wrapped text, <code>false</code> if the preferred
     *            height should be calculated without wrapping.
     * @since 1.6
     */
    public void setCalculateWrappedHeight(boolean calculateWrappedHeight) {
        this.calculateWrappedHeight = calculateWrappedHeight;
    }
}

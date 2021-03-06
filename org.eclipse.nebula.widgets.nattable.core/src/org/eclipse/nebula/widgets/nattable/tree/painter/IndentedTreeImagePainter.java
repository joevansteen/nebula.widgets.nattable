/*******************************************************************************
 * Copyright (c) 2012, 2020 Original authors and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.tree.painter;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.tree.config.DefaultTreeLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Implementation of CellPainterWrapper that is used to render tree structures
 * in NatTable. It puts indentation to tree nodes to visualize the tree
 * structure and adds expand/collapse icons corresponding to the state if a tree
 * node has children.
 */
public class IndentedTreeImagePainter extends CellPainterWrapper {
    /**
     * The number of pixels to indent per depth.
     */
    private final int treeIndent;

    private final CellPainterDecorator internalPainter;

    /**
     * Creates an IndentedTreeImagePainter. Will use 10 pixels for indentation
     * per depth and a default TreeImagePainter for rendering the icons in the
     * tree.
     */
    public IndentedTreeImagePainter() {
        this(10, new TreeImagePainter());
    }

    /**
     * Creates an IndentedTreeImagePainter. Will use the given number of pixels
     * for indentation per depth and a default {@link TreeImagePainter} for
     * rendering the icons in the tree.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     */
    public IndentedTreeImagePainter(int treeIndent) {
        this(treeIndent, new TreeImagePainter());
    }

    /**
     * Creates an IndentedTreeImagePainter using the given indentation per depth
     * and {@link ICellPainter} for painting the icons in the tree.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     * @param treeImagePainter
     *            The {@link ICellPainter} that should be used to paint the
     *            images in the tree. When using the
     *            DefaultTreeLayerConfiguration the content painter needs to be
     *            of type of {@link TreeImagePainter} that paints
     *            expand/collapse/leaf icons regarding the node state, because
     *            the ui bindings for expand/collapse are registered against
     *            that type.
     * @since 1.6
     */
    public IndentedTreeImagePainter(int treeIndent, ICellPainter treeImagePainter) {
        this(treeIndent, CellEdgeEnum.LEFT, treeImagePainter);
    }

    /**
     * Creates an IndentedTreeImagePainter using the given indentation per depth
     * and {@link ICellPainter} for painting the icons in the tree to the
     * specified cell edge.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     * @param cellEdge
     *            the edge of the cell on which the tree state indicator
     *            decoration should be applied
     * @param treeImagePainter
     *            The {@link ICellPainter} that should be used to paint the
     *            images in the tree. When using the
     *            DefaultTreeLayerConfiguration the content painter needs to be
     *            of type of {@link TreeImagePainter} that paints
     *            expand/collapse/leaf icons regarding the node state, because
     *            the ui bindings for expand/collapse are registered against
     *            that type.
     * @since 1.6
     */
    public IndentedTreeImagePainter(
            int treeIndent,
            CellEdgeEnum cellEdge,
            ICellPainter treeImagePainter) {

        this.treeIndent = treeIndent;
        this.internalPainter =
                new CellPainterDecorator(null, cellEdge, treeImagePainter);

        setWrappedPainter(this.internalPainter);
    }

    // the following constructors are intended to configure the
    // CellPainterDecorator that is created as
    // the wrapped painter of this IndentedTreeImagePainter

    /**
     * Creates a {@link IndentedTreeImagePainter} that uses the given
     * {@link ICellPainter} as base {@link ICellPainter}. It will use the
     * {@link TreeImagePainter} as decorator for tree state related decorations
     * at the specified cell edge, which can be configured to render the
     * background or not via method parameter. With the additional parameters,
     * the behaviour of the created {@link CellPainterDecorator} can be
     * configured in terms of rendering.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     * @param interiorPainter
     *            the base {@link ICellPainter} to use
     * @param cellEdge
     *            the edge of the cell on which the tree state indicator
     *            decoration should be applied
     * @param paintBg
     *            flag to configure whether the {@link TreeImagePainter} should
     *            paint the background or not
     * @param spacing
     *            the number of pixels that should be used as spacing between
     *            cell edge and decoration
     * @param paintDecorationDependent
     *            flag to configure if the base {@link ICellPainter} should
     *            render decoration dependent or not. If it is set to
     *            <code>false</code>, the base painter will always paint at the
     *            same coordinates, using the whole cell bounds,
     *            <code>true</code> will cause the bounds of the cell to shrink
     *            for the base painter.
     */
    public IndentedTreeImagePainter(
            int treeIndent,
            ICellPainter interiorPainter,
            CellEdgeEnum cellEdge,
            boolean paintBg,
            int spacing,
            boolean paintDecorationDependent) {

        this.treeIndent = treeIndent;

        ICellPainter painter = new TreeImagePainter(paintBg);
        this.internalPainter =
                new CellPainterDecorator(
                        interiorPainter,
                        cellEdge,
                        spacing,
                        painter,
                        paintDecorationDependent,
                        paintBg);
        setWrappedPainter(this.internalPainter);
    }

    /**
     * Creates a {@link IndentedTreeImagePainter} that uses the given
     * {@link ICellPainter} as base {@link ICellPainter}. It will use the given
     * {@link ICellPainter} as decorator for tree state related decorations at
     * the specified cell edge, which can be configured to render the background
     * or not via method parameter. With the additional parameters, the
     * behaviour of the created {@link CellPainterDecorator} can be configured
     * in terms of rendering.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     * @param interiorPainter
     *            the base {@link ICellPainter} to use
     * @param cellEdge
     *            the edge of the cell on which the tree state indicator
     *            decoration should be applied
     * @param decoratorPainter
     *            the {@link ICellPainter} that should be used to paint the tree
     *            state related decoration
     * @param paintBg
     *            flag to configure whether the {@link CellPainterDecorator}
     *            should paint the background or not
     * @param spacing
     *            the number of pixels that should be used as spacing between
     *            cell edge and decoration
     * @param paintDecorationDependent
     *            flag to configure if the base {@link ICellPainter} should
     *            render decoration dependent or not. If it is set to
     *            <code>false</code>, the base painter will always paint at the
     *            same coordinates, using the whole cell bounds,
     *            <code>true</code> will cause the bounds of the cell to shrink
     *            for the base painter.
     */
    public IndentedTreeImagePainter(
            int treeIndent,
            ICellPainter interiorPainter,
            CellEdgeEnum cellEdge,
            ICellPainter decoratorPainter,
            boolean paintBg,
            int spacing,
            boolean paintDecorationDependent) {

        this.treeIndent = treeIndent;

        this.internalPainter =
                new CellPainterDecorator(
                        interiorPainter,
                        cellEdge,
                        spacing,
                        decoratorPainter,
                        paintDecorationDependent,
                        paintBg);
        setWrappedPainter(this.internalPainter);
    }

    /**
     * Creates a {@link IndentedTreeImagePainter} that uses the given
     * {@link ICellPainter} as base {@link ICellPainter} and decorate it with
     * the {@link TreeImagePainter} on the right edge of the cell. This
     * constructor gives the opportunity to configure the behaviour of the
     * {@link TreeImagePainter} and the {@link CellPainterDecorator} for some
     * attributes. Remains because of downwards compatibility.
     *
     * @param treeIndent
     *            The number of pixels to indent per depth.
     * @param interiorPainter
     *            the base {@link ICellPainter} to use
     * @param paintBg
     *            flag to configure whether the {@link TreeImagePainter} should
     *            paint the background or not
     * @param interiorPainterToSpanFullWidth
     *            flag to configure how the bounds of the base painter should be
     *            calculated
     */
    public IndentedTreeImagePainter(
            int treeIndent,
            ICellPainter interiorPainter,
            boolean paintBg,
            boolean interiorPainterToSpanFullWidth) {

        this.treeIndent = treeIndent;

        ICellPainter painter = new TreeImagePainter(paintBg);
        this.internalPainter =
                new CellPainterDecorator(
                        interiorPainter,
                        CellEdgeEnum.RIGHT,
                        0,
                        painter,
                        !interiorPainterToSpanFullWidth,
                        paintBg);
        setWrappedPainter(this.internalPainter);
    }

    /**
     * @return The ICellPainter that is used to paint the images in the tree.
     *         Usually it is some type of TreeImagePainter that paints
     *         expand/collapse/leaf icons regarding the node state.
     */
    public ICellPainter getTreeImagePainter() {
        return this.internalPainter.getDecoratorCellPainter();
    }

    /**
     * @param cellPainter
     *            The {@link ICellPainter} that should be used to paint the
     *            images in the tree. Usually it is some type of
     *            {@link TreeImagePainter} that paints expand/collapse/leaf
     *            icons regarding the node state.
     */
    public void setTreeImagePainter(ICellPainter cellPainter) {
        this.internalPainter.setDecoratorCellPainter(cellPainter);
    }

    /**
     * @param cellPainter
     *            The base {@link ICellPainter} that should be used to render
     *            the cell content.
     */
    public void setBaseCellPainter(ICellPainter cellPainter) {
        this.internalPainter.setBaseCellPainter(cellPainter);
    }

    /**
     *
     * @return The {@link CellPainterDecorator} that is wrapped by this
     *         {@link IndentedTreeImagePainter}. Can be used to perform specific
     *         decoration configurations, e.g. set the decoration dependent
     *         rendering option.
     * @since 1.6
     */
    public CellPainterDecorator getInternalPainter() {
        return this.internalPainter;
    }

    @Override
    public Rectangle getWrappedPainterBounds(
            ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
        int depth = getDepth(cell);
        int indent = getIndent(depth, configRegistry);

        return new Rectangle(
                bounds.x + indent,
                bounds.y,
                bounds.width - indent,
                bounds.height);
    }

    @Override
    public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
        super.paintCell(
                cell,
                gc,
                getWrappedPainterBounds(cell, gc, bounds, configRegistry),
                configRegistry);
    }

    @Override
    public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
        int depth = getDepth(cell);
        int indent = getIndent(depth, configRegistry);
        return indent + super.getPreferredWidth(cell, gc, configRegistry);
    }

    /**
     * @param depth
     *            The depth/level in the tree structure for which the indent is
     *            requested.
     * @param configRegistry
     *            The {@link IConfigRegistry} needed for accessing the dpi
     *            converter.
     * @return The number of pixels the content should be indented.
     * @since 2.0
     */
    protected int getIndent(int depth, IConfigRegistry configRegistry) {
        return GUIHelper.convertHorizontalPixelToDpi(this.treeIndent * depth, configRegistry);
    }

    /**
     * @param cell
     *            The cell for which the depth/level in the tree structure is
     *            requested.
     * @return The depth/level in the tree structure the given cell is located.
     *
     * @since 1.4
     */
    protected int getDepth(ILayerCell cell) {
        int depth = 0;

        for (String configLabel : cell.getConfigLabels()) {
            if (configLabel.startsWith(DefaultTreeLayerConfiguration.TREE_DEPTH_CONFIG_TYPE)) {
                String[] tokens = configLabel.split("_"); //$NON-NLS-1$
                depth = Integer.valueOf(tokens[tokens.length - 1]).intValue();
            }
        }

        return depth;
    }

}

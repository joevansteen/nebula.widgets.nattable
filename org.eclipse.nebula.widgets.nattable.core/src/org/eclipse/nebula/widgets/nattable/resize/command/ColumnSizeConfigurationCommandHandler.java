/*****************************************************************************
 * Copyright (c) 2015, 2020 CEA LIST.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.widgets.nattable.resize.command;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * {@link ILayerCommandHandler} for the {@link ColumnSizeConfigurationCommand}.
 * Determines the column positions to resize based on the given label.
 *
 * @since 1.4
 */
public class ColumnSizeConfigurationCommandHandler implements ILayerCommandHandler<ColumnSizeConfigurationCommand> {

    private final DataLayer dataLayer;

    public ColumnSizeConfigurationCommandHandler(DataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public boolean doCommand(ILayer targetLayer, ColumnSizeConfigurationCommand command) {
        if (command.label == null) {
            if (command.newColumnWidth == null) {
                this.dataLayer.setColumnPercentageSizing(true);
            } else {
                this.dataLayer.setDefaultColumnWidth(command.newColumnWidth);
            }
        } else {
            // find column position
            for (int i = 0; i < this.dataLayer.getColumnCount(); i++) {
                if (this.dataLayer.getConfigLabelsByPosition(i, 0).hasLabel(command.label)) {
                    if (command.newColumnWidth == null) {
                        this.dataLayer.setColumnPercentageSizing(i, true);
                    } else {
                        if (command.percentageSizing) {
                            this.dataLayer.setColumnWidthPercentageByPosition(i, command.newColumnWidth);
                        } else {
                            this.dataLayer.setColumnWidthByPosition(i, command.newColumnWidth);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Class<ColumnSizeConfigurationCommand> getCommandClass() {
        return ColumnSizeConfigurationCommand.class;
    }

}

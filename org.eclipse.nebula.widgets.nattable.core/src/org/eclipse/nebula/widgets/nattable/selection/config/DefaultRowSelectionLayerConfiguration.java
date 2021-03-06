/*******************************************************************************
 * Copyright (c) 2014, 2020 Original authors and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.selection.config;

import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * Default configuration for row only based selections. Used by the
 * {@link SelectionLayer}.
 */
public class DefaultRowSelectionLayerConfiguration extends DefaultSelectionLayerConfiguration {

    @Override
    protected void addSelectionUIBindings() {
        addConfiguration(new RowOnlySelectionBindings());
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void addMoveSelectionConfig() {
        addConfiguration(new RowOnlySelectionConfiguration());
    }
}

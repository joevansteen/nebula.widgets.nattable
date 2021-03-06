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
package org.eclipse.nebula.widgets.nattable.formula.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized {@link AbstractMathFunction} that only allows a single value for
 * processing.
 *
 * @since 1.4
 */
public abstract class AbstractMathSingleValueFunction extends AbstractMathFunction {

    public AbstractMathSingleValueFunction() {
        this(new ArrayList<FunctionValue>());
    }

    public AbstractMathSingleValueFunction(FunctionValue value) {
        this(Arrays.asList(value));
    }

    public AbstractMathSingleValueFunction(List<FunctionValue> values) {
        super(values);

        if (this.values.size() > 1) {
            throw new IllegalArgumentException("Only single values are supported by this function"); //$NON-NLS-1$
        }
    }

    @Override
    public void addFunctionValue(FunctionValue value) {
        if (!this.values.isEmpty()) {
            throw new IllegalArgumentException("Only single values are supported by this function"); //$NON-NLS-1$
        }
        super.addFunctionValue(value);
    }

    /**
     * @return The single value that is contained in this function.
     */
    protected FunctionValue getSingleValue() {
        if (this.values.isEmpty()) {
            throw new IllegalArgumentException("No value specified"); //$NON-NLS-1$
        }
        return this.values.get(0);
    }

}

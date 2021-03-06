/*******************************************************************************
 * Copyright (c) 2013, 2020 Dirk Fauth and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.edit.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

/**
 * This implementation is a proof of concept for special cell editors that wrap
 * dialogs. The {@link FileDialog} is wrapped by this implementation. It will
 * open the default file selection dialog on trying to activate the cell editor.
 */
public class FileDialogCellEditor extends AbstractDialogCellEditor {

    /**
     * The selection result of the {@link FileDialog}. Needed to update the data
     * model after closing the dialog.
     */
    private String selectedFile;
    /**
     * Flag to determine whether the dialog was closed or if it is still open.
     */
    private boolean closed = false;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #open()
     */
    @Override
    public int open() {
        this.selectedFile = getDialogInstance().open();
        if (this.selectedFile == null) {
            this.closed = true;
            return Window.CANCEL;
        } else {
            commit(MoveDirectionEnum.NONE);
            this.closed = true;
            return Window.OK;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #createDialogInstance()
     */
    @Override
    public FileDialog createDialogInstance() {
        this.closed = false;
        return new FileDialog(this.parent.getShell(), SWT.OPEN);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #getDialogInstance()
     */
    @Override
    public FileDialog getDialogInstance() {
        return (FileDialog) this.dialog;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #getEditorValue()
     */
    @Override
    public Object getEditorValue() {
        return this.selectedFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #setEditorValue(java.lang.Object)
     */
    @Override
    public void setEditorValue(Object value) {
        getDialogInstance()
                .setFileName(value != null ? value.toString() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #close()
     */
    @Override
    public void close() {
        // as the FileDialog does not support a programmatically way of closing,
        // this method is forced to do nothing
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #isClosed()
     */
    @Override
    public boolean isClosed() {
        return this.closed;
    }

}

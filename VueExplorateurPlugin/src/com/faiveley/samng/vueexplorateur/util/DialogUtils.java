package com.faiveley.samng.vueexplorateur.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DialogUtils {

	private static MessageBox getMessageBox(Shell parent, int style, String title, String message) {
		MessageBox msgBox = new MessageBox(parent, style);
		msgBox.setText(title);
		msgBox.setMessage(message);
		return msgBox;
	}
	
	public static MessageBox getErrorMessageBox(Shell parent, String title, String message) {
		return getMessageBox(parent, SWT.ICON_ERROR | SWT.OK, title, message);
	}
	
	public static MessageBox getConfirmMessageBox(Shell parent, String title, String message) {
		return getMessageBox(parent, SWT.ICON_QUESTION | SWT.YES | SWT.NO, title, message);
	}
}

package org.dadacoalition.paragraphwrap.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.dadacoalition.paragraphwrap.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.COLUMN_WIDTH, 80);
		store.setDefault(PreferenceConstants.USE_PRINT_MARGIN, true);
	}

}

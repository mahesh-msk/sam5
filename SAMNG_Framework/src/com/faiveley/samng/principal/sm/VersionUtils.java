package com.faiveley.samng.principal.sm;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public final class VersionUtils {

	public static String getFormattedVersion() {
		Platform.getBundle(VersionUtils.class.getName());
		Version version = FrameworkUtil.getBundle(VersionUtils.class).getVersion();
				
		String vSAM = version.toString();
		int lastIndex = vSAM.lastIndexOf(".");
		String deb = vSAM.substring(0, lastIndex);
		String build = vSAM.substring(lastIndex);
		build = build.replace(".", " build");
		vSAM = deb + build;

		return vSAM;
	}
}

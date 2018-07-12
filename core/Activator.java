/**
 * 
 */
package emf_toolcenter.plugin.backend.core;

/**
 * @author DEP7EC
 *
 */
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import emf_toolcenter.plugin.backend.util.User32HandlerCommands;
import emf_toolcenter.plugin.backend.util.User32LibraryLink;

/**
 * The activator class controls the plug-in life cycle
 */
public final class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "EMF_ToolCenter"; //$NON-NLS-1$
	
	private Setup pluginSetup;
	
	/**
	 * The shared instance
	 */
	private static Activator plugin = new Activator();
	
	/**
	 * The constructor for Activator
	 */
	public Activator() {
		this.pluginSetup = new Setup();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		User32LibraryLink.getWindowState(new String []{"EMF-Toolcenter Preferences"}, 
				User32HandlerCommands.CLOSE);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		if(plugin != null){
			return plugin;
		}
		else{
			return plugin = new Activator();
		}
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public Setup accessSetup(){
		return this.pluginSetup;
	}
}
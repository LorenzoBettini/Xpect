package org.eclipse.xpect.ui.util;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xpect.XpectFile;
import org.eclipse.xpect.ui.internal.XpectActivator;
import org.eclipse.xtext.resource.XtextResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.util.JdtClasspathUriResolver;

import com.google.inject.Injector;

public class XpectUtil {
	public static XpectFile load(IFile file) {
		Injector injector = XpectActivator.getInstance().getInjector(XpectActivator.ORG_ECLIPSE_XPECT_XPECT);
		XtextResourceSet rs = new XtextResourceSet();
		IJavaProject javaProject = JavaCore.create(file.getProject());
		if (javaProject != null && javaProject.exists()) {
			rs.setClasspathURIContext(javaProject);
			rs.setClasspathUriResolver(new JdtClasspathUriResolver());
		}
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		Resource resource = injector.getInstance(XtextResourceFactory.class).createResource(uri);
		rs.getResources().add(resource);
		try {
			resource.load(Collections.emptyMap());
			for (EObject obj : resource.getContents())
				if (obj instanceof XpectFile)
					return (XpectFile) obj;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}

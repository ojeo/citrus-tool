package com.alibaba.eclipse.plugin.webx.extension;

import static com.alibaba.eclipse.plugin.webx.util.SpringExtPluginUtil.getProjectFromURL;
import static com.alibaba.eclipse.plugin.webx.util.SpringExtPluginUtil.getSchemaNameFromURL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IProject;
import org.osgi.service.url.AbstractURLStreamHandlerService;

import com.alibaba.citrus.springext.Schema;
import com.alibaba.eclipse.plugin.webx.util.SpringExtSchemaResourceSet;

public class SpringExtURLStreamHandler extends AbstractURLStreamHandlerService {
    @Override
    public URLConnection openConnection(URL url) throws IOException {
        return new URLConnection(url) {
            @Override
            public void connect() throws IOException {
            }

            @Override
            public InputStream getInputStream() throws IOException {
                IProject project = getProjectFromURL(url);

                if (project != null) {
                    SpringExtSchemaResourceSet schemas = SpringExtSchemaResourceSet.getInstance(project);

                    if (schemas != null) {
                        String schemaName = getSchemaNameFromURL(url);
                        Schema schema = schemas.getNamedMappings().get(schemaName);

                        if (schema != null) {
                            return schema.getInputStream();
                        }
                    }
                }

                throw new IOException("Unable to open a stream for URL: " + url);
            }
        };
    }
}
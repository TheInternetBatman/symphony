/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.TextXmlRenderer;
import org.b3log.symphony.model.sitemap.Sitemap;
import org.b3log.symphony.service.SitemapQueryService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sitemap processor.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 24, 2016
 * @since 1.6.0
 */
@RequestProcessor
public class SitemapProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SitemapProcessor.class);

    /**
     * Sitemap query service.
     */
    @Inject
    private SitemapQueryService sitemapQueryService;

    /**
     * Returns the sitemap.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = "/sitemap.xml", method = HttpMethod.GET)
    public void sitemap(final RequestContext context) {
        final TextXmlRenderer renderer = new TextXmlRenderer();

        context.setRenderer(renderer);

        final Sitemap sitemap = new Sitemap();

        try {
            LOGGER.log(Level.INFO, "Generating sitemap....");

            sitemapQueryService.genIndex(sitemap);
            sitemapQueryService.genDomains(sitemap);
            sitemapQueryService.genArticles(sitemap);

            final String content = sitemap.toString();

            LOGGER.log(Level.INFO, "Generated sitemap");

            renderer.setContent(content);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get blog article feed error", e);

            try {
                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

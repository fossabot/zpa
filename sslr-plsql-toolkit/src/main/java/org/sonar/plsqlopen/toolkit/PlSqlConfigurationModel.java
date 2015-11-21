/*
 * Sonar PL/SQL Plugin (Community)
 * Copyright (C) 2015 Felipe Zorzo
 * felipe.b.zorzo@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plsqlopen.toolkit;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.plsqlopen.parser.PlSqlParser;
import org.sonar.plsqlopen.squid.PlSqlConfiguration;
import org.sonar.plugins.plsqlopen.api.PlSqlKeyword;
import org.sonar.sslr.toolkit.AbstractConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;
import org.sonar.sslr.toolkit.Validators;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

public class PlSqlConfigurationModel extends AbstractConfigurationModel {

    private static final Logger LOG = LoggerFactory.getLogger(PlSqlConfigurationModel.class);

    private static final String CHARSET_PROPERTY_KEY = "sonar.sourceEncoding";

    @VisibleForTesting
    ConfigurationProperty charsetProperty = new ConfigurationProperty("Charset",
            CHARSET_PROPERTY_KEY,
            getPropertyOrDefaultValue(CHARSET_PROPERTY_KEY, "UTF-8"),
            Validators.charsetValidator());

    @Override
    public Charset getCharset() {
        return Charset.forName(charsetProperty.getValue());
    }

    @Override
    public List<ConfigurationProperty> getProperties() {
        return ImmutableList.of(charsetProperty);
    }

    @Override
    public Parser<Grammar> doGetParser() {
        return PlSqlParser.create(getConfiguration());
    }

    @Override
    public List<Tokenizer> doGetTokenizers() {
        return ImmutableList.of((Tokenizer) new KeywordsTokenizer("<span class=\"k\">", "</span>", PlSqlKeyword.keywordValues()));
    }

    @VisibleForTesting
    PlSqlConfiguration getConfiguration() {
        return new PlSqlConfiguration(Charset.forName(charsetProperty.getValue()));
    }

    @VisibleForTesting
    static String getPropertyOrDefaultValue(String propertyKey, String defaultValue) {
        String propertyValue = System.getProperty(propertyKey);

        if (propertyValue == null) {
            LOG.info("The property \"" + propertyKey + "\" is not set, using the default value \"" + defaultValue + "\".");
            return defaultValue;
        }
        LOG.info("The property \"" + propertyKey + "\" is set, using its value \"" + propertyValue + "\".");
        return propertyValue;
    }

}
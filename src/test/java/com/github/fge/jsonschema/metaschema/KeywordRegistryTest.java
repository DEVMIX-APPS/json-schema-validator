/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.metaschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.main.Keyword;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.old.syntax.SyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxValidator;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordRegistryTest
{
    private static final String KEYWORD = "k";
    private static final SyntaxChecker CHECKER
        = new SyntaxChecker()
    {
        @Override
        public void checkSyntax(SyntaxValidator validator, final List<Message> messages,
            final JsonNode schema)
        {
        }
    };

    private KeywordRegistry keywordRegistry;

    @BeforeMethod
    public void init()
    {
        keywordRegistry = new KeywordRegistry();
    }

    @Test
    public void canRegisterSyntaxChecker()
    {
        final Map<String, SyntaxChecker> checkers
            = new ImmutableMap.Builder<String, SyntaxChecker>()
                .put(KEYWORD, CHECKER).build();

        keywordRegistry.addSyntaxCheckers(checkers);

        final Map<String, SyntaxChecker> map
            = keywordRegistry.getSyntaxCheckers();

        assertEquals(map.size(), 1);
        assertSame(map.get(KEYWORD), CHECKER);
    }

    @Test
    public void canRegisterKeywordValidator()
    {
        final Map<String, Class<? extends KeywordValidator>> validators
            = ImmutableMap.<String, Class<? extends KeywordValidator>>builder()
                .put(KEYWORD, KeywordValidator.class).build();

        keywordRegistry.addValidators(validators);

        final Map<String, Class<? extends KeywordValidator>> map
            = keywordRegistry.getValidators();

        assertEquals(map.size(), 1);
        assertSame(map.get(KEYWORD), KeywordValidator.class);
    }

    @Test
    public void canRegisterKeywordWithSyntaxCheckerOnly()
    {
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withSyntaxChecker(CHECKER).build();
        keywordRegistry.addKeyword(keyword);

        final Map<String, SyntaxChecker> checkers
            = keywordRegistry.getSyntaxCheckers();
        final Map<String, Class<? extends KeywordValidator>> validators
            = keywordRegistry.getValidators();

        assertEquals(checkers.size(), 1);
        assertSame(checkers.get(KEYWORD), CHECKER);

        assertTrue(validators.isEmpty());
    }

    @Test
    public void canRegisterKeywordWithKeywordValidatorOnly()
    {
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withValidatorClass(KeywordValidator.class).build();
        keywordRegistry.addKeyword(keyword);

        final Map<String, SyntaxChecker> checkers
            = keywordRegistry.getSyntaxCheckers();
        final Map<String, Class<? extends KeywordValidator>> validators
            = keywordRegistry.getValidators();

        assertTrue(checkers.isEmpty());

        assertEquals(validators.size(), 1);
        assertSame(validators.get(KEYWORD), KeywordValidator.class);
    }

    @Test
    public void canRegisterFullKeyword()
    {
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withSyntaxChecker(CHECKER)
            .withValidatorClass(KeywordValidator.class).build();
        keywordRegistry.addKeyword(keyword);

        final Map<String, SyntaxChecker> checkers
            = keywordRegistry.getSyntaxCheckers();
        final Map<String, Class<? extends KeywordValidator>> validators
            = keywordRegistry.getValidators();

        assertEquals(checkers.size(), 1);
        assertSame(checkers.get(KEYWORD), CHECKER);

        assertEquals(validators.size(), 1);
        assertSame(validators.get(KEYWORD), KeywordValidator.class);
    }

    @Test(dependsOnMethods = "canRegisterFullKeyword")
    public void canUnregisterKeyword()
    {
        final Keyword keyword = Keyword.withName(KEYWORD)
            .withSyntaxChecker(CHECKER)
            .withValidatorClass(KeywordValidator.class).build();
        keywordRegistry.addKeyword(keyword);

        final String name = keyword.getName();
        keywordRegistry.removeKeyword(name);

        final Map<String, SyntaxChecker> checkers
            = keywordRegistry.getSyntaxCheckers();
        final Map<String, Class<? extends KeywordValidator>> validators
            = keywordRegistry.getValidators();

        assertTrue(checkers.isEmpty());
        assertTrue(validators.isEmpty());
    }

    @Test
    public void canRegisterAndUnregisterFormatAttribute()
    {
        final String name = "dummy";
        final FormatAttribute attribute = mock(FormatAttribute.class);
        keywordRegistry.addFormatAttribute(name, attribute);

        final Map<String, FormatAttribute> attributes
            = keywordRegistry.getFormatAttributes();

        assertEquals(attributes.size(), 1);
        assertSame(attributes.get(name), attribute);

        keywordRegistry.removeFormatAttribute(name);

        assertEquals(keywordRegistry.getFormatAttributes().size(), 0);
    }
}
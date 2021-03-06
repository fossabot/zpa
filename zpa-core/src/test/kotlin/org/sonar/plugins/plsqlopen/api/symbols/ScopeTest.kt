/**
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2019 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.plsqlopen.api.symbols

import com.sonar.sslr.api.AstNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.sonar.plugins.plsqlopen.api.symbols.Symbol.Kind

class ScopeTest {

    @Test
    fun testScope() {
        val node = mock(AstNode::class.java)
        val scope = Scope(null, node, false, hasExceptionHandler = false)
        assertThat(scope.outer()).isNull()
        assertThat(scope.tree()).isEqualTo(node)
        assertThat(scope.isAutonomousTransaction).isFalse()
    }

    @Test
    fun getSymbolsInScope() {
        val scope = Scope(null, null, false, hasExceptionHandler = false)

        val symbol1 = createSymbol(scope, "foo", Kind.VARIABLE)
        scope.addSymbol(symbol1)

        val symbol2 = createSymbol(scope, "bar", Kind.VARIABLE)
        scope.addSymbol(symbol2)

        assertThat(scope.symbols).containsExactly(symbol1, symbol2)
    }

    @Test
    fun getSymbolsByKind() {
        val scope = Scope(null, null, isAutonomousTransaction = false, hasExceptionHandler = false)

        val symbol1 = createSymbol(scope, "foo", Kind.VARIABLE)
        scope.addSymbol(symbol1)

        val symbol2 = createSymbol(scope, "bar", Kind.CURSOR)
        scope.addSymbol(symbol2)

        assertThat(scope.getSymbols(Kind.VARIABLE)).containsExactly(symbol1)
        assertThat(scope.getSymbols(Kind.CURSOR)).containsExactly(symbol2)
    }

    @Test
    fun getSymbolsAcessibleInScope() {
        val scope = Scope(null, null, false, hasExceptionHandler = false)

        val symbol1 = createSymbol(scope, "foo", Kind.VARIABLE)
        scope.addSymbol(symbol1)

        val symbol2 = createSymbol(scope, "bar", Kind.VARIABLE)
        scope.addSymbol(symbol2)

        assertThat(scope.getSymbolsAcessibleInScope("foo")).containsExactly(symbol1)
        assertThat(scope.getSymbolsAcessibleInScope("foo", Kind.VARIABLE)).containsExactly(symbol1)
        assertThat(scope.getSymbolsAcessibleInScope("foo", Kind.CURSOR)).isEmpty()
    }

    @Test
    fun getSymbolsAcessibleInScopeConsideringOuterScope() {
        val outerScope = Scope(null, null, isAutonomousTransaction = false, hasExceptionHandler = false)
        val symbol1 = createSymbol(outerScope, "foo", Kind.VARIABLE)
        outerScope.addSymbol(symbol1)

        val innerScope = Scope(outerScope, null, isAutonomousTransaction = false, hasExceptionHandler = false)
        val symbol2 = createSymbol(innerScope, "bar", Kind.VARIABLE)
        innerScope.addSymbol(symbol2)

        assertThat(innerScope.getSymbolsAcessibleInScope("foo")).containsExactly(symbol1)
        assertThat(innerScope.getSymbolsAcessibleInScope("foo", Kind.VARIABLE)).containsExactly(symbol1)
        assertThat(innerScope.getSymbolsAcessibleInScope("foo", Kind.CURSOR)).isEmpty()
    }

    @Test
    fun getSymbol() {
        val scope = Scope(null, null, isAutonomousTransaction = false, hasExceptionHandler = false)

        val symbol1 = createSymbol(scope, "foo", Kind.VARIABLE)
        scope.addSymbol(symbol1)

        val symbol2 = createSymbol(scope, "bar", Kind.VARIABLE)
        scope.addSymbol(symbol2)

        assertThat(scope.getSymbol("foo")).isEqualTo(symbol1)
        assertThat(scope.getSymbol("foo", Kind.VARIABLE)).isEqualTo(symbol1)
        assertThat(scope.getSymbol("foo", Kind.CURSOR)).isNull()
        assertThat(scope.getSymbol("baz")).isNull()
    }

    @Test
    fun getSymbolConsideringOuterScope() {
        val outerScope = Scope(null, null, isAutonomousTransaction = false, hasExceptionHandler = false)
        val symbol1 = createSymbol(outerScope, "foo", Kind.VARIABLE)
        outerScope.addSymbol(symbol1)

        val innerScope = Scope(outerScope, null, isAutonomousTransaction = false, hasExceptionHandler = false)
        val symbol2 = createSymbol(innerScope, "bar", Kind.VARIABLE)
        innerScope.addSymbol(symbol2)

        assertThat(innerScope.getSymbol("foo")).isEqualTo(symbol1)
        assertThat(innerScope.getSymbol("foo", Kind.VARIABLE)).isEqualTo(symbol1)
        assertThat(innerScope.getSymbol("foo", Kind.CURSOR)).isNull()
        assertThat(innerScope.getSymbol("baz")).isNull()
    }

    private fun createSymbol(scope: Scope, name: String, kind: Kind): Symbol {
        val node = mock(AstNode::class.java)
        `when`(node.tokenOriginalValue).thenReturn(name)
        return Symbol(node, kind, scope, null)
    }

}

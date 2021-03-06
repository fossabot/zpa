/*
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
package org.sonar.plsqlopen.highlight;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.plsqlopen.TokenLocation;
import org.sonar.plugins.plsqlopen.api.PlSqlKeyword;
import org.sonar.plugins.plsqlopen.api.PlSqlTokenType;
import org.sonar.plugins.plsqlopen.api.checks.PlSqlCheck;

public class PlSqlHighlighterVisitor extends PlSqlCheck {

    private NewHighlighting highlighting;

    public PlSqlHighlighterVisitor(SensorContext context, InputFile inputFile) {
        highlighting = context.newHighlighting().onFile(inputFile);
    }
    
    @Override
    public void leaveFile(AstNode astNode) {
        if (highlighting != null) {
            highlighting.save();
        }
    }
    
    @Override
    public void visitToken(Token token) {
        if (token.getType() instanceof PlSqlTokenType) {
            highlight(token, TypeOfText.STRING);
        }
        if (token.getType() instanceof PlSqlKeyword) {
            highlight(token, TypeOfText.KEYWORD);
        }
    }

    @Override
    public void visitComment(Trivia trivia, String content) {
        Token token = trivia.getToken();
        if (token.getValue().startsWith("/**")) {
            highlight(token, TypeOfText.STRUCTURED_COMMENT);
        } else {
            highlight(token, TypeOfText.COMMENT);
        }
    }

    private void highlight(Token token, TypeOfText code) {
        TokenLocation location = TokenLocation.from(token);
        if (highlighting != null) {
            highlighting.highlight(location.line(), location.column(), location.endLine(), location.endColumn(), code);
        }
    }

}

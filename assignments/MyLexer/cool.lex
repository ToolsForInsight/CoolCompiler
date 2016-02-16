/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    // initial
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup
%notunix

%%

[0-9]+                          { System.out.println("Integer: " + yytext()); }          

[a-z][a-zA-Z]+                 { String token = yytext();
                                   if (token.toLowerCase().equals("true") ||
                                       token.toLowerCase().equals("false")) {
      
                                       System.out.println("Keyword: " + token.toLowerCase());
                                   }
                                   else {
    
                                       System.out.println("Identifier: " + token);
                                   }
                                 }
[a-zA-Z_0-9]+[a-zA-Z_]*         { String token = yytext();

                                    if (token.toLowerCase().equals("class") ||
                                        token.toLowerCase().equals("esle") ||
                                        token.toLowerCase().equals("fi") ||
                                        token.toLowerCase().equals("if") ||
                                        token.toLowerCase().equals("in") ||
                                        token.toLowerCase().equals("inherits") ||
                                        token.toLowerCase().equals("isvoid") ||
                                        token.toLowerCase().equals("let") ||
                                        token.toLowerCase().equals("loop") ||
                                        token.toLowerCase().equals("pool") ||
                                        token.toLowerCase().equals("then") ||
                                        token.toLowerCase().equals("while") ||
                                        token.toLowerCase().equals("case") ||
                                        token.toLowerCase().equals("esac") ||
                                        token.toLowerCase().equals("new") ||
                                        token.toLowerCase().equals("of") ||
                                        token.toLowerCase().equals("not")) {

				        System.out.println("Keyword: " + token.toLowerCase());
		                    }
                                    else {
                                        
				        System.out.println("Identifier: " + token); 
                                    }
                                  }

[+-*/~<=(){}]                   { System.out.println("Special: " + yytext()); }
[<=]                            { System.out.println("Special: " + yytext()); }

\"[.]*\"                        { System.out.println("String: " + yytext()); }

--[.]\n                         { System.out.println("Comment: " + yytext()); }
\*[.]\*                         { System.out.println("Comment: " + yytext()); }

[" "\n\f\r\t\v]                { String token = yytext();
                                 String classifier = "Whitespace: ";
                                 String type;

                                 if (token.equals(" ")) {
                                 
				     type = " ";
				 }
				 else {
                                    
				     char escape = token.charAt(0);

                                     switch (escape) {
				         case '\n': type = "\\n";break;
				         case '\f': type = "\\f";break;
				         case '\r': type = "\\r";break;
				         case '\t': type = "\\t";break;
                                         default: type = "\\v";
                                     }
				 }
                                 System.out.println(classifier + type);
                               }

.                               { /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }

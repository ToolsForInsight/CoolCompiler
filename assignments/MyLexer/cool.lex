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

%%

<YYINITIAL>"=>"			{ /* Sample lexical rule for "=>" arrow.
                                     Further lexical rules should be defined
                                     here, after the last %% separator */
                                  return new Symbol(TokenConstants.DARROW); }

// Integers
// integers are non-empty strings of digits 0-9

// Identifiers
// identifiers are strings (other than keywords) consisting of letters, digits, and the
// underscore character
    // type identifiers begin with a capital letter
    // object identifiers begin with a lowercase letter
    // self is a special identifier but not a keyword
    // SELF_TYPE is a special identifier but not a keyword

// Special Syntactic Symbols
// special syntactic sybmbols include: +, -, *, /, ~, <, <=, =, (, ), {, }
// [note to self: <-]

// Strings
// strings are enclosed in double quotes "..."
    // within a string, a sequence "\c" denotes the character "c". exceptions:
        // \b backspace
        // \t tab
        // \n newline
        // \f formfeed
    // a non-escaped newline character may not appear in a string (needs backslash)
    // a string may not contain EOF
    // a string may not contain the null (character \0)
    // any other character may be included in a string
    // strings cannot cross file boundaries

// Comments
// comments are either 1) any characters between two dashes "--" and the next newline
// (or EOF, if there is no next newline) or 2) any characters between two asterisks
// (*...*).  
    // The latter form of comment may be nested.
    // Comments cannot cross file boundaries.

// Keywords
// Cool keywords are: class, else, false, fi, if, in, inherits, isvoid, let, loop, pool,
// then, while, case, esac, new, of, not, true.
    // all keywords except for "true" and "false" are case INsensitive.
    // the first letter of "true" and "false" must be lowercase.

// White space
// white space consists of any sequence of the characters: blank (ascii 32), \n (newline,
// ascii 10), \f (form feed, ascii 12), \r (carriage return, ascii 13), \t (tab, ascii 9),
// \v (vertical tab, ascii 11).

.                               { /* This rule should be the very last
                                     in your lexical specification and
                                     will match match everything not
                                     matched by other lexical rules. */
                                  System.err.println("LEXER BUG - UNMATCHED: " + yytext()); }

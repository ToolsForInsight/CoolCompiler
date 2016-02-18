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
    StringBuffer string_buf;
    char strBufChar;
    int lookBackIter = 1;
    int numEscs;
    boolean containsNullChar = false;
    boolean eofInString = true;

    // For dealing with comments
    StringBuffer comment_buf;
    int numNestComnts = 0;
    int currCmntType;
    static int NESTED = 1;
    static int REGULAR = 0; 

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void lookBackStrBuf() {
      
      if (string_buf.length()-lookBackIter < 0) {
	return;
      } 
      strBufChar = string_buf.charAt(string_buf.length()-lookBackIter);

      switch (strBufChar) {
	case '\\': 
          numEscs++;
          lookBackIter++;
	  lookBackStrBuf();
	  break;
	default: 
	  break;
	}
    }

    void resetCharCounters() {

      lookBackIter = 1;
      numEscs = 0;
    }

    String buildString(StringBuffer stringBuffer) {
  
      if (stringBuffer.length() == 0) {
	return "";
      }

      int charPos = 0;
      char currChar;
      char nextChar;
      char specialChar;
      
      while (charPos < stringBuffer.length()) {
	//System.out.println("beginning charPos: " + charPos);
	//System.out.println("beginning string: " + stringBuffer.toString());
	currChar = stringBuffer.charAt(charPos);
	
        if (currChar == '\0') {
          containsNullChar = true;
	  return "";
	}
	else if (currChar == '\\') {

	  nextChar = stringBuffer.charAt(charPos+1);
	  
	  if (nextChar == '\0') {
	    containsNullChar = true;
	    return "";
	  }
	  else if (nextChar == 'b' || nextChar == 'f' ||
	           nextChar == 'n' || nextChar == 't') {

	    switch (nextChar) {
	      case 'b': specialChar = '\b'; break;
	      case 'n': specialChar = '\n'; break;
	      case 'f': specialChar = '\f'; break;
	      default: specialChar = '\t'; break;
	    }
          
	    stringBuffer.setCharAt(charPos, specialChar);
	    stringBuffer.deleteCharAt(charPos+1);
	  }
	  else {
	    stringBuffer.deleteCharAt(charPos);
	  }
	}
	charPos++;
	//System.out.println("Ending string: " + string);
      }
      return stringBuffer.toString();
    }

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

%state STRING, COMMENT

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
      case YYINITIAL:
        break;
      case STRING:
	yybegin(YYINITIAL);
        return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("String contains EOF"));
      case COMMENT:
	if (currCmntType == NESTED) {
          yybegin(YYINITIAL);
          return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("Comment contains EOF"));
	}
	break;
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup
%notunix
%char
%line

%%

<YYINITIAL>[0-9]+               { return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }          

<YYINITIAL>[a-zA-Z][a-zA-Z_0-9]* { String token = yytext();
                                  
				  if (token.equalsIgnoreCase("CLASS")) {
				    return new Symbol(TokenConstants.CLASS); 
                                  }
			          else if (token.equalsIgnoreCase("ELSE")) {
				    return new Symbol(TokenConstants.ELSE); 
				  }
                                  else if (token.equalsIgnoreCase("FI")) {
				    return new Symbol(TokenConstants.FI); 
				  }
                                  else if (token.equalsIgnoreCase("IF")) {
				    return new Symbol(TokenConstants.IF); 
				  }
                                  else if (token.equalsIgnoreCase("IN")) {
				    return new Symbol(TokenConstants.IN); 
			          }
                                  else if (token.equalsIgnoreCase("INHERITS")) {
				    return new Symbol(TokenConstants.INHERITS); 
				  }
                                  else if (token.equalsIgnoreCase("ISVOID")) {
				    return new Symbol(TokenConstants.ISVOID); 
				  }
                                  else if (token.equalsIgnoreCase("LET")) {
				    return new Symbol(TokenConstants.LET); 
				  }
				  else if (token.equalsIgnoreCase("LOOP")) {
				    return new Symbol(TokenConstants.LOOP); 
				  }
                                  else if (token.equalsIgnoreCase("POOL")) {
				    return new Symbol(TokenConstants.POOL); 
				  }
                                  else if (token.equalsIgnoreCase("THEN")) {
				    return new Symbol(TokenConstants.THEN); 
				  }
				  else if (token.equalsIgnoreCase("WHILE")) {
				    return new Symbol(TokenConstants.WHILE); 
				  }
                                  else if (token.equalsIgnoreCase("CASE")) {
				    return new Symbol(TokenConstants.CASE); 
				  }
                                  else if (token.equalsIgnoreCase("ESAC")) {
				    return new Symbol(TokenConstants.ESAC); 
				  }
                                  else if (token.equalsIgnoreCase("NEW")) {
				    return new Symbol(TokenConstants.NEW); 
				  }
				  else if (token.equalsIgnoreCase("OF")) {
				    return new Symbol(TokenConstants.OF); 
				  }
				  else if (token.equalsIgnoreCase("NOT")) {
				    return new Symbol(TokenConstants.NOT); 
				  } 			
		                  else if (java.lang.Character.isLowerCase(token.charAt(0))) {
                                    if (token.toLowerCase().equals("true") ||
                                        token.toLowerCase().equals("false")) {
                                      return new Symbol(TokenConstants.BOOL_CONST, AbstractTable.stringtable.addString(token.toLowerCase()));
                                    }
                                    else {
				      return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(token));
                                    }
				  }
				  else {
				    return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(token));
				  }
                                }

<YYINITIAL>[-*/<=(){},~.@;:+]   { String token = yytext();

                                 if (token.equals("+")) {
				   return new Symbol(TokenConstants.PLUS); 
                                 }
			         else if (token.equals("-")) {
		         	    return new Symbol(TokenConstants.MINUS); 
				  }
                                  else if (token.equals("*")) {
				    return new Symbol(TokenConstants.MULT); 
				  }
                                  else if (token.equals("/")) {
				    return new Symbol(TokenConstants.DIV); 
				  }
                                  else if (token.equals("<")) {
				    return new Symbol(TokenConstants.LT); 
			          }
                                  else if (token.equals("=")) {
				    return new Symbol(TokenConstants.EQ); 
				  }
                                  else if (token.equals("(")) {
				    return new Symbol(TokenConstants.LPAREN); 
				  }
                                  else if (token.equals(")")) {
				    return new Symbol(TokenConstants.RPAREN); 
				  }
				  else if (token.equals("{")) {
				    return new Symbol(TokenConstants.LBRACE); 
				  }
                                  else if (token.equals("}")) {
				    return new Symbol(TokenConstants.RBRACE); 
				  }
                                  else if (token.equals(".")) {
				    return new Symbol(TokenConstants.DOT); 
				  }
				  else if (token.equals("~")) {
				    return new Symbol(TokenConstants.NEG); 
				  }		
		                  else if (token.equals(";")) {
				    return new Symbol(TokenConstants.SEMI); 
				  }
		                  else if (token.equals("@")) {
				    return new Symbol(TokenConstants.AT); 
				  }
		                  else if (token.equals(",")) {
				    return new Symbol(TokenConstants.COMMA); 
				  }
		                  else {
				    return new Symbol(TokenConstants.COLON); 
				  }
                                }

<YYINITIAL>=>                   { return new Symbol(TokenConstants.DARROW); }

<YYINITIAL>(<-)                 { return new Symbol(TokenConstants.ASSIGN); }

<YYINITIAL>(<=)                 { return new Symbol(TokenConstants.LE); }

<YYINITIAL>\"                   { //System.out.println("entered string literal");
                                  string_buf = new StringBuffer();
                                  yybegin(STRING); }

<STRING>([^\n\0\"]|\0)          { //System.out.println("appending char: " + yytext());
                                  string_buf.append(yytext()); //needs EOF
                                }

<STRING>\n|\"                  { //System.out.println("encountered newline or quote; looking back");
                                  
                                  char token = yytext().charAt(0);
                                  boolean empty = string_buf.length() == 0;

                                  if (!empty) {
                                  
                                    resetCharCounters();
                                    lookBackStrBuf();
				  }

                                  if (numEscs % 2 == 1) {
				    //System.out.println("Escapes is " + numEscs + " so appending char");

				      if (token == '\n') {
					curr_lineno++;
				      }
                                      string_buf.append(token);
				  }
				  else {
				    yybegin(YYINITIAL);

				    if (token == '\n') {
                                      curr_lineno++;
                                      //System.out.println("Escapes is " + numEscs + " so newline not escaped");
                                      return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("Unterminated string constant"));
				    }
				    else  if (token == '"') {
                                      //System.out.println("Reached end; building string literal");
                                      containsNullChar = false;
                                      String stringConstant = buildString(string_buf);
				      if (containsNullChar) {
					return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("String constant contains null character"));
				      }
				      else if (stringConstant.length() > 1024) {
					return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("String constant too long"));
				      }
                                      return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(stringConstant));
				    }
				  }
                                }                   

<YYINITIAL>--                   { comment_buf = new StringBuffer();
                                  currCmntType = REGULAR;
                                  yybegin(COMMENT);
                                }

<YYINITIAL,COMMENT>\(\*         { if (yy_lexical_state == COMMENT &&
                                      currCmntType == REGULAR) {

                                    comment_buf.append(yytext());
                                  }
                                  else {
                                    comment_buf = new StringBuffer();
                                    numNestComnts++;
                                    currCmntType = NESTED;
                                    yybegin(COMMENT); 
                                  }
                                }

<COMMENT>\*\)                   { if (currCmntType == NESTED) {

                                    numNestComnts--;
                                    
                                    if (numNestComnts == 0) {
				      yybegin(YYINITIAL);
                                    }
                                  }
                                  else {
				    comment_buf.append(yytext());
                                  }
                                }

<COMMENT>(.|\n|\r)              { char token = yytext().charAt(0);
                                  comment_buf.append(token);

                                  if (token == '\n' | token == '\r') {
				    curr_lineno++;
				    if (currCmntType == REGULAR) {
				      yybegin(YYINITIAL);
				    }
				  }
                                }

<YYINITIAL>\*\)                 { return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("Unmatched *)")); }

<YYINITIAL>[" "\n\f\r\t\x0b]    { String token = yytext();
                                  String whitespaceType;

                                  if (token.equals(" ")) {
                                 
				      whitespaceType = " ";
				  }
				  else {
                                    
				      char escape = token.charAt(0);

                                      switch (escape) {
				          case '\n': 
					    whitespaceType = "\\n"; 
					    curr_lineno++;
					    break;
				          case '\f': whitespaceType = "\\f"; break;
				          case '\r': whitespaceType = "\\r"; break;
				          case '\t': whitespaceType = "\\t"; break;
                                          default: whitespaceType = "\\v";
                                      }
				  }
                               }

<YYINITIAL>.                   { return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString(yytext())); }

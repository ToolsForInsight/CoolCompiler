/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 1;
	private final int YYINITIAL = 0;
	private final int COMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		18,
		21
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NOT_ACCEPT,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NOT_ACCEPT,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"11:9,10,12,10:3,11:18,10,11,9,11:5,13,15,14,4:2,8,4:2,1:10,4:2,7,5,6,11,4,2" +
":26,11:4,3,11,2:26,4,11,4:2,11,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,27,
"0,1,2,3,1:14,4,5,6,7,8,9,10,6,11")[0];

	private int yy_nxt[][] = unpackFromString(12,16,
"1,2,3,4,5,19,4,22,24,6,7,4,7,25,26,5,-1:17,2,-1:15,3:3,-1:12,1,14:8,15,14:2" +
",15,14:3,-1:6,8,-1:23,12,-1,1,16:12,20,23,16,-1:5,9,-1:2,10,-1:22,17,-1:8,1" +
"1,-1:22,13");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }
					case -3:
						break;
					case 3:
						{ String token = yytext();
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
					case -4:
						break;
					case 4:
						{ return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString(yytext())); }
					case -5:
						break;
					case 5:
						{ String token = yytext();
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
					case -6:
						break;
					case 6:
						{ //System.out.println("entered string literal");
                                  string_buf = new StringBuffer();
                                  yybegin(STRING); }
					case -7:
						break;
					case 7:
						{ String token = yytext();
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
					case -8:
						break;
					case 8:
						{ return new Symbol(TokenConstants.DARROW); }
					case -9:
						break;
					case 9:
						{ return new Symbol(TokenConstants.LE); }
					case -10:
						break;
					case 10:
						{ return new Symbol(TokenConstants.ASSIGN); }
					case -11:
						break;
					case 11:
						{ comment_buf = new StringBuffer();
                                  currCmntType = REGULAR;
                                  yybegin(COMMENT);
                                }
					case -12:
						break;
					case 12:
						{ if (yy_lexical_state == COMMENT &&
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
					case -13:
						break;
					case 13:
						{ return new Symbol(TokenConstants.ERROR, AbstractTable.stringtable.addString("Unmatched *)")); }
					case -14:
						break;
					case 14:
						{ //System.out.println("appending char: " + yytext());
                                  string_buf.append(yytext()); //needs EOF
                                }
					case -15:
						break;
					case 15:
						{ //System.out.println("encountered newline or quote; looking back");
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
					case -16:
						break;
					case 16:
						{ char token = yytext().charAt(0);
                                  comment_buf.append(token);
                                  if (token == '\n' | token == '\r') {
				    curr_lineno++;
				    if (currCmntType == REGULAR) {
				      yybegin(YYINITIAL);
				    }
				  }
                                }
					case -17:
						break;
					case 17:
						{ if (currCmntType == NESTED) {
                                    numNestComnts--;
                                    if (numNestComnts == 0) {
				      yybegin(YYINITIAL);
                                    }
                                  }
                                  else {
				    comment_buf.append(yytext());
                                  }
                                }
					case -18:
						break;
					case 19:
						{ String token = yytext();
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
					case -19:
						break;
					case 20:
						{ char token = yytext().charAt(0);
                                  comment_buf.append(token);
                                  if (token == '\n' | token == '\r') {
				    curr_lineno++;
				    if (currCmntType == REGULAR) {
				      yybegin(YYINITIAL);
				    }
				  }
                                }
					case -20:
						break;
					case 22:
						{ String token = yytext();
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
					case -21:
						break;
					case 23:
						{ char token = yytext().charAt(0);
                                  comment_buf.append(token);
                                  if (token == '\n' | token == '\r') {
				    curr_lineno++;
				    if (currCmntType == REGULAR) {
				      yybegin(YYINITIAL);
				    }
				  }
                                }
					case -22:
						break;
					case 24:
						{ String token = yytext();
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
					case -23:
						break;
					case 25:
						{ String token = yytext();
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
					case -24:
						break;
					case 26:
						{ String token = yytext();
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
					case -25:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}

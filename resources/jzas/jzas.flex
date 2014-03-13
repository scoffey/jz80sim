package ar.edu.itba.it.obc.jzas.lexer;
import static ar.edu.itba.it.obc.jzas.lexer.JZasTokenTypes.*;

%%
%ignorecase
%unicode
%public
%class JZasLexer
%byaccj
%extends LexerAdapter
%type JZasTokenTypes
%line
%column
%char
%state BLOCK_COMMENT
%state MACRO_HEADER
%state MACRO_BODY

%{
	private boolean eof = false;
	public JZasLexer(){
	}
	
	public int line(){
		return yyline;
	}
	
	public int column(){
		return yycolumn;
	}
	
	public void setLine(int line){
		yyline = line;
	}
	
	public void setColumn(int column){
		yycolumn = column;
	}
	
	
	private void logError(String message) {
		logger.showError(fileName, yyline+1, yycolumn, message);
	}
%}

%eofval{
	if (eof) {
		return null;
	} else {
		eof = true;
		return NL;
	}
%eofval}

DIGIT=[0-9]
HEX_DIGIT=[0-9a-fA-F]
OCT_DIGIT=[0-7]
BIN_DIGIT=[01]
HEX_INT=[0-9][0-9a-fA-F]*[hH]
INTEGER={DIGIT}+
REAL={DIGIT}*\.?{DIGIT}+
OCT_INT={OCT_DIGIT}+[qQ]
BIN_INT={BIN_DIGIT}+[bB]
ID=[a-zA-Z$._@]+[a-zA-Z0-9._@?0-9]*
LITERAL=\"[^\"]*\"|\'[^\']*\'
CHARACTER=\'[^']\' | \\n | \\t | \\r
NEW_LINE=\r\n|\n
%%

<YYINITIAL> {
	/* Registros */
	"a" 			{ return R_A; }
	"b" 			{ return R_B; }
	"c" 			{ return RF_C; }
	"d"				{ return R_D; }
	"e"				{ return R_E; }
	"h"				{ return R_H; }
 	"l"				{ return R_L; }
	"bc"			{ return R_BC; }
	"de"			{ return R_DE; }
	"hl"			{ return R_HL; }
	"af"			{ return R_AF; }
 	"sp"			{ return R_SP; }
 	"ix"			{ return R_IX; }
	"iy"			{ return R_IY; }
	"r"				{ return R_R; }
	"i"				{ return R_I; }
		
	/* Flags */
	"nz"		{ return F_NZ; }
	"z"			{ return F_Z; }
	"nc"		{ return F_NC; }
	"po"		{ return F_PO; }
	"pe"		{ return F_PE; }
 	"p"			{ return F_P; }
	"m"			{ return F_M; }
	
	/* Directivas al ensamblador */
	"equ" 			{ return D_EQU;	}
	"defl"			{ return D_DEFL; }
	"macro" 		{ yybegin(MACRO_HEADER); return D_MACRO; }
	"defb"    		{ return D_DEFB; }
	"db"			{ return D_DEFB; }
	"defw"			{ return D_DEFW; }
	"dw"  			{ return D_DEFW; }
	"defm" 			{ return D_DEFM; }
	"defs"			{ return D_DEFS; }
	"ds"			{ return D_DEFS; }
	"deff" 			{ return D_DEFF; }
	"end" 			{ return D_END;	 }
	"local" 		{ return D_LOCAL;	 }
	"public" 		{ return D_PUBLIC; }
	"extern" 		{ return D_EXTERN; }
	"global" 		{ return D_GLOBAL; }
	"org"			{ return D_ORG;	 }
	"aseg" 			{ return D_ASEG; }
	"cseg" 			{ return D_CSEG; }
	"dseg"			{ return D_DSEG; }
	"include" 		{ return D_INCLUDE; }
	"endm"			{ return D_ENDM; }

	/* Instrucciones */
	"add"		{ return I_ADD; }
	"adc"		{ return I_ADC; }
	"sub"		{ return I_SUB; }
	"sbc"		{ return I_SBC; }
	"inc"		{ return I_INC; }
	"dec"		{ return I_DEC; }
	"neg"		{ return I_NEG; }
	"mlt"		{ return I_MLT; }
	"ld"		{ return I_LD; }
	"and"		{ return I_AND; }
	"or"		{ return I_OR; }
	"xor"		{ return I_XOR; }
	"cpl"		{ return I_CPL; }
	"cp"		{ return I_CP; }
	"jp"		{ return I_JP; }
	"jr"		{ return I_JR; }
	"djnz"		{ return I_DJNZ; }
	"sla"		{ return I_SLA; }
	"sra"		{ return I_SRA; }
	"srl"		{ return I_SRL; }
	"rla"		{ return I_RLA; }
	"rl"		{ return I_RL; }
	"rlca"		{ return I_RLCA; }
	"rlc"		{ return I_RLC; }
	"rra"		{ return I_RRA; }
	"rr"		{ return I_RR; }
	"rrca"		{ return I_RRCA; }
	"rrc"		{ return I_RRC; }
	"rld"		{ return I_RLD; }
	"rrd"		{ return I_RRD; }
	"set"		{ return I_SET; }
	"res"		{ return I_RES; }
	"push"		{ return I_PUSH; }
	"pop"		{ return I_POP; }
	"call"		{ return I_CALL; }
	"ret"		{ return I_RET; }
	"daa"		{ return I_DAA; }
	"nop"		{ return I_NOP; }
	"rst"		{ return I_RST; }
	"in"		{ return I_IN; }
	"out"		{ return I_OUT; }
	"halt"		{ return I_HALT; }
	"ex"		{ return I_EX; }
	"exx"		{ return I_EXX; }
	"ei"		{ return I_EI; }
	"di"		{ return I_DI; }
	"bit"		{ return I_BIT; }
	"scf"		{ return I_SCF; }
	"ccf"		{ return I_CCF; }
	
	
	/* Otros */
	{NEW_LINE}	{	/*	Nota: Por cada NL se devuelven 2 NL. Esto se hace para
						que cuando haya una invocación a una macro y en
						consecuencia un cambio del origen del Reader, no se
						pierdan caracteres del buffer interno del lexes. */
					if (yycolumn != 0) {
						yycolumn = 0;
						yypushback(1);
					}
					return NL;
				 }
	[ \t]+		{ /* Solo interesa si esta en la primer columna */
					if (column() == 0)
						return SPACE; }
	"+"			{ return PLUS; }
	"-"			{ return MINUS; }
	"*"			{ return TIMES; }
	"/"			{ return DIVIDE; }
	":"			{ return COLON; }
	","			{ return COMMA; }
	"("			{ return OPEN_PAREN; }
	")"			{ return CLOSE_PAREN; }
	"$"			{ return DOLLAR_SIGN; }
	
	// Constantes
	{HEX_INT}		{ finalText = yytext(); return HEXA; }
	{INTEGER}		{ finalText = yytext(); return INTEGER; }
	{REAL}			{ finalText = yytext(); return REAL; }
	{OCT_INT}		{ finalText = yytext(); return INTEGER; /*TODO*/}
	{BIN_INT}		{ finalText = yytext(); return INTEGER; /*TODO*/}
	{ID}			{ finalText = yytext(); return ID; }
	{CHARACTER}		{ finalText = yytext(); return CHARACTER; }
	{LITERAL}		{ finalText = yytext(); return LITERAL; }
	
	";"				{ yybegin(BLOCK_COMMENT); }
	
	.				{ logError("Unexpected token: " + yytext()); }
}


<MACRO_HEADER>{
	{ID}			{ finalText = yytext(); return ID; }
	","				{ return COMMA; }
	[ \t]			{ ; }
	\r				{ ; }
	{NEW_LINE}		{ yybegin(MACRO_BODY); return NL; }
	";"				{ yybegin(BLOCK_COMMENT); }
	.				{ logError("Unexpected token: " + yytext()); }
}	

<MACRO_BODY>{
	({ID}":"?)?[ \t]+/"endm".*			{ yybegin(YYINITIAL); return SPACE; }
	.*									{ finalText = yytext(); return MACRO_LINE; }
	\n									{ finalText = yytext(); return MACRO_LINE; /* NOTA: no combinar este pattern con el de arriba! */ }
	.									{ logError("Unexpected token: " + yytext()); }
}

<BLOCK_COMMENT>{
	{NEW_LINE}		{ yybegin(YYINITIAL); return NL; }
	.				{ ; }
}




// Recuperación de errores léxicos
// - Por confusión de sistemas de representación (e.g.: decimal, hexa, octal, binario)
// - Cualquier cosa que no sea un rótulo contra el margen
// ... TODO ...

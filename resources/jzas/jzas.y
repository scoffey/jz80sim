%{
import ar.edu.itba.it.obc.jzas.lexer.*;
import ar.edu.itba.it.obc.jzas.util.*;
import ar.edu.itba.it.obc.jzas.run.Compiler;
import ar.edu.itba.it.obc.jzas.parser.*;
import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.*;
import ar.edu.itba.it.obc.jzas.semantic.*;
import ar.edu.itba.it.obc.jzas.semantic.nodes.*;
import java.util.List;
import java.util.ArrayList;

%}


/* Registros */
%token	R_B		300
%token	RF_C	301
%token	R_D		302
%token	R_E		303
%token	R_H		304
%token	R_L		305
%token	R_BC	306
%token	R_DE	307
%token	R_HL	308
%token	R_AF	309
%token	R_SP	310
%token	R_IX	311
%token	R_IY	312
%token	R_A		313
%token	R_F		314
%token	R_R		315
%token	R_I		316

/* Flag */
%token	F_NZ	400
%token	F_Z		401
%token	F_NC	402
%token	F_PO	403
%token	F_PE	404
%token	F_P		405
%token	F_M		406
	
/* Directivas al compilador */
%token	D_EQU		450
%token	D_DEFL		451
%token	D_MACRO		452
%token	D_DEFB		453
%token	D_DEFW		454
%token	D_DEFM		455
%token	D_DEFS		456
%token	D_DEFF		457
%token	D_END		458
%token	D_LOCAL		459
%token	D_PUBLIC	460
%token	D_ENT		461
%token	D_EXTERN	462
%token	D_GLOBAL	463
%token	D_ASEG		464
%token	D_ORG		465
%token	D_CSEG		466
%token	D_DSEG		467
%token	D_INCLUDE	468
%token	D_ENDM		469
	
/* Instrucciones */
%token	I_ADD		500
%token	I_ADC		501
%token	I_SUB		502
%token	I_SBC		503
%token	I_INC		504
%token	I_DEC		505
%token	I_NEG		506
%token	I_MLT		507
%token	I_LD		508
%token	I_AND		509
%token	I_OR		510
%token	I_XOR		511
%token	I_CPL		512
%token	I_CP		513
%token	I_JP		514
%token	I_DJNZ		515
%token	I_SLA		516
%token	I_SRA		517
%token	I_SRL		518
%token	I_RLA		519
%token	I_RL		520
%token	I_RLCA		521
%token	I_RLC		522
%token	I_RRA		523
%token	I_RR		524
%token	I_RRCA		525
%token	I_RRC		526
%token	I_RLD		527
%token	I_RRD		528
%token	I_SET		529
%token	I_RES		530
%token	I_PUSH		531
%token	I_POP		532
%token	I_CALL		533
%token	I_RET		534
%token	I_DAA		535
%token	I_NOP		536
%token	I_JR		537
%token	I_RST		538
%token	I_IN		539
%token	I_OUT		540
%token	I_HALT		541
%token	I_EX		542
%token	I_EXX		543
%token	I_EI		544
%token	I_DI		545
%token	I_BIT		546
%token	I_SCF		547
%token	I_CCF		548

/* Otros */
%token	NL				600
%token	SPACE			601
%token	PLUS			602
%token	MINUS			603
%token	COLON			604
%token	COMMA			605
%token	OPEN_PAREN		606
%token	CLOSE_PAREN		607
%token	LITERAL			608
%token	ID				609
%token	DOUBLE_COLON	610
%token	INTEGER			611
%token	REAL			612
%token	CHARACTER		613
%token	BINARY			614
%token	HEXA			615
%token	TIMES			616
%token	DIVIDE			617
%token	DOLLAR_SIGN		618
%token 	MACRO_LINE 		619

%left MINUS PLUS
%left TIMES DIVIDE MODULE
%left UMINUS
%left OPEN_PAREN CLOSE_PAREN
%left ASSOC_PAREN
%left ADDRESS_ACCESS_PAREN


%%
pgm:	pgm pgm_line			
		| empty					
		;

empty:
		;

pgm_line:	directive NL																																
			| instruction NL 											{ analyzer.pgmInstructionLine((OCRecord) $1);}
			| optative_space NL										
			| rotule_optative ID macro_params NL						{ analyzer.invokeMacro((String) $2, (List<String>) $3); }
			| rotule NL													{ $$ = analyzer.declareRotule((String) $1); }
			| error														{ analyzer.reportError("Error in instruction"); }
			;

macro_params:	macro_param_list										{ $$ = $1; }
				| empty													{ $$ = new ArrayList<String>(); }
				;

macro_param_list:	macro_param_list COMMA macro_param					{ ((List<String>) ($$ = $1)).add((String) $3); }
					| macro_param										{ ((List<String>) ($$ = new ArrayList<String>())).add((String) $1); }
					;
					
macro_param:	macro_param PLUS macro_param							{ $$ = ((String) $1) + "+" + ((String) $3); }
				| macro_param MINUS macro_param							{ $$ = ((String) $1) + "-" + ((String) $3); }
				| macro_param TIMES macro_param							{ $$ = ((String) $1) + "*" + ((String) $3); }
				| macro_param DIVIDE macro_param						{ $$ = ((String) $1) + "/" + ((String) $3); }
				| MINUS macro_param %prec UMINUS						{ $$ = "-" + ((String) $1); }
				| OPEN_PAREN macro_param CLOSE_PAREN					{ $$ = "(" + ((String) $2) + ")"; }
				| int_number											{ $$ = ((Integer) $1).toString(); }
				| double_number											{ $$ = ((Double) $1).toString(); }
				| ID													{ $$ = $1; }
				| DOLLAR_SIGN											{ $$ = "$"; }
				| R_A													{ $$ = "A"; }
				| R_B													{ $$ = "B"; }
				| RF_C													{ $$ = "C"; }
				| R_D													{ $$ = "D"; }
				| R_E													{ $$ = "E"; }
				| R_H													{ $$ = "H"; }
				| R_L													{ $$ = "L"; }
				| R_BC													{ $$ = "BC"; }
				| R_DE													{ $$ = "DE"; }
				| R_HL													{ $$ = "HL"; }
				| R_SP													{ $$ = "SP"; }
				| R_IX													{ $$ = "IX"; }
				| R_IY													{ $$ = "IY"; }
				| R_F													{ $$ = "F"; }
				| F_NZ													{ $$ = "NZ"; }
				| F_Z													{ $$ = "Z"; }
				| F_NC													{ $$ = "NC"; }
				| F_PO													{ $$ = "PO"; }
				| F_PE													{ $$ = "PE"; }
				| F_P													{ $$ = "P"; }
				| F_M													{ $$ = "M"; }
				| I_ADD													{ $$ = "ADD"; }
				| I_ADC													{ $$ = "ADC"; }
				| I_SUB													{ $$ = "SUB"; }
				| I_SBC													{ $$ = "SBC"; }
				| I_INC													{ $$ = "INC"; }
				| I_DEC													{ $$ = "DEC"; }
				| I_NEG													{ $$ = "NEG"; }
				| I_MLT													{ $$ = "MLT"; }
				| I_LD													{ $$ = "LD"; }
				| I_AND													{ $$ = "AND"; }
				| I_OR													{ $$ = "OR"; }
				| I_XOR													{ $$ = "XOR"; }
				| I_CPL													{ $$ = "CPL"; }
				| I_CP													{ $$ = "CP"; }
				| I_JP													{ $$ = "JP"; }
				| I_DJNZ												{ $$ = "DJNZ"; }
				| I_SLA													{ $$ = "SLA"; }
				| I_SRA													{ $$ = "SRA"; }
				| I_SRL													{ $$ = "SRL"; }
				| I_RLA													{ $$ = "RLA"; }
				| I_RL													{ $$ = "RL"; }
				| I_RLCA												{ $$ = "RLCA"; }
				| I_RLC													{ $$ = "RLC"; }
				| I_RRA													{ $$ = "RRA"; }
				| I_RR													{ $$ = "RR"; }
				| I_RRCA												{ $$ = "RRCA"; }
				| I_RRC													{ $$ = "RRC"; }
				| I_RLD													{ $$ = "RLD"; }
				| I_RRD													{ $$ = "RRD"; }
				| I_SET													{ $$ = "SET"; }
				| I_RES													{ $$ = "RES"; }
				| I_PUSH												{ $$ = "PUSH"; }
				| I_POP													{ $$ = "POP"; }
				| I_CALL												{ $$ = "CALL"; }
				| I_RET													{ $$ = "RET"; }
				| I_DAA													{ $$ = "DAA"; }
				| I_NOP													{ $$ = "NOP"; }
				| I_JR													{ $$ = "JR"; }
				| I_RST													{ $$ = "RST"; }
				| I_IN													{ $$ = "IN"; }
				| I_HALT												{ $$ = "HALT"; }
				| I_EX													{ $$ = "EX"; }
				| I_EXX													{ $$ = "EXX"; }
				| I_EI													{ $$ = "EI"; }
				| I_DI													{ $$ = "DI"; }
				| I_BIT													{ $$ = "BIT"; }
				| I_SCF													{ $$ = "SCF"; }
				| I_CCF													{ $$ = "CCF"; }
				;
			
instruction:	rotule_optative instr 									{ $$ = analyzer.instruction((String) $1, (OCRecord) $2); }
				;

rotule_optative:	rotule 												{ $$ = $1; }
					| SPACE												{ $$ = new String(""); }
					;

rotule:		ID															{ $$ = $1; }
			| ID COLON													{ $$ = $1; }
			;

optative_space:	SPACE
				| empty
				;
			
directive: 		macro_directive												{ $$ = $1; }
				| direct_macro												{ $$ = $1; }
				| direct_extern												{ $$ = $1; }
				
/* Directivas */
macro_directive:	direct_equ												{ $$ = $1; }
					| direct_defl											{ $$ = $1; }
					| direct_defb											{ $$ = $1; }
					| direct_defw											{ $$ = $1; }
					| direct_defm											{ $$ = $1; }
					| direct_defs											{ $$ = $1; }
					| direct_deff											{ $$ = $1; }
					| direct_end											{ $$ = $1; }
					| direct_public											{ $$ = $1; }
					| direct_aseg											{ $$ = $1; }
					| direct_org											{ $$ = $1; }
					| direct_cseg											{ $$ = $1; }
					| direct_dseg											{ $$ = $1; }
					| direct_include										{ $$ = $1; }
					;
				
direct_equ:		rotule D_EQU double_exp									{ analyzer.directiveEqu((String) $1, ((Double) $3).toString()); }
				// TODO: Ver todas las cosas con las que se
				;
				
direct_extern:	rotule_optative D_EXTERN ids							{ analyzer.directiveExtern((List<String>) $3); }
				;

direct_defl:	rotule_optative D_DEFL double_exp						{ analyzer.directiveDefl((String) $1, ((Double) $3).toString()); }
				// TODO: Ver todas las cosas con las que se
				;
				
double_exp:	double_exp PLUS double_exp									{ $$ = analyzer.MathExpression(MathOperator.PLUS, (Double) $1, (Double) $3); }
			| double_exp MINUS double_exp								{ $$ = analyzer.MathExpression(MathOperator.MINUS, (Double) $1, (Double) $3); }
			| double_exp TIMES double_exp								{ $$ = analyzer.MathExpression(MathOperator.TIMES, (Double) $1, (Double) $3); }
			| double_exp DIVIDE double_exp								{ $$ = analyzer.MathExpression(MathOperator.DIVIDE, (Double) $1, (Double) $3); }
			| MINUS double_exp %prec UMINUS								{ $$ = analyzer.MathExpression((Double) $2); }
			| OPEN_PAREN double_exp CLOSE_PAREN							{ $$ = $2; }
			| double_number												{ $$ = $1; }
			| int_number												{ $$ = new Double((Integer) $1); }
			| ID														{ $$ = new Double(analyzer.rotuleAliasExpression((String) $1)); }
			| DOLLAR_SIGN												{ $$ = new Double((Integer) analyzer.getNextAddress()); }
			;
		
int_exp:	int_exp PLUS int_exp										{ $$ = analyzer.MathExpression(MathOperator.PLUS, (IntExpression) $1, (IntExpression) $3); }
			| int_exp MINUS int_exp										{ $$ = analyzer.MathExpression(MathOperator.MINUS, (IntExpression) $1, (IntExpression) $3); }
			| int_exp TIMES int_exp										{ $$ = analyzer.MathExpression(MathOperator.TIMES, (IntExpression) $1, (IntExpression) $3); }
			| int_exp DIVIDE int_exp									{ $$ = analyzer.MathExpression(MathOperator.DIVIDE, (IntExpression) $1, (IntExpression) $3); }
			| MINUS int_exp %prec UMINUS								{ $$ = analyzer.MathExpression((IntExpression) $2); }
			| int_number												{ $$ = new IntExpression((Integer) $1); }
			| ID														{ $$ = analyzer.rotuleExpression((String) $1); }
			| DOLLAR_SIGN												{ $$ = analyzer.dollarSignExpression(); }
			| OPEN_PAREN int_exp CLOSE_PAREN %prec ASSOC_PAREN			{$$ = $2;}
			;

int_16bits_exp:	int_exp	%prec ADDRESS_ACCESS_PAREN						{ if (Integer.toBinaryString(((IntExpression) $1).getData()).length() > 16) { analyzer.reportError("Number out of 16 bits bounds"); } $$ = $1; }
				;
				
int_8bits_exp:	int_exp													{ if (Integer.toBinaryString(((IntExpression) $1).getData()).length() > 8) { analyzer.reportError("Number out of 8 bits bounds"); } $$ = ((IntExpression)$1).getData(); }
				;
				
int_3bits_exp:	int_exp													{ if (Integer.toBinaryString(((IntExpression) $1).getData()).length() > 3) { analyzer.reportError("Number out of 3 bits bounds"); } $$ = ((IntExpression)$1).getData(); }
				;

						
int_number:		INTEGER													{ $$ = Integer.parseInt((String) $1); }
				| HEXA													{ $$ = Integer.parseInt(((String) $1).substring(0, ((String) $1).length() - 1), 16); }
				| CHARACTER												{ $$ = parseChar((String) $1); }
				;
				
double_number:	REAL													{ $$ = Double.parseDouble((String) $1); }
				;
			
direct_macro:	rotule D_MACRO ids NL macro_instrs rotule_optative D_ENDM		{ analyzer.defineMacro((String) $1, (List<String>) $3, (String) $5); }
				;
				
ids:	id_list															{ $$ = $1; }
		| empty															{ $$ = new ArrayList<String>(); }
		;

id_list:	id_list COMMA ID											{ ((List<String>)($$ = $1)).add((String) $3); }
			| ID														{ ((List<String>)($$ = new ArrayList<String>())).add((String) $1); }
			;


macro_instrs:	macro_instrs MACRO_LINE									{ $$ = ((String) $1) + ((String) $2); }
				| empty													{ $$ = ""; }
				;
				
direct_defb:	rotule_optative D_DEFB int_exp_8bits_list				{ analyzer.directiveDefb((String) $1, (List<Integer>) $3); }
				;
			
int_exp_8bits_list:	int_exp_8bits_list COMMA int_8bits_exp				{ ((List<Integer>) ($$ = $1)).add((Integer) $2); }
					| int_8bits_exp										{ ((List<Integer>) ($$ = new ArrayList<Integer>())).add((Integer) $1); }
					;
					
int_exp_16bits_list:	int_exp_16bits_list COMMA int_16bits_exp		{ ((List<Integer>) ($$ = $1)).add((Integer) $2); }
						| int_16bits_exp								{ ((List<Integer>) ($$ = new ArrayList<Integer>())).add((Integer)((IntExpression) $1).getData()); }
						;
					

float_exp_list:	float_exp_list COMMA double_exp							{ ((List<Float>) ($$ = $1)).add(new Float((Double) $2)); }
					| double_exp										{ ((List<Float>) ($$ = new ArrayList<Float>())).add(new Float((Double) $1)); }
					;
				
// TODO: el esquema de la lista de segmentos para este unico caso no funcionaria
// (habria que implementar una lsita de listas)
direct_defw:	rotule_optative D_DEFW int_exp_16bits_list				{ analyzer.directiveDefw((String) $1, (List<Integer>) $3); }
				;
				
direct_defm:	rotule_optative D_DEFM LITERAL							{ analyzer.directiveDefm((String) $1, (String) $3); }
				;

direct_defs:	rotule_optative D_DEFS int_exp							{ analyzer.directiveDefs((String) $1, ((Integer) ((IntExpression) $3).getData())); }
				;
				
direct_deff:	rotule_optative D_DEFF float_exp_list					{ analyzer.directiveDeff((String) $1, (List<Float>) $3); }
				;

direct_end:		rotule_optative D_END ID								{ analyzer.directiveEnd((String) $3); }
				;
								
direct_public:	rotule_optative D_PUBLIC id_list						{ analyzer.directivePublic((List<String>) $3); }
				| rotule_optative D_GLOBAL id_list						{ analyzer.directivePublic((List<String>) $3); }
				;
				
direct_aseg:	rotule_optative D_ASEG									{ analyzer.directiveAseg(); }
				;

direct_org:		rotule_optative D_ORG int_number						{ analyzer.directiveOrg((Integer) $3); }
				;

direct_cseg:	rotule_optative D_CSEG									{ analyzer.directiveCseg(); }
				;

direct_dseg:	rotule_optative D_DSEG									{ analyzer.directiveDseg(); }
				;

direct_include: rotule_optative D_INCLUDE id_list						{ analyzer.directiveInclude((List<String>) $3); }
				;

/* Instrucciones */
instr:	instr_add														{ $$ = $1; }
		| instr_adc														{ $$ = $1; }
		| instr_sub														{ $$ = $1; }
		| instr_sbc														{ $$ = $1; }
		| instr_inc														{ $$ = $1; }
		| instr_dec														{ $$ = $1; }
		| instr_neg														{ $$ = $1; }
		| instr_mlt														{ $$ = $1; }
		| instr_ld														{ $$ = $1; }
		| instr_and														{ $$ = $1; }
		| instr_or														{ $$ = $1; }
		| instr_xor														{ $$ = $1; }
		| instr_cpl														{ $$ = $1; }
		| instr_cp														{ $$ = $1; }
		| instr_jp														{ $$ = $1; }
		| instr_djnz													{ $$ = $1; }
		| instr_shift													{ $$ = $1; }
		| instr_rotate													{ $$ = $1; }
		| instr_set														{ $$ = $1; }
		| instr_res														{ $$ = $1; }
		| instr_push													{ $$ = $1; }
		| instr_pop														{ $$ = $1; }
		| instr_call													{ $$ = $1; }
		| instr_ret														{ $$ = $1; }
		| instr_daa														{ $$ = $1; }
		| instr_nop														{ $$ = $1; }
		| instr_rst														{ $$ = $1; }
		| instr_in														{ $$ = $1; }
		| instr_out														{ $$ = $1; }
		| instr_halt													{ $$ = $1; }
		| instr_ex														{ $$ = $1; }
		| instr_exx														{ $$ = $1; }
		| instr_ei														{ $$ = $1; }
		| instr_di														{ $$ = $1; }
		| instr_bit														{ $$ = $1; }
		| instr_scf														{ $$ = $1; }
		| instr_ccf														{ $$ = $1; }
		;


instr_add:	I_ADD R_A COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("10000" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_ADD R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("86", 16, analyzer.getNextAddress())); }
			| I_ADD R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("DD86" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_ADD R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("FD86" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_ADD R_A COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("C6" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			| I_ADD R_HL COMMA t3										{ $$ = analyzer.newInstruction(new OCRecord("00" + ((Register) $4).getCodification().substring(1) + "1001", 2, analyzer.getNextAddress())); }						
			| I_ADD R_IX COMMA t4										{ $$ = analyzer.newInstruction(new OCRecord("1101110100" + ((Register) $4).getCodification().substring(1) + "1001", 2, analyzer.getNextAddress())); }
			| I_ADD R_IY COMMA t5										{ $$ = analyzer.newInstruction(new OCRecord("1111110100" + ((Register) $4).getCodification().substring(1) + "1001", 2, analyzer.getNextAddress())); }
			| I_ADD t1													{ analyzer.reportWarning("Instruction ADD must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("10000" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_ADD int_8bits_exp										{ analyzer.reportWarning("Instruction ADD must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("C6" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }			
			;
			
instr_adc:	I_ADC R_A COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("10001" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_ADC R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("8E", 16, analyzer.getNextAddress())); }
			| I_ADC R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("DD8E" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_ADC R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("FD8E" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_ADC R_A COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("CE" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			| I_ADC R_HL COMMA t3										{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + ((Register) $4).getCodification().substring(1) + "1010", 2, analyzer.getNextAddress())); }
			| I_ADC t1													{ analyzer.reportWarning("Instruction ADC must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("10001" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_ADC int_8bits_exp										{ analyzer.reportWarning("Instruction ADC must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("CE" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			;

instr_sub:	I_SUB t1													{ $$ = analyzer.newInstruction(new OCRecord("10010" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_SUB OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("96", 16, analyzer.getNextAddress())); }
			| I_SUB OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DD96" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_SUB OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FD96" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_SUB int_8bits_exp										{ $$ = analyzer.newInstruction(new OCRecord("D6" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			| I_SUB R_A COMMA t1										{ analyzer.reportWarning("Instruction SUB must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("10010" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_SUB R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ analyzer.reportWarning("Instruction SUB must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("96", 16, analyzer.getNextAddress())); }
			| I_SUB R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction SUB must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("DD96" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_SUB R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction SUB must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FD96" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_SUB R_A COMMA int_8bits_exp								{ analyzer.reportWarning("Instruction SUB must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("D6" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			;
			
instr_sbc:	I_SBC R_A COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("10011" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }														
			| I_SBC R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("9E", 16, analyzer.getNextAddress())); }
			| I_SBC R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("DD9E" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_SBC R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("FD9E" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_SBC R_A COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("DE" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			| I_SBC R_HL COMMA t3										{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + ((Register) $4).getCodification().substring(1) + "0010", 2, analyzer.getNextAddress())); }
			| I_SBC t1													{ analyzer.reportWarning("Instruction SBC must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("10011" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_SBC t3													{ analyzer.reportWarning("Instruction SBC must have 2 operands"); $$ = analyzer.newInstruction(new OCRecord("1110110101" + ((Register) $2).getCodification() + "0010", 2, analyzer.getNextAddress())); }
			;// System.out.println(new CodeGenerator(null, null)
// .newInstruction(new OCRecord("00"
// + Register.A.getCodification()
// + "110"
// + NumberConversionUtil
// .leastSignificativeBin((Integer) 0))));


instr_inc:	I_INC t1													{ $$ = analyzer.newInstruction(new OCRecord("00" + ((Register) $2).getCodification() + "100", 2, analyzer.getNextAddress())); }
			| I_INC OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("34", 16, analyzer.getNextAddress())); }
			| I_INC OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DD34" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_INC OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FD34" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_INC t3													{ $$ = analyzer.newInstruction(new OCRecord("00" + ((Register) $2).getCodification() + "0011", 2, analyzer.getNextAddress())); }
			| I_INC R_IX												{ $$ = analyzer.newInstruction(new OCRecord("DD23", 16, analyzer.getNextAddress())); }
			| I_INC R_IY												{ $$ = analyzer.newInstruction(new OCRecord("FD23", 16, analyzer.getNextAddress())); }
			;
			
instr_dec:	I_DEC t1													{ $$ = analyzer.newInstruction(new OCRecord("00" + ((Register) $2).getCodification() + "101", 2, analyzer.getNextAddress())); }
			| I_DEC OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("35", 16, analyzer.getNextAddress())); }
			| I_DEC OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DD35" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_DEC OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FD35" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_DEC t3													{ $$ = analyzer.newInstruction(new OCRecord("00" + ((Register) $2).getCodification() + "1011", 2, analyzer.getNextAddress())); }			
			| I_DEC R_IX												{ $$ = analyzer.newInstruction(new OCRecord("DD2B", 16, analyzer.getNextAddress())); }
			| I_DEC R_IY												{ $$ = analyzer.newInstruction(new OCRecord("FD2B", 16, analyzer.getNextAddress())); }
			;
			
instr_neg:	I_NEG														{ $$ = analyzer.newInstruction(new OCRecord("ED44", 16, analyzer.getNextAddress())); }
			| I_NEG	R_A													{ analyzer.reportWarning("Instruction NEG must have 0 operands"); $$ = analyzer.newInstruction(new OCRecord("ED44", 16, analyzer.getNextAddress())); }
			;
			
instr_mlt:	I_MLT t3													{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + ((Register) $2).getCodification().substring(1) + "1100", 2, analyzer.getNextAddress())); }
			;

			
bin_offset:	PLUS int_16bits_exp											{ $$ = leastSignificativeBin(((IntExpression) $2).getData()); }
			| MINUS int_16bits_exp										{ $$ = leastSignificativeBin(-((IntExpression) $2).getData()); }
			| 															{ $$ = "00000000"; }
			;
			
hex_offset:	PLUS int_16bits_exp											{ $$ = leastSignificativeHex(((IntExpression) $2).getData()); }
			| MINUS int_16bits_exp										{ $$ = leastSignificativeHex(-((IntExpression) $2).getData()); }
			| 															{ $$ = "00"; }
			;
			
instr_ld:	I_LD R_A COMMA R_I											{ $$ = analyzer.newInstruction(new OCRecord("ED57", 16, analyzer.getNextAddress())); }	
			| I_LD R_A COMMA R_R										{ $$ = analyzer.newInstruction(new OCRecord("ED5F", 16, analyzer.getNextAddress())); }
			| I_LD R_I COMMA R_A										{ $$ = analyzer.newInstruction(new OCRecord("ED47", 16, analyzer.getNextAddress())); }
			| I_LD R_R COMMA R_A										{ $$ = analyzer.newInstruction(new OCRecord("ED4F", 16, analyzer.getNextAddress())); }
			| I_LD R_A COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.A.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_B COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.B.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD RF_C COMMA t1										{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.C.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_D COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.D.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_E COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.E.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_H COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.H.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_L COMMA t1											{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.L.getCodification() + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_A COMMA OPEN_PAREN R_BC CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("0A", 16, analyzer.getNextAddress())); }
			| I_LD R_A COMMA OPEN_PAREN R_DE CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("1A", 16, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_BC CLOSE_PAREN COMMA R_A				{ $$ = analyzer.newInstruction(new OCRecord("02", 16, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_DE CLOSE_PAREN COMMA R_A				{ $$ = analyzer.newInstruction(new OCRecord("12", 16, analyzer.getNextAddress())); }
			| I_LD R_A COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("3A" + littleEndianHex(((IntExpression) $5).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol()); }
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_A		{ $$ = analyzer.newInstruction(new OCRecord("32" + littleEndianHex(((IntExpression) $3).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol());}
			| I_LD R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.A.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD R_B COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.B.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD RF_C COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.C.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD R_D COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.D.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD R_E COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.E.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD R_H COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.H.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD R_L COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("01" + Register.L.getCodification() + "110", 2, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_HL CLOSE_PAREN COMMA t1 				{ $$ = analyzer.newInstruction(new OCRecord("01110" + ((Register) $6).getCodification(), 2, analyzer.getNextAddress())); }
			| I_LD R_A COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.A.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_B COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.B.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD RF_C COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.C.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_D COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.D.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_E COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.E.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_H COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.H.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_L COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1101110101" + Register.L.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_A COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.A.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_B COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.B.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD RF_C COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.C.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_D COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.D.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_E COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.E.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_H COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.H.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD R_L COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1111110101" + Register.L.getCodification() + "110" + ((String) $6), 2, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_IX bin_offset CLOSE_PAREN COMMA t1		{ $$ = analyzer.newInstruction(new OCRecord("1101110101110" + ((Register) $7).getCodification() + ((String) $4), 2, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_IY bin_offset CLOSE_PAREN COMMA t1		{ $$ = analyzer.newInstruction(new OCRecord("1111110101110" + ((Register) $7).getCodification() + ((String) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_A COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.A.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_B COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.B.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD RF_C COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.C.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_D COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.D.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_E COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.E.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_H COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.H.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD R_L COMMA int_8bits_exp								{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.L.getCodification() + "110" + leastSignificativeBin((Integer) $4), 2, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_HL CLOSE_PAREN COMMA int_8bits_exp		{ $$ = analyzer.newInstruction(new OCRecord("36" + leastSignificativeHex((Integer) $6), 16, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_IX bin_offset CLOSE_PAREN COMMA int_8bits_exp	{ $$ = analyzer.newInstruction(new OCRecord("DD36" + bin2Hex((String) $4) + leastSignificativeHex((Integer) $7), 16, analyzer.getNextAddress())); }
			| I_LD OPEN_PAREN R_IY bin_offset CLOSE_PAREN COMMA int_8bits_exp	{ $$ = analyzer.newInstruction(new OCRecord("FD36" + bin2Hex((String) $4) + leastSignificativeHex((Integer) $7), 16, analyzer.getNextAddress())); }
// /* LD 16 bits */
			| I_LD R_SP COMMA R_HL										{ $$ = analyzer.newInstruction(new OCRecord("F9", 16, analyzer.getNextAddress())); }
			| I_LD R_SP COMMA R_IX										{ $$ = analyzer.newInstruction(new OCRecord("DDF9", 16, analyzer.getNextAddress())); }
			| I_LD R_SP COMMA R_IY										{ $$ = analyzer.newInstruction(new OCRecord("FDF9", 16, analyzer.getNextAddress())); }
			// TODO: La instruccion comentada de abajo es equivalente a una que
			// esta mas abajo. Ver issue en un mail.
// | I_LD R_HL COMMA OPEN_PAREN int_exp CLOSE_PAREN { $$ =
// analyzer.newInstruction(new OCRecord("2A" + littleEndianHex((Integer) $5)));
// }
			| I_LD R_IX COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("DD2A" + littleEndianHex(((IntExpression) $5).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol());}
			| I_LD R_IY COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("FD2A" + littleEndianHex(((IntExpression) $5).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol());}
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_HL		{ $$ = analyzer.newInstruction(new OCRecord("22" + littleEndianHex(((IntExpression) $3).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol());}
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_IX		{ $$ = analyzer.newInstruction(new OCRecord("DD22" + littleEndianHex(((IntExpression) $3).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol());}
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_IY		{ $$ = analyzer.newInstruction(new OCRecord("FD22" + littleEndianHex(((IntExpression) $3).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol());}
			| I_LD R_BC COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.BC.getCodification().substring(1) + "1011" + littleEndianBin(((IntExpression) $5).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol()); }
			| I_LD R_DE COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.DE.getCodification().substring(1) + "1011" + littleEndianBin(((IntExpression) $5).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol()); }
			| I_LD R_HL COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.HL.getCodification().substring(1) + "1011" + littleEndianBin(((IntExpression) $5).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol()); }
			| I_LD R_SP COMMA OPEN_PAREN int_16bits_exp CLOSE_PAREN		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.SP.getCodification().substring(1) + "1011" + littleEndianBin(((IntExpression) $5).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $5).getSymbol()); }
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_BC		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.BC.getCodification().substring(1) + "0011" + littleEndianBin(((IntExpression) $3).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol()); }
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_DE		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.DE.getCodification().substring(1) + "0011" + littleEndianBin(((IntExpression) $3).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol()); }
			| I_LD OPEN_PAREN int_16bits_exp CLOSE_PAREN COMMA R_SP		{ $$ = analyzer.newInstruction(new OCRecord("1110110101" + Register.SP.getCodification().substring(1) + "0011" + littleEndianBin(((IntExpression) $3).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $3).getSymbol()); }
			| I_LD R_IX COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("DD21" + littleEndianHex(((IntExpression) $4).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_LD R_IY COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("FD21" + littleEndianHex(((IntExpression) $4).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_LD R_BC COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.BC.getCodification() + "0001" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_LD R_DE COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.DE.getCodification() + "0001" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_LD R_HL COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.HL.getCodification() + "0001" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_LD R_SP COMMA int_16bits_exp							{ $$ = analyzer.newInstruction(new OCRecord("00" + Register.SP.getCodification() + "0001" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			;
			
instr_and:	I_AND t1													{ $$ = analyzer.newInstruction(new OCRecord("10100" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_AND OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("A6", 16, analyzer.getNextAddress())); }
			| I_AND OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDA6" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_AND OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDA6" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_AND int_8bits_exp										{ $$ = analyzer.newInstruction(new OCRecord("E6" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			| I_AND R_A COMMA t1										{ analyzer.reportWarning("Instruction AND must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("10100" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_AND R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ analyzer.reportWarning("Instruction AND must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("A6", 16, analyzer.getNextAddress())); }
			| I_AND R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction AND must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("DDA6" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_AND R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction AND must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FDA6" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_AND R_A COMMA int_8bits_exp								{ analyzer.reportWarning("Instruction AND must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("E6" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			;

instr_or:	I_OR t1														{ $$ = analyzer.newInstruction(new OCRecord("10110" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_OR OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("B6", 16, analyzer.getNextAddress())); }
			| I_OR OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDB6" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_OR OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDB6" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_OR int_8bits_exp										{ $$ = analyzer.newInstruction(new OCRecord("F6" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			| I_OR R_A COMMA t1											{ analyzer.reportWarning("Instruction OR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("10110" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_OR R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ analyzer.reportWarning("Instruction OR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("B6", 16, analyzer.getNextAddress())); }
			| I_OR R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN		{ analyzer.reportWarning("Instruction OR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("DDB6" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_OR R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN		{ analyzer.reportWarning("Instruction OR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FDB6" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_OR R_A COMMA int_8bits_exp								{ analyzer.reportWarning("Instruction OR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("F6" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			;

instr_xor:	I_XOR t1													{ $$ = analyzer.newInstruction(new OCRecord("10101" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_XOR OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("AE", 16, analyzer.getNextAddress())); }
			| I_XOR OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDAE" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_XOR OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDAE" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_XOR int_8bits_exp										{ $$ = analyzer.newInstruction(new OCRecord("EE" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			| I_XOR R_A COMMA t1										{ analyzer.reportWarning("Instruction XOR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("10101" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_XOR R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ analyzer.reportWarning("Instruction XOR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("AE", 16, analyzer.getNextAddress())); }
			| I_XOR R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction XOR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("DDAE" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_XOR R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN	{ analyzer.reportWarning("Instruction XOR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FDAE" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_XOR R_A COMMA int_8bits_exp								{ analyzer.reportWarning("Instruction XOR must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("EE" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			;
			
instr_cpl:	I_CPL														{ $$ = analyzer.newInstruction(new OCRecord("2F", 16, analyzer.getNextAddress())); }
			| I_CPL R_A													{ analyzer.reportWarning("Instruction CPL must have 0 operands"); $$ = analyzer.newInstruction(new OCRecord("2F", 16, analyzer.getNextAddress())); }
			;
			
instr_cp:	I_CP t1														{ $$ = analyzer.newInstruction(new OCRecord("10111" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
			| I_CP OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("BE", 16, analyzer.getNextAddress())); }
			| I_CP OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDBE" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_CP OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDBE" + ((String) $4), 16, analyzer.getNextAddress())); }
			| I_CP int_8bits_exp										{ $$ = analyzer.newInstruction(new OCRecord("FE" + leastSignificativeHex((Integer) $2), 16, analyzer.getNextAddress())); }
			| I_CP R_A COMMA t1											{ analyzer.reportWarning("Instruction CP must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("10111" + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_CP R_A COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ analyzer.reportWarning("Instruction CP must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("BE", 16, analyzer.getNextAddress())); }
			| I_CP R_A COMMA OPEN_PAREN R_IX hex_offset CLOSE_PAREN		{ analyzer.reportWarning("Instruction CP must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("DDBE" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_CP R_A COMMA OPEN_PAREN R_IY hex_offset CLOSE_PAREN		{ analyzer.reportWarning("Instruction CP must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FDBE" + ((String) $6), 16, analyzer.getNextAddress())); }
			| I_CP R_A COMMA int_8bits_exp								{ analyzer.reportWarning("Instruction CP must have 1 operand"); $$ = analyzer.newInstruction(new OCRecord("FE" + leastSignificativeHex((Integer) $4), 16, analyzer.getNextAddress())); }
			;
			
instr_jp:	I_JP rotule_name												{ $$ = analyzer.newInstruction(new OCRecord("C3" + littleEndianHex(((IntExpression) $2).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $2).getSymbol()); }
			| I_JP OPEN_PAREN R_HL CLOSE_PAREN								{ $$ = analyzer.newInstruction(new OCRecord("E9", 16, analyzer.getNextAddress())); }
			| I_JP OPEN_PAREN R_IX CLOSE_PAREN								{ $$ = analyzer.newInstruction(new OCRecord("DDE9", 16, analyzer.getNextAddress())); }
			| I_JP OPEN_PAREN R_IY CLOSE_PAREN								{ $$ = analyzer.newInstruction(new OCRecord("FDE9", 16, analyzer.getNextAddress())); }
			| I_JR rotule_name												{ $$ = analyzer.newInstruction(new OCRecord("18" + leastSignificativeHex(relativeAddress(((IntExpression) $2).getData() - 2)), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $2).getSymbol()); }
			| I_JP t6 COMMA rotule_name										{ $$ = analyzer.newInstruction(new OCRecord("11" + ((Flag) $2).getCodification() + "010" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_JR RF_C COMMA rotule_name									{ $$ = analyzer.newInstruction(new OCRecord("38" + leastSignificativeHex(relativeAddress(((IntExpression) $4).getData() - 2)), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_JR F_NC COMMA rotule_name									{ $$ = analyzer.newInstruction(new OCRecord("30" + leastSignificativeHex(relativeAddress(((IntExpression) $4).getData() - 2)), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_JR F_Z COMMA rotule_name									{ $$ = analyzer.newInstruction(new OCRecord("28" + leastSignificativeHex(relativeAddress(((IntExpression) $4).getData() - 2)), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			| I_JR F_NZ COMMA rotule_name									{ $$ = analyzer.newInstruction(new OCRecord("20" + leastSignificativeHex(relativeAddress(((IntExpression) $4).getData() - 2)), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol()); }
			;
			
instr_djnz:	I_DJNZ rotule_name												{ $$ = analyzer.newInstruction(new OCRecord("10" + leastSignificativeHex(relativeAddress(((IntExpression) $2).getData()) - 2), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $2).getSymbol()); }
			;
			
instr_shift:	I_SLA t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100100" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_SLA OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB26", 16, analyzer.getNextAddress())); }
				| I_SLA OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "26", 16, analyzer.getNextAddress())); }
				| I_SLA OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "26", 16, analyzer.getNextAddress())); }
				| I_SRA t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100101" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_SRA OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB2E", 16, analyzer.getNextAddress())); }
				| I_SRA OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "2E", 16, analyzer.getNextAddress())); }
				| I_SRA OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "2E", 16, analyzer.getNextAddress())); }
				| I_SRL t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100111" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_SRL OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB3E", 16, analyzer.getNextAddress())); }
				| I_SRL OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "3E", 16, analyzer.getNextAddress())); }
				| I_SRL OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "3E", 16, analyzer.getNextAddress())); }
				;

instr_rotate:	I_RLA														{ $$ = analyzer.newInstruction(new OCRecord("17", 16, analyzer.getNextAddress())); }
				| I_RL t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100010" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }														
				| I_RL OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB16", 16, analyzer.getNextAddress())); }
				| I_RL OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "16", 16, analyzer.getNextAddress())); }
				| I_RL OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "16", 16, analyzer.getNextAddress())); }
				| I_RLCA													{ $$ = analyzer.newInstruction(new OCRecord("07", 16, analyzer.getNextAddress())); }
				| I_RLC t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100000" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_RLC OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB06", 16, analyzer.getNextAddress())); }
				| I_RLC OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "06", 16, analyzer.getNextAddress())); }
				| I_RLC OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "06", 16, analyzer.getNextAddress())); }
				| I_RRA														{ $$ = analyzer.newInstruction(new OCRecord("1F", 16, analyzer.getNextAddress())); }
				| I_RR t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100011" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_RR OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB1E", 16, analyzer.getNextAddress())); }
				| I_RR OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "1E", 16, analyzer.getNextAddress())); }
				| I_RR OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "1E", 16, analyzer.getNextAddress())); }
				| I_RRCA													{ $$ = analyzer.newInstruction(new OCRecord("0F", 16, analyzer.getNextAddress())); }
				| I_RRC t1													{ $$ = analyzer.newInstruction(new OCRecord("1100101100001" + ((Register) $2).getCodification(), 2, analyzer.getNextAddress())); }
				| I_RRC OPEN_PAREN R_HL CLOSE_PAREN							{ $$ = analyzer.newInstruction(new OCRecord("CB0E", 16, analyzer.getNextAddress())); }
				| I_RRC OPEN_PAREN R_IX hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("DDCB" + ((String) $4) + "0E", 16, analyzer.getNextAddress())); }
				| I_RRC OPEN_PAREN R_IY hex_offset CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("FDCB" + ((String) $4) + "0E", 16, analyzer.getNextAddress())); }
				| I_RLD														{ $$ = analyzer.newInstruction(new OCRecord("ED6F", 16, analyzer.getNextAddress())); }
				| I_RRD														{ $$ = analyzer.newInstruction(new OCRecord("ED67", 16, analyzer.getNextAddress())); }
				;
				
instr_set:	I_SET int_3bits_exp COMMA t1										{ $$ = analyzer.newInstruction(new OCRecord("1100101111" + toBin((Integer) $2, 3) + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_SET int_3bits_exp COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("1100101111" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			| I_SET int_3bits_exp COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1101110111001011" + ((String) $6) + "11" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			| I_SET int_3bits_exp COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1111110111001011" + ((String) $6) + "11" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			;
			
instr_res:	I_RES int_3bits_exp COMMA t1										{ $$ = analyzer.newInstruction(new OCRecord("1100101110" + toBin(((Integer) $2), 3) + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }			
			| I_RES int_3bits_exp COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("1100101110" + toBin(((Integer) $2), 3) + "110", 2, analyzer.getNextAddress())); }
			| I_RES int_3bits_exp COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1101110111001011" + ((String) $6) + "10" + toBin(((Integer) $2), 3) + "110", 2, analyzer.getNextAddress())); }
			| I_RES int_3bits_exp COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1111110111001011" + ((String) $6) + "10" + toBin(((Integer) $2), 3) + "110", 2, analyzer.getNextAddress())); }
			;

instr_push:	I_PUSH t2														{ $$ = analyzer.newInstruction(new OCRecord("11" + ((Register) $2).getCodification().substring(1) + "0101", 2, analyzer.getNextAddress())); }
			| I_PUSH  R_IX													{ $$ = analyzer.newInstruction(new OCRecord("DDE5", 16, analyzer.getNextAddress())); }
			| I_PUSH  R_IY													{ $$ = analyzer.newInstruction(new OCRecord("FDE5", 16, analyzer.getNextAddress())); }
			;

instr_pop:	I_POP t2														{ $$ = analyzer.newInstruction(new OCRecord("11" + ((Register) $2).getCodification().substring(1) + "0001", 2, analyzer.getNextAddress())); }
			| I_POP  R_IX													{ $$ = analyzer.newInstruction(new OCRecord("DDE1", 16, analyzer.getNextAddress())); }
			| I_POP  R_IY													{ $$ = analyzer.newInstruction(new OCRecord("FDE1", 16, analyzer.getNextAddress())); }
			;
			
instr_call:	I_CALL rotule_name												{ $$ = analyzer.newInstruction(new OCRecord("CD" + littleEndianHex(((IntExpression) $2).getData()), 16, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $2).getSymbol());}
			| I_CALL t6 COMMA rotule_name									{ $$ = analyzer.newInstruction(new OCRecord("11" + ((Flag) $2).getCodification() + "100" + littleEndianBin(((IntExpression) $4).getData()), 2, analyzer.getNextAddress())); analyzer.saveAddressSegmentsInfo(((IntExpression) $4).getSymbol());}
			;
			
instr_ret:	I_RET															{ $$ = analyzer.newInstruction(new OCRecord("C9", 16, analyzer.getNextAddress())); }
			| I_RET t6														{ $$ = analyzer.newInstruction(new OCRecord("11" + ((Flag) $2).getCodification() + "000", 2, analyzer.getNextAddress())); }
			;
			
instr_daa:	I_DAA															{ $$ = analyzer.newInstruction(new OCRecord("27", 16, analyzer.getNextAddress())); }
			| I_DAA	R_A														{ analyzer.reportWarning("Instruction DAA must have 0 operands"); $$ = analyzer.newInstruction(new OCRecord("27", 16, analyzer.getNextAddress())); }
			;
			
instr_nop:	I_NOP															{ $$ = analyzer.newInstruction(new OCRecord("00", 16, analyzer.getNextAddress())); }
			;
			
instr_rst:	I_RST int_number												{ String offset = getRSTPageZeroOffset((Integer) $2); if (offset.equals("")) { analyzer.reportError("Wrong parameter for instruction RST"); }$$ = analyzer.newInstruction(new OCRecord("11" + offset + "111", 2, analyzer.getNextAddress())); }
			;

instr_in:	I_IN R_A COMMA OPEN_PAREN int_exp CLOSE_PAREN					{ if (((IntExpression) $5).getSymbol() != null) { analyzer.reportError("IN instruction argument must be 8 bits long"); } else { $$ = analyzer.newInstruction(new OCRecord("DB" + leastSignificativeHex((Integer) ((IntExpression) $5).getData()), 16, analyzer.getNextAddress())); } }
			;
			
instr_out:	I_OUT OPEN_PAREN int_exp CLOSE_PAREN COMMA R_A					{ if (((IntExpression) $3).getSymbol() != null) { analyzer.reportError("OUT instruction argument must be 8 bits long"); } else { $$ = analyzer.newInstruction(new OCRecord("D3" + leastSignificativeHex((Integer) ((IntExpression) $3).getData()), 16, analyzer.getNextAddress())); } }
			;
			

instr_halt:	I_HALT 															{ $$ = analyzer.newInstruction(new OCRecord("76", 16, analyzer.getNextAddress())); }
			;

instr_exx:	I_EXX 															{ $$ = analyzer.newInstruction(new OCRecord("D9", 16, analyzer.getNextAddress())); }
			;

instr_ex:	I_EX OPEN_PAREN R_SP CLOSE_PAREN COMMA R_HL						{ $$ = analyzer.newInstruction(new OCRecord("E3", 16, analyzer.getNextAddress())); }
			| I_EX OPEN_PAREN R_SP CLOSE_PAREN COMMA R_IX					{ $$ = analyzer.newInstruction(new OCRecord("DDE3", 16, analyzer.getNextAddress())); }
			| I_EX OPEN_PAREN R_SP CLOSE_PAREN COMMA R_IY					{ $$ = analyzer.newInstruction(new OCRecord("FDE3", 16, analyzer.getNextAddress())); }
			| I_EX R_DE COMMA R_HL											{ $$ = analyzer.newInstruction(new OCRecord("EB", 16, analyzer.getNextAddress())); }
			| I_EX R_AF COMMA R_AF											{ $$ = analyzer.newInstruction(new OCRecord("08", 16, analyzer.getNextAddress())); }
			;

instr_ei:	I_EI 															{ $$ = analyzer.newInstruction(new OCRecord("FB", 16, analyzer.getNextAddress())); }
			;

instr_di:	I_DI															{ $$ = analyzer.newInstruction(new OCRecord("F3", 16, analyzer.getNextAddress())); }
			;

instr_bit:	I_BIT int_3bits_exp COMMA t1										{ $$ = analyzer.newInstruction(new OCRecord("1100101101" + toBin((Integer) $2, 3) + ((Register) $4).getCodification(), 2, analyzer.getNextAddress())); }
			| I_BIT int_3bits_exp COMMA OPEN_PAREN R_HL CLOSE_PAREN				{ $$ = analyzer.newInstruction(new OCRecord("1100101101" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			| I_BIT int_3bits_exp COMMA OPEN_PAREN R_IX bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1101110111001011" + ((String) $6) + "01" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			| I_BIT int_3bits_exp COMMA OPEN_PAREN R_IY bin_offset CLOSE_PAREN	{ $$ = analyzer.newInstruction(new OCRecord("1111110111001011" + ((String) $6) + "01" + toBin((Integer) $2, 3) + "110", 2, analyzer.getNextAddress())); }
			;

instr_scf:	I_SCF 															{ $$ = analyzer.newInstruction(new OCRecord("37", 16, analyzer.getNextAddress())); }
			;

instr_ccf:	I_CCF 															{ $$ = analyzer.newInstruction(new OCRecord("3F", 16, analyzer.getNextAddress())); }
			;
				
rotule_name:	int_16bits_exp												{ $$ = $1; }
				;
			
t1:	R_B			{ $$ = Register.B; }
	| RF_C		{ $$ = Register.C; }
	| R_D		{ $$ = Register.D; }
	| R_E		{ $$ = Register.E; }
	| R_H		{ $$ = Register.H; }
	| R_L		{ $$ = Register.L; }
	| R_A		{ $$ = Register.A; }
	;	
	
t2:	R_BC		{ $$ = Register.BC; }
	| R_DE		{ $$ = Register.DE; }
	| R_HL		{ $$ = Register.HL; }
	| R_AF		{ $$ = Register.AF; }
	;

t3:	R_BC		{ $$ = Register.BC; }
	| R_DE		{ $$ = Register.DE; }
	| R_HL		{ $$ = Register.HL; }
	| R_SP		{ $$ = Register.SP; }
	;
	
t4:	R_BC		{ $$ = Register.BC; }
	| R_DE		{ $$ = Register.DE; }
	| R_IX		{ $$ = Register.IX; }
	| R_SP		{ $$ = Register.SP; }
	;

t5:	R_BC		{ $$ = Register.BC; }
	| R_DE		{ $$ = Register.DE; }
	| R_IY		{ $$ = Register.IY; }
	| R_SP		{ $$ = Register.SP; }
	;
			
t6: F_NZ		{ $$ = Flag.NZ; }
	| F_Z		{ $$ = Flag.Z; }
	| F_NC		{ $$ = Flag.NC; }
	| RF_C		{ $$ = Flag.C; }
	| F_PO		{ $$ = Flag.PO; }
	| F_PE		{ $$ = Flag.PE; }
	| F_P		{ $$ = Flag.P; }
	| F_M		{ $$ = Flag.M; }
	;
	
%%
protected void setYylval(String parserVal) {
	this.yylval = parserVal;
}

protected int yyparseWrapper() {
	return yyparse();
}

private void logError(String message) {
	System.out.println("logError");
	logger.showError(getFileName(), line(), column(), message);
}
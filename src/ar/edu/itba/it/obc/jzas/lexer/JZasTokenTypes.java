package ar.edu.itba.it.obc.jzas.lexer;

/**
 * List of tokens retrieved by the parser.
 * <p>
 * Each token has a
 * </p>
 */
public enum JZasTokenTypes {

	/* Registros */
	R_B(300, "B"),
	RF_C(301, "C"),
	R_D(302, "D"),
	R_E(303, "E"),
	R_H(304, "H"),
	R_L(305, "L"),
	R_BC(306, "BC"),
	R_DE(307, "DE"),
	R_HL(308, "HL"),
	R_AF(309, "AF"),
	R_SP(310, "SP"),
	R_IX(311, "IX"),
	R_IY(312, "IY"),
	R_A(313, "A"),
	R_F(314, "F"),
	R_R(315, "R"),
	R_I(316, "I"),
		
	/* Flags */
	F_NZ(400, "NZ"),
	F_Z(401, "Z"),
	F_NC(402, "NC"),
	F_PO(403, "PO"),
	F_PE(404, "PE"),
	F_P(405, "F_P"),
	F_M(406, "M"),
	
	/* Directivas al compilador */
	D_EQU(450, "EQU"),
	D_DEFL(451, "DEFL"),
	D_MACRO(452, "MACRO"),
	D_DEFB(453, "DEFB"),
	D_DEFW(454, "DEFW"),
	D_DEFM(455, "DEFM"),
	D_DEFS(456, "DEFS"),
	D_DEFF(457, "DEFF"),
	D_END(458, "END"),
	D_LOCAL(459, "LOCAL"),
	D_PUBLIC(460, "PUBLIC"),
	D_ENT(461, "ENT"), // TODO: hay diferencia entre public y ent. que es "::" ver pagina 18 del capitulo 7 del libro
	D_EXTERN(462, "EXTERN"),
	D_GLOBAL(463, "GLOBAL"),
	D_ASEG(464, "ASEG"),
	D_ORG(465, "ORG"),
	D_CSEG(466, "CSEG"),
	D_DSEG(467, "DSEG"),
	D_INCLUDE(468, "INCLUDE"),
	D_ENDM(469, "ENDM"),
	
	/* Instrucciones */
	I_ADD(500, "ADD"),
	I_ADC(501, "ADC"),
	I_SUB(502, "SUB"),
	I_SBC(503, "SBC"),
	I_INC(504, "INC"),
	I_DEC(505, "DEC"),
	I_NEG(506, "NEG"),
	I_MLT(507, "MLT"),
	I_LD(508, "LD"),
	I_AND(509, "AND"),
	I_OR(510, "OR"),
	I_XOR(511, "XOR"),
	I_CPL(512, "CPL"),
	I_CP(513, "CP"),
	I_JP(514, "JP"),
	I_DJNZ(515, "DJNZ"),
	I_SLA(516, "SLA"),
	I_SRA(517, "SRA"),
	I_SRL(518, "SRL"),
	I_RLA(519, "RLA"),
	I_RL(520, "RL"),
	I_RLCA(521, "RLCA"),
	I_RLC(522, "RLC"),
	I_RRA(523, "RRA"),
	I_RR(524, "RR"),
	I_RRCA(525, "RRCA"),
	I_RRC(526, "RRC"),
	I_RLD(527, "RLD"),
	I_RRD(528, "RRD"),
	I_SET(529, "SET"),
	I_RES(530, "RES"),
	I_PUSH(531, "PUSH"),
	I_POP(532, "POP"),
	I_CALL(533, "CALL"),
	I_RET(534, "RET"),
	I_DAA(535, "DAA"),
	I_NOP(536, "NOP"),
	I_JR(537, "JR"),
	I_RST(538, "RST"),
	I_IN(539, "IN"),
	I_OUT(540, "OUT"),
	I_HALT(541, "HALT"),
	I_EX(542, "EX"),
	I_EXX(543, "EXX"),
	I_EI(544, "EI"),
	I_DI(545, "DI"),
	I_BIT(546, "BIT"),
	I_SCF(547, "SCF"),
	I_CCF(548, "CCF"),
	
	/* Otros */
	NL(600, "NL"),
	SPACE(601, "SPACE"),
	PLUS(602, "+"),
	MINUS(603, "-"),
	COLON(604, ":"),
	COMMA(605, "\",\""),
	OPEN_PAREN(606, "("),
	CLOSE_PAREN(607, ")"),
	LITERAL(608, "LITERAL"),
	ID(609, "ID"),
	DOUBLE_COLON(610, "::"),
	INTEGER(611, "INTEGER"),
	REAL(612, "REAL"),
	CHARACTER(613, "CHARACTER"),
	BINARY(614, "BYNARY"),
	HEXA(615, "HEXA"),
	TIMES(616, "*"),
	DIVIDE(617, "/"),
	DOLLAR_SIGN(618, "$"),
	MACRO_LINE(619, "MACRO_LINE")
	;
	
	private int value;
	private String caption;

	private JZasTokenTypes(int value) {
		this.value = value;
	}

	private JZasTokenTypes(int value, String caption) {
		this.value = value;
		this.caption = caption;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.caption == null ? super.toString() : this.caption;
	}

}

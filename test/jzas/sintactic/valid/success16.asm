; BIBLIOTECA fechas.asm
; Contiene subrutinas para el manejo de fechas en formato de string
; (escritura, validacion, comparacion, lectura, conversiones ASCII-enteros)

; Autores:	Bigio, Rafael Martín
;		Coffey, Santiago Andrés

; ############################################	####################################
; INTERFACE
	; SUBRUTINAS PRINCIPALES
		global ArmaFecha
		global EsFechaOK
		global EsBisiesto
		global CpFechas
		global FinDeMes
		global ParteFecha
		global AtoI
		global ItoA
		global LtoA

	; SUBRUTINAS AUXILIARES
		global EsAnoOk		; auxiliar EsFechaNumericaOk, EsBisiesto
		global EsFechaNumericaOk	; auxiliar EsFechaOK, ArmaFecha, FinDeMes
		global FinMes		; auxiliar EsFechaNumericaOk
		global DivHLDE		; auxiliar Itoa, LtoA, EsBisiesto
		global dig2num		; auxiliar AtoI

	; MACROS AUXILIARES ArmaddOmm, PoneBarra, ResOverflow, SetOverflow
	;	include fechas.mac


; ################################################################################
; IMPLEMENTACION

		cseg

; ################################################################################
; EsAnoOk (subrutina auxiliar)
; Recibe del stack el año que se quiere chequear. 
; Si es valido, apaga el flag de overflow. En caso contrario, lo enciende.
; No altera otros flags ni registros.

EsAnoOk			
		ld (EsAnoOk_backup), hl		; resguardos
		ld (EsAnoOk_backup + 2), de
		pop hl
		pop de
		push hl
		push af 	;salvo los flags
		
		ld HL, 9999	; valida que sea <=9999
		add a, 0	;carry =0
		sbc hl, de
		jp M, EsAnoOk_invalido
		add a, 0		; carry = 0
		ld HL, 1699
		sbc hl, de
		jp P, EsAnoOk_invalido
		ResOverflow hl, l	;apago el "overflow" y lo mando a la pila
		jr EsAnoOk_fin
EsAnoOk_invalido	SetOverflow hl, l	;prendo el "overflow" en el stack

EsAnoOk_fin	pop af
		ld hl, (EsAnoOk_backup)		;restauro backups
		ld de, (EsAnoOk_backup + 2)
		ret
		
		dseg
EsAnoOk_backup	ds 4
		cseg


; ################################################################################
; EsBisiesto
; Recibe por HL el año. Si es invalido enciende el overflow. Sino lo apaga.
; Si ademas es bisiesto, enciende el flag Z. Sino, lo apaga.
; No altera otros flags ni registros.

EsBisiesto:	ld (EsBisiesto_aux), HL	; resguardos
		push DE
		push BC
		push AF
		pop BC		; BC = AF
		set 2, C	; por default el "flag de C" P=1 (hay error)
		push HL		; HL a la pila
		call EsAnoOk	; controla si el rango es OK
		jp PE, EsBisiesto_fin	; si el rango es invalido, fin con error
		res 2, C	; sino el "flag de C" P=0 (no hay error)
		set 6, C	; por default el "flag de C" Z=1 (es bisiesto)

		ld DE, 400
		call DivHLDE	; chequea que sea divisible por 400
		ld DE, 0
		add A, 0	; apaga el carry antes de adc y suma 0 
		adc HL, DE	; para ver si HL=0 (resto 0 => es bisiesto)
		jp Z, EsBisiesto_fin	; si es divisible por 400 es bisiesto

		ld HL, (EsBisiesto_aux)
		ld A, L
		and 03h		; si es divisible por 4, A=0
		cp 0
		jp NZ, EsBisiesto_no	; si no es divisible por 4 no es bisiesto

		ld DE, 100
		call DivHLDE
		ld A, L		; el resto es de 1 byte, si es 0 es div.x100
		cp 0
		jp NZ, EsBisiesto_fin	; si es div.x4 pero no div.x100 es bisiesto

EsBisiesto_no:	res 6, C	; deja el "flag de C" Z=0 (no es bisiesto)

EsBisiesto_fin:	push BC
		pop AF		; AF=BC
		pop BC		; restauraciones
		pop DE
		ld HL, (EsBisiesto_aux)
		ret

		Dseg
EsBisiesto_aux:	ds 2
		Cseg

; ################################################################################
; FinMes (subrutina auxiliar)
; Recide el mes por C y devuelve la cantidad de dias que tiene en D.
; Recibe por HL entra el año. Se supone que los datos estan validados.
; No altera los flags ni los registros.

FinMes	
		push hl		;salvo el año
		push bc		;salvo bc
		push af		;salvo los flags y a
		ld a, c
		cp 8
		jp M, FinMes_sigo
		inc c		;se poner inc a directamente
FinMes_sigo	ld a, c		;backup de c para utilizar el sra
		srl c		;quiero saber si el mes es 30 o 31 días
		ld c, a
		jp C, FinMes_31
		cp 2			;mes de 30
		jp Z, FinMes_febrero
		ld d, 30
		jp FinMes_salida
FinMes_febrero	call EsBisiesto		;chequea si es bisiesto
		jp NZ, FinMes_noesbisiesto		
		ld d, 29
		jp FinMes_salida	;el año es bisiesto
FinMes_noesbisiesto	ld d, 28
		jp FinMes_salida
FinMes_31	ld d, 31
FinMes_salida	pop af		;restauro los flags y a
		pop bc		;restauro bc
		pop hl		;restauro hl
		ret


; ################################################################################
; EsFechaNumericaOk (subrutina auxiliar)
; Dada una fecha numerica, chequea que sea valida.
; Por el stack se recibe primero el año y luego el dia y el mes en zona alta baja.
; No altera otros flags ni registros.

EsFechaNumericaOk
		ld (EsFechaNumericaOk_backup), hl	; backups
		ld (EsFechaNumericaOk_backup+2),bc
		ld (EsFechaNumericaOk_backup+4), de
			
		pop de			; ret
		pop hl			; hl=año
		pop bc			; b=dia, c=mes
		push de
			
		push hl			; checkeo que el año sea válido
		call EsAnoOk
		push af 		; backup de af
		jp PE, EsFechaNumericaOk_fin
		ld a, b			; validar 1<=dia
		cp 1
		jp M, EsFechaNumericaOk_invalido

		ld a, c			; validar 1<=mes<=12
		cp 1
		jp M, EsFechaNumericaOk_invalido
		cp 13
		jp P, EsFechaNumericaOk_invalido
		call FinMes
		ld A, D
		ld (EsFechaNumericaOk_cantDias), A

		inc d
EsFechaNumericaOk_check
		ld a, b
		cp d
		jp p, EsFechaNumericaOk_invalido
		ResOverflow de, e	; apago el overflow en el stack
		jp EsFechaNumericaOk_fin
EsFechaNumericaOk_invalido
		SetOverflow de, e	; enciendo el overflow en el stack
EsFechaNumericaOk_fin
		pop af			; restauraciones
		ld hl, (EsFechaNumericaOk_backup)
		ld bc, (EsFechaNumericaOk_backup+2)
		ld de, (EsFechaNumericaOk_backup+4)
		ret

		dseg
EsFechaNumericaOk_backup		
		ds 6
EsFechaNumericaOk_cantDias
		ds 1
		cseg

; ################################################################################
; EsFechaOK
; Valida la fecha apuntada por IX.
; Si es valida apaga el overflow. Sino lo enciende.
; No altera otros flags ni registros.

EsFechaOK					
		ld (EsFechaOK_backup), iy	;backups
		ld (EsFechaOK_backup+2), hl
		ld (EsFechaOK_backup+4), ix
		ld (EsFechaOK_backup+6), bc
		ld (EsFechaOK_backup+8), a

		ld a, (ix+2)		;salvo las "barras"
		ld b, a
		ld a, (ix+5)
		ld c, a
		push bc
		push af		
	
		ld a, '/'		; chequea las barras
		cp (ix+2)
		jp NZ, EsFechaOK_invalido
		cp (ix+5)
		jp NZ, EsFechaOK_invalido
			
		ld b, 3			;contador del ciclo
		ld a, 0
		ld (ix+2), a		;cambio las barras por el numero cero
		ld (ix+5), a
		ld iy, EsFechaOK_rot
EsFechaOK_ciclo	call AtoI
		jp PE, EsFechaOK_invalido	;si el formato es incorrecto sale
		ld a, 0
		cp (ix+1)
		jp Z, EsFechaOK_invalido	; si el segundo es un 0 sale
		inc ix				
		inc ix
		inc ix
		inc iy
		djnz EsFechaOK_ciclo
		ld a, (iy-3)		; HL=dia-mes
		ld h, a
		ld a, (iy-2)
		ld l, a
		push hl			; dia-mes a la pila
		ld a, (iy-1)		; hl = año
		ld l, a
		ld a, (iy)
		ld h, a
		push hl			; año a la pila
		call EsFechaNumericaOk	; valida la fecha (numerica)
		jp PE, EsFechaOK_invalido
		ResOverflow hl, l	; apago el overflow en el stack
		jp EsFechaOK_fin
EsFechaOK_invalido	
		SetOverflow hl, l	; enciendo el overflow en el stack
EsFechaOK_fin	pop af
		pop bc
		ld a, b
		ld ix, (EsFechaOK_backup+4)			
		ld (ix+2), a			;restauro las "barras"
		ld a, c
		ld (ix+5), a

		ld iy, (EsFechaOK_backup)	;restauro backup's
		ld hl, (EsFechaOK_backup+2)
		ld ix, (EsFechaOK_backup+4)
		ld bc, (EsFechaOK_backup+6)
		ld a, (EsFechaOK_backup+8)
		ret

			dseg
EsFechaOK_rot		ds 4	;aca se guarda la fecha en formato entero
EsFechaOK_backup	ds 9
			cseg



; ################################################################################
; ArmaFecha
; Escribe en la direccion apuntada por IX, la fecha en formato de string segun
; los parametros recibidos (D=dia, E=mes, HL=año).
; Si la fecha a armar es invalida enciende el flag de overflow. Sino lo apaga.
; No altera otros flags ni registros.

ArmaFecha
		ld (ArmaFecha_backup), iy
		ld (ArmaFecha_backup+2), bc
	
		push af			; guardo los flags
		SetOverflow bc, c	; por default flag P=1

		ld iy, ArmaFecha_rot
		push de			; meto en la pila el dia, el mes y el año
		push hl
		call EsFechaNumericaOk
		jp PE, ArmaFecha_invalida
	
		push ix			; swap ix, iy
		pop iy
		ld ix, ArmaFecha_rot

		ArmaddOmm d	; armo dia
		ArmaddOmm e	; armo mes
		ld a, l		; armo el año: primero subo a memoria la parte baja
		ld (ix), a
		ld a, h		; pongo en memoria la parte alta
		ld (ix+1), a
		call LtoA
		ResOverflow bc, c	; apago el overflow

ArmaFecha_invalida
		pop af
		ld iy, (ArmaFecha_backup)
		ld bc, (ArmaFecha_backup+2)
		ret

		dseg
ArmaFecha_rot	ds 2
ArmaFecha_backup	ds 4
		cseg


; ################################################################################
; FinDeMes
; Retorna en la 2da direccion que levanta el stack la fecha correspondiente
; al ultimo dia del mes de la fecha de la 1ra direccion que se levanta del stack.
; En caso de que la fecha sea invalida, enciende el flag de overflow. 
; En caso contrario lo apaga. No altera otros flags ni registros.

FinDeMes					
		ld (FinDeMes_backup), de		;backups
		ld (FinDeMes_backup+2), IX
		ld (FinDeMes_backup+4), IY
	
		pop de			;ret
		pop ix			;dir fecha
		pop iy			;dir salida
		push de			;pongo ret en la pila
		push HL
		push bc
		push af

		call EsFechaOK			;valido la fecha
		jp PE, FinDeMes_invalida	;si es invalida no proceso nada
		ld A, (EsFechaNumericaOk_cantDias)
		ld D, A
		ld a, (EsFechaOK_rot+1)	;cuando se llamo a EsFechaOK, en EsFechaOK_rot 
					;quedó la conversión a integer de la fecha
		ld c, a					;c=mes
		ld hl, (EsFechaOK_rot + 2)
		ld e, c			;e=c
		push iy
		pop ix			;ix = iy
		call ArmaFecha		;armo la fecha
		push ix			;iy = ix (pongo la salida en iy)
		pop iy				
		ResOverflow bc, c	;apago el overflow

		jp FinDeMes_fin		;termino
FinDeMes_invalida	SetOverflow bc, c	;seteo el overflow
FinDeMes_fin	pop af			;pongo los flags modificados
		pop bc
		pop HL
		ld de, (FinDeMes_backup)		;restauro los backups
		ld IX, (FinDeMes_backup+2)
		ld IY, (FinDeMes_backup+4)

		ret
		dseg
FinDeMes_backup	ds 6
		cseg

; ################################################################################
; CpFechas
; Levanta del stack la direccion de dos fechas (Fecha1 y Fecha2, en ese orden)
; Modifica los flags Z (cero), S (signo) y P (overflow)
; Si alguna de las dos direcciones no corresponde a un string 
; de fecha valido: P=1. En otro caso P=0, y S y Z no se modifican.
; Si Fecha1 < Fecha2: S=1, Z=0
; Si Fecha1 = Fecha2: S=0, Z=1
; Si Fecha1 > Fecha2: S=0, Z=0
; No altera otros flags, ni los registros.

CpFechas:	ld (CpFechas_bkp), HL
		ld (CpFechas_bkp+2), IX
		ld (CpFechas_bkp+4), IY	; resguarda HL, IX y IY
		pop HL		; levanta dir de ret en HL
		pop IX		; IX = primera fecha
		pop IY		; IY = segunda fecha
		push HL		; devuelve dir de ret al stack
		push DE
		push BC
		push AF		; resguarda AF

		call EsFechaOK
		jp PE, CpFechas_error	; validacion de Fecha1
		; ahora se aprovecha lo calculado por EsFechaOK
		ld BC, (EsFechaOK_rot)	; BC = mes y dia
		ld HL, (EsFechaOK_rot+2)	; HL = año

		push IX		; ahora se intercambia IX por IY a traves del stack
		push IY
		pop IX		; IX = segunda fecha (ex-IY)
		pop IY		; IY = primera fecha (ex-IX)
		call EsFechaOK
		jp PE, CpFechas_error	; validacion de Fecha2

		; ahora se aprovecha lo calculado por EsFechaOK
		ld DE, (EsFechaOK_rot+2)	; carga el Año2 en DE
		add A, 0		; apaga el carry antes de sbc
		sbc HL, DE		; resta Año1-Año2
		jp M, CpFechas_menor
		jp Z, CpFechas_CpMes
		jp P, CpFechas_mayor

CpFechas_CpMes:	ld HL, EsFechaOK_rot+1
		ld A, B
		cp (HL)
		jp M, CpFechas_menor
		jp Z, CpFechas_CpDia
		jp P, CpFechas_mayor

CpFechas_CpDia:	ld HL, EsFechaOK_rot
		ld A, C
		cp (HL)
		jp M, CpFechas_menor
		jp Z, CpFechas_igual
		jp P, CpFechas_mayor

		; ahora segun <, =, > o error se modifican los flags
CpFechas_menor:	pop HL		; carga el resguardo de AF en HL
		ld A, L
		and 0BBh	; apaga Z y P
		or 80h		; enciende S
		ld L, A
		push HL		; deja el nuevo AF en stack
		jp CpFechas_fin

CpFechas_igual:	pop HL		; carga el resguardo de AF en HL
		ld A, L
		or 40h		; enciende Z
		and 7Bh		; apaga S y P
		ld L, A
		push HL		; deja el nuevo AF en stack
		jp CpFechas_fin

CpFechas_mayor:	pop HL		; carga el resguardo de AF en HL
		ld A, L
		and 3Bh		; apaga Z, S y P
		ld L, A
		push HL		; deja el nuevo AF en stack
		jp CpFechas_fin

CpFechas_error:	pop HL		; carga el resguardo de AF en HL
		ld A, L
		or 04h		; enciende P
		ld L, A
		push HL		; deja el nuevo AF en stack

CpFechas_fin:	pop AF		; restaura AF con los nuevos flags
		pop BC
		pop DE
		ld IY, (CpFechas_bkp+4)	; restaura IY, IX y HL
		ld IX, (CpFechas_bkp+2)
		ld HL, (CpFechas_bkp)
		ret

		Dseg
CpFechas_bkp:	ds 6
		Cseg

; ################################################################################
; ParteFecha
; Levanta del stack la direccion de una fecha, luego en zona alta el ASCII del 
; campo solicitado ('d' o 'D' para dia, 'm' o 'M' para mes, 'a' o 'A' para año)
; y por ultimo la direccion para la respuesta.
; Si la fecha y el campo son validos devuelve el dia, mes o año de la fecha
; en forma numerica y apaga el flag de overflow. En caso contrario, lo enciende.
; No altera registros.

ParteFecha:	ld (ParteFecha_bkp), IX
		ld (ParteFecha_bkp+2), IY
		ld (ParteFecha_bkp+4), DE
		ld (ParteFecha_bkp+6), BC
		pop BC	; levanta la dir de ret en HL
		pop IX	; IX = dir de fecha
		pop DE	; D = ASCII del campo, E = basura
		pop IY	; IY = dir de respuesta
		push BC	; devuelve la dir de ret al stack
		push AF	; resguarda AF

		SetOverflow BC, C	; por default el overflow estara encendido
		call EsFechaOK		; valida la fecha
		jp PE, ParteFecha_fin

		ld A, D
		cp 'a'
		jp M, ParteFecha_may
		sub 'a'-'A'	; pasa un ASCII>='a' a mayuscula
ParteFecha_may:	ld DE, 0	; DE servira para desplazarse al sig. campo
		cp 'D'
		jp Z, ParteFecha_ok
		inc DE
		cp 'M'
		jp Z, ParteFecha_ok
		inc DE
		cp 'A'
		jp NZ, ParteFecha_fin

ParteFecha_ok:	ld IX, EsFechaOK_rot	; aprovecha lo ya calculado por EsFechaOK
		add IX, DE
		ld A, (IX)
		ld (IY), A
		ResOverflow BC, C	; apaga el overflow en stack
		ld A, E		; si se pidio año, todavia falta un byte, sino fin
		cp 2
		jp NZ, ParteFecha_fin
		ld A, (IX+1)
		ld (IY+1), A

ParteFecha_fin:	ld IX, (ParteFecha_bkp)	; restauraciones
		ld IY, (ParteFecha_bkp+2)
		ld DE, (ParteFecha_bkp+4)
		ld BC, (ParteFecha_bkp+6)
		pop AF
		ret

		Dseg
ParteFecha_bkp:	ds 8
		Cseg

; ################################################################################
; DivHLDE ("Dividir HL/DE") (subrutina auxiliar)
; Divide un numero de 16 bits no negativo almacenado en HL (0 <= HL < 8000h)
; por un numero de 16 bits positivo almacenado en DE (0 < DE < 8000h)
; dejando en HL el resto y en DE el cociente.
; No altera los flags. Solo afecta a los registros HL y DE.
; Si el dividendo o el divisor es negativo el resultado es indeterminado.
; En el caso en que DE=00h, cae en un ciclo infinito.

DivHLDE:	push AF		; salva AF y BC
		push BC
		ld BC, 0	; BC acumulara el cociente

DivHLDE_ciclo:	add A, 0	; apaga el carry antes del sbc
		sbc HL, DE	; HL = HL-DE sucesivamente
		jp M, DivHLDE_fin	; si el resultado es negativo, se paso
		inc BC		; BC cuenta cuantas restas se hicieron
		jr DivHLDE_ciclo	; repite el ciclo de la resta hasta que HL<0

DivHLDE_fin:	add HL, DE	; incrementa HL en DE porque se paso
				; (ahora en HL queda el resto)
		ld D, B
		ld E, C		; deja en DE el cociente BC

		pop BC
		pop AF		; restaura AF y BC
		ret

; ################################################################################
; ItoA ("Integer to ASCII")
; Recibe la direccion de un entero sin signo de 8 bits apuntada por IX y escribe 
; a partir de la direccion apuntada por IY el string que representa en decimal.
; En la respuesta, puede llegar a pisar hasta 4 bytes
; (centena, decena, unidad y byte 00h de fin de string).
; No altera flags ni registros.

ItoA:		push BC
		push IX		; resguardos

		ld C, (IX)
		ld B, 0		; carga BC = (IX) (el numero a convertir)
		ld (ItoA_aux), BC	; lo carga en ItoA_aux como si fuera de 16 bits
		ld IX, ItoA_aux	; y lo apunta con IX
		call LtoA	; llama a LtoA para terminar

		pop IX		; restauraciones
		pop BC
		ret

		Dseg
ItoA_aux:	ds 2
		Cseg

; ################################################################################
; LtoA ("Long integer to ASCII")
; Recibe la direccion de un entero sin signo de 16 bits apuntada por IX y escribe 
; a partir de la direccion apuntada por IY el string que representa en decimal.
; Si el entero es mayor estricto que 9999 en decimal, enciende el flag de overflow 
; y no escribe nada en memoria. Sino, lo apaga y escribe en memoria lo antedicho.
; En la respuesta, puede llegar a pisar hasta 5 bytes
; (unidad de mil, centena, decena, unidad y byte 00h de fin de string).
; No altera registros. Solo modifica el flag de overflow si hubo error.

LtoA:		push BC
		push DE
		push HL
		push IX
		push IY
		push AF		; resguardo de registros
		SetOverflow BC, C	; overflow por default encendido en el stack

		ld A, (IX+1)
		cp 0		; si el primer byte del "long integer"
		jp M, LtoA_fin	; es negativo (con signo), seguro es >=1000 (sin signo)
		ld H, A
		ld L, (IX)	; carga HL=(IX) en little-endian
		ld DE, 10000
		add A, 0
		sbc HL, DE	; apaga el carry y resta HL=HL-10000
		jp P, LtoA_fin	; si el resultado es >=0 salta para dar overflow
		add HL, DE	; restaura HL al valor original

	; ahora se cargara a partir de LtoA_aux la unidad, 
	; la decena, la centena y la u de mil en ese orden
		ld IX, LtoA_aux
		ld B, 3
LtoA_ciclo:	ld DE, 10	; DE=10 (divisor)
		call DivHLDE	; divide HL/DE
		ld (IX), L	; pasa el resto que estaba en HL (unidad/decena/centena)
				; a (IX), el cual seguro es de 8 bits
		ld L, E		; y el cociente que estaba en DE (u de mil, centena 
		ld H, D		; y decena/u de mil y centena/u de mil) a HL
		ld DE, 0	; evalua si los digitos mas signif. seran todos 0
		add A, 0	; (si HL=0000h no hay que seguir convirtiendo digitos)
		adc HL, DE
		jp Z, LtoA_ceros
		inc IX
		djnz LtoA_ciclo

		ld (IX), L	; carga el primer digito no nulo en IX
LtoA_ceros:	ld A, 4
		sub B	; A=4-B (para saber cuanto digitos a escribir)
		cp 0
		jp NZ, LtoA_cero
		inc A		; considera caso A=0
LtoA_cero:		ld B, A		; B = cant. digitos a escribir comenzando desde (IX)

	; este ciclo escribe los digitos que corresponden a partir de (IY)
LtoA_escribir:	ld A, (IX)
		add A, '0'
		ld (IY), A
		dec IX
		inc IY
		djnz LtoA_escribir
		ld (IY), 0	; termina el string con 00h
		ResOverflow BC, C	; apaga el overflow en stack

LtoA_fin:	pop AF		; demas restauraciones
		pop IY
		pop IX
		pop HL
		pop DE
		pop BC
		ret

		Dseg
LtoA_aux:	ds 4
		Cseg

; ################################################################################
; AtoI ("ASCII to integer") (subrutina auxiliar)
; Recibe un string numerico de 1 a 4 caracteres en la direccion apuntada por IX
; y devuelve el entero que representa en la direccion apuntada por IY.
; La respuesta ocupa 1 byte si el string es de 1 o 2 bytes.
; La respuesta ocupa 2 bytes si el string es de 3 o 4 bytes.
; No altera registros. Solo modifica el flag de overflow si hubo error.

AtoI:		push BC
		push DE
		push HL
		push IX
		push AF		; resguardo de registros
		SetOverflow BC, C	; por default flag P=1

		ld A, 0
		cp (IX)
		jp Z, AtoI_fin	; si es un sting vacio, error
		ld B, 4
AtoI_busca0:	ld A, (IX+1)
		cp 0
		jp Z, AtoI_hallo0	; busca el 00h de fin de string
		inc IX
		djnz AtoI_busca0
		jp AtoI_fin	; si no lo encontro entre (IX+1) y (IX+4), error

AtoI_hallo0:	ld A, 4
		sub B	; con esto A = long. string - 1
		ld B, A
		inc B		; deja en B la longitud del string
		srl A		; decala A para que si el string era de 1 o 2 
				; bytes => A=0, y si era de 3 o 4 bytes => A=1
		ld (AtoI_aux), A	; guarda ese resultado en AtoI_aux
		ld C, 0	; C contara el exp. de la potencia de 10 corresp. al digito
		ld HL, 0	; HL acumulara la suma del numero a convertir

AtoI_ciclo:	ld A, (IX)	; carga el caracter en A
		cp '0'
		jp M, AtoI_fin
		cp '9'+1
		jp P, AtoI_fin
		sub '0'		; si el caracter era numerico, lo convierte a numero
		ld D, C		; D = potencia de 10 corresp. al digito
		ld E, A		; E = digito
		call dig2num	; DE = E*10^D
		add HL, DE	; acumula el resultado de la conversion en HL
		inc C	; incrementa el exp. de la potencia de 10 corresp. al digito
		dec IX		; y pasa a un digito mas significativo
		djnz AtoI_ciclo

		ResOverflow BC, C	; apaga flag P en stack antes de escribir
		ld A, (AtoI_aux)	; AtoI_aux determina bytes a escribir
		cp 0
		ld (IY), L	; si A=0, escribe un solo byte
		jr Z, AtoI_fin
		ld (IY+1), H	; sino escribe 2 bytes

AtoI_fin:	pop AF		; demas restauraciones
		pop IX
		pop HL
		pop DE
		pop BC
		ret

		Dseg
AtoI_aux:	db 0
		Cseg

; ################################################################################
; dig2num ("digit to number")
; Multiplica el digito (0-9) almacenado en E por la potencia de 10
; almacenada en D, que debe estar comprendida entre 0 y 3 inclusive.
; Deja el resultado en DE. No altera otros registros ni los flags.
; Los casos donde los parametros estan fuera de rango no estan contemplados.

dig2num:	push BC
		push HL
		push AF

	; primero HL = 10^D (D es el exponente de la potencia de 10)
		ld HL, 1	; HL se inicializa en 1 (caso exponente 0)
		ld B, D		; B = exponente de la potencia de 10
		inc B
		jr dig2num_djnz1
	; este ciclo multiplica HL*10 tantas veces como indique B, mientras B<=3
dig2num_pot:	ld H, 10
		mlt HL
dig2num_djnz1:	djnz dig2num_pot

	; luego HL = HL*E (E es el digito)
		ld B, H
		ld C, L		; se pasa HL a BC
		ld HL, 0	; HL acumulara la suma (inicializado en 0)
		inc E
		jr dig2num_djnz2
	; este ciclo de suma BC a HL tantas veces como indique E
dig2num_mlt:	add HL, BC
dig2num_djnz2:	dec E
		jr NZ, dig2num_mlt
		ld D, H
		ld E, L		; el resultado se deja en DE

		pop AF
		pop HL
		pop BC
		ret

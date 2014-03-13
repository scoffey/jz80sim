
	; SUBRUTINAS PRINCIPALES
		extern ArmaFecha
		extern EsFechaOK
		extern EsBisiesto
		extern CpFechas
		extern FinDeMes
		extern ParteFecha
		extern AtoI
		extern ItoA
		extern LtoA

	; SUBRUTINAS AUXILIARES
		extern EsAnoOk		; auxiliar EsFechaNumericaOk, EsBisiesto
		extern EsFechaNumericaOk	; auxiliar EsFechaOK, ArmaFecha
		extern FinMes		; auxiliar FinDeMes, EsFechaNumericaOk
		extern DivHLDE		; auxiliar Itoa, LtoA, EsBisiesto
		extern dig2num		; auxiliar AtoI

		Cseg

PRUEBA1:	ld SP, 0
		ld D, 5		; ingresar en D el dia
		ld E, 3		; ingresar en E el mes
		ld HL, 1997	; ingresar en HL el año
		ld IX, rta
		call ArmaFecha
		rst 38h

PRUEBA2:	ld SP, 0
		ld IX, fecha1
		call EsFechaOK
		rst 38h

PRUEBA3:	ld SP, 0	; ingresar en HL el año
		call EsBisiesto
		rst 38h

PRUEBA4:	ld SP, 0
		ld IX, fecha1
		ld IY, fecha2
		push IY
		push IX
		call CpFechas
		rst 38h

PRUEBA5:	ld SP, 0
		ld IX, fecha1
		ld IY, rta
		push IY
		push IX
		call FinDeMes
		rst 38h

PRUEBA6:	ld SP, 0
		ld IX, fecha1
		ld H, 'm'	; ingresar en H el campo a solicitar
		ld IY, rta
		push IY
		push HL
		push IX
		call ParteFecha
		rst 38h

PRUEBA7:	ld SP, 0
		ld IX, string
		ld IY, rta
		call AtoI
		rst 38h

PRUEBA8:	ld SP, 0
		ld IX, entero8
		ld IY, rta
		call ItoA
		rst 38h

PRUEBA9:	ld SP, 0
		ld IX, entero16
		ld IY, rta
		call LtoA
		rst 38h

;	; MODIFICAR LOS DATOS PARA PROBAR OTROS RESULTADOS
		Dseg
fecha1:		defm "29/02/1904"
		db 0
fecha2:		defm "01/05/1995"
		db 0
string:		defm "0000"
		db 0
entero8:	db 255
entero16:	dw 0
rta:		db 0,0,0,0

		Cseg
		end PRUEBA2	; MODIFICAR PARA PROBAR DISTINTAS SUBRUTINAS

; "HOW-TO" de la linkedicion:
; zas -lfechas fechas.asm
; zas -lfechas_p fechas_p.asm
; link -ofechas_p.cpm fechas_p.obj fechas.obj
; Cargar en el Simulador fechas_p.cpm

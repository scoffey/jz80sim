		aseg
		org 3000h

start0:		jp start

identidad:	ld (aux), BC
		ld (aux+2), DE
		ld (aux+4), IX
		ld (aux+6), IY	; resguarda registros que reciben parametros
		pop DE	; levanta ret
		pop IX	; IX = matriz
		pop BC	; B = cant filas
		pop IY	; IY = respuesta
		push DE	; devuelve ret
		push AF	; resguarda AF
		ld D, B	; D = indice filas que faltan
		ld E, B	; E = indice col que faltan
cicloid:	ld A, D		; comparo D y E
		cp E
		ld A, (IX)	; cargo en A el elemento de indice DE "transpuesto"
		jp NZ, nodiag	; si D y E son distintos, no estoy en la diagonal
		dec A		; pero si estoy en la diagonal, dec A para que A deba ser 0
nodiag:		cp 0
		jp NZ, noesid	; si A no es 0 no es matriz identidad
		inc IX		; apunto con IX al siguiente elemento
		dec E		; y dec E (indice de columnas que faltan)
		jp NZ, cicloid	; si E>0 repito el ciclo (faltan col)
		ld E, B		; sino reseteo E=B (indice de col que faltan)
		dec D		; y dec D (indice de filas que faltan)
		jp NZ, cicloid	; si D>0 repito el ciclo (faltan filas)
		ld A, 1		; si se salio del ciclo es porque era identidad
		jr esid
noesid:		ld A, 0
esid:		ld (IY), A	; carga 1 o 0 si era identidad o no
		pop AF		; restauraciones
		ld BC, (aux)
		ld DE, (aux+2)
		ld IX, (aux+4)
		ld IY, (aux+6)
		jr aux+8
aux:		ds 8
		ret

start:		ld SP,0
		ld BC, respuesta
		push BC
		ld B, 3
		push BC
		ld BC, matriz
		push BC
		call identidad
		rst 38h
matriz:		db 1,0,0,0,1,0,0,0,1
respuesta:	ds 1
		end start0

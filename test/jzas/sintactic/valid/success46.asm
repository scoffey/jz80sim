		aseg
		org 3000h

start0:		jp start

	; Intercambia A y C
swap:		ld (aux), A	;aux=A
		ld A, C
		ld (aux+1), A	;aux+1=C
		ld A, (aux)
		ld C, A		;C=aux
		ld A, (aux+1)	;A=aux+1
		jr aux+2
aux:		ds 2
		ret

	; Intercambia (IX) y (IY)
swapMemo:	ld (auxi), A	;auxi=A
		ld A, (IX)
		ld (auxi+1), A	;auxi+1=(IX)
		ld A, (IY)
		ld (IX), A	;(IX)=(IY)
		ld A, (auxi+1)
		ld (IY), A	;(IY)=auxi+1
		ld A, (auxi)
		jr auxi+2
auxi:		ds 2
		ret

	; Calcula el comienzo de la fila indicada por C de una matriz
	; que empieza en la direccion apuntada por IX y que tiene
	; la cant de columnas indicada por B
calculaDirecc:	push AF
		push BC
		dec C
		mlt BC
		add IX, BC
		pop BC
		pop AF
		ret

	; Intercambia las filas indicadas por A y C de una matriz
	; que empieza en la direccion apuntada por HL y que tiene
	; la cant de columnas indicada por B
	; MODIFICACION: argumentos por stack: cant columnas y basura (B), filas a intercambiar (A y C), direccion de matriz (HL)
intercambiar:	ld (auxil+2), BC
		ld (auxil+4), DE
		ld (auxil+6), HL	; resguardo registros que tomaran los argumentos pasados por stack
		pop BC
		ld (auxil+8), BC	; resguardo instr. sig. al ret de esta subrutina
		pop BC			; B = cant columnas, C = basura
		pop DE
		ld A, D
		ld C, E			; A y C indican filas a intercambiar, como antes
		pop HL			; HL = matriz, como antes
		push IX
		push IY
		push AF			; resguardo registros a utilizar en stack
		ld D, B
ciclo:		ld (auxil), HL
		ld IX, (auxil)		; IX = matriz
		call calculaDirecc	; IX = comienzo de fila C
		ld (auxil), IX
		ld IY, (auxil)		; IY = IX
		call swap		; (intercambio A y C)
		ld (auxil), HL
		ld IX, (auxil)		; IX = matriz
		call calculaDirecc	; IX = comienzo de fila A
		call swapMemo		; (intercambio (IX) y (IY))
		inc HL
		ld (auxil), A		; simula un djnz con D, resguardando A
		dec D
		ld A, D
		cp 0
		ld A, (auxil)
		jp NZ, ciclo
		jr auxil+10
auxil:		ds 10
		pop AF			; restauraciones de datos en stack
		pop IY
		pop IX
		ld BC, (auxil+8)
		push BC			; restaura instr. sig. al ret
		ld BC, (auxil+2)	; restaura registros que tomaron los argumentos
		ld DE, (auxil+4)
		ld HL, (auxil+6)
		ret

start:		ld SP, 0FFFFh
		ld BC, matriz
		push BC
		ld B, 1
		ld C, 3
		push BC
		ld B, columnas
		ld C, 0
		push BC
		call intercambiar
		rst 38h
matriz		db 1,2,3,4,5,6,7,8,9
columnas	equ 3
		end start0

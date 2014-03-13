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
intercambiar:	push IX
		push IY
		push AF
		push BC
		push DE
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
		jr auxil+2
auxil:		ds 2
		pop DE
		pop BC
		pop AF
		pop IY
		pop IX
		ret

start:		ld SP, 0FFFFh
		ld HL, matriz
		ld A, 1
		ld B, columnas
		ld C, 3
		call intercambiar
		rst 38h
matriz		db 1,2,3,4,5,6,7,8,9
columnas	equ 3
		end start0

		aseg
		org 3000h

start0:		jp start

transponer:	ld (aux), BC
		ld (aux+2), DE
		ld (aux+4), IX
		ld (aux+6), IY	; resguarda los registros
		pop DE		; levanta dir de ret
		pop IX		; IX = matriz
		pop BC		; B = cant columnas, C = basura
		push DE		; restaura dir de ret
		push AF		; resguarda AF
		ld D, 0
		ld E, B		; DE = desplazamiento para sig. fila
		dec B		; salteo la diagonal
		ld C, B		; resguardo B en C
		push IX		; resguardo IX = matriz
cicloFila:	push IX
		pop IY		; paso IX a IY
		ld B, C		; recargo B
cicloCol:	inc IX		; IX apunta al sig. elemento de la fila
		add IY, DE	; IY apunta a la sig. columna
		ld A, (IX)
		ld (aux+8), A	; (aux+8) = (IX)
		ld A, (IY)
		ld (IX), A	; (IY) = (IX)
		ld A, (aux+8)
		ld (IY), A	; (IY) = (aux+8)
		djnz cicloCol
		pop IX		; IX recupera la dir inicial
		add IX, DE	; y se desplaza por la diagonal
		inc IX		; sumando cant. columnas +1
		push IX		; se vuelve a pasar IX por el stack
		dec C		; C-=1 (itera por filas)
		jp NZ, cicloFila
		pop IX		; restauraciones
		pop AF
		ld IY, (aux+6)
		ld IX, (aux+4)
		ld DE, (aux+2)
		ld BC, (aux)
		jr aux+9
aux:		ds 9
		ret

start:		ld SP,0
		ld B, 5
		ld C, 0
		push BC
		ld BC, matriz
		push BC
		call transponer
		rst 38h
matriz:		db 11h,12h,13h,14h,15h,21h,22h,23h,24h,25h,31h,32h,33h,34h,35h,41h,42h,43h,44h,45h,51h,52h,53h,54h,55h
		end start0

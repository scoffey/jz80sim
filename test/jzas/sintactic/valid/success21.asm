		aseg
		org 3000h

start0:		jp start

ledmarquee:	push BC
		push AF
		ld A, 80h	; cargo el valor inicial en A
sigo:		out (82h), A
		ld B, A		; resguardo A en B
		in A, (81h)	; me fijo si se pulso una tecla
		cp 1
		ld A, B		; restauro A
		rrca		; rotacion de bits en A (pasa al sig. valor)
		jp NZ, sigo	; si es cero, termino, sino sigo
		pop AF
		pop BC
		ret

start:		ld SP,0
		call ledmarquee
		rst 38h
		end start0

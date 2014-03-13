		aseg
		org 14D0h
start:		ld IX, 1500h	; IX apunta al numero IEEE
		ld A, (IX+3)	; por little endian, el primer byte del IEEE esta en el byte IX+3
		add A, A	; lo duplico para quedarme con los primeros 7 bits del exponente
		ld B, A		; y guardo el resultado en B
		ld A, (IX+2)	; cargo el segundo byte en A
		add A, 80h	; dejo en el carry el 8vo bit del exponente
		jp NC, fin	; (el mas significativo del segundo byte)
		inc B		; agrego a B el 8vo bit si hubo carry
fin:		ld A, B		; el exponente queda en A
		rst 38h
		end start

		aseg
		org 3000h

start0:		jp start

imprimir:	ld (aux), BC	; resguardo registros
		ld (aux+2), IX
		pop BC		; levanto ret
		pop IX		; IX = cadena
		push BC		; devuelvo ret
ciclocadena:	ld A, (IX)	; cargo el caracter en A
		cp 0
		jr Z, fincadena	; si es 0h termino la cadena
		out (84h), A	; imprimo en pantalla (output) el caracter
		inc IX		; apunto al siguiente caracter
		jp ciclocadena	; repito el ciclo
fincadena:	ld IX, (aux+2)	; restauro registros
		ld BC, (aux)
		jr aux+4
aux:		ds 4
		ret

start:		ld SP,0
		ld BC, cadena
		push BC
		call imprimir
		rst 38h
cadena:		defm "Esta es una cadena cualquiera."
		db 0
		end start0

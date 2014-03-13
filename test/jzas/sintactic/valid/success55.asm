		aseg
		org 3000h

start0:		jp start

num2led:	push HL
		push DE
		push BC
		push AF		; resguardo registros en stack
		ld B, 10	; B = cant. pulsaciones a analizar
esperatecla:	in A, (81h)
		cp 1
		jp NZ, esperatecla	; espera interrupcion de teclado en puerto 81h
		in A, (80h)
		cp '0'
		jp M, noesnum
		cp '9'+1
		jp M, esnum	; carga tecla en A y evalua si es un numero
noesnum:	ld A, '9'+1	; si no es numero, se prenderan todos los leds
esnum:		sub '0'
		ld HL, leds
		ld D, 0
		ld E, A
		add HL, DE	; suma a HL el numero pulsado para que 
		ld A, (HL)	; (HL) sea valor que debe pasarse a los leds
		out (82h), A
		djnz esperatecla
		jr saltadatos
leds:		db 3Fh,06h,5Bh,4Fh,66h,6Dh,7Dh,07h,7Fh,67h,FFh
saltadatos:	pop AF
		pop BC
		pop DE
		pop HL		; restaura registros
		ret

start:		ld SP,0
		call num2led
		rst 38h
		end start0
